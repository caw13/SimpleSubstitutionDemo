/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package edu.ccsu.gencyber.cryptography;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author cw1491
 */
public abstract class CryptoBase implements Crypto {

  public void encryptFile(String plaintextFile, String ciphertextFile)
      throws IOException, FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(plaintextFile));
    String plaintext = reader.readLine();
    String ciphertext = encrypt(plaintext);
    FileWriter writer = new FileWriter(ciphertextFile);
    writer.write(ciphertext);
    writer.flush();
    reader.close();
    writer.close();
  }

  public void decryptFile(String plaintextFile, String ciphertextFile)
      throws IOException, FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(ciphertextFile));
    String ciphertext = reader.readLine();
    String plaintext = decrypt(ciphertext);
    FileWriter writer = new FileWriter(plaintextFile);
    writer.write(plaintext);
    writer.flush();
    reader.close();
    writer.close();
  }

  public static void getStatistics(String ciphertextFile, String statFile) throws IOException {
    StringBuffer buffer = new StringBuffer();
    TreeMap<Character, Integer> charStatistics = new TreeMap<Character, Integer>();
    char[] cipherChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    for (int i = 0; i < cipherChars.length; i++) {
      charStatistics.put(cipherChars[i], 0);
    }

    BufferedReader reader = new BufferedReader(new FileReader(ciphertextFile));
    String ciphertext = reader.readLine();
    char[] ciphertextChars = ciphertext.toCharArray();
    for (int j = 0; j < ciphertextChars.length; j++) {
      Character curChar = ciphertextChars[j];
      if (charStatistics.containsKey(curChar)) {
        Integer curCount = charStatistics.get(curChar);
        charStatistics.put(curChar, curCount.intValue() + 1);
      }
    }
    Iterator<Character> charKeyIter = charStatistics.keySet().iterator();
    TreeMap<Double, Character> statSort =
        new TreeMap<Double, Character>(Collections.reverseOrder());
    while (charKeyIter.hasNext()) {
      Character key = charKeyIter.next();
      double value = ((Integer) charStatistics.get(key)).doubleValue();
      statSort.put(value, key);
    }
    Iterator<Double> frequencyKeyIter = statSort.keySet().iterator();
    buffer.append("Sorted most frequent character at top:\n");
    while (frequencyKeyIter.hasNext()) {
      Double key = (Double) frequencyKeyIter.next();
      Object character = statSort.get(key);
      // buffer.append(character+": "+((key.doubleValue()/ciphertext.length())*100.0)+"%\n");
      buffer.append(character + ":  "
          + String.format("%,.3f", ((key.doubleValue() / ciphertext.length()) * 100.0)) + "%\n");
    }
    FileWriter writer = new FileWriter(statFile);
    writer.write(buffer.toString());
    writer.flush();
    writer.close();
    reader.close();
  }

  public void readArgs(String[] args) throws Exception {
    if (args.length != 3 && args.length != 4) {
      System.out.println("args[0] - GetStatistics");
      System.out.println("args[1] - ciphertextFile");
      System.out.println("args[2] - statFile");
      System.out.println("---------- OR --------------");
      System.out.println("arg[0] - 'encrypt'|'decrypt'");
      System.out.println("arg[1] - 'cipher'");
      System.out.println("arg[2] - plaintextFile");
      System.out.println("arg[3] - ciphertextFile");
      return;
    }
    boolean encrypt = false;
    if (args[0].equalsIgnoreCase("GetStatistics")) {
      getStatistics(args[1], args[2]);
      return;
    } else if (args[0].equalsIgnoreCase("encrypt")) {
      encrypt = true;
    } else if (args[0].equalsIgnoreCase("decrypt")) {
      encrypt = false;
    } else {
      throw new IllegalArgumentException("1st argument must be 'encrypt' or 'decrypt'");
    }
    readCipher(args[1]);
    if (encrypt) {
      encryptFile(args[2], args[3]);
    } else {
      decryptFile(args[2], args[3]);
    }
  }
}
