package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
}
