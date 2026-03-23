package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

@Entity
public class Resource {
    @GeneratedValue
    @Id
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_stereo_id", referencedColumnName = "id")
    private CarStereo carStereo;

    private String name;
    private String icon;
    private String link;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
