package ru.mooncess.payment_service.service;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mooncess.grpc.PaymentServiceGrpc;
import ru.mooncess.grpc.PaymentServiceProto;

@GrpcService
@AllArgsConstructor
public class PaymentService extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final PaymentProviderService paymentProviderService;
    @Override
    public void createPaymentForm(PaymentServiceProto.PaymentRequest request,
                                  StreamObserver<PaymentServiceProto.PaymentResponse> responseObserver) {
        responseObserver.onNext(next(request.getId(), request.getAmount(), request.getRedirectUrl()));
        responseObserver.onCompleted();
    }

    private PaymentServiceProto.PaymentResponse next(long id, String amount, String redirectUrl) {
        return PaymentServiceProto.PaymentResponse.newBuilder()
                .setPaymentUrl(paymentProviderService.formPaymentPage(id, amount, redirectUrl))
                .build();
    }
}
