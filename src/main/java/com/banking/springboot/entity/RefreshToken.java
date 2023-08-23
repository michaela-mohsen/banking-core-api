package com.banking.springboot.entity;

import com.banking.springboot.auth.User;
import lombok.Data;
import lombok.Generated;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Generated
@Table(name = "tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
