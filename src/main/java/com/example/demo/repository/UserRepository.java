package com.example.demo.repository;


import com.example.demo.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String username);

    Optional<User> findUserByVerificationCodeAndVerificationCodeIsFalse(String verificationCode);

    Optional<User> findById(long id);
    Page<User> findAllByUsernameContaining(String username, PageRequest request);
    @Query("select count(u) from User u where :user member of u.following")
    Long countFollowers(@Param("user") User user);
    @Query("SELECT COUNT(u) FROM User u WHERE :currentUser MEMBER OF u.followers")
    long countFollowing(@Param("currentUser") User currentUser);

}
