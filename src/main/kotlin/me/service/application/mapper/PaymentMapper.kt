package me.service.application.mapper

import me.payment.Payment.PaymentMessage
import me.service.application.entity.PaymentEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentMapper {
    fun toPaymentMessage(paymentEntity: PaymentEntity): PaymentMessage = PaymentMessage.newBuilder().apply {
        this.id = paymentEntity.id.toString()
        this.amount = paymentEntity.amount!!
        this.currency = paymentEntity.currency
    }.build()
}
