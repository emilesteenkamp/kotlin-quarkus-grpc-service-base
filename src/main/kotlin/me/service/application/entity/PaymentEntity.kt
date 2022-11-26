package me.service.application.entity

import me.common.entity.CommonEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity(name = "payment")
open class PaymentEntity : CommonEntity() {
    @Column(nullable = false)
    open var amount: Int? = null
    @Column(nullable = false)
    open var currency: String? = null
}
