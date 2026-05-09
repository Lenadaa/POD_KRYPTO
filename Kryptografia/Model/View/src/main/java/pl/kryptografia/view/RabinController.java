package pl.kryptografia.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.kryptografia.Messege;
import pl.kryptografia.Rabin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class RabinController {
    @FXML private TextField pKey, qKey, nKey;
    @FXML private TextField messageToEncode, messageToDecode;
    @FXML private TextField selectedFile, fileEncoded, fileSave, fileEncodedToSave;
    @FXML private RadioButton radioFile, radioText;
    @FXML private Button fileOpen, fileEncodeOpen;

    private final ToggleGroup modeGroup = new ToggleGroup();
    private Rabin rabin = new Rabin();
    private File fileToProcess; // Wybrany plik wejściowy
    private byte[] encodedFileBytes; // Zaszyfrowane dane pliku (z BigInteger)
    private byte[] decodedFileBytes; // Odszyfrowane dane pliku

    @FXML
    public void initialize() {
        radioText.setToggleGroup(modeGroup);
        radioFile.setToggleGroup(modeGroup);
        radioText.setSelected(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void genereteKeys(ActionEvent event) {
        rabin.generateKeys(1024);
        pKey.setText(rabin.getP().toString());
        qKey.setText(rabin.getQ().toString());
        nKey.setText(rabin.getN().toString());
    }

    @FXML
    public void encode(ActionEvent event) {
        if (rabin.getN() == null) {
            showAlert("Błąd", "Najpierw wygeneruj klucze!");
            return;
        }

        if (radioText.isSelected()) {
            String text = messageToEncode.getText();
            if (text == null || text.isEmpty()) return;

            Messege m = new Messege(text);
            BigInteger result = rabin.encode(m.messegeToBytes());
            messageToDecode.setText(result.toString());
        }
        else if (radioFile.isSelected()) {
            if (fileToProcess == null) {
                showAlert("Błąd", "Wybierz plik do zaszyfrowania!");
                return;
            }
            try {
                // Odczyt pliku jako bajty
                byte[] data = Files.readAllBytes(fileToProcess.toPath());
                // Szyfrowanie (zwraca BigInteger)
                BigInteger cipher = rabin.encode(data);
                // Konwersja na bajty do zapisu w pliku
                this.encodedFileBytes = cipher.toByteArray();
                showAlert("Sukces", "Plik został zaszyfrowany w pamięci.");
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się odczytać pliku.");
            }
        }
    }

    @FXML
    public void decode(ActionEvent event) {
        if (rabin.getP() == null || rabin.getQ() == null) {
            showAlert("Błąd", "Klucz prywatny jest wymagany do deszyfrowania!");
            return;
        }

        if (radioText.isSelected()) {
            try {
                BigInteger cipher = new BigInteger(messageToDecode.getText());
                byte[] decrypted = rabin.decode(cipher);
                messageToEncode.setText(new String(decrypted, StandardCharsets.UTF_8));
            } catch (Exception e) {
                showAlert("Błąd", "Niepoprawny szyfrogram lub błąd paddingu.");
            }
        }
        else if (radioFile.isSelected()) {
            if (fileToProcess == null) {
                showAlert("Błąd", "Wybierz plik do odszyfrowania!");
                return;
            }
            try {
                // Odczyt zaszyfrowanego pliku
                byte[] data = Files.readAllBytes(fileToProcess.toPath());
                // Tworzymy BigInteger z bajtów pliku i odszyfrowujemy go
                this.decodedFileBytes = rabin.decode(new BigInteger(data));
                showAlert("Sukces", "Plik został odszyfrowany w pamięci.");
            } catch (Exception e) {
                showAlert("Błąd", "Błąd deszyfrowania pliku (prawdopodobnie zły klucz lub plik).");
            }
        }
    }

    @FXML
    public void openFileToEncode(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);
        if (f != null) {
            fileToProcess = f;
            selectedFile.setText(f.getAbsolutePath());
            if (radioText.isSelected()) {
                try {
                    messageToEncode.setText(Files.readString(f.toPath()));
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    @FXML
    public void saveEncoded(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(null);
        if (f != null) {
            try {
                if (radioFile.isSelected()) {
                    if (encodedFileBytes == null) {
                        showAlert("Błąd", "Brak danych do zapisu. Najpierw zaszyfruj plik.");
                        return;
                    }
                    Files.write(f.toPath(), encodedFileBytes);
                } else {
                    Files.writeString(f.toPath(), messageToDecode.getText());
                }
                fileEncodedToSave.setText(f.getAbsolutePath());
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML
    public void saveDecoded(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(null);
        if (f != null) {
            try {
                if (radioFile.isSelected()) {
                    if (decodedFileBytes == null) {
                        showAlert("Błąd", "Brak danych do zapisu. Najpierw odszyfruj plik.");
                        return;
                    }
                    Files.write(f.toPath(), decodedFileBytes);
                } else {
                    Files.writeString(f.toPath(), messageToEncode.getText());
                }
                fileSave.setText(f.getAbsolutePath());
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML
    public void switchToDesScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stage.setScene(new Scene(root));
    }
}