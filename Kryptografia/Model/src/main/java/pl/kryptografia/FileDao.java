package pl.kryptografia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDao{
    private final String file;
    private final Des des = new Des();
    private byte[] output;

    public FileDao(String file){
        this.file = file;
    }

    public void write(byte[] key) throws IOException {
        Path input = Path.of(file);
        byte[] data = Files.readAllBytes(input);
        this.output = des.encode(data, key);
    }

    public void read(byte[] key) throws IOException{
        Path input = Path.of(file);
        byte[] encoded = Files.readAllBytes(input);
        this.output = des.decode(encoded, key);
    }

    public byte[] getOutput(){
        return output;
    }
}
