package edu.ccsu.gencyber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.ccsu.gencyber.cryptography.CryptoBase;
import edu.ccsu.gencyber.cryptography.SubstitutionCipher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public class MainController {

  private enum Mode {
    CIPHER, CIPHERTEXT, PLAINTEXT, STATS, ENCRYPT
  }

  private Map<Mode, Font> modeFontMapping = new HashMap<Mode, Font>();
  private Mode currentMode = Mode.CIPHERTEXT;
  
  private static MainController controllerInstance = null; 

  @FXML
  private TextArea ciphertextArea;
  @FXML
  private Button randomizeCipherBtn;
  @FXML
  private Button initializeCipherBtn;
  @FXML
  private Button encryptBtn;
  @FXML
  private Button loadSampleCiphertext;
  @FXML
  private ImageView letterFrequencyImage;

  private String cipherTextPath = "ciphertext.txt";
  private String sampleCipherTextPath = "sampleciphertext.txt";
  private String plainTextPath = "plaintext.txt";
  private String cipherPath = "cipher.txt";
  private String statsPath = "stats.txt";
  private final String BASE_BREAKING_CIPHER =  "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public MainController() {
    modeFontMapping.put(Mode.CIPHERTEXT, Font.font("Courier New", 12));
    modeFontMapping.put(Mode.CIPHER, Font.font("Courier New", 12));
    modeFontMapping.put(Mode.PLAINTEXT, Font.font("Courier New", 12));
    modeFontMapping.put(Mode.STATS, Font.font("Courier New", 12));
    modeFontMapping.put(Mode.ENCRYPT, Font.font("Courier New", 12));
	  File ciphertextFile = new File(cipherTextPath);
	  // If this is first time running program initialize files with sample one
	  if (!ciphertextFile.exists()) {
	    	URL sampleCiphertextResource = getClass().getResource(sampleCipherTextPath);
	    	try {
	    	String sampleCiphertext = loadFile(sampleCiphertextResource);

	    	saveFile(cipherTextPath, sampleCiphertext);
		  saveFile(cipherPath,BASE_BREAKING_CIPHER);
		      } catch (Exception e) {
			        e.printStackTrace();
			        showDialog("Error initializing files: " + e.getMessage());
			      }
	  }
	  controllerInstance = this;
  }
  
  public static MainController getControllerInstance() {
	  return controllerInstance;
  }

  public void increaseFont(ActionEvent event) {
    ciphertextArea.setFont(Font.font("Courier New", ciphertextArea.getFont().getSize() + 2));
    modeFontMapping.put(currentMode, ciphertextArea.getFont());
  }

  public void decreaseFont(ActionEvent event) {
    ciphertextArea.setFont(Font.font("Courier New", ciphertextArea.getFont().getSize() - 2));
    modeFontMapping.put(currentMode, ciphertextArea.getFont());
  }

  public void showCiphertext(ActionEvent event) {

    if (changeMode(Mode.CIPHERTEXT)) {
      loadText();
      ciphertextArea.setWrapText(true);
    }
  }
  
  public void loadSampleCiphertext(ActionEvent event) {
	    try {
	    	URL sampleCiphertextResource = getClass().getResource(sampleCipherTextPath);
	    	String sampleCiphertext = loadFile(sampleCiphertextResource);
	    	saveFile(cipherTextPath, sampleCiphertext);
		    loadText();
	      } catch (Exception e) {
	        e.printStackTrace();
	        showDialog("Error loading sample ciphertext: " + e.getMessage());
	      }
	  }



