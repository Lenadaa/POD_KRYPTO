package pl.kryptografia;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DesTest {

    private Des des;
    private BitOpertions bits;

    private final byte[] STANDARD_KEY = {
            0x13, 0x34, 0x57, 0x79, (byte) 0x9B, (byte) 0xBC, (byte) 0xDF, (byte) 0xF1
    };

    @Before
    public void setUp() {
        des = new Des();
        bits = new BitOpertions();
    }

    @Test
    public void testGetBitAt() {
        byte[] data = { (byte) 0b10110000 };
        assertEquals(1, bits.getBitAt(data, 0));
        assertEquals(0, bits.getBitAt(data, 1));
        assertEquals(1, bits.getBitAt(data, 2));
        assertEquals(1, bits.getBitAt(data, 3));
        assertEquals(0, bits.getBitAt(data, 4));
    }

    @Test
    public void testSetBitAt() {
        byte[] data = new byte[1];
        bits.setBitAt(data, 0, 1);
        assertEquals(1, bits.getBitAt(data, 0));

        bits.setBitAt(data, 0, 0);
        assertEquals(0, bits.getBitAt(data, 0));

        bits.setBitAt(data, 7, 1);
        assertEquals(1, bits.getBitAt(data, 7));
    }

    @Test
    public void testShiftLeft() {
        byte[] data = new byte[4];
        bits.setBitAt(data, 0, 1); // bit 0 = 1, reszta = 0

        byte[] shifted = bits.shiftLeft(data, 1, 28);

        // Shift LEFT: bit z pozycji 0 trafia na pozycję 27 (cyklicznie)
        assertEquals(0, bits.getBitAt(shifted, 0));
        assertEquals(1, bits.getBitAt(shifted, 27));
    }

    @Test
    public void testSBoxS1SingleExample() {
        BitOpertions bits = new BitOpertions();

        byte[] eData = new byte[6];
        bits.setBitAt(eData, 0, 0);
        bits.setBitAt(eData, 1, 1);
        bits.setBitAt(eData, 2, 1);
        bits.setBitAt(eData, 3, 0);
        bits.setBitAt(eData, 4, 1);
        bits.setBitAt(eData, 5, 1);

        byte[] result = des.sBoxs(eData);

        assertEquals(0, bits.getBitAt(result, 0));
        assertEquals(1, bits.getBitAt(result, 1));
        assertEquals(0, bits.getBitAt(result, 2));
        assertEquals(1, bits.getBitAt(result, 3));
    }

    @Test
    public void testSBoxsFullRound() {
        // https://page.math.tu-berlin.de/~kant/teaching/hess/krypto-ws2006/des.htm
        BitOpertions bits = new BitOpertions();

        int[] inputBits = {
                0,1,1,0,0,0,  // B1 → S1
                0,1,0,0,0,1,  // B2 → S2
                0,1,1,1,1,0,  // B3 → S3
                1,1,1,0,1,0,  // B4 → S4
                1,0,0,0,0,1,  // B5 → S5
                1,0,0,1,1,0,  // B6 → S6
                0,1,0,1,0,0,  // B7 → S7
                1,0,0,1,1,1   // B8 → S8
        };

        byte[] eData = new byte[6]; // 48 bitów
        for (int i = 0; i < inputBits.length; i++) {
            bits.setBitAt(eData, i, inputBits[i]);
        }

        byte[] result = des.sBoxs(eData);

        int[] expectedBits = {
                0,1,0,1,  // S1 = 5
                1,1,0,0,  // S2 = 12
                1,0,0,0,  // S3 = 8
                0,0,1,0,  // S4 = 2
                1,0,1,1,  // S5 = 11
                0,1,0,1,  // S6 = 5
                1,0,0,1,  // S7 = 9
                0,1,1,1   // S8 = 7
        };

        for (int i = 0; i < expectedBits.length; i++) {
            System.out.println("bit " + i + " (S-box " + (i/4 + 1) + ", pozycja " + (i%4) + ")");
            assertEquals(expectedBits[i], bits.getBitAt(result, i));
        }
    }
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        return data;
    }

    private String bitsToHex(byte[] bitArray, int numBits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBits; i += 4) {
            int nibble = 0;
            for (int j = 0; j < 4; j++)
                nibble = (nibble << 1) | bits.getBitAt(bitArray, i + j);
            sb.append(Integer.toHexString(nibble).toUpperCase());
        }
        return sb.toString();
    }
    @Test
    public void testIP() {
        byte[] M = hexToBytes("0123456789ABCDEF");
        byte[] ip = des.tranformArray(M, des.getIP());
        // oczekiwane z dokumentacji:
        // 1100 1100 0000 0000 1100 1100 1111 1111
        // 1111 0000 1010 1010 1111 0000 1010 1010
        assertEquals("CC00CCFFF0AAF0AA", bitsToHex(ip, 64));
    }

    @Test
    public void testSubKey1() {
        byte[] K = hexToBytes("133457799BBCDFF1");
        byte[][] keys = des.subKeys(K);
        // K1 = 000110 110000 001011 101111 111111 000111 000001 110010
        assertEquals("1B02EFFC7072", bitsToHex(keys[0], 48));
    }

    @Test
    public void testExpansionR0() {
        byte[] M = hexToBytes("0123456789ABCDEF");
        byte[] ip = des.tranformArray(M, des.getIP());
        byte[] R0 = bits.splitBit(ip, 32, 64);
        byte[] eR0 = des.tranformArray(R0, des.getEXPENSION());
        // E(R0) = 011110 100001 010101 010101 011110 100001 010101 010101
        assertEquals("7A15557A1555", bitsToHex(eR0, 48));
    }

    @Test
    public void testSubKey2() {
        byte[] K = hexToBytes("133457799BBCDFF1");
        byte[][] keys = des.subKeys(K);
        assertEquals("1B02EFFC7072", bitsToHex(keys[0], 48));
    }
    @Test
    public void testSBoxRound1() {
        byte[] K = hexToBytes("133457799BBCDFF1");
        byte[][] keys = des.subKeys(K);
        byte[] M = hexToBytes("0123456789ABCDEF");
        byte[] ip = des.tranformArray(M, des.getIP());
        byte[] R0 = bits.splitBit(ip, 32, 64);
        byte[] xor = des.xorFunction(R0, keys[0]);
        byte[] sboxed = des.sBoxs(xor);
        // 0101 1100 1000 0010 1011 0101 1001 0111
        assertEquals("5C82B597", bitsToHex(sboxed, 32));
    }

    @Test
    public void testFullEncode() {
        byte[] M = hexToBytes("0123456789ABCDEF");
        byte[] K = hexToBytes("133457799BBCDFF1");

        byte[] C = des.encode(M, K);

        assertEquals("85E813540F0AB405", bitsToHex(C, 64));
    }

    @Test
    public void testDecode() {
        byte[] M = hexToBytes("0123456789ABCDEF");
        byte[] K = hexToBytes("133457799BBCDFF1");

        byte[] C = des.encode(M, K);
        byte[] decoded = des.decode(C, K);

        assertEquals("0123456789ABCDEF", bitsToHex(decoded, 64));
    }

    @Test
    public void testEncodeDecodeMessage() {
        byte[] K = hexToBytes("133457799BBCDFF1");

        String message = "Brainrot";
        byte[] M = message.getBytes();

        byte[] C = des.encode(M, K);
        byte[] decoded = des.decode(C, K);

        assertEquals(message, new String(decoded));
    }
}
