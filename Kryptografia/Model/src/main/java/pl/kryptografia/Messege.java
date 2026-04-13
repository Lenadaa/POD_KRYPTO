package pl.kryptografia;

import java.nio.charset.StandardCharsets;

public class Messege {
    private String messege;
    public Messege(String messege) {
        this.messege = messege;
    }

    public String getMessege() {
        return messege;
    }

    public byte[] messegeToBytes() {
        byte[] result = messege.getBytes(StandardCharsets.UTF_8);
        return result;
    }

    public byte[] encodeMessage(byte[] key){
        Des des = new Des();
        byte[] encode = des.encode(messegeToBytes(),key);
        return encode;
    }

    public String decodeMessage(byte[] encoded, byte[] key) {
        Des des = new Des();
        byte[] decode = des.decode(encoded,key);
        return new String(decode,StandardCharsets.UTF_8);
    }
}
