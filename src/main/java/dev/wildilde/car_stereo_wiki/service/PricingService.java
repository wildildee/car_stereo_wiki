package dev.wildilde.car_stereo_wiki.service;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.PricingInfo;
import dev.wildilde.car_stereo_wiki.entity.PricingItem;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.PricingInfoRepository;
import dev.wildilde.car_stereo_wiki.repository.PricingItemRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PricingService {
    private static final Logger log = LoggerFactory.getLogger(PricingService.class);
    private static final Duration UPDATE_INTERVAL = Duration.ofDays(1); // 1 day TODO: Make configurable via enviroment variable
    private static final List<String> PRICING_WEBSITES = List.of("ebay", "yahoo"); // TODO: Make configurable via enviroment variable
    private static final int EBAY_PRICES_LIMIT = 8; // TODO: Make configurable via enviroment variable
    private static final int YAHOO_PRICES_LIMIT = 8;
    private static final String EBAY_TITLE_FILTER = " -\"face plate\" -faceplate -\"parts only\"";
    @Value("${ebay.api.currency}")
    private static final String EBAY_PRICE_CURRENCY = "CAD";
    
    private final PricingInfoRepository pricingInfoRepository;
    private final CarStereoRepository carStereoRepository;
    private final PricingItemRepository pricingItemRepository;
    private final RestTemplate restTemplate;

    @Value("${ebay.api.client-id}")
    private String ebayClientId;

    @Value("${ebay.api.client-secret}")
    private String ebayClientSecret;

    @Value("${pricing.service.logging.enabled:true}")
    private boolean loggingEnabled;

    public PricingService(PricingInfoRepository pricingInfoRepository, 
                          CarStereoRepository carStereoRepository,
                          PricingItemRepository pricingItemRepository) {
        this.pricingInfoRepository = pricingInfoRepository;
        this.carStereoRepository = carStereoRepository;
        this.pricingItemRepository = pricingItemRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public CarStereo updatePricingInfo(CarStereo carStereo) {
        List<PricingInfo> pricingInfos = carStereo.getPricingInfos();
        
        Set<String> existingWebsites = new HashSet<>();
        if (pricingInfos != null) {
            for (PricingInfo info : pricingInfos) {
                existingWebsites.add(info.getWebsite());
            }
        }
        
        boolean created = false;
        for (String website : PRICING_WEBSITES) {
            if (!existingWebsites.contains(website)) {
                PricingInfo pricingInfo = new PricingInfo();
                pricingInfo.setCarStereo(carStereo);
                if (carStereo.getPricingInfos() == null) {
                    carStereo.setPricingInfos(new ArrayList<>());
                }
                carStereo.getPricingInfos().add(pricingInfo);
                pricingInfo.setWebsite(website);
                pricingInfo.setLastUpdated(Instant.now().minus(7, ChronoUnit.DAYS));
                pricingInfoRepository.save(pricingInfo);
                created = true;
            }
        }

        if (created) {
            // Refetch
            carStereo = carStereoRepository.findCarStereoByName(carStereo.getName());
            pricingInfos = carStereo.getPricingInfos();
        }

        boolean updated = false;
        // Copy list to avoid ConcurrentModificationException if Hibernate flushes/modifies it
        for (PricingInfo info : new ArrayList<>(pricingInfos)) {
            if (shouldUpdate(info)) {
                updatePricingInfo(info);
                updated = true;
            }
        }

        if (updated) {
            return carStereoRepository.findCarStereoByName(carStereo.getName());
        }

        return carStereo;
    }

    private boolean shouldUpdate(PricingInfo info) {
        if (info.getLastUpdated() == null) {
            return true;
        }
        return Duration.between(info.getLastUpdated(), Instant.now()).compareTo(UPDATE_INTERVAL) > 0;
    }

    private void updatePricingInfo(PricingInfo info) {
        // Clear old prices
        if (info.getPrices() != null) {
            pricingItemRepository.deleteAll(info.getPrices());
            info.setPrices(new ArrayList<>());
        }


        switch (info.getWebsite()) {
            case "ebay":
                fetchEbayPrices(info);
                break;
            case "yahoo":
                fetchYahooPrices(info);
                break;
            default:
        }
        
        // Update timestamp and save info
        info.setLastUpdated(Instant.now());
        pricingInfoRepository.save(info);
    }

    private void fetchEbayPrices(PricingInfo info) {
        try {
            if (loggingEnabled) {
                log.info("Fetching eBay prices for: {}", info.getCarStereo().getName());
            }
            String token = getEbayAccessToken();
            if (token == null) {
                log.error("Failed to get eBay access token");
                return;
            }

            String query = info.getCarStereo().getName();

            // eBay Browse API: search for sold items
            String url = "https://api.ebay.com/buy/browse/v1/item_summary/search?q=" + query + EBAY_TITLE_FILTER +
                         "&filter=conditions:%7BUSED%7D,price:[" + info.getMinPrice() + "..],priceCurrency:" + EBAY_PRICE_CURRENCY +
                         "&sort=price" +
                         "&limit=" + EBAY_PRICES_LIMIT;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("X-EBAY-C-MARKETPLACE-ID", "EBAY_CA");
            headers.set("X-EBAY-C-CURRENCY", EBAY_PRICE_CURRENCY);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            if (loggingEnabled) {
                log.info("Sending request to eBay Browse API: {}", url);
            }
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> itemSummaries = (List<Map<String, Object>>) response.getBody().get("itemSummaries");
                if (itemSummaries != null) {
                    if (loggingEnabled) {
                        log.info("Found {} item(s) from eBay", itemSummaries.size());
                    }
                    // Clear old prices
                    if (info.getPrices() != null) {
                        pricingItemRepository.deleteAll(info.getPrices());
                        info.getPrices().clear();
                    } else {
                        info.setPrices(new ArrayList<>());
                    }

                    for (Map<String, Object> item : itemSummaries) {
                        PricingItem pricingItem = new PricingItem();
                        pricingItem.setPricingInfo(info);
                        
                        Map<String, Object> priceMap = (Map<String, Object>) item.get("price");
                        if (priceMap != null) {
                            pricingItem.setPrice(Float.parseFloat(priceMap.get("value").toString()));
                        }

                        // Strip any query parameters to keep the link short
                        String link = item.get("itemWebUrl").toString().split("&")[0];
                        System.out.println(link);
                        pricingItem.setLink(link);
                        
                        Map<String, Object> imageMap = (Map<String, Object>) item.get("image");
                        if (imageMap != null) {
                            pricingItem.setImage((String) imageMap.get("imageUrl"));
                        }
                        
                        pricingItemRepository.save(pricingItem);
                        info.getPrices().add(pricingItem);
                    }
                    
                    // Sort in memory after adding all items
                    info.getPrices().sort(Comparator.comparing(PricingItem::getPrice));
                } else {
                    // Something went wrong, log it
                    System.err.println("No item summaries found in eBay response");
                    System.err.println(response.getBody());
                }
            }
        } catch (Exception e) {
            // Log error in real world
            System.err.println("Error fetching eBay prices: " + e.getMessage());
        }
    }

    private String getEbayAccessToken() {
        try {
            if (loggingEnabled) {
                log.info("Requesting new eBay access token");
            }
            String url = "https://api.ebay.com/identity/v1/oauth2/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(ebayClientId, ebayClientSecret);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");
            map.add("scope", "https://api.ebay.com/oauth/api_scope");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            System.err.println("Error getting eBay access token: " + e.getMessage());
        }
        return null;
    }

    private void fetchYahooPrices(PricingInfo info) {
        try {
            if (loggingEnabled) {
                log.info("Fetching Yahoo Auctions prices for: {}", info.getCarStereo().getName());
            }

            float jpyToTargetRate = getJpyExchangeRate(EBAY_PRICE_CURRENCY);
            if (loggingEnabled) {
                log.info("Exchange rate JPY to {}: {}", EBAY_PRICE_CURRENCY, jpyToTargetRate);
            }

            int minPriceJpy = Math.round(info.getMinPrice() / jpyToTargetRate);
            String query = info.getCarStereo().getName().replace(" ", "+");
            String url = "https://auctions.yahoo.co.jp/closedsearch/closedsearch?p=" + query + "+-ジャンク&price_type=currentprice&min=" + minPriceJpy;

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .get();

            Elements items = doc.select("li.sc-93f00b27-2.iDpbCh");
            if (loggingEnabled) {
                log.info("Found {} item(s) from Yahoo Auctions", items.size());
            }

            // Clear old prices
            if (info.getPrices() != null) {
                pricingItemRepository.deleteAll(info.getPrices());
                info.getPrices().clear();
            } else {
                info.setPrices(new ArrayList<>());
            }

            int count = 0;
            for (Element item : items) {
                if (count >= YAHOO_PRICES_LIMIT) break;

                PricingItem pricingItem = new PricingItem();
                pricingItem.setPricingInfo(info);

                // Price
                Element priceElement = item.selectFirst(".sc-eeafbf50-16.jqxDNt");
                if (priceElement != null) {
                    String priceText = priceElement.text().replaceAll("[^0-9]", "");
                    if (!priceText.isEmpty()) {
                        float priceInJpy = Float.parseFloat(priceText);
                        pricingItem.setPrice(priceInJpy * jpyToTargetRate);
                    }
                }

                // Link
                Element linkElement = item.selectFirst("a");
                if (linkElement != null) {
                    pricingItem.setLink(linkElement.attr("abs:href"));
                }

                // Image
                Element imgElement = item.selectFirst("img");
                if (imgElement != null) {
                    pricingItem.setImage(imgElement.attr("src").split("\\?")[0]);
                }

                pricingItemRepository.save(pricingItem);
                info.getPrices().add(pricingItem);
                count++;
            }
            
            // Sort in memory after adding all items
            info.getPrices().sort(Comparator.comparing(PricingItem::getPrice));
        } catch (Exception e) {
            log.error("Error fetching Yahoo Auctions prices: {}", e.getMessage());
        }
    }
    private float getJpyExchangeRate(String targetCurrency) {
        try {
            String url = "https://api.exchangerate-api.com/v4/latest/JPY";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> rates = (Map<String, Object>) response.getBody().get("rates");
                if (rates != null && rates.containsKey(targetCurrency)) {
                    Object rate = rates.get(targetCurrency);
                    if (rate instanceof Number) {
                        return ((Number) rate).floatValue();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching exchange rate: {}", e.getMessage());
        }
        // Fallback or default rate if API fails (approximate JPY to CAD)
        if ("CAD".equals(targetCurrency)) return 0.0087f;
        if ("USD".equals(targetCurrency)) return 0.0063f;
        return 1.0f;
    }

}
