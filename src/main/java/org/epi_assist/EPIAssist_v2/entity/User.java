package org.epi_assist.EPIAssist_v2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private String email;

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
