package pl.kryptografia.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.kryptografia.Rabin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RabinController {
    @FXML private TextField pKey, qKey, nKey;
    @FXML private TextField messageToEncode, messageToDecode;
    @FXML private TextField file, fileEncoded, fileSave, fileEncodedToSave;

    @FXML private Button fileOpen, fileEncodeOpen;
    @FXML private RadioButton radioFile, radioText;

    private final ToggleGroup modeGroup = new ToggleGroup();
    private Rabin rabin = new Rabin();
    private File selectedFile;

    // ZMIANA: Typ na tablicę BigInteger[] zgodnie z nową wersją klasy Rabin
    private BigInteger[] encodedResult;
    private byte[] decodedResult;

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

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void genereteKeys(ActionEvent event) {
        // Generujemy klucze (1024 lub 2048 bitów)
        rabin.generateKeys(1024);
        pKey.setText(rabin.getP().toString());
        qKey.setText(rabin.getQ().toString());
        nKey.setText(rabin.getN().toString());
    }

    @FXML
    public void encode(ActionEvent event) {
        if (rabin.getN() == null) {
            showAlert("Brak klucza", "Najpierw wygeneruj klucze.");
            return;
        }

        try {
            byte[] dataToEncrypt;
            if (radioText.isSelected()) {
                String text = messageToEncode.getText();
                if (text == null || text.isEmpty()) return;
                dataToEncrypt = text.getBytes(StandardCharsets.UTF_8);
            } else {
                if (selectedFile == null) {
                    showAlert("Brak pliku", "Wybierz plik do zaszyfrowania.");
                    return;
                }
                dataToEncrypt = Files.readAllBytes(selectedFile.toPath());
            }

            // ZMIANA: Wywołanie zwraca tablicę BigInteger[]
            this.encodedResult = rabin.encrypt(dataToEncrypt);

            messageToDecode.setText("Zaszyfrowano bloków: " + encodedResult.length);
            showInfoAlert("Sukces", "Zaszyfrowano dane (liczba bloków: " + encodedResult.length + ")");

        } catch (IOException e) {
            showAlert("Błąd", "Błąd odczytu pliku: " + e.getMessage());
        }
    }

    @FXML
    public void decode(ActionEvent event) {
        if (rabin.getP() == null || rabin.getQ() == null) {
            showAlert("Brak klucza", "Klucz prywatny jest wymagany.");
            return;
        }

        try {
            BigInteger[] toDecrypt;
            String input = messageToDecode.getText();

            // Parsowanie szyfrogramu z formatu tekstowego (oddzielone przecinkami)
            if (input != null && input.contains(",")) {
                toDecrypt = Arrays.stream(input.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(BigInteger::new)
                        .toArray(BigInteger[]::new); // Konwersja na tablicę BigInteger[]
            } else {
                toDecrypt = this.encodedResult;
            }

            if (toDecrypt == null || toDecrypt.length == 0) {
                showAlert("Błąd", "Brak danych do odszyfrowania.");
                return;
            }

            // ZMIANA: Wywołanie decrypt przyjmuje tablicę BigInteger[]
            this.decodedResult = rabin.decrypt(toDecrypt);

            if (radioText.isSelected()) {
                messageToEncode.setText(new String(decodedResult, StandardCharsets.UTF_8));
            } else {
                showInfoAlert("Sukces", "Plik został odszyfrowany. Rozmiar: " + decodedResult.length + " bajtów.");
            }
        } catch (Exception e) {
            showAlert("Błąd", "Błąd deszyfrowania: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void saveEncoded(ActionEvent event) {
        if (encodedResult == null || encodedResult.length == 0) {
            showAlert("Błąd", "Brak zaszyfrowanych danych.");
            return;
        }

        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(null);
        if (f != null) {
            // ZMIANA: Streamowanie z tablicy Arrays.stream()
            String contentToSave = Arrays.stream(encodedResult)
                    .map(BigInteger::toString)
                    .collect(Collectors.joining(","));

            String path = f.getAbsolutePath();
            if (!path.endsWith(".rabin")) path += ".rabin";

            try {
                Files.writeString(Path.of(path), contentToSave);
                fileEncodedToSave.setText(path);
                showInfoAlert("Sukces", "Szyfrogram został zapisany.");
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się zapisać pliku.");
            }
        }
    }

    @FXML
    public void openFileToEncode(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);
        if (f != null) {
            selectedFile = f;
            file.setText(f.getPath());
            if (radioText.isSelected()) {
                try {
                    messageToEncode.setText(Files.readString(f.toPath()));
                } catch (IOException e) {
                    showAlert("Błąd", "Nie udało się odczytać pliku.");
                }
            }
        }
    }

    @FXML
    public void fileEncode(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);
        if (f != null) {
            selectedFile = f;
            fileEncoded.setText(f.getPath());
            try {
                messageToDecode.setText(Files.readString(f.toPath()));
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się odczytać szyfrogramu.");
            }
        }
    }

    @FXML
    public void saveDecoded(ActionEvent event) {
        if (decodedResult == null) {
            showAlert("Błąd", "Brak danych do zapisania.");
            return;
        }

        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(null);
        if (f != null) {
            fileSave.setText(f.getAbsolutePath());
            try {
                Files.write(f.toPath(), decodedResult);
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się zapisać pliku.");
            }
        }
    }

    @FXML
    public void switchToDesScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void switchToRabinScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/rabinGui.fxml"));
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        stage.setScene(new Scene(root));
    }
}