package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.CarStereoComment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarStereoCommentRepository extends CrudRepository<CarStereoComment, Long> {
    List<CarStereoComment> findAllByCarStereoIdOrderByCreatedAtDesc(Long carStereoId);
    List<CarStereoComment> findAllByCarStereoIdAndUserIdOrderByCreatedAtDesc(Long carStereoId, String userId);
}
