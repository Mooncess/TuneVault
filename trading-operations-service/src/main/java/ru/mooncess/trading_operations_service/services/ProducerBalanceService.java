package ru.mooncess.trading_operations_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.trading_operations_service.client.MediaCatalogServClient;
import ru.mooncess.trading_operations_service.domain.AuthorSaleInfo;
import ru.mooncess.trading_operations_service.entities.ProducerBalance;
import ru.mooncess.trading_operations_service.entities.Sale;
import ru.mooncess.trading_operations_service.repositories.ProducerBalanceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerBalanceService {
    private final ProducerBalanceRepository producerBalanceRepository;
    private final RevenueService revenueService;
    private final MediaCatalogServClient mediaCatalogServClient;
    @Value("${mcs.api.key}")
    private String secretApiKey;
    public void increaseAuthorsBalance(BigDecimal amountIncome, List<AuthorSaleInfo> authors, Sale sale) {
        BigDecimal remainingAmount = amountIncome;

        for (int i = 0; i < authors.size() - 1; i++) {
            AuthorSaleInfo author = authors.get(i);
            BigDecimal authorAmount = amountIncome.multiply(author.getPercentageOfSale())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            ProducerBalance producerBalance = producerBalanceRepository.findById(author.getProducerId())
                    .orElseThrow(() -> new RuntimeException("ProducerBalance Not Found with ID: " + author.getProducerId()));

            producerBalance.setBalance(producerBalance.getBalance().add(authorAmount));
            producerBalanceRepository.save(producerBalance);

            revenueService.createRevenue(authorAmount, sale, author.getProducerId());
            remainingAmount = remainingAmount.subtract(authorAmount);
        }

        AuthorSaleInfo lastAuthor = authors.get(authors.size() - 1);

        ProducerBalance producerBalance = producerBalanceRepository.findById(lastAuthor.getProducerId())
                .orElseThrow(() -> new RuntimeException("ProducerBalance Not Found with ID: " + lastAuthor.getProducerId()));

        producerBalance.setBalance(producerBalance.getBalance().add(remainingAmount));
        producerBalanceRepository.save(producerBalance);

        revenueService.createRevenue(remainingAmount, sale, lastAuthor.getProducerId());

        log.info("Распределен доход {} для Sale {} между {} авторами",
                amountIncome, sale.getId(), authors.size());
    }

    public Optional<ProducerBalance> findProducerBalanceById(Long id) {
        return producerBalanceRepository.findById(id);
    }

    public BigDecimal getBalanceByEmail(String email) {
        try {
            return producerBalanceRepository.findById(
                            mediaCatalogServClient.getProducerIdByEmail(email, secretApiKey)
                                    .getBody())
                    .map(ProducerBalance::getBalance)
                    .orElse(new BigDecimal("0"));
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Can't get producer id");
        }
    }

    public Long createNewProducerBalance() {
        ProducerBalance producerBalance = new ProducerBalance();
        producerBalance.setBalance(new BigDecimal("0"));
        producerBalance = producerBalanceRepository.save(producerBalance);
        return producerBalance.getId();
    }
}
