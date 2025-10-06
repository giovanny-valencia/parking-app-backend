package com.parkingapp.backendapi.infrastructure.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

// This utility class is for generating a new, secure JWT secret key.
public class SecretKeyGenerator {

  /**
   * Generates a new cryptographically secure key and prints it to the console. The key is 512 bits
   * (64 bytes) to be compatible with the HS512 algorithm.
   *
   * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    // Define the key length in bytes (512 bits / 8 bits per byte = 64 bytes)
    final int keyLengthInBytes = 64;

    // Use a cryptographically secure random number generator.
    SecureRandom secureRandom = new SecureRandom();
    byte[] keyBytes = new byte[keyLengthInBytes];
    secureRandom.nextBytes(keyBytes);

    // Encode the key bytes into a Base64 string.
    String base64EncodedKey = Base64.getEncoder().encodeToString(keyBytes);

    System.out.println("------------------------------------------------------------------------");
    System.out.println("New Secure JWT Secret Key:");
    System.out.println(base64EncodedKey);
    System.out.println("------------------------------------------------------------------------");
  }
}
