package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;
    private String action;
    private String message;
    private LocalDateTime createdAt;

    public Log(String method, String action, String message, LocalDateTime createdAt) {
        this.method = method;
        this.action = action;
        this.message = message;
        this.createdAt = createdAt;
    }
}
