package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.client.AuthServClient;
import ru.mooncess.media_catalog_service.client.TradingOperationServClient;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;
import ru.mooncess.media_catalog_service.domain.enums.UserStatus;
import ru.mooncess.media_catalog_service.dto.ProducerInfo;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;
import ru.mooncess.media_catalog_service.repositories.ProducerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final MusicResourceRepository musicResourceRepository;
    private final AuthServClient authServClient;
    private final MessageSender messageSender;
    private final TradingOperationServClient tradingOperationServClient;
    @Value("${msg.subject}")
    private String msgSubject;
    @Value("${mcs.api.key}")
    private String secretApiKey;
    @Value("${default.logo.uri}")
    private String defaultLogoUri;

    public boolean createNewProducer(ProducerInfo producerInfo) {
        Optional<Producer> optionalProducer = producerRepository.findByEmail(producerInfo.getEmail());
        if (optionalProducer.isEmpty()) {
            Long id = tradingOperationServClient.createNewProducerBalance(producerInfo.getEmail(), secretApiKey).getBody();

            Producer producer = new Producer();
            producer.setId(id);
            producer.setNickname(producerInfo.getNickname());
            producer.setUserStatus(UserStatus.ACTIVE);
            producer.setRegistrationDate(LocalDate.now());
            producer.setEmail(producerInfo.getEmail());
            producer.setLogoUri(defaultLogoUri);
            producerRepository.save(producer);
            return true;
        }

        return false;
    }

    public boolean updateProducer(UpdateProducerInfo updateProducerInfo, String email) throws RuntimeException{
        Optional<Producer> optionalProducer = producerRepository.findByEmail(email);
        if (optionalProducer.isEmpty()) return false;
        Producer producer = optionalProducer.get();

        if (updateProducerInfo.getAbout() != null) {
            producer.setAbout(updateProducerInfo.getAbout());
        }

        if (updateProducerInfo.getNickname() != null) {
            producer.setNickname(updateProducerInfo.getNickname());
        }

        producerRepository.save(producer);
        return true;
    }
    public List<Producer> findAllUserProducers() {
        return producerRepository.findAllByUserStatus(UserStatus.ACTIVE);
    }

    public void updateEmailOfProducer(String newEmail, String oldEmail) {
        Optional<Producer> optionalProducer = producerRepository.findByEmail(oldEmail);
        Producer producer = optionalProducer.get();
        producer.setEmail(newEmail);
        producerRepository.save(producer);
    }

    public void deleteProducer(Long id) {
        producerRepository.deleteById(id);
    }

    public Optional<Producer> findByEmail(String email) {
        return producerRepository.findByEmail(email);
    }

    public Optional<Producer> findById(Long id) {
        return producerRepository.findById(id);
    }

    public List<Producer> findProducersByNicknamePart(String nicknamePart) {
        return producerRepository.findAllByNicknameContainingIgnoreCaseAndUserStatus(nicknamePart, UserStatus.ACTIVE);
    }

    public void strike(Producer producer) {
        try {
            if (authServClient.strikeAndDelete(producer.getId(), secretApiKey).getBody()) {
                blockProducer(producer);
            }
            else {
                String message = "Your resource has been removed from our platform due to a violation of our Terms of Service or Community Guidelines. Repeated violations may result in additional penalties, including temporary suspension or permanent termination of your account.\n\nTo avoid further action, please review our policies before uploading new content.";
                messageSender.sendEmailMessage(producer.getEmail(), msgSubject, message);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Error when sending a request to another service");
        }
    }

    public void blockProducer(Producer producer) {
        producer.setUserStatus(UserStatus.BLOCKED);
        producerRepository.save(producer);

        authServClient.blockUser(producer.getId(), secretApiKey);

        List<MusicResource> list = musicResourceRepository.findAllByProducer(producer);

        list.forEach(i -> {
            if (!i.getStatus().equals(MusicResourceStatus.AVAILABLE)) {
                i.setStatus(MusicResourceStatus.UNAVAILABLE);
                musicResourceRepository.save(i);
            }
        });

        String message = "Your resource has been removed from our platform due to a violation of our Terms of Service or Community Guidelines. Repeated violations may result in additional penalties, including temporary suspension or permanent termination of your account.\n\nTo avoid further action, please review our policies before uploading new content.";
        messageSender.sendEmailMessage(producer.getEmail(), msgSubject, message);
        message = "Due to numerous violations of the rules of the site, your account has been blocked.";
        messageSender.sendEmailMessage(producer.getEmail(), msgSubject, message);
    }

    public void save(Producer producer) {
        producerRepository.save(producer);
    }

    public void disable(Long id) {
        var producer = producerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producer not found"));

        musicResourceRepository.findAllByProducer(producer).forEach(i -> {
            if (i.getStatus().equals(MusicResourceStatus.AVAILABLE))
                i.setStatus(MusicResourceStatus.UNAVAILABLE);
            musicResourceRepository.save(i);
        });

        producer.setUserStatus(UserStatus.INACTIVE);
        producerRepository.save(producer);
    }

    public void uploadLogo(String username, String logoURI) {
        var producer = producerRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        producer.setLogoUri(logoURI);
        producerRepository.save(producer);
    }
    public String updateLogo(String username) {
        var producer = producerRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        return producer.getLogoUri();
    }


    public String deleteLogo(String username) {
        var producer = producerRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        var logoUri = producer.getLogoUri();
        producer.setLogoUri(defaultLogoUri);
        producerRepository.save(producer);
        return logoUri;
    }
}
