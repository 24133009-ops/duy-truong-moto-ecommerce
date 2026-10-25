package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Role;
import com.duytruongmoto.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    List<User> findByRole(Role role);
}
