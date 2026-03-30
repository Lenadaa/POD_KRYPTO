package pl.kryptografia;
import pl.kryptografia.BitOpertions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class Des {
    private BitOpertions bits = new BitOpertions();

    private final byte[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2,  8, 24, 14,
            32, 27,  3,  9,
            19, 13, 30,  6,
            22, 11,  4, 25
    };
    public byte[] getP() {
        return P;
    }
    private final byte[] IP_INV = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41,  9, 49, 17, 57, 25
    };
    public byte[] getI() {
        return IP_INV;
    }
    private final byte[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };
    public byte[] getPC1() {
        return PC1;
    }
    private final byte[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };
    final byte[] shifts = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
    public byte[] getPC2() {
        return PC2;
    }
    private final byte[] IP={
            58,50,42,34,26,18,10,2,
            60,52,44,36,28,20,12,4,
            62,54,46,38,30,22,14,6,
            64,56,48,40,32,24,16,8,
            57,49,41,33,25,17,9,1,
            59,51,43,35,27,19,11,3,
            61,53,45,37,29,21,13,5,
            63,55,47,39,31,23,15,7
    };
    public byte[] getIP() {
        return IP;
    }

    public byte[] randKey(){
        BigInteger bigInteger = new BigInteger(64,1,new Random());
        byte[] k = bigInteger.toByteArray();
        return k;
    }
    public String byteToHex(byte[] num) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : num) {
            hexString.append(Character.forDigit((b >> 4) & 0xF, 16));
            hexString.append(Character.forDigit((b & 0xF), 16));
        }
        return hexString.toString();
    }
    private final byte[] sBox = {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, // S1
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13,
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, // S2
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9,
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, // S3
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12,
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, // S4
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14,
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, // S5
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3,
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, // S6
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13,
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, // S7
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12,
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, // S8
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
    };
    public byte[] getSBoxs() {
        return sBox;
    }

    private byte[] EXPENSION = {
            32, 1,  2,  3,  4,  5,
            4, 5,  6,  7,  8,  9,
            8, 9, 10, 11, 12, 13,
            12,13, 14, 15, 16, 17,
            16,17, 18, 19, 20, 21,
            20,21, 22, 23, 24, 25,
            24,25, 26, 27, 28, 29,
            28,29, 30, 31, 32,  1
    };

    public byte[] getEXPENSION() {
        return EXPENSION;
    }

    public byte[] tranformArray(byte[] array,byte[] blueprint){
        int length = (blueprint.length + 7) / 8;
        byte[] newArray = new byte[length];
        for(int i=0;i<blueprint.length;i++){
            int value = bits.getBitAt(array, blueprint[i]-1);
            bits.setBitAt(newArray,i,value);
        }
        return newArray;
    }

    public byte[][] subKeys(byte[] key){
        byte[][]keys = new byte[16][];
        byte[] transKey = tranformArray(key,PC1);
        byte[] C = bits.splitBit(transKey,0,28);
        byte[] D = bits.splitBit(transKey, 28,56);
        for(int i = 0; i<16; i++){
            C = bits.shiftLeft(C, shifts[i], 28);
            D = bits.shiftLeft(D, shifts[i], 28);
            byte[] cd = bits.joinBlockOfBits(C, 28,D,28);
            keys[i] = tranformArray(cd,PC2);
        }
        return keys;
    }


    public byte[] xorFunction(byte[] R, byte[] key) {
        byte[] rExpand = tranformArray(R, EXPENSION);

        int numBits = 48;
        byte[] xor = new byte[rExpand.length];
        for (int i = 0; i < numBits; i++) {
            int b1 = bits.getBitAt(key, i);
            int b2 = bits.getBitAt(rExpand, i);
            bits.setBitAt(xor, i, b1 ^ b2);
        }
        return xor;
    }

    public byte[] sBoxs(byte[] eData) {
        byte[] result = new byte[4];

        for (int s = 0; s < 8; s++) {
            int startBit = s * 6;
            int b0 = bits.getBitAt(eData, startBit);
            int b1 = bits.getBitAt(eData, startBit + 1);
            int b2 = bits.getBitAt(eData, startBit + 2);
            int b3 = bits.getBitAt(eData, startBit + 3);
            int b4 = bits.getBitAt(eData, startBit + 4);
            int b5 = bits.getBitAt(eData, startBit + 5);
            int row = (b0 << 1) | b5;
            int col = (b1 << 3) | (b2 << 2) | (b3 << 1) | b4;

            int sBoxIndex = s * 64 + row * 16 + col;
            int sBoxValue = sBox[sBoxIndex];

            int outBit = s * 4;
            bits.setBitAt(result, outBit,     (sBoxValue >> 3) & 1);
            bits.setBitAt(result, outBit + 1, (sBoxValue >> 2) & 1);
            bits.setBitAt(result, outBit + 2, (sBoxValue >> 1) & 1);
            bits.setBitAt(result, outBit + 3, (sBoxValue)      & 1);
        }

        return result;
    }

    public byte[] encode(byte[] message, byte[] key) {
        byte[] messegeIP = tranformArray(message, IP);
        byte[][] keys = subKeys(key);

        byte[] L = bits.splitBit(messegeIP, 0, 32);
        byte[] R = bits.splitBit(messegeIP, 32, 64);

        for (int i = 0; i < 16; i++) {
            byte[] previousR = R;
            byte[] expanded  = xorFunction(R, keys[i]);
            byte[] sBoxed    = sBoxs(expanded);
            byte[] fResult   = tranformArray(sBoxed, P);

            byte[] newR = new byte[L.length];
            for (int j = 0; j < 32; j++) {
                int b1 = bits.getBitAt(L, j);
                int b2 = bits.getBitAt(fResult, j);
                bits.setBitAt(newR, j, b1 ^ b2);
            }

            R = newR;
            L = previousR;
        }

        byte[] R16L16 = bits.joinBlockOfBits(R, 32, L, 32);
        return tranformArray(R16L16, IP_INV);
    }
    public byte[] decode(byte[] message, byte[] key) {
        byte[] messegeIP = tranformArray(message, IP);
        byte[][] keys = subKeys(key);

        byte[] L = bits.splitBit(messegeIP, 0, 32);
        byte[] R = bits.splitBit(messegeIP, 32, 64);

        for (int i = 15; i >= 0; i--) {
            byte[] previousR = R;
            byte[] expanded  = xorFunction(R, keys[i]);
            byte[] sBoxed    = sBoxs(expanded);
            byte[] fResult   = tranformArray(sBoxed, P);

            byte[] newR = new byte[L.length];
            for (int j = 0; j < 32; j++) {
                int b1 = bits.getBitAt(L, j);
                int b2 = bits.getBitAt(fResult, j);
                bits.setBitAt(newR, j, b1 ^ b2);
            }

            R = newR;
            L = previousR;
        }

        byte[] R16L16 = bits.joinBlockOfBits(R, 32, L, 32);
        return tranformArray(R16L16, IP_INV);
    }
}
