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
    @NotNull
    private Long musicResourceId;
    @NotNull
    private String description;
}
