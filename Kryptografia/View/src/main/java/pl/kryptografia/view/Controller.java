package pl.kryptografia.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import pl.kryptografia.Des;
import pl.kryptografia.FileDao;
import pl.kryptografia.FileManager;
import pl.kryptografia.Messege;

import java.io.File;
import java.io.IOException;

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

    @FXML private File selectedFile1;
    @FXML private File selectedEncodedFile;

    private final ToggleGroup modeGroup = new ToggleGroup();

    private Des des = new Des();
    private byte[][] encoded;
    private byte[] K;
    private File selectedFile;

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

    private boolean validateKey() {
        if (K == null || K.length == 0) {
            showAlert("Brak klucza", "Najpierw wygeneruj klucz.");
            return false;
        }
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
            if(selectedEncodedFile == null){
                showAlert("Brak pliku", "Wybierz plik do odszyfrowania.");
                return;
            }
            FileDao dao = new FileDao(selectedEncodedFile.getPath());
            try {
                dao.read(K);
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
    public void openFile(ActionEvent event) {
        FileChooser fileChoose = new FileChooser();
        File file1 = fileChoose.showOpenDialog(fileOpen.getScene().getWindow());
        if(file1 == null){
            return;
        }
        file.setText(file1.getPath());
        selectedFile = file1;
    }
    @FXML
    public void openFileDecode(ActionEvent event) {
        FileChooser fileChoose = new FileChooser();
        fileChoose.setTitle("Wybierz plik do odszyfrowania");
        File file1 = fileChoose.showOpenDialog(fileOpen.getScene().getWindow());
        if(file1 == null){
            return;
        }
        selectedEncodedFile = file1;
        fileEncoded.setText(file1.getPath());

        if(radioText.isSelected()){
            try {
                encoded = FileManager.loadEncoded(file1.getPath());
                StringBuilder sb = new StringBuilder();
                for (byte[] block : encoded)
                    for (byte b : block)
                        sb.append(String.format("%02X", b));
                messageToDecode.setText(sb.toString());
            } catch (Exception e) {
                messageToDecode.setText("Błąd odczytu: " + e.getMessage());
            }
        }
    }

    @FXML
    public void saveFile(ActionEvent event) {
        String text = messageToEncode.getText();
        if (text == null || text.trim().isEmpty()) {
            showAlert("Brak tekstu", "Brak tekstu do zapisania.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        File filename = fileChooser.showSaveDialog(fileSave.getScene().getWindow());
        if (filename == null) return;
        try {
            FileManager.saveText(filename.getPath(), text);
            fileSave.setText(filename.getPath());
        } catch (Exception e) {
            messageToEncode.setText("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    public void saveEncodedFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik z szyfrogramem");
        File filename = fileChooser.showSaveDialog(fileEncodeOpen.getScene().getWindow());
        if (filename == null || encoded == null) return;
        try {
            FileManager.saveEncoded(filename.getPath(), encoded);
            fileEncodedToSave.setText(filename.getPath());
        } catch (Exception e) {
            messageToDecode.setText("Błąd zapisu: " + e.getMessage());
        }
    }
}