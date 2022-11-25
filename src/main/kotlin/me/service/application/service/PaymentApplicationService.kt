package me.service.application.service

import me.service.application.data.PaymentData
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentApplicationService {
    fun getPayment(id: String): PaymentData = PaymentData(id)

    fun getPaymentList(): List<PaymentData> = listOf()

    fun placePayment(): PaymentData = PaymentData("id")
}
