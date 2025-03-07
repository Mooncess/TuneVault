package ru.mooncess.auth_service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoundProducerInfo {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
