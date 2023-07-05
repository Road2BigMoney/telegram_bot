package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "SELECT u FROM userTG u WHERE u.userName = :username ")
    User findUserByUserName(@Param("username") String username);

    @Query(value = "SELECT u " +
            "FROM userTG u " +
            "WHERE u.userName IN :list " +
            "ORDER BY u.umorPoint DESC " +
            "LIMIT 10 ")
    List<User> findAllUsersByUsername(@Param("list") Iterable<String> list);

}
