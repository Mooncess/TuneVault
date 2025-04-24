package ru.mooncess.trading_operations_service.services;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.grpc.PaymentServiceGrpc;
import ru.mooncess.grpc.WithdrawServiceGrpc;
import ru.mooncess.grpc.PaymentServiceProto;

import java.math.BigDecimal;

@Service
public class PaymentServiceGrpcImpl {
    @Value("${grpc.client.payment-service.address}")
    private String address;

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub;

    @GrpcClient("payment-service")
    private WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawServiceBlockingStub;

    public String createPaymentForm(long id, BigDecimal amount, String redirectUrl) {
        PaymentServiceProto.PaymentRequest request = buildPaymentRequest(id, amount.toString(), redirectUrl);
        PaymentServiceProto.PaymentResponse response = paymentServiceBlockingStub.createPaymentForm(request);
        return response.getPaymentUrl();
    }

    public boolean createWithdrawForm(long id, BigDecimal amount, String destination) {
        PaymentServiceProto.WithdrawRequest request = buildWithdrawRequest(id, amount.toString(), destination);
        PaymentServiceProto.WithdrawResponse response = withdrawServiceBlockingStub.createWithdrawForm(request);
        return response.getSuccess();
    }

    private PaymentServiceProto.PaymentRequest buildPaymentRequest(long id, String amount, String redirectUrl) {
        return PaymentServiceProto.PaymentRequest.newBuilder()
                .setId(id)
                .setAmount(amount)
                .setRedirectUrl(redirectUrl)
                .build();
    }

    private PaymentServiceProto.WithdrawRequest buildWithdrawRequest(long id, String amount, String destination) {
        return PaymentServiceProto.WithdrawRequest.newBuilder()
                .setId(id)
                .setAmount(amount)
                .setDestination(destination)
                .build();
    }
}
