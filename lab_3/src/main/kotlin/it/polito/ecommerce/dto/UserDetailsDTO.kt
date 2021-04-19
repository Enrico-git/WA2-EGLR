package it.polito.ecommerce.dto
import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsDTO(
    val id: Long?,
    private val username: String,
    private val password: String,
    private val isEnabled: Boolean,
    val email: String,
    val roles: Set<Rolename>,
    val confirmPassword: String? = null

    ): UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val grantedAuthorities = mutableSetOf<GrantedAuthority>()
        roles.forEach { grantedAuthorities.add(SimpleGrantedAuthority(it.toString())) }
        return grantedAuthorities;
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

}

fun User.toDTO(): UserDetailsDTO {
    return UserDetailsDTO(
        id = getId()!!,
        username = username,
        password = password,
        email = email,
        isEnabled = isEnabled,
        roles = this.getRoles()
    )
}