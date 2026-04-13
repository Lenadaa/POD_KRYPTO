package pl.kryptografia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileDao{
    private final String file;
    private final Des des = new Des();
    private byte[] output;

    public FileDao(String file){
        this.file = file;
    }

    public File read() throws IOException {
        return null;
    }

    public void write(byte[] key) throws IOException {
        Path input = Path.of(file);
        byte[] data = Files.readAllBytes(input);
        this.output = encrypteBlocks(data,key);
    }

    public void read(byte[] key) throws IOException{
        Path input = Path.of(file);
        byte[] encoded = Files.readAllBytes(input);
        this.output = decrypteBlocks(encoded,key);
    }
    public void outputToFile(String output) throws IOException{
        File file = new File(output);
        file.createNewFile();
        Path path = Path.of(output);
        Files.write(path,this.output);
    }

    private byte[] encrypteBlocks(byte[] data, byte[] key){
        int blockSize = 8;
        int pad = blockSize - (data.length % blockSize);
        byte[] padded = new byte[data.length + pad];
        System.arraycopy(data,0,padded,0,data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) pad;
        }

        byte[] result = new byte[padded.length];
        for (int i = 0; i < padded.length; i += blockSize) {
            byte[] block = Arrays.copyOfRange(padded, i, i + blockSize);
            byte[] encoded = des.encode(block,key);
            System.arraycopy(encoded, 0, result, i, blockSize);
        }
        return result;
    }

    private byte[] decrypteBlocks(byte[] data, byte[] key){
        int blockSize = 8;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i += blockSize) {
            byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
            byte[] decoded = des.decode(block,key);
            System.arraycopy(decoded, 0, result, i, blockSize);
        }
        int pad = result[result.length - 1] & 0xFF;
        return Arrays.copyOf(result, result.length - pad);
    }

    public byte[] getOutput(){
        return output;
    }
    public void setOutput(byte[] output){
        this.output = output;
    }
}
