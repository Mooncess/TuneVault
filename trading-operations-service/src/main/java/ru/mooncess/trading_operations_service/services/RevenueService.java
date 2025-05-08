package ru.mooncess.trading_operations_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.trading_operations_service.client.MediaCatalogServClient;
import ru.mooncess.trading_operations_service.entities.Revenue;
import ru.mooncess.trading_operations_service.entities.Sale;
import ru.mooncess.trading_operations_service.repositories.RevenueRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final MediaCatalogServClient mediaCatalogServClient;

    @Value("${mcs.api.key}")
    private String secretApiKey;

    public void createRevenue(BigDecimal amount, Sale sale, Long producerId) {
        var revenue = new Revenue();

        revenue.setAmount(amount);
        revenue.setSale(sale);
        revenue.setProducerId(producerId);

        revenueRepository.save(revenue);
    }

    public List<Revenue> findAllByProducer(String email) {
        try {
            return revenueRepository.findAllByProducerIdOrderBySaleDateDesc(mediaCatalogServClient
                    .getProducerIdByEmail(email, secretApiKey).getBody());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }
    }
}

