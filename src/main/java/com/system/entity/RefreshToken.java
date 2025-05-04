package com.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Date expiryAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
