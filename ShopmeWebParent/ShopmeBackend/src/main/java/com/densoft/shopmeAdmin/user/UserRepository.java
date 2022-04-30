package com.densoft.shopmeAdmin.user;

import com.densoft.shopmeAdmin.paging.SearchRepository;
import com.densoft.shopmecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends SearchRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Long countById(Integer id);


    @Query("UPDATE User  u SET u.enabled = :enabled WHERE u.id = :id")
    void updateEnabledStatus(@Param("id") Integer id, @Param("enabled") boolean enabled);


    @Query("SELECT u FROM User u WHERE CONCAT(u.id ,' ',u.email,' ',u.firstName,' ',u.lastName) LIKE %?1%")
    Page<User> findAll(String keyWord, Pageable pageable);


}
