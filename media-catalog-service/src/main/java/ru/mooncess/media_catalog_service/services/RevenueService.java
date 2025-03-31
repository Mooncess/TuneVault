package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.entities.Revenue;
import ru.mooncess.media_catalog_service.entities.Sale;
import ru.mooncess.media_catalog_service.repositories.RevenueRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final ProducerService producerService;
    public void createRevenue(BigDecimal amount, Sale sale, Producer producer) {
        var revenue = new Revenue();

        revenue.setAmount(amount);
        revenue.setSale(sale);
        revenue.setProducer(producer);

        revenueRepository.save(revenue);
    }

    public List<Revenue> findAllByProducer(Long id) {
        var producer = producerService.findById(id);

        if (producer.isPresent()) {
            return revenueRepository.findAllByProducer(producer.get());
        }
        else {
            throw new RuntimeException("Producer not found with id: " + id);
        }

    }
}
