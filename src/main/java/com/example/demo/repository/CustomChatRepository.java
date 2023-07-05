package com.example.demo.repository;

import com.example.demo.model.CustomChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChatRepository extends JpaRepository<CustomChat, Long> {
    @Query("SELECT c FROM chatTG c JOIN FETCH c.users WHERE c.id = :id")
    CustomChat findCustomChatsWithUsers(@Param("id") Long id);
}
