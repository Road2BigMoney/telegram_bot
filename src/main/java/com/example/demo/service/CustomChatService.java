package com.example.demo.service;

import com.example.demo.model.CustomChat;
import com.example.demo.model.User;
import com.example.demo.repository.CustomChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomChatService {
    private final CustomChatRepository chatRepository;

    public CustomChat findById(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    @Transactional
    public void addMember(User user, Long chatId) {
            log.info(chatId.toString());
            CustomChat chat = chatRepository.findCustomChatsWithUsers(chatId);
            chat.getUsers().add(user);
            chatRepository.save(chat);
            log.info("В список чата добавлен пользователь: " + user.getUserName());
    }

    @Transactional
    public void save(Long chatId, String name) {
        if (chatRepository.findById(chatId).isEmpty()) {
            CustomChat chat = new CustomChat();
            chat.setId(chatId);
            chat.setName(name);
            chatRepository.save(chat);
        }
    }
    @Transactional
    public void save(CustomChat chat) {
        chatRepository.save(chat);
    }

}
