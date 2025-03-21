package ru.mooncess.auth_service.service;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.mooncess.auth_service.domain.*;
import ru.mooncess.auth_service.exception.AppError;
import ru.mooncess.auth_service.repository.UserRepository;
import ru.mooncess.auth_service.clients.MediaCatalogClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final MediaCatalogClient mediaCatalogClient;
    @Value("${mcs.api.key}")
    private String secretApiKey;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createNewUser(RegistrationRequest registrationUserDto, long id) {
        var user = new User();
        user.setId(id);
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        return userRepository.save(user);
    }

    public ResponseEntity<?> updateUser(UpdateRequest updateRequest, Authentication authentication) throws RuntimeException{
        Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
        if (optionalUser.isEmpty()) return ResponseEntity.badRequest().build();
        User user = optionalUser.get();

        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(authentication.getName())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
            }

            try {
                ResponseEntity<Void> status = mediaCatalogClient.updateEmailOfProducer(updateRequest.getUsername(), authentication.getName(), secretApiKey);
                if (status.getStatusCode() == HttpStatus.OK)
                    user.setUsername(updateRequest.getUsername());
            } catch (Exception e) {
                return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
            }
        }

        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        userRepository.saveAndFlush(user);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteUser(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            try {
                System.out.println("OPA");
                ResponseEntity<Void> status = mediaCatalogClient.deleteProducer(user.get().getId(), secretApiKey);
                System.out.println(status.getStatusCode());
                if (status.getStatusCode() == HttpStatus.NO_CONTENT)
                    userRepository.delete(user.get());
            } catch (Exception e) {
                return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
