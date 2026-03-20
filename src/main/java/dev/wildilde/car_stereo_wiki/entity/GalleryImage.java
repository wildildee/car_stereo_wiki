package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;

@Entity
public class GalleryImage {
    @GeneratedValue
    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "car_stereo_id", referencedColumnName = "id")
    private CarStereo carStereo;

    private String imageURL;

    public GalleryImage() {
    }

    public GalleryImage(CarStereo carStereo, String imageURL) {
        this.carStereo = carStereo;
        this.imageURL = imageURL;
    }

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
