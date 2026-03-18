package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class CarStereo {

    private static final int MAX_DESCRIPTION_LENGTH = 4000;

    @GeneratedValue
    @Id
    private long id;

    @Column(unique = true)
    private String name;
    private int year;
    private String image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
