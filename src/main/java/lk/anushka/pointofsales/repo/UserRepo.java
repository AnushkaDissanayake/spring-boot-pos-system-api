package lk.anushka.pointofsales.repo;

import lk.anushka.pointofsales.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
