package org.springframework.samples.petclinic.service

import org.springframework.samples.petclinic.model.BaseEntity
import java.util.NoSuchElementException

object EntityUtils {

    @JvmStatic
    fun <T : BaseEntity> getById(entities: Collection<T>, entityClass: Class<T>, entityId: Int): T {
        for (entity in entities) {
            if (entity.id != null && entity.id == entityId && entityClass.isInstance(entity)) {
                return entity
            }
        }
        throw NoSuchElementException("No ${entityClass.simpleName} entity with id $entityId found")
    }

}