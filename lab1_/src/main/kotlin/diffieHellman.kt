import java.math.BigInteger

object DiffieHellman {
    fun privateKey(prime: BigInteger): BigInteger {
        if (prime.intValueExact() < 1)
            throw Exception("number must be higher than 1")
        var rand = 0
        while (rand <= 1 || rand >= prime.intValueExact())
            rand = (prime.intValueExact() * Math.random()).toInt()
        return rand.toBigInteger()
    }

    fun publicKey(p: BigInteger, g: BigInteger, privKey: BigInteger): BigInteger {
        return g.modPow(privKey,p)

    }
    fun secret(prime: BigInteger, publicKey: BigInteger, privateKey: BigInteger): BigInteger {
        return publicKey.modPow(privateKey,prime)
    }
}

fun main() {
    val p = 163.toBigInteger()
    val g = 37.toBigInteger()
    val alice = DiffieHellman
    val bob = DiffieHellman
    val alicePrivateKey = alice.privateKey(p)
    println(alicePrivateKey)
    val bobPrivateKey = bob.privateKey(p)
    println(bobPrivateKey)
    val alicePublicKey = alice.publicKey(p,g,alicePrivateKey)
    println(alicePublicKey)
    val bobPublicKey = bob.publicKey(p,g,bobPrivateKey)
    println(bobPublicKey)
    val aliceSecret = alice.secret(p,bobPublicKey,alicePrivateKey)
    println(aliceSecret)
    val bobSecret = bob.secret(p,alicePublicKey,bobPrivateKey)
    println(bobSecret)
    println(aliceSecret==bobSecret)
}