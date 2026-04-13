import org.junit.Assert;
import org.junit.Test;
import pl.kryptografia.Des;
import pl.kryptografia.Messege;

import java.math.BigInteger;
import java.sql.SQLOutput;
import java.util.Random;

public class MessegeTest {
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        return data;
    }
    @Test
    public void debugTest(){
        Messege test = new Messege("Przykład");
        System.out.println(test.getMessege().length());
        byte[] result = test.messegeToBytes();
    }
    @Test
    public void testEncodeDecodeFullMessage() {
        Des des = new Des();
        Messege mes = new Messege("Glorp");
        byte[] K = hexToBytes("133457799BBCDFF1");

        byte[] encoded = mes.encodeMessage(K);
        String decoded = mes.decodeMessage(encoded, K);

        Assert.assertEquals(mes.getMessege(), decoded);
    }

    @Test
    public void testEncodeDecodeMessageWithRandomKey() {
        Des des = new Des();
        Messege mes = new Messege("Glorp");
        byte[] key = des.randKey();
        byte[] encoded = mes.encodeMessage(key);
        String decoded = mes.decodeMessage(encoded, key);
        Assert.assertEquals(mes.getMessege(), decoded);
    }

}
