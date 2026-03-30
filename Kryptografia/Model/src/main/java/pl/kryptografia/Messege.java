package pl.kryptografia;

public class Messege {
    private String messege;
    public Messege(String messege) {
        this.messege = messege;
    }

    public String getMessege() {
        return messege;
    }

    public byte[] messegeToBytes() {
        byte[] result = new byte[this.messege.length()];
        for (int i = 0; i < messege.length(); i++) {
            result[i] = (byte) messege.charAt(i);
        }
        return result;
    }

    public byte[][] chunkMessegeToBytes() {
        int paddingLenght = ((this.messege.length() + 8) / 8)*8;
        byte[] padding = new byte[paddingLenght];
        byte[] msgBytes = messege.getBytes();
        //Do wypełniania zerami
        System.arraycopy(msgBytes, 0, padding, 0, msgBytes.length);
        int chunk = paddingLenght / 8;
        byte[][] chunks = new byte[chunk][8];
        for (int i = 0; i < chunk; i++) {
            System.arraycopy(padding, i*8, chunks[i], 0, 8);
        }
        return chunks;
    }

    public byte[][] encodeMessage(byte[] key){
        Des des = new Des();
        byte[][] chunks = chunkMessegeToBytes();
        byte[][] encode = new byte[chunks.length][];
        for (int i = 0; i < chunks.length; i++) {
            encode[i] = des.encode(chunks[i],key);
        }
        return encode;
    }

    public String decodeMessage(byte[][] encoded, byte[] key) {
        StringBuilder sb = new StringBuilder();
        Des des = new Des();
        for (byte[] block : encoded) {
            byte[] decoded = des.decode(block, key);
            sb.append(new String(decoded));
        }
        String result = sb.toString();
        int end = result.indexOf('\0');
        return end == -1 ? result : result.substring(0, end);
    }
}
