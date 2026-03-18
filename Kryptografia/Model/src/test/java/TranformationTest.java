import org.junit.Test;
import pl.kryptografia.Tranformation;

public class TranformationTest {

    @Test
    public void stringToByteTest() {
        Tranformation tranformation = new Tranformation();
        String str = "Hello World";
        System.out.println(tranformation.stringToByte(str));

    }
}
