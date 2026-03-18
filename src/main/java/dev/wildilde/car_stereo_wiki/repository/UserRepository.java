package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
