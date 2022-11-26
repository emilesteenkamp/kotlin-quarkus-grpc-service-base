package me.common.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class CommonEntity : PanacheEntity() {
    @CreationTimestamp
    @Column(nullable = false)
    open var createdAt: Instant? = null
    @UpdateTimestamp
    @Column(nullable = false)
    open var updatedAt: Instant? = null
}
