package me.service.application.service

import io.grpc.Status
import me.service.application.entity.PaymentEntity
import me.service.application.repository.PaymentRepository
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class PaymentApplicationService(
    private val paymentRepository: PaymentRepository
) {
    fun getPayment(id: Long): PaymentEntity = paymentRepository.findById(id)
        ?: throw Status.NOT_FOUND
            .withDescription("No payment found with id $id")
            .asException()

    fun getPaymentList(): List<PaymentEntity> = paymentRepository.findAll().list()

    @Transactional
    fun placePayment(
        amount: Int,
        currency: String
    ): PaymentEntity {
        val paymentEntity = PaymentEntity()
        paymentEntity.amount = amount
        paymentEntity.currency = currency

        paymentEntity.persist()

        return paymentEntity
    }
}
