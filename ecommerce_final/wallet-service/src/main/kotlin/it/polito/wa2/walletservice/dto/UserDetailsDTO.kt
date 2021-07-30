package it.polito.wa2.walletservice.dto


import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsDTO(
    val id: ObjectId?,
    private val username: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private var password: String?,
    private val isEnabled: Boolean?,
    val email: String?,
    val roles: String?
) : UserDetails {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val grantedAuthorities = mutableSetOf<GrantedAuthority>()
        val setRoles = roles!!.split(",")
        setRoles.forEach { grantedAuthorities.add(SimpleGrantedAuthority(it)) }
        return grantedAuthorities
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return username
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return isEnabled!!
    }
}

