package ru.mooncess.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mooncess.auth_service.domain.*;
import ru.mooncess.auth_service.exception.AppError;
import ru.mooncess.auth_service.repository.UserRepository;
import ru.mooncess.auth_service.clients.MediaCatalogClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private final RedisService redisService;
    private PasswordEncoder passwordEncoder;
    private final MediaCatalogClient mediaCatalogClient;
    @Value("${secret.api.key}")
    private String secretApiKey;
    @Value("${strike.max.count}")
    private int maxCountStrike;

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
        user.setCountStrike(0);
        return userRepository.save(user);
    }

    public ResponseEntity<?> updateUser(String newEmail, String newPassword, Authentication authentication) throws RuntimeException{
        Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
        if (optionalUser.isEmpty()) return ResponseEntity.badRequest().build();
        User user = optionalUser.get();

        if (newEmail != null && !newEmail.equals(authentication.getName())) {
            if (userRepository.existsByUsername(newEmail)) {
                return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
            }

            try {
                ResponseEntity<Void> status = mediaCatalogClient.updateEmailOfProducer(newEmail, authentication.getName(), secretApiKey);
                if (status.getStatusCode() == HttpStatus.OK)
                    user.setUsername(newEmail);
            } catch (Exception e) {
                return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
            }
        }

        if (newPassword != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.saveAndFlush(user);
        return ResponseEntity.ok().build();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public ResponseEntity<?> deleteUserRequest(Long id) {
        try {
            return mediaCatalogClient.deleteProducer(id, secretApiKey);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public boolean strike(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setCountStrike(user.getCountStrike() + 1);

        if (user.getCountStrike() == maxCountStrike) {
            user.setStatus(Status.BLOCKED);
            userRepository.save(user);
            redisService.delete(user.getUsername());
            return true;
        }
        else {
            userRepository.save(user);
            return false;
        }
    }

    public void disable(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        mediaCatalogClient.disable(user.getId(), secretApiKey);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    public void block(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setStatus(Status.BLOCKED);
        userRepository.save(user);
        redisService.delete(user.getUsername());
    }
}
