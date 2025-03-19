package ru.mooncess.media_catalog_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.Status;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producer {
    @Id
    private Long id;
    private double balance = 0.0;
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String nickname;
    private String about;

    private String logoUri = "default_logo_uri";
    private String email;
}
