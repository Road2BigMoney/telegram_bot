package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Entity(name = "userTG")
@Data
public class User implements Comparable {
    @Id
    private Long id;

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

    @Column(name = "umor_point")
    private Long umorPoint;


    @Override
    public String toString() {
        return "First Name : " + this.getFirstName() +"\n" +
                "Last Name  : " + this.getLastName() + "\n" +
                "Username   : " + this.getUserName() + "\n" +
                "Registered : " + this.getRegisteredAt() + "\n" +
                "Очков юмора : " + this.getUmorPoint();
    }

    @Override
    public int compareTo(Object o) {
        if(o == null){
            return -1;
        }
        User user = (User)o;
        return (int)(umorPoint - user.umorPoint);
    }
}
