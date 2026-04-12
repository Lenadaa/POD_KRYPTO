package pl.kryptografia.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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


    private Des des = new Des();
    private byte[][] encoded;
    private byte[] K;
    private File selectedFile;
    @FXML
    public void encode(ActionEvent event) {
        if(radioText.isSelected()){
            String message = messageToEncode.getText();
            Messege messege = new Messege(message);
            encoded = messege.encodeMessage(K);
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded[0])
                sb.append(String.format("%02X", b));
            messageToDecode.setText(sb.toString());
        }
        if(radioFile.isSelected()){
            if(selectedFile == null){
                errorMess.setText("Nie wybrano pliku");
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
        if(radioText.isSelected()){
            String message = messageToDecode.getText();
            Messege mess = new Messege(message);
            String decoded = mess.decodeMessage(encoded, K);
            messageToEncode.setText(decoded);
        }
        if(radioFile.isSelected()){
            if(selectedFile == null){
                errorMess.setText("Nie wybrano pliku");
            }
            FileDao dao = new FileDao(selectedFile.getPath());
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
        file.setText(file1.getPath());
        if(file1 == null){
            return;
        }
        selectedFile = file1;
    }
    @FXML
    public void openFileDecode(ActionEvent event) {
        FileChooser fileChoose = new FileChooser();
        File file1 = fileChoose.showOpenDialog(fileOpen.getScene().getWindow());
        fileEncoded.setText(file1.getPath());
        if(file1 == null){
            return;
        }
        selectedFile = file1;
    }


    @FXML
    public void saveFile(ActionEvent event) {
        String filename = fileSave.getText().trim();
        if (filename.isEmpty()) return;
        try {
            FileManager.saveText(filename, messageToEncode.getText());
        } catch (Exception e) {
            messageToEncode.setText("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    public void openEncodedFile(ActionEvent event) {
        String filename = fileEncoded.getText().trim();
        if (filename.isEmpty()) return;
        try {
            encoded = FileManager.loadEncoded(filename);
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
        String filename = fileEncodedToSave.getText().trim();
        if (filename.isEmpty() || encoded == null) return;
        try {
            FileManager.saveEncoded(filename, encoded);
        } catch (Exception e) {
            messageToDecode.setText("Błąd zapisu: " + e.getMessage());
        }
    }
}