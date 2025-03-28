package ru.mooncess.media_catalog_service.services;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.grpc.PaymentServiceGrpc;
import ru.mooncess.grpc.PaymentServiceProto;

import java.math.BigDecimal;

@Service
public class PaymentServiceGrpcImpl {
    @Value("${grpc.client.payment-service.address}")
    private String address;

    @GrpcClient("payment-service")
    PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub;

    public String createPaymentForm(long id, BigDecimal amount, String redirectUrl) {
        PaymentServiceProto.PaymentResponse response = paymentServiceBlockingStub
                .createPaymentForm(generatedPaymentRequest(id, amount.toString(), redirectUrl));
        return response.getPaymentUrl();
    }

    private PaymentServiceProto.PaymentRequest generatedPaymentRequest(long id, String amount, String redirectUrl) {
        return PaymentServiceProto.PaymentRequest.newBuilder()
                .setId(id)
                .setAmount(amount)
                .setRedirectUrl(redirectUrl)
                .build();
    }
}