package it.polito.ecommerce.domain

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.*

@MappedSuperclass
abstract class EntityBase<T: Serializable> {
    companion object {
        private const val serialVersionUID = -43869754L
    }

    @Id
    //@GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "pk_generator")
//    @GeneratedValue
//    @SequenceGenerator(name="pk_generator",
//        sequenceName = "pk_sequence",
//        initialValue = 1,
//        allocationSize = 1)
    private val id:T?  = null

    fun getId(): T? = id

    override fun toString(): String {
        return "@Entity ${this.javaClass.name}(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as EntityBase<*>
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return 31 //any value will do
    }

}