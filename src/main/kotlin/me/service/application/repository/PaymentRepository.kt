package me.service.application.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import me.service.application.entity.PaymentEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentRepository : PanacheRepository<PaymentEntity>
