package ru.mooncess.media_catalog_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.UserStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private double balance = 0.0;
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String nickname;
    private String about;

    private String logoUri = "default_logo_uri";
    private String email;
}
