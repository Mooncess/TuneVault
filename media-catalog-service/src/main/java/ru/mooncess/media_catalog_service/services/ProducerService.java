package ru.mooncess.media_catalog_service.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.enums.UserStatus;
import ru.mooncess.media_catalog_service.dto.ProducerInfo;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.repositories.ProducerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;

    public boolean createNewProducer(ProducerInfo producerInfo) {
        Optional<Producer> optionalProducer = producerRepository.findByEmail(producerInfo.getEmail());
        if (optionalProducer.isEmpty()) {
            Producer producer = new Producer();
            producer.setNickname(producerInfo.getNickname());
            producer.setUserStatus(UserStatus.ACTIVE);
            producer.setRegistrationDate(LocalDate.now());
            producer.setEmail(producerInfo.getEmail());
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
    public List<Producer> findAllProducers() {
        return producerRepository.findAll();
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
        return producerRepository.findAllByNicknameContainingIgnoreCase(nicknamePart);
    }

    public void increaseBalance(double amountIncome, MusicResource musicResource) {
    }

    public void decreaseBalance(Producer producer, BigDecimal amount) {
    }
}
