package me.service.grpc.service

import io.quarkus.grpc.GrpcService
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import me.payment.Payment
import me.payment.Payment.GetPaymentListResponse
import me.payment.Payment.PaymentMessage
import me.payment.PaymentService
import me.service.application.mapper.PaymentMapper
import me.service.application.service.PaymentApplicationService

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
        val amount = request!!.amount
        val currency = request.currency

        val payment = paymentService.placePayment(
            amount,
            currency
        )

        return Uni.createFrom().item { paymentMapper.toPaymentMessage(payment) }
    }
}
