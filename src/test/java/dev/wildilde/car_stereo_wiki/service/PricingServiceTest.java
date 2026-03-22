package dev.wildilde.car_stereo_wiki.service;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.PricingInfo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.PricingInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@org.junit.jupiter.api.Disabled("Fails due to environment config (MySQL and OAuth2)")
class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private CarStereoRepository carStereoRepository;

    @Autowired
    private PricingInfoRepository pricingInfoRepository;

    @Test
    void testUpdatePricingInfoCreatesSupportedWebsitesIfMissing() {
        CarStereo carStereo = new CarStereo();
        carStereo.setName("NewStereo");
        carStereo = carStereoRepository.save(carStereo);

        carStereo = pricingService.updatePricingInfo(carStereo);

        assertNotNull(carStereo.getPricingInfos());
        assertFalse(carStereo.getPricingInfos().isEmpty());
        // For now, it should at least have 'ebay' since it's in the list
        assertTrue(carStereo.getPricingInfos().stream().anyMatch(p -> "ebay".equals(p.getWebsite())));
    }

    @Test
    void testUpdatePricingInfoUpdatesIfOld() {
        CarStereo carStereo = new CarStereo();
        carStereo.setName("OldPricingStereo");
        carStereo = carStereoRepository.save(carStereo);

        PricingInfo info = new PricingInfo();
        info.setCarStereo(carStereo);
        // Set update time to 2 days ago
        Instant twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS);
        info.setLastUpdated(twoDaysAgo);
        pricingInfoRepository.save(info);

        carStereo = carStereoRepository.findCarStereoByName("OldPricingStereo");
        
        carStereo = pricingService.updatePricingInfo(carStereo);

        PricingInfo updatedInfo = carStereo.getPricingInfos().get(0);
        assertTrue(updatedInfo.getLastUpdated().isAfter(twoDaysAgo));
    }
}
