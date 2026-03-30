import org.junit.Assert;
import org.junit.Test;
import pl.kryptografia.BitOpertions;

import java.math.BigInteger;

public class BitOperationsTest {

    @Test
    public void BitOperationsTest() {
        BitOpertions bitOper = new BitOpertions();
        byte[] keys = {1,4,3,2};
        Assert.assertEquals(bitOper.getBitAt(keys,7),1);
        bitOper.setBitAt(keys,7,0);
        Assert.assertEquals(bitOper.getBitAt(keys,7),0);
        bitOper.setBitAt(keys,7,1);
    }

    @Test
    public void testLeftShift() {
        BitOpertions bitOper = new BitOpertions();

        byte[] data = {1};
        byte[] expected = {2};
        Assert.assertArrayEquals(expected, bitOper.shiftLeft(data, 1, 8));

        byte[] data2 = {8};
        byte[] expected2 = {1};
        Assert.assertArrayEquals(expected2, bitOper.shiftLeft(data2, 5, 8));
    }
    @Test
    public void testJoin() {
        BitOpertions bitOper = new BitOpertions();
        byte[] b1 = { (byte)0b11110000 };
        byte[] b2 = { (byte)0b10100000 };
        byte[] result = bitOper.joinBlockOfBits(b1, 4, b2, 4);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals((byte)0xFA, result[0]);
    }

}
