package pl.kryptografia;

public class BitOpertions {
    public byte getBitAt(byte[] array,int postion){
        int posByte = postion / 8 ;// Array widzimy jako ciąg 64 bitów więc musimy sprawdzić w której części znajduje listy znajduje się szukany bit
        int postBit =  postion % 8;// Teraz namierzamy o który bit chodzi
        byte value = array[posByte];
        int valBit = value >> (7 - postBit) & 1;
        return (byte) valBit;
    }

    public void setBitAt(byte[] data, int pos, int val) {
        int posByte = pos / 8;
        int postBit = 7 - (pos % 8);
        if (val == 1) {
            data[posByte] |= (1 << postBit);
        } else {
            data[posByte] &= ~(1 << postBit);
        }
    }
}
