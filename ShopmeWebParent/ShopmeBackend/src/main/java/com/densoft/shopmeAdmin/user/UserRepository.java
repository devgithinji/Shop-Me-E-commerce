package com.densoft.shopmeAdmin.user;

import com.densoft.shopmecommon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Long countById(Integer id);


    @Query("UPDATE User  u SET u.enabled = :enabled WHERE u.id = :id")
    void updateEnabledStatus(@Param("id") Integer id, @Param("enabled") boolean enabled);


}
