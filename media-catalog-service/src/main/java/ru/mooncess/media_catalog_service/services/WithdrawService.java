package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.entities.Withdraw;
import ru.mooncess.media_catalog_service.repositories.WithdrawRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final PaymentServiceGrpcImpl paymentServiceGrpc;
    private final MessageSender messageSender;
    @Value("${msg.subject}")
    private String msgSubject;
    public boolean createWithdraw(Producer producer, BigDecimal amount, String destination) throws RuntimeException {
        if (producer.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("The current balance is less than the withdrawal amount");
        }

        var withdraw = new Withdraw();
        withdraw.setAmount(amount);
        withdraw.setProducer(producer);
        withdraw.setWithdrawDate(LocalDateTime.now());

        producer.setBalance(producer.getBalance().subtract(amount));

        if (paymentServiceGrpc.createWithdrawForm(producer.getId(), amount, destination)) {
            withdrawRepository.save(withdraw);
            String message = "The amount has been successfully withdrawn from your Tune Vault balance: " + amount;
            messageSender.sendWithdrawEmailMessage(producer.getEmail(), msgSubject, message);
            return true;
        }
        else {
            return false;
        }
    }
}
