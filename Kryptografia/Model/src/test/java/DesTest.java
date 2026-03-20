import org.junit.Assert;
import org.junit.Test;
import pl.kryptografia.Des;

import java.util.Random;

public class DesTest {
    @Test
    public void tranformTest(){
        byte[] PC1 = {
                57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4
        };
        Des des = new Des();
        byte[] data = {5,2,3,1,0,2,3,1,6};
        for(byte b : data){

            System.out.println();
            for(int i=7;i>=0;i--){
                System.out.print((b >> i) & 1);
            }
        }
        System.out.println();
        byte[] newArray = des.tranformArray(data,PC1);
        for(byte b : newArray){
            System.out.println();
            for(int i=7;i>=0;i--){
                System.out.print((b >> i) & 1);
            }
        }

    }

}
