package pl.kryptografia.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import pl.kryptografia.Des;
import pl.kryptografia.Messege;

import javax.management.DescriptorAccess;

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

    @FXML private Button fileToSave;
    @FXML private Button fileChoose1;
    @FXML private Button fileOpen;
    @FXML private Button fileEncodeOpen;
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
}