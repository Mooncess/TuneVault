package ru.mooncess.trading_operations_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mooncess.trading_operations_service.client.MediaCatalogServClient;
import ru.mooncess.trading_operations_service.entities.ProducerBalance;
import ru.mooncess.trading_operations_service.entities.Withdraw;
import ru.mooncess.trading_operations_service.repositories.WithdrawRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final ProducerBalanceService producerBalanceService;
    private final PaymentServiceGrpcImpl paymentServiceGrpc;
    private final MessageSender messageSender;
    private final MediaCatalogServClient mediaCatalogServClient;

    @Value("${msg.subject}")
    private String msgSubject;
    @Value("${mcs.api.key}")
    private String secretApiKey;

    @Transactional
    public void createWithdraw(String email, BigDecimal amount, String destination) throws RuntimeException {
        Long producerId;

        try {
            producerId = mediaCatalogServClient.getProducerIdByEmail(email, secretApiKey).getBody();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("User no found with email: " + email);
        }

        ProducerBalance producerBalance = producerBalanceService.findProducerBalanceById(producerId)
                .orElseThrow(() -> new RuntimeException("Producer Balance not found with id: " + producerId));

        if (producerBalance.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("The current balance is less than the withdrawal amount");
        }

        var withdraw = new Withdraw();
        withdraw.setAmount(amount);
        withdraw.setProducerId(producerId);
        withdraw.setWithdrawDate(LocalDateTime.now());

        producerBalance.setBalance(producerBalance.getBalance().subtract(amount));

        if (paymentServiceGrpc.createWithdrawForm(producerId, amount, destination)) {
            withdrawRepository.save(withdraw);
            String message = "The amount has been successfully withdrawn from your Tune Vault balance: " + amount;
            messageSender.sendEmailMessage(email, msgSubject, message);
        }
    }

    public List<Withdraw> findAllByProducer(String email) {
        try {
            return withdrawRepository.findAllByProducerIdOrderByWithdrawDateDesc(mediaCatalogServClient
                    .getProducerIdByEmail(email, secretApiKey).getBody());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }
    }
}

