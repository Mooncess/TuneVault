package ru.mooncess.admin_panel_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.admin_panel_service.domain.enums.ClaimStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime createdDate;
    private String senderEmail;
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;
    private Long musicResourceId;
    private String description;
}
