package pl.kryptografia;

public class BitOpertions {
    public byte getBitAt(byte[] array,int postion){
        int posByte = postion / 8;
        int posBit = 7 -(postion % 8);
        byte value = array[posByte];
        int valBit = value >> (posBit) & 1;
        return (byte) valBit;
    }

    public void setBitAt(byte[] data, int pos, int value) {
        int byteIdx = pos / 8;
        int bitIdx = 7 - (pos % 8);
        if (value == 1) {
            data[byteIdx] |= (1 << bitIdx);
        } else {
            data[byteIdx] &= ~(1 << bitIdx);
        }
    }

    public byte[] splitBit(byte[] data, int start, int end) {
        int bitCount = end - start;
        int byteCount = (bitCount + 7) / 8;
        byte[] result = new byte[byteCount];
        for (int i = 0; i < bitCount; i++) {
            int value = getBitAt(data, start + i);
            setBitAt(result, i, value);
        }
        return result;
    }

    public byte[] shiftLeft(byte[] data, int shifts, int bitLength) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < bitLength; i++) {
            int srcByteIdx = i / 8;
            int srcBitOffset = 7 - (i % 8);
            int bit = (data[srcByteIdx] >> srcBitOffset) & 1;
            int destPos = (i - shifts);
            while (destPos < 0) destPos += bitLength;
            destPos %= bitLength;

            int destByteIdx = destPos / 8;
            int destBitOffset = 7 - (destPos % 8);
            if (bit == 1) {
                result[destByteIdx] |= (1 << destBitOffset);
            }
        }
        return result;
    }

    public byte[] joinBlockOfBits(byte[] B1, int b1Bits, byte[] B2, int b2Bits) {
        int num = (b1Bits+b2Bits - 1)/8 + 1;
        byte[] result = new byte[num];
        int j = 0;
        for (int i = 0; i < b1Bits; i++) {
            int val = getBitAt(B1,i);
            setBitAt(result,j,val);
            j++;
        }
        for (int i = 0; i < b2Bits; i++) {
            int val = getBitAt(B2,i);
            setBitAt(result,j,val);
            j++;
        }
        return result;
    }
}
