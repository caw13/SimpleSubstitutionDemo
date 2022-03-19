/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package edu.ccsu.gencyber.cryptography;

/**
 *
 * @author cw1491
 */
public interface Crypto {
  public void readCipher(String cipherFile) throws Exception;

  public String encrypt(String plaintext);

  public String decrypt(String ciphertext);
}
