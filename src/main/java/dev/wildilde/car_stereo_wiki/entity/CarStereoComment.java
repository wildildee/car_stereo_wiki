package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_stereo_comment")
public class CarStereoComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String text;

    @ElementCollection
    @CollectionTable(name = "car_stereo_comment_photo", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_stereo_id")
    private CarStereo carStereo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public CarStereoComment() {
        this.createdAt = LocalDateTime.now();
    }

    public CarStereoComment(String text, List<String> photoUrls, CarStereo carStereo, User user) {
        this();
        this.text = text;
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
        this.carStereo = carStereo;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CarStereo getCarStereo() {
        return carStereo;
    }

    public void setCarStereo(CarStereo carStereo) {
        this.carStereo = carStereo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
