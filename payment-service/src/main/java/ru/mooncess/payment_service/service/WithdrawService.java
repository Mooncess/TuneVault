package ru.mooncess.payment_service.service;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mooncess.grpc.WithdrawServiceGrpc;
import ru.mooncess.grpc.PaymentServiceProto;

@GrpcService
@AllArgsConstructor
public class WithdrawService extends WithdrawServiceGrpc.WithdrawServiceImplBase {
    private final PaymentProviderService paymentProviderService;

    @Override
    public void createWithdrawForm(PaymentServiceProto.WithdrawRequest request,
                                   StreamObserver<PaymentServiceProto.WithdrawResponse> responseObserver) {
        boolean result = processWithdraw(
                request.getId(),
                request.getAmount(),
                request.getDestination()
        );

        responseObserver.onNext(buildResponse(result));
        responseObserver.onCompleted();
    }

    private boolean processWithdraw(long id, String amount, String destination) {
        try {
            return paymentProviderService.confirmWithdraw(id, amount, destination);
        } catch (Exception e) {
            return false;
        }
    }

    private PaymentServiceProto.WithdrawResponse buildResponse(boolean success) {
        return PaymentServiceProto.WithdrawResponse.newBuilder()
                .setSuccess(success)
                .build();
    }
}
