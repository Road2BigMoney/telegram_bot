package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Entity(name = "userTG")
@Data
public class User {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "registered_at")
    private Timestamp registeredAt;


    @Override
    public String toString() {
        return "First Name : " + this.getFirstName() +"\n" +
                "Last Name  : " + this.getLastName() + "\n" +
                "Username   : " + this.getUserName() + "\n" +
                "Registered : " + this.getRegisteredAt() + "\n" +
                "Chat ID  : " + this.getChatId() + "\n";
    }
}
