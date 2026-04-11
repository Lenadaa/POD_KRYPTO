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
        StringBuilder sb = new StringBuilder();
        for (byte[] block : encoded) {
            for (byte b : block) {
                sb.append(String.format("%02x", b));
            }
        }
        Files.writeString(Path.of(filename), sb.toString());
    }

    public static byte[][] loadEncoded(String filename) throws IOException {
        String text = Files.readString(Path.of(filename));
        int blockCount = text.length() / 16; //8 bajtów = 16 znaków
        byte[][] blocks = new byte[blockCount][8];
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < 8; j++) {
                String byteStr = text.substring(i * 16 + j * 2, i * 16 + j * 2 + 2);
                blocks[i][j] = (byte) Integer.parseInt(byteStr, 16);
            }
        }
        return blocks;
    }
}
