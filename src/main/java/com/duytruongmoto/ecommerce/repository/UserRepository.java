package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Role;
import com.duytruongmoto.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstname) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<User> searchUsers(@Param("kw") String keyword);
}
