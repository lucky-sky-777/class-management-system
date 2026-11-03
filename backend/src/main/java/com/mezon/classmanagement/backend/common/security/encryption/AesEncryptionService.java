package com.mezon.classmanagement.backend.common.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public final class AesEncryptionService {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH = 128;
	private static final int KEY_LENGTH = 32;

	private final SecretKeySpec secretKey;
	private final SecureRandom secureRandom = new SecureRandom();

	public AesEncryptionService(String base64Key) {
		byte[] key = Base64.getDecoder().decode(base64Key);

		if (key.length != KEY_LENGTH) {
			throw new IllegalArgumentException(
					"AES-256 key must be exactly 32 bytes"
			);
		}

		this.secretKey = new SecretKeySpec(key, "AES");
	}

	public String encrypt(String plaintext) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(
					Cipher.ENCRYPT_MODE,
					secretKey,
					new GCMParameterSpec(TAG_LENGTH, iv)
			);

			byte[] ciphertext = cipher.doFinal(
					plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8)
			);

			// Format: IV (12 bytes) + ciphertext + authentication tag
			byte[] result = ByteBuffer
					.allocate(iv.length + ciphertext.length)
					.put(iv)
					.put(ciphertext)
					.array();

			return Base64.getEncoder().encodeToString(result);

		} catch (Exception e) {
			throw new IllegalStateException("Encryption failed", e);
		}
	}

	public String decrypt(String encryptedText) {
		try {
			byte[] data = Base64.getDecoder().decode(encryptedText);

			if (data.length < IV_LENGTH + TAG_LENGTH / 8) {
				throw new IllegalArgumentException(
						"Invalid encrypted data"
				);
			}

			ByteBuffer buffer = ByteBuffer.wrap(data);

			byte[] iv = new byte[IV_LENGTH];
			buffer.get(iv);

			byte[] ciphertext = new byte[buffer.remaining()];
			buffer.get(ciphertext);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(
					Cipher.DECRYPT_MODE,
					secretKey,
					new GCMParameterSpec(TAG_LENGTH, iv)
			);

			byte[] plaintext = cipher.doFinal(ciphertext);

			return new String(
					plaintext,
					java.nio.charset.StandardCharsets.UTF_8
			);

		} catch (Exception e) {
			throw new IllegalStateException(
					"Decryption failed; key or data may be invalid", e
			);
		}
	}
}