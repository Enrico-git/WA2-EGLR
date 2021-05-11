import java.math.BigInteger

object DiffieHellman {
    fun privateKey(prime: BigInteger): BigInteger {
        if (prime.intValueExact() < 1)
            throw Exception("not prime or not higher than 1")
        else
            for (i in 2..(prime.intValueExact() / 2))
                if (prime.intValueExact() % i == 0)
                    throw Exception("not prime")

        var p: Int = 0
        while (p <= 1 || p >= prime.intValueExact())
            p = (prime.intValueExact() * Math.random()).toInt()
        return p.toBigInteger()
    }

    fun publicKey(p: BigInteger, g: BigInteger, privKey: BigInteger):
            BigInteger {
        val a = privKey.intValueExact()
        val gTimesA = g.intValueExact().times(a)
        val A = gTimesA % p.intValueExact()
        return A.toBigInteger()
    }

    fun secret(
        prime: BigInteger, publicKey: BigInteger, privateKey:
        BigInteger
    ): BigInteger {
        return (publicKey.intValueExact().times(privateKey.intValueExact()) % prime.intValueExact()).toBigInteger()
    }
}

fun main() {
    try {
        val p = 17.toBigInteger()
        val g = 37.toBigInteger()
        val alice_priv = DiffieHellman.privateKey(p)
        val bob_priv = DiffieHellman.privateKey(g)
        val alice_pub = DiffieHellman.publicKey(p, g, alice_priv)
        val bob_pub = DiffieHellman.publicKey(p, g, bob_priv)
        val alice_secret = DiffieHellman.secret(p, bob_pub, alice_priv)
        val bob_secret = DiffieHellman.secret(p, alice_pub, bob_priv)
        print(alice_secret == bob_secret)
    } catch (e: Exception) {
        println(e)
    }
}