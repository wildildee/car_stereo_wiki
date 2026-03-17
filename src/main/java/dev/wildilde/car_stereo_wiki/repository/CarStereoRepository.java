package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarStereoRepository extends JpaRepository<CarStereo, Long> {
    CarStereo findByName(String name);
}
