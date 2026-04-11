package pl.kryptografia.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import pl.kryptografia.Des;
import pl.kryptografia.FileManager;
import pl.kryptografia.Messege;

import javax.management.DescriptorAccess;
import java.io.File;
import java.util.logging.FileHandler;
import javafx.stage.FileChooser;

public class Controller {
    @FXML
    private Button encodeFx;
    @FXML
    private Button decodeFx;
    @FXML
    private Button randomKey;

    @FXML
    private TextField messageToEncode;
    @FXML
    private TextField messageToDecode;
    @FXML
    private TextField genetorKey;

    @FXML
    private TextField fileSave;
    @FXML
    private TextField fileEncoded;
    @FXML
    private TextField file;
    @FXML
    private TextField fileEncodedToSave;

    @FXML
    private Button fileToSave;
    @FXML
    private Button fileChoose1;
    @FXML
    private Button fileOpen;
    @FXML
    private Button fileEncodeOpen;
    private Des des = new Des();
    private byte[][] encoded;
    private byte[] K;

    @FXML
    public void encode(ActionEvent event) {
        String message = messageToEncode.getText();
        Messege messege = new Messege(message);
        encoded = messege.encodeMessage(K);
        StringBuilder sb = new StringBuilder();
        for (byte b : encoded[0])
            sb.append(String.format("%02X", b));
        messageToDecode.setText(sb.toString());
    }

    @FXML
    public void decode(ActionEvent event) {
        String message = messageToDecode.getText();
        Messege mess = new Messege(message);
        String decoded = mess.decodeMessage(encoded, K);
        messageToEncode.setText(decoded);
    }

    @FXML
    public void generateKey(ActionEvent event) {
        byte[] key = des.randKey();
        this.K = key;
        genetorKey.setText(des.byteToHex(K));
    }

    @FXML
    public void openFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Otwóz plik z tekstem");

        chooser.setInitialFileName(file.getText().trim());

        File selectedFile = chooser.showOpenDialog(file.getScene().getWindow());

        if (selectedFile == null) return;

        file.setText(selectedFile.getAbsolutePath());

        try {
            String text = FileManager.loadText(selectedFile.getAbsolutePath());
            messageToEncode.setText(text);
        } catch (Exception e) {
            messageToEncode.setText("Błąd odczytu: " + e.getMessage());
        }
    }

    @FXML
    public void saveFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz plik z tekstem");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt"));

        chooser.setInitialFileName(fileSave.getText().trim());

        File selectedFile = chooser.showSaveDialog(fileSave.getScene().getWindow());
        if (selectedFile == null) return;

        fileSave.setText(selectedFile.getAbsolutePath());

        try {
            FileManager.saveText(selectedFile.getAbsolutePath(), messageToEncode.getText());
        } catch (Exception e) {
            messageToEncode.setText("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    public void openEncodedFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Otwórz plik z szyfrogramem");

        chooser.setInitialFileName(fileEncoded.getText().trim());

        File selectedFile = chooser.showOpenDialog(fileEncoded.getScene().getWindow());
        if (selectedFile == null) return;

        fileEncoded.setText(selectedFile.getAbsolutePath());

        try {
            encoded = FileManager.loadEncoded(selectedFile.getAbsolutePath());
            StringBuilder sb = new StringBuilder();
            for (byte[] block : encoded)
                for (byte b : block)
                    sb.append(String.format("%02X", b));
            messageToDecode.setText(sb.toString());
        } catch (Exception e) {
            messageToDecode.setText("Błąd odczytu: " + e.getMessage());
        }
    }

    @FXML
    public void saveEncodedFile(ActionEvent event) {
        if (encoded == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz plik z szyfrogramem");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt")
        );

        chooser.setInitialFileName(fileEncodedToSave.getText().trim());

        File selectedFile = chooser.showSaveDialog(fileEncodedToSave.getScene().getWindow());
        if (selectedFile == null) return;

        fileEncodedToSave.setText(selectedFile.getAbsolutePath());

        try {
            FileManager.saveEncoded(selectedFile.getAbsolutePath(), encoded);
        } catch (Exception e) {
            messageToDecode.setText("Błąd zapisu: " + e.getMessage());
        }
    }
}