package me.service.application.mapper

import me.payment.Payment.PaymentMessage
import me.service.application.data.PaymentData
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentMapper {
    fun toPaymentMessage(paymentData: PaymentData): PaymentMessage = PaymentMessage.newBuilder().apply {
        this.id = paymentData.id
    }.build()
}
