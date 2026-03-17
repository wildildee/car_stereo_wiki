package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class CarStereo {
    @GeneratedValue()
    @Id
    private long id;
    private String name;

    private int year;
    private String image;
    @OneToMany
    @JoinTable(name = "car_stereo_brand_tag")
    private Set<Tag> brands;
    @OneToOne
    private Tag size;
    @OneToMany
    @JoinTable(name = "car_stereo_display_tag")
    private Set<Tag> displays;
    @OneToMany
    @JoinTable(name = "car_stereo_input_tag")
    private Set<Tag> inputs;

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

    public Set<Tag> getBrands() {
        return brands;
    }

    public void setBrands(Set<Tag> brands) {
        this.brands = brands;
    }

    public Tag getSize() {
        return size;
    }

    public void setSize(Tag size) {
        this.size = size;
    }

    public Set<Tag> getInputs() {
        return inputs;
    }

    public void setInputs(Set<Tag> inputs) {
        this.inputs = inputs;
    }

    public Set<Tag> getDisplays() {
        return displays;
    }

    public void setDisplays(Set<Tag> displays) {
        this.displays = displays;
    }
}
