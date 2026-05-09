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

    public BigInteger[] encrypt(byte[] data) {
        int blockSize = (n.bitLength() / 8) - 2;
        if (blockSize <= 0) blockSize = 1;

        int numOfBlocks = (int) Math.ceil((double) data.length / blockSize);
        BigInteger[] ciphertexts = new BigInteger[numOfBlocks];

        for (int i = 0; i < numOfBlocks; i++) {
            int start = i * blockSize;
            int length = Math.min(blockSize, data.length - start);

            byte[] block = new byte[length + 2];
            System.arraycopy(data, start, block, 0, length);

            byte lastByte = data[start + length - 1];
            block[block.length - 2] = lastByte;
            block[block.length - 1] = lastByte;

            BigInteger m = new BigInteger(1, block);
            ciphertexts[i] = m.modPow(TWO, n);
        }
        return ciphertexts;
    }


    public byte[] decrypt(BigInteger[] ciphertexts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int fullBlockSize = (n.bitLength() / 8);

        BigInteger ep = p.add(BigInteger.ONE).divide(FOUR);
        BigInteger eq = q.add(BigInteger.ONE).divide(FOUR);
        BigInteger yp = p.modInverse(q);
        BigInteger yq = q.modInverse(p);

        for (BigInteger c : ciphertexts) {
            BigInteger mp = c.modPow(ep, p);
            BigInteger mq = c.modPow(eq, q);

            BigInteger r1 = yq.multiply(q).multiply(mp).add(yp.multiply(p).multiply(mq)).mod(n);
            BigInteger r2 = n.subtract(r1);
            BigInteger r3 = yq.multiply(q).multiply(mp).subtract(yp.multiply(p).multiply(mq)).mod(n);
            BigInteger r4 = n.subtract(r3).mod(n);

            BigInteger[] roots = {r1, r2, r3, r4};
            boolean found = false;

            for (BigInteger root : roots) {
                byte[] resBytes = fixBytes(root, fullBlockSize);

                if (padding(resBytes)) {
                    outputStream.write(removePadding(resBytes), 0, removePadding(resBytes).length);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.err.println("Błąd: Nie odnaleziono poprawnego pierwiastka w bloku.");
            }
        }
        return outputStream.toByteArray();
    }

    private byte[] fixBytes(BigInteger root, int len) {
        byte[] raw = root.toByteArray();
        byte[] fixed = new byte[len];

        int srcPos = 0;
        int length = raw.length;

        if (raw.length > 0 && raw[0] == 0) {
            srcPos = 1;
            length--;
        }
        if (length > len) length = len;
        System.arraycopy(raw, srcPos, fixed, len - length, length);

        return fixed;
    }

    private boolean padding(byte[] data) {
        if (data.length < 3) return false;
        int len = data.length;
        return data[len - 1] == data[len - 2] && data[len - 1] == data[len - 3];
    }

    private byte[] removePadding(byte[] data) {
        byte[] result = new byte[data.length - 2];
        System.arraycopy(data, 0, result, 0, result.length);
        return result;
    }
}