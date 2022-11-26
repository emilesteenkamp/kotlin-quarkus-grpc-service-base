package me.service.grpc.service

import io.grpc.stub.StreamObserver
import io.quarkus.grpc.GrpcService
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import me.payment.Payment
import me.payment.Payment.GetPaymentListResponse
import me.payment.Payment.PaymentMessage
import me.payment.PaymentService
import me.payment.PaymentServiceGrpc
import me.service.application.mapper.PaymentMapper
import me.service.application.service.PaymentApplicationService
import java.lang.RuntimeException

@GrpcService
class PaymentGrpcService(
    // Services
    private val paymentService: PaymentApplicationService,
    // Mappers
    private val paymentMapper: PaymentMapper
) : PaymentService {
    @Blocking
    override fun getPayment(request: Payment.GetPaymentRequest?): Uni<PaymentMessage> {
        val id = request!!.paymentId.toLong()

        val payment = paymentService.getPayment(id)

        return Uni.createFrom().item { paymentMapper.toPaymentMessage(payment) }
    }

    @Blocking
    override fun getPaymentList(request: Payment.GetPaymentListRequest?): Uni<GetPaymentListResponse> {
        val paymentList = paymentService.getPaymentList()

        return Uni.createFrom().item {
            GetPaymentListResponse.newBuilder()
                .addAllPaymentMessage(paymentList.map { paymentMapper.toPaymentMessage(it) })
                .build()
        }
    }

    @Blocking
    override fun placePayment(request: Payment.PlacePaymentRequest?): Uni<PaymentMessage> {
        throw RuntimeException("Throw")

        val amount = request!!.amount
        val currency = request.currency

        val payment = paymentService.placePayment(
            amount,
            currency
        )

        return Uni.createFrom().item { paymentMapper.toPaymentMessage(payment) }
    }
}

//@GrpcService
//class PaymentGrpcService(
//    // Services
//    private val paymentService: PaymentApplicationService,
//    // Mappers
//    private val paymentMapper: PaymentMapper
//) : PaymentServiceGrpc.PaymentServiceImplBase() {
//    @Blocking
//    override fun getPayment(request: Payment.GetPaymentRequest?, responseObserver: StreamObserver<PaymentMessage>?) {
//        val id = request!!.paymentId.toLong()
//
//        val payment = paymentService.getPayment(id)
//
//        responseObserver!!.onNext(paymentMapper.toPaymentMessage(payment))
//        responseObserver.onCompleted()
//    }
//
//    @Blocking
//    override fun getPaymentList(
//        request: Payment.GetPaymentListRequest?,
//        responseObserver: StreamObserver<GetPaymentListResponse>?
//    ) {
//        val paymentList = paymentService.getPaymentList()
//
//        responseObserver!!.onNext(
//            GetPaymentListResponse.newBuilder()
//                .addAllPaymentMessage(paymentList.map { paymentMapper.toPaymentMessage(it) })
//                .build()
//        )
//        responseObserver.onCompleted()
//    }
//
//    @Blocking
//    override fun placePayment(
//        request: Payment.PlacePaymentRequest?,
//        responseObserver: StreamObserver<PaymentMessage>?
//    ) {
//        throw RuntimeException("Throw")
//
//        val amount = request!!.amount
//        val currency = request.currency
//
//        val payment = paymentService.placePayment(amount, currency)
//
//        responseObserver!!.onNext(paymentMapper.toPaymentMessage(payment))
//        responseObserver.onCompleted()
//    }
//}
