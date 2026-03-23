package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

import java.util.List;

@Table(name = "car_stereo")
@Entity
public class CarStereo {

    private static final int MAX_DESCRIPTION_LENGTH = 4000;

    @GeneratedValue
    @Id
    private long id;

    @Column(unique = true)
    private String name;
    private int year;
    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @ManyToMany
    @JoinTable(name = "car_stereo_brand_tag")
    private List<Tag> brands;
    @ManyToMany
    @JoinTable(name = "car_stereo_size_tag")
    private List<Tag> sizes;
    @ManyToMany
    @JoinTable(name = "car_stereo_display_tag")
    private List<Tag> displays;
    @ManyToMany
    @JoinTable(name = "car_stereo_input_tag")
    private List<Tag> inputs;

    @OneToMany(mappedBy = "carStereo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GalleryImage> galleryImages;

    @OneToMany(mappedBy = "carStereo")
    private List<PricingInfo> pricingInfos;

    @OneToMany(mappedBy = "carStereo")
    private List<Resource> resources;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<Tag> getBrands() {
        return brands;
    }

    public void setBrands(List<Tag> brands) {
        this.brands = brands;
    }

    public List<Tag> getInputs() {
        return inputs;
    }

    public void setInputs(List<Tag> inputs) {
        this.inputs = inputs;
    }

    public List<Tag> getDisplays() {
        return displays;
    }

    public void setDisplays(List<Tag> displays) {
        this.displays = displays;
    }

    public List<Tag> getSizes() {
        return sizes;
    }

    public void setSizes(List<Tag> sizes) {
        this.sizes = sizes;
    }

    public List<GalleryImage> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<GalleryImage> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PricingInfo> getPricingInfos() {
        return pricingInfos;
    }

    public void setPricingInfos(List<PricingInfo> pricingInfos) {
        this.pricingInfos = pricingInfos;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
