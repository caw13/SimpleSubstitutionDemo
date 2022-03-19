package edu.ccsu.gencyber.cryptography;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implements a substitution cipher
 * 
 * @author cw1491
 */
public class SubstitutionCipher extends CryptoBase {

  private char[] alphabet;
  private char[] substitutions = null;

  /**
   * Class should be constructed using loadSubstitutionCipher method
   */
  private SubstitutionCipher() {

  }

  public static SubstitutionCipher loadSubstitutionCipher(String cipherFile) throws IOException {
    SubstitutionCipher cipher = new SubstitutionCipher();
    cipher.readCipher(cipherFile);
    return cipher;
  }

  public void readCipher(String cipherFile) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(cipherFile));
    String line = reader.readLine();
    alphabet = line.toCharArray();
    line = reader.readLine();
    substitutions = line.toCharArray();
    reader.close();
  }

  public String encrypt(String plaintext) {
    String ciphertext = plaintext;
    for (int i = 0; i < 26; i++) {
      ciphertext = ciphertext.replace(alphabet[i], substitutions[i]);
    }
    return ciphertext;
  }

  public String decrypt(String ciphertext) {
    String plaintext = ciphertext;
    for (int i = 0; i < 26; i++) {
      plaintext = plaintext.replace(substitutions[i], alphabet[i]);
    }
    return plaintext;
  }

  public static void main(String[] args) {
    try {
      SubstitutionCipher cipher = new SubstitutionCipher();
      cipher.readArgs(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
