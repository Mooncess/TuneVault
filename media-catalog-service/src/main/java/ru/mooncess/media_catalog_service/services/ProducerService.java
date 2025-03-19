package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.Status;
import ru.mooncess.media_catalog_service.dto.ProducerInfo;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.repositories.ProducerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;

    public void createNewProducer(ProducerInfo producerInfo) {
        Producer producer = new Producer();
        producer.setId(producerInfo.getId());
        producer.setNickname(producerInfo.getNickname());
        producer.setStatus(Status.ACTIVE);
        producer.setRegistrationDate(LocalDate.now());
        producer.setEmail(producerInfo.getEmail());
        producerRepository.save(producer);
    }

    public boolean updateProducer(UpdateProducerInfo updateProducerInfo) throws RuntimeException{
        Optional<Producer> optionalProducer = producerRepository.findByEmail(updateProducerInfo.getEmail());
        if (optionalProducer.isEmpty()) return false;
        Producer producer = optionalProducer.get();

        if (updateProducerInfo.getAbout() != null) {
            producer.setAbout(updateProducerInfo.getAbout());
        }

        if (updateProducerInfo.getNickname() != null) {
            producer.setAbout(updateProducerInfo.getNickname());
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
}
