package pl.kryptografia;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Rabin {
    private BigInteger n;
    private BigInteger p;
    private BigInteger q;

    private static final SecureRandom random = new SecureRandom();
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);

    public BigInteger getN() { return n; }
    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }

    public void generateKeys(int bitLength) {
        do {
            p = BigInteger.probablePrime(bitLength / 2, random);
        } while (!p.mod(FOUR).equals(THREE));

        do {
            q = BigInteger.probablePrime(bitLength / 2, random);
        } while (!q.mod(FOUR).equals(THREE) || q.equals(p));

        n = p.multiply(q);
    }

    public BigInteger[] encode(byte[] data) {
        int blockSize = (n.bitLength() / 8) - 3;
        if (blockSize <= 0) blockSize = 1;

        int numOfBlocks = (int) Math.ceil((double) data.length / blockSize);
        BigInteger[] ciphertexts = new BigInteger[numOfBlocks];

        for (int i = 0; i < numOfBlocks; i++) {
            int start = i * blockSize;
            int length = Math.min(blockSize, data.length - start);

            byte[] block = new byte[length + 3];
            block[0] = (byte) length; 
            System.arraycopy(data, start, block, 1, length);

            byte lastDataByte = data[start + length - 1];
            block[block.length - 2] = lastDataByte;
            block[block.length - 1] = lastDataByte;

            BigInteger m = new BigInteger(1, block);
            ciphertexts[i] = m.modPow(TWO, n);
        }
        return ciphertexts;
    }

    public byte[] decode(BigInteger[] encrypted) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int fullBlockSize = (n.bitLength() / 8);

        BigInteger ep = p.add(BigInteger.ONE).divide(FOUR);
        BigInteger eq = q.add(BigInteger.ONE).divide(FOUR);
        BigInteger yp = p.modInverse(q);
        BigInteger yq = q.modInverse(p);

        for (BigInteger c : encrypted) {
            BigInteger mp = c.modPow(ep, p);
            BigInteger mq = c.modPow(eq, q);

            BigInteger r1 = yq.multiply(q).multiply(mp).add(yp.multiply(p).multiply(mq)).mod(n);
            BigInteger r2 = n.subtract(r1);
            BigInteger r3 = yq.multiply(q).multiply(mp).subtract(yp.multiply(p).multiply(mq)).mod(n).add(n).mod(n);
            BigInteger r4 = n.subtract(r3).mod(n);

            BigInteger[] roots = {r1, r2, r3, r4};
            boolean found = false;

            for (BigInteger root : roots) {
                byte[] resBytes = fixBytes(root, fullBlockSize);

                if (checkRed(resBytes)) {
                    byte[] data = extractData(resBytes);
                    if (data != null) {
                        outputStream.write(data, 0, data.length);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                throw new RuntimeException("Błąd deszyfrowania: nie znaleziono poprawnego pierwiastka dla bloku.");
            }
        }
        return outputStream.toByteArray();
    }

    private boolean checkRed(byte[] block) {
        if (block.length < 3) return false;
        int len = block.length;
        return block[len - 1] == block[len - 2];
    }

    private byte[] extractData(byte[] block) {
        int startPos = 0;
        while (startPos < block.length && block[startPos] == 0) {
            startPos++;
        }

        if (startPos >= block.length) return null;

        int dataLength = block[startPos] & 0xFF;
        if (dataLength <= 0 || startPos + 1 + dataLength > block.length) return null;

        byte lastByte = block[startPos + dataLength];
        if (block[startPos + dataLength + 1] != lastByte || block[startPos + dataLength + 2] != lastByte) {
            return null;
        }

        byte[] data = new byte[dataLength];
        System.arraycopy(block, startPos + 1, data, 0, dataLength);
        return data;
    }

    private byte[] fixBytes(BigInteger root, int len) {
        byte[] raw = root.toByteArray();
        byte[] fixed = new byte[len];
        int length = Math.min(raw.length, len);
        int srcPos = Math.max(0, raw.length - len);

        if (raw.length > len && raw[0] == 0) {
            srcPos = 1;
            length = len;
        }

        System.arraycopy(raw, srcPos, fixed, len - length, length);
        return fixed;
    }
}