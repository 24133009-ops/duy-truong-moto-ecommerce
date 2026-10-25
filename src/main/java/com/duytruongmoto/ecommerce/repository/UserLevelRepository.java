package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    Optional<UserLevel> findByName(String name);
}
