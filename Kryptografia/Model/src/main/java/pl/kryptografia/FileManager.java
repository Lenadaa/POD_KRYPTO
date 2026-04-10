package pl.kryptografia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    public static void saveText(String filename, String text) throws IOException {
        Files.writeString(Path.of(filename), text);
    }

    public static String loadText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    public static void saveEncoded(String filename, byte[][] encoded) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            dos.writeInt(encoded.length);
            for (byte[] block : encoded) {
                dos.write(block);
            }
        }
    }

    public static byte[][] loadEncoded(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int blockCount = dis.readInt();
            byte[][] blocks = new byte[blockCount][8];
            for (int i = 0; i < blockCount; i++) {
                blocks[i] = dis.readNBytes(8);
            }
            return blocks;
        }
    }
}
