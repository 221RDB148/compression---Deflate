import java.util.*;

public class Huff {
    // A  000
    // B  000
    // C  000
    // D  000
    // E  000
    // F  00
    // G  0000
    // H  0000

    //  A B C D E  F  G H
    //[ 3 3 3 3 3  2  4 4 ]
    public static void main(String[] args){
        //                      (285)         
        // int[] bl_count = new int[15];
        // bl_count[2] = 1;
        // bl_count[3] = 5;
        // bl_count[8] = 2;
        //                0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 (all code lengths)
        // int[] bl_count = {0,0,1,5,2,0,0,0,0,0, 0, 0, 0, 0, 0, 0};
        int[] bl_count = {0,1,5,2};
        int MAX_BITS = bl_count.length;
        int[] next_code = new int[MAX_BITS];
        bl_count[0] = 0;
        

        int code = 0;
        for(int bit_len=1; bit_len < MAX_BITS; bit_len++){
            code = (code + bl_count[bit_len-1]) << 1;
            next_code[bit_len] = code;
        }
        System.out.println(Arrays.toString(next_code));

        int len;
        for(int n=0; n <=14; n++){
            // len = 
        }
    }
}
