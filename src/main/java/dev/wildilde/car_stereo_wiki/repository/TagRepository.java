package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByType(String type);
    Optional<Tag> findByNameAndType(String name, String type);
}
