package pl.kryptografia;

import java.io.File;
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

    public File read() throws IOException {
        return null;
    }

    private byte[] padding(byte[] data){
        int padding = 8 - (data.length % 8);
        if(padding == 0){
            padding = 8;
        }
        byte[] result = new byte[data.length + padding];
        System.arraycopy(data,0,result,0,data.length);

        for(int i = data.length; i<result.length;i++){
            result[i] = (byte) padding;
        }
        return result;
    }
    private byte[] removePadding(byte[] data){
        if(data == null || data.length ==0){
            return data;
        }

        int padding = data[data.length-1] & 0xFF;

        if(padding < 1 || padding > 8 || padding > data.length){
            return data;
        }

        for(int i = data.length - padding;i<data.length;i++){
            if((data[i] & 0xFF) != padding){
                return data;
            }
        }

        byte[] result = new byte[data.length - padding];
        System.arraycopy(data,0,result,0,result.length);
        return result;


    }

    public void write(byte[] key) throws IOException {
        Path input = Path.of(file);
        byte[] data = Files.readAllBytes(input);

        byte[] padded = padding(data);

        byte[] encode = new byte[padded.length];

        for(int i=0;i<padded.length;i+=8){
            byte[] block = new byte[8];
            System.arraycopy(padded,i,block,0,8);

            byte[] encodeBlock = des.encode(block,key);
            System.arraycopy(encodeBlock,0,encode,i,8);
        }
        this.output = encode;
    }

    public void read(byte[] key) throws IOException{
        Path input = Path.of(file);

        byte[] encoded = Files.readAllBytes(input);

        byte[] decoded = new byte[encoded.length];

        for(int i =0;i<encoded.length;i+=8){
            byte[] block = new byte[8];
            System.arraycopy(encoded,i,block,0,8);

            byte[] decodedBlock = des.decode(block,key);
            System.arraycopy(decodedBlock,0,decoded,i,8);
        }

        byte[] removePad = removePadding(decoded);
        this.output = removePad;
    }
    public void outputToFile(byte[] key, String output) throws IOException{
        File file = new File(output);
        file.createNewFile();
        Path path = Path.of(output);
        Files.write(path,this.output);
    }
    public byte[] getOutput(){
        return output;
    }
    public void setOutput(byte[] output){
        this.output = output;
    }
}
