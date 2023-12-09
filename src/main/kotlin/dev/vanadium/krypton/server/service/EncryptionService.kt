package dev.vanadium.krypton.server.service

import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

@Service
class EncryptionService {

    fun loadPublicToken(keyPub: String): RSAPublicKey {
        val keyBytes = keyPub.encodeToByteArray()
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(keyBytes)
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    fun encryptToken(keyPub: String, token: String): String {
        val key = loadPublicToken(keyPub)
        val cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(token.encodeToByteArray()).decodeToString()
    }

}