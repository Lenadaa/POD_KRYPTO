package pl.kryptografia;

public class Main {
    public static void main(String[] args) {
        final byte[] key = {1};
        for(byte b : key){
            for(int i=0;i<8;i++){
                System.out.print((b>>i)&1);
            }
        }
    }
}
