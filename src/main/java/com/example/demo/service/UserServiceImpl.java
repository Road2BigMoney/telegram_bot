package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<User> sendUmorPoint(String senderName, String recipientName, Long amount) {

        User sender = userRepository.findUserByUserName(senderName);
        User recipient = userRepository.findUserByUserName(recipientName);
        if (sender == null) {
            throw new RuntimeException("We can not find sender by name");
        }
        if (recipient == null) {
            throw new RuntimeException("We can not find recipient by name");
        }

        long senderBalance = sender.getUmorPoint();
        if (senderBalance < amount) {
            throw new RuntimeException("Не достаточно юмора на балансе для отправки");
        }

        long newSenderBalance = senderBalance - amount;
        long recipientBalance = recipient.getUmorPoint() + amount;
        sender.setUmorPoint(newSenderBalance);
        recipient.setUmorPoint(recipientBalance);
        userRepository.save(sender);
        userRepository.save(recipient);
        return List.of(sender, recipient);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User registerOrUpdateUser(Message msg) {
        var chatId = msg.getChatId();
        var chat = msg.getChat();
        Optional<User> optionalUser = userRepository.findById(chatId);
        System.out.println(optionalUser);

        User user = new User();
        if(optionalUser.isEmpty()){

            user.setId(chatId);
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            user.setUmorPoint(10000L);

            User newUser = userRepository.save(user);
            log.info("user saved: " + user);
            return newUser;
        } else {

            user.setChatId(optionalUser.get().getChatId());
            user.setFirstName(msg.getFrom().getFirstName());
            user.setLastName(msg.getFrom().getLastName());
            user.setUserName(msg.getFrom().getUserName());
            user.setRegisteredAt(optionalUser.get().getRegisteredAt());
            user.setUmorPoint(optionalUser.get().getUmorPoint());
            User updateUser = userRepository.save(user);
            log.info("user updated: " + user);
            return updateUser;
        }

    }

//    @Transactional(readOnly = true)
//    public List<User> getMembersInChat(Message message) {
//        List<String> usernamesInChat = message.getm;
//        log.info(usernamesInChat.toString());
//        return userRepository.findAllUsersByUsername(usernamesInChat);
//    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteById(long chatId) {
        userRepository.deleteById(chatId);
    }

    public User findUserByUserName(String username) {
        return userRepository.findUserByUserName(username);
    }

    public Optional<User> findById(Long chatId) {
        return userRepository.findById(chatId);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
}
