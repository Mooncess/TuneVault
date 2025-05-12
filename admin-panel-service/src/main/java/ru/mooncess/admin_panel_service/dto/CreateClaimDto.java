package ru.mooncess.admin_panel_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClaimDto {
    @Email
    @NotNull
    private String senderEmail;
    private Long musicResourceId;
    @NotNull
    private Long producerId;
    @NotNull
    private String description;
}
