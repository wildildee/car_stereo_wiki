package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

@Entity
public class Resource {
    @GeneratedValue
    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "car_stereo_id", referencedColumnName = "id")
    private CarStereo carStereo;
    public static final java.util.Map<String, String> TYPES = java.util.Map.of(
            "pdf", "fas fa-file-pdf",
            "link", "fas fa-link"
    );

    public Resource() {}

    public Resource(CarStereo carStereo, String type, String name, String link) {
        this.carStereo = carStereo;
        this.type = type;
        this.name = name;
        this.link = link;
    }

    private String name;
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return TYPES.getOrDefault(type != null ? type.toLowerCase() : "", "fas fa-question-circle");
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
