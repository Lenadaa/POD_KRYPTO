package pl.kryptografia.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import pl.kryptografia.Des;
import pl.kryptografia.FileDao;
import pl.kryptografia.Messege;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {
    @FXML private Button encodeFx;
    @FXML private Button decodeFx;
    @FXML private Button randomKey;

    @FXML private TextField messageToEncode;
    @FXML private TextField messageToDecode;
    @FXML private TextField genetorKey;

    @FXML private TextField fileSave;
    @FXML private TextField fileEncoded;
    @FXML private TextField file;
    @FXML private TextField fileEncodedToSave;
    @FXML private TextField errorMess;

    @FXML private Button fileToSave;
    @FXML private Button fileChoose1;
    @FXML private Button fileOpen;
    @FXML private Button fileEncodeOpen;

    @FXML private RadioButton radioText;
    @FXML private RadioButton radioFile;


    private final ToggleGroup modeGroup = new ToggleGroup();

    private Des des = new Des();
    private byte[][] encoded;
    private byte[] K;
    private File selectedFile;
    private byte[] encodedFile;
    private byte[] decodedFile;
    @FXML
    public void initialize() {
        radioText.setToggleGroup(modeGroup);
        radioFile.setToggleGroup(modeGroup);
        radioText.setSelected(true);
    }

    @FXML
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

    private byte[] hexStringToByteArray(String s) {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        }
        return data;
    }

    private boolean validateKey() {
        String keyText = genetorKey.getText().trim();

        if (keyText.isEmpty()) {
            showAlert("Brak klucza", "Najpierw wygeneruj klucz.");
            return false;
        }

        //0 - 9, A - F, a - f
        if (!keyText.matches("[0-9A-Fa-f]+")) {
            showAlert("Błędny klucz", "Klucz zawiera niedozwolone znaki.");
            return false;
        }

        if (keyText.length() != 16) {
            showAlert("Błędny klucz", "Klucz DES musi się składać z dokładnie 16 znaków heksadecymalnych.");
            return false;
        }

        this.K = hexStringToByteArray(keyText);
        return true;
    }

    @FXML
    public void encode(ActionEvent event) {
        if(!validateKey()) return;

        if(radioText.isSelected()){
            String message = messageToEncode.getText();
            if (message == null || message.trim().isEmpty()) {
                showAlert("Brak tekstu", "Wpisz tekst do zaszyfrowania.");
                return;
            }
            Messege messege = new Messege(message);
            encoded = messege.encodeMessage(K);
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded[0])
                sb.append(String.format("%02X", b));
            messageToDecode.setText(sb.toString());
        }
        if(radioFile.isSelected()){
            if(selectedFile == null){
                showAlert("Brak pliku", "Wybierz plik do zaszyfrowania.");
                return;
            }
            FileDao dao = new FileDao(selectedFile.getPath());
            try {
                dao.write(K);
                messageToDecode.setText(dao.getOutput().toString() + ": Zaszyfrowano");
                this.encodedFile = dao.getOutput();

                showInfoAlert("Sukces", "Plik został zaszyfrowany.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    public void decode(ActionEvent event) {
        if(!validateKey()) return;

        if(radioText.isSelected()){
            String message = messageToDecode.getText();
            if (message.trim().isEmpty()) {
                showAlert("Brak szyfrogramu", "Pole szyfrogramu jest puste..");
                return;
            }
            Messege mess = new Messege(message);
            String decoded = mess.decodeMessage(encoded, K);
            messageToEncode.setText(decoded);
        }
        if(radioFile.isSelected()){
            if(selectedFile == null){
                showAlert("Brak pliku", "Wybierz plik do odszyfrowania.");
                return;
            }
            FileDao dao = new FileDao(selectedFile.getPath());
            try {
                dao.read(K);
                this.decodedFile = dao.getOutput();

                showInfoAlert("Sukces", "Plik został odszyfrowany.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void generateKey(ActionEvent event) {
        byte[] key = des.randKey();
        this.K = key;
        genetorKey.setText(des.byteToHex(K));
    }

    @FXML
    public void openFileToEncode(ActionEvent event) {
        FileChooser fileChoose = new FileChooser();
        File file1 = fileChoose.showOpenDialog(fileOpen.getScene().getWindow());
        if(file1 == null){
            return;
        }
        file.setText(file1.getPath());
        selectedFile = file1;
    }

    @FXML
    public void fileEncode(ActionEvent event) {
        FileChooser fileChoose = new FileChooser();
        File file1 = fileChoose.showOpenDialog(fileOpen.getScene().getWindow());
        if(file1 == null){
            return;
        }
        fileEncoded.setText(file1.getPath());
        selectedFile = file1;

        if (radioText.isSelected()) {
            try {
                String content = Files.readString(file1.toPath());
                messageToDecode.setText(content);
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się odczytać zawartości pliku szyfrogramu.");
            }
        }
    }

    @FXML
    public void saveDecoded(ActionEvent event) {
        if(radioFile.isSelected()){
            if (decodedFile == null) {
                showAlert("Błąd", "Nie odszyfrowano żadnego pliku.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            File outputFile = fileChooser.showSaveDialog(fileSave.getScene().getWindow());
            if (outputFile == null) {
                return;
            }

            String path = outputFile.getAbsolutePath();

            try {
                Files.write(Path.of(path), decodedFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (radioText.isSelected()) {
            String message = messageToEncode.getText();
            if (message == null || message.trim().isEmpty()) {
                showAlert("Brak tekstu", "Wpisz tekst do odszyfrowania.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            File output = fileChooser.showSaveDialog(fileSave.getScene().getWindow());
            if (output == null) {
                return;
            }

            try {
                Files.writeString(output.toPath(), message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void saveEncoded(ActionEvent event) {
        if(radioFile.isSelected()){
            if (encodedFile == null) {
                showAlert("Błąd", "Nie zaszyfrowano żadnego pliku.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            File outputFile = fileChooser.showSaveDialog(fileEncodedToSave.getScene().getWindow());
            if (outputFile == null) {
                return;
            }

            String path = outputFile.getAbsolutePath();
            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1);
            path = path + "." + extension + "." + "enc";

            try {
                Files.write(Path.of(path), encodedFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (radioText.isSelected()) {
            String message = messageToDecode.getText();
            if (message == null || message.trim().isEmpty()) {
                showAlert("Brak tekstu", "Wpisz tekst do odszyfrowania.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            File output = fileChooser.showSaveDialog(fileSave.getScene().getWindow());
            if (output == null) {
                return;
            }

            try {
                Files.writeString(output.toPath(), message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}