package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Table(name = "pricing_info")
@Entity
public class PricingInfo {
    @GeneratedValue
    @Id
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_stereo_id", referencedColumnName = "id")
    private CarStereo carStereo;
    private String website;

    // Filters
    private int minPrice = 20;

    private Instant lastUpdated;
    @OneToMany(mappedBy = "pricingInfo")
    private List<PricingItem> prices;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CarStereo getCarStereo() {
        return carStereo;
    }

    public void setCarStereo(CarStereo carStereo) {
        this.carStereo = carStereo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<PricingItem> getPrices() {
        return prices;
    }

    public void setPrices(List<PricingItem> prices) {
        this.prices = prices;
    }
}