public void decrypt(ActionEvent event) {
    if (changeMode(Mode.PLAINTEXT)) {
      try {
        CryptoBase decryptor = SubstitutionCipher.loadSubstitutionCipher(cipherPath);
        decryptor.decryptFile(plainTextPath, cipherTextPath);
      } catch (Exception e) {
        e.printStackTrace();
        showDialog("Error decrypting: " + e.getMessage());
      }
      loadText();
      ciphertextArea.setWrapText(true);
    }
  }

  public void encrypt(ActionEvent event) {
    if (changeMode(Mode.ENCRYPT)) {
      loadText();
      ciphertextArea.setWrapText(true);
    }
  }

  public void initializeCipher(ActionEvent event) {
    ciphertextArea.setText(BASE_BREAKING_CIPHER);
  }

  public void randomizeCipher(ActionEvent event) {
    List<String> plaintextCharsList = Arrays.asList("abcdefghijklmnopqrstuvwxyz".split(""));
    Collections.shuffle(plaintextCharsList);
    String plaintextMapping = "";
    for (Object o : plaintextCharsList) {
      plaintextMapping += o.toString();
    }
    plaintextMapping += "\nABCDEFGHIJKLMNOPQRSTUVWXYZ";
    ciphertextArea.setText(plaintextMapping);
  }

  public void encryptContents(ActionEvent event) {
    String plaintext = ciphertextArea.getText().toLowerCase();
    ciphertextArea.setText(plaintext);
    try {
      CryptoBase encryptor = SubstitutionCipher.loadSubstitutionCipher(cipherPath);
      String ciphertext = encryptor.encrypt(plaintext);
      FileWriter writer = new FileWriter(cipherTextPath);
      writer.write(ciphertext);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      showDialog("Error encrypting: " + e.getMessage());
    }
  }

  private boolean changeMode(Mode newMode) {
    // If mode didn't change nothing to check
    if (this.currentMode == newMode) {
      return true;
      // Else check if mode can be changed and process change of mode
    } else {
      if ((currentMode == Mode.CIPHER) && (!validCipherMapping())) {
        showDialog("Invalid cipher substitutions must be same length");
        return false;
      }
      if (newMode == Mode.CIPHERTEXT) {
    	  loadSampleCiphertext.setVisible(true);
      }else {
    	  loadSampleCiphertext.setVisible(false);
      }
      if (newMode == Mode.ENCRYPT) {
        encryptBtn.setVisible(true);
      } else {
        encryptBtn.setVisible(false);
      }
      if (newMode == Mode.CIPHER) {
        randomizeCipherBtn.setVisible(true);
        initializeCipherBtn.setVisible(true);
      } else {
        randomizeCipherBtn.setVisible(false);
        initializeCipherBtn.setVisible(false);
      }
      if (newMode == Mode.STATS) {
        letterFrequencyImage.setVisible(true);
      } else {
        letterFrequencyImage.setVisible(false);
      }
    }
    this.currentMode = newMode;
    return true;
  }

  public void showCipher(ActionEvent event) {
    if (changeMode(Mode.CIPHER)) {
      loadText();
      ciphertextArea.setWrapText(false);
    }
  }

  public void showStats(ActionEvent event) {
    if (changeMode(Mode.STATS)) {

      // Calculate stats
      try {
        CryptoBase.getStatistics(cipherTextPath, statsPath);
      } catch (Exception e) {
        System.err.println("Error getting stats: " + e.getMessage());
        showDialog("Error getting stats: " + e.getMessage());
      }
      loadText();
      ciphertextArea.setWrapText(false);
    }
  }

  private void showDialog(String message) {
    // Creating a dialog
    Dialog<String> dialog = new Dialog<String>();
    // Setting the title
    dialog.setTitle("Dialog");
    ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
    // Setting the content of the dialog
    dialog.setContentText(message);
    // Adding buttons to the dialog pane
    dialog.getDialogPane().getButtonTypes().add(type);
    dialog.showAndWait();
  }

  private boolean validCipherMapping() {
    String cipherMapping = ciphertextArea.getText();
    String[] lines = cipherMapping.split("\n");
    if (lines[0].length() == lines[1].length()) {
      saveFile(cipherPath, cipherMapping);
      return true;
    } else {
      return false;
    }
  }


  private void loadText() {
    String content = "";
    try {
      if (this.currentMode == Mode.CIPHERTEXT) {
    		  content = loadFile(cipherTextPath);
      } else if (this.currentMode == Mode.CIPHER) {
        content = loadFile(cipherPath);
      } else if (this.currentMode == Mode.PLAINTEXT) {
        content = loadFile(plainTextPath);
      } else if (this.currentMode == Mode.STATS) {
        content = loadFile(statsPath);
      } else if (this.currentMode == Mode.ENCRYPT) {
        content = "";
      }
    } catch (IOException ie) {
      content = ie.getMessage();
    }
    ciphertextArea.setFont(modeFontMapping.get(this.currentMode));
    ciphertextArea.setText(content);
  }

  private String loadFile(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    StringBuffer returnBuffer = new StringBuffer();
    String line = reader.readLine();
    while (line != null) {
      returnBuffer.append(line + "\n");
      line = reader.readLine();
    }
    reader.close();
    return returnBuffer.toString();
  }

  private String loadFile(URL resource) throws IOException{
	    BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)resource.getContent()));
	    StringBuffer returnBuffer = new StringBuffer();
	    String line = reader.readLine();
	    while (line != null) {
	      returnBuffer.append(line + "\n");
	      line = reader.readLine();
	    }
	    reader.close();
	    return returnBuffer.toString();
}
  
  private void saveFile(String filename, String contents) {
    try {
      FileWriter writer = new FileWriter(filename);
      writer.write(contents);
      writer.flush();
      writer.close();
    } catch (IOException ie) {
      System.err.println(ie.getMessage());
      showDialog("Unable to save cipher:  " + ie.getMessage());
    }
  }
}
