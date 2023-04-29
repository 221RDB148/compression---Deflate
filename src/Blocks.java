import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

//pagaidām programma tikai saspiež 1.blokā (bez lzss) tikai parastu pliku tekstu(piem text.txt) 


public class Blocks {
    static Deque<String> buffer = new LinkedList<>();
    static String filename = "File2.html.lzss";
    // static String filename = "File1.html";
	static int len = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Block0();
        Compress_with_Block1("File3.html.lzss");

        // int length = 258;
        // System.out.println(getLength(length));
    }

    public static void Block0() throws IOException {
        FileInputStream in = new FileInputStream(filename);
        FileOutputStream out = new FileOutputStream(filename + ".bl1");

        Write("001"); //block header



        //bufferī ir palikusas liekās vertibas, lets output them + end Bl (256)
        out.write(Write("0000000")); //256 value Huffman tabulā (1blokam)
        
        in.close();
        out.close();
        System.out.println("(bl0)compressed in file: \""+filename+".bl\"");
    }

    public static void Compress_with_Block1(String fileName) throws IOException, InterruptedException {
        System.out.println("computing block1");
        FileInputStream in = new FileInputStream(fileName);
        FileOutputStream out = new FileOutputStream(fileName + ".bl1");

        int data = 0;
        String ObCode;
        int val = 0;

        Write("001"); //block header

        while((data = in.read()) != -1){
            //check if LITERAL or length
            if((data & 0xFF) == 0){                    //LITERAL
                data = in.read();
                ObCode = getFixedHuffman(data);
                //before output, make sure value is 8-bit
                val = Write(ObCode);
                if(val != -1) out.write(val);
                //before output, make sure value is 8-bit
            }
            else {                                     //LENGTH
                val = (data & 0xFF) << 8;
                val += in.read() - 61440;
                // System.out.print(val+"   ");
                ObCode = getLengthFixedHuffman(val);
                // System.out.print(ObCode + "<"+val+":");
                
                //before output, make sure value is 8-bit
                val = Write(ObCode);
                if(val != -1) out.write(val);
                //before output, make sure value is 8-bit

                //                                     //DISTANCE
                val = (in.read() & 0xFF) << 8;
                val += (in.read() & 0xFF);
                // System.out.println(val+">");
                ObCode = getDISTANCEfixedHuffman(val);

                //before output, make sure value is 8-bit
                val = Write(ObCode);
                if(val != -1) out.write(val);
                //before output, make sure value is 8-bit
                
            }

            // Thread.sleep(10);
        }

        //bufferī ir palikusas liekās vertibas, lets output them + end Bl (256)
        out.write(Write("0000000")); //256 value Huffman tabulā (1blokam)
        
        in.close();
        out.close();
        System.out.println("compressed in file: \""+filename+".bl\"");
        // TODO:
        // - buferī iespējams ir vēl kautkas (to vajag sakt outputot, kad rakstis nakamo bloku)
        // - jauztaisa sistema, kura pievienos lieko bufferi, ja ir pedejais bloks
    }

    public static String getDISTANCEfixedHuffman(int data) {
        String bin = ""; 
        if(data < 5) {
            bin = Integer.toBinaryString(data-1);
            if (bin.length() != 5){
                for (int j = bin.length(); j<5;j++) bin = "0" + bin;
            }
            return bin;
        }

        if(data < 7) return extraB_forDistance(4,1, data,5);
        if(data < 9) return extraB_forDistance(5,1, data,7);
        if(data < 13) return extraB_forDistance(6,2, data,9);
        if(data < 17) return extraB_forDistance(7,2, data,13);
        if(data < 25) return extraB_forDistance(8,3, data,17);
        if(data < 33) return extraB_forDistance(9,3, data,25);
        if(data < 49) return extraB_forDistance(10,4, data,33);
        if(data < 65) return extraB_forDistance(11,4, data,49);
        if(data < 97) return extraB_forDistance(12,5, data,65);
        if(data < 129) return extraB_forDistance(13,5, data,97);
        if(data < 193) return extraB_forDistance(14,6, data,129);
        if(data < 257) return extraB_forDistance(15,6, data,193);
        if(data < 385) return extraB_forDistance(16,7, data,257);
        if(data < 513) return extraB_forDistance(17,7, data,385);
        if(data < 769) return extraB_forDistance(18,8, data,513);
        if(data < 1025) return extraB_forDistance(19,8, data,769);
        if(data < 1537) return extraB_forDistance(20,9, data,1025);
        if(data < 2049) return extraB_forDistance(21,9, data,1537);
        if(data < 3073) return extraB_forDistance(22,10, data,2049);
        if(data < 4097) return extraB_forDistance(23,10, data,3073);
        if(data < 6145) return extraB_forDistance(24,11, data,4097);
        if(data < 8193) return extraB_forDistance(25,11, data,6145);
        if(data < 12289) return extraB_forDistance(26,12, data,8193);
        if(data < 16385) return extraB_forDistance(27,12, data,12289);
        if(data < 24577) return extraB_forDistance(28,13, data,16385);
        if(data < 32768) return extraB_forDistance(29,13, data,24577);
        
        
        System.out.println("ERROR: distance \""+data+"\" not found");
        return "";
    }

    private static String extraB_forDistance(int bin_v,int bin_len, int data, int start){
        String bin = Integer.toBinaryString(bin_v);
        if (bin.length() != 5){
            for (int j = bin.length(); j<5;j++) bin = "0" + bin;
        }
        

        String extra = Integer.toBinaryString(data - start) ;
        if (extra.length() != bin_len){
            for (int j = extra.length(); j<bin_len;j++) extra = "0" + extra;
        }
        
        // System.out.println("distance bin="+bin + " "+extra);
        return bin + extra;
    }

    public static String getLengthFixedHuffman(int data) {
        String extra;
        //length 3 - 10
        if(data < 11){ 
            data += 254;
            return getFixedHuffman(data);
        }
        
        //length 11 - 18
        if(data < 13){//extra bits 1
            int x = - (11 - data);
            extra = Integer.toBinaryString(x);
            return getFixedHuffman(data + 254 - x) + extra;
        }
        if(data < 15){//extra bits 1
            int x = - (13 - data);
            extra = Integer.toBinaryString(x);
            return getFixedHuffman(data + 253 - x) + extra;
        }
        if(data < 17){//extra bits 1
            int x = - (15 - data);
            extra = Integer.toBinaryString(x);
            return getFixedHuffman(data + 252 - x) + extra;
        }
        if(data < 19){//extra bits 1
            int x = - (17 - data);
            extra = Integer.toBinaryString(x);
            return getFixedHuffman(data + 251 - x) + extra;
        }
        
        //length 19 - 35
        if(data < 23){//extra bits 2
            int x = - (19 - data);
            extra = Integer.toBinaryString(x);
            if(x==0 | x==1) extra = "0" + extra;
            return getFixedHuffman(data + 250 - x) + extra;
        }
        if(data < 27){//extra bits 2
            int x = - (23 - data);
            extra = Integer.toBinaryString(x);
            if(x==0 | x==1) extra = "0" + extra;
            return getFixedHuffman(data + 247 - x) + extra;
        }
        if(data < 31){//extra bits 2
            int x = - (27 - data);
            extra = Integer.toBinaryString(x);
              if(x==0 | x==1) extra = "0" + extra;
            return getFixedHuffman(data + 244 - x) + extra;
        }
        if(data < 35){//extra bits 2
            int x = - (31 - data);
            extra = Integer.toBinaryString(x);
            if(x==0 | x==1) extra = "0" + extra;
            return getFixedHuffman(data + 241 - x) + extra;
        }
        
        //length 35 - 66
        if(data < 43) return extra_3bits(43, data);
        if(data < 51) return extra_3bits(51, data);
        if(data < 59) return extra_3bits(59, data);
        if(data < 67) return extra_3bits(67, data);

        //length 67 - 130
        if(data < 83) return extra_4bits(67, data);
        if(data < 99) return extra_4bits(83, data);
        if(data < 115) return extra_4bits(99, data);
        if(data < 131) return extra_4bits(115, data);
        
        //length 131 - 257
        if(data < 163) return extra_5bits(131, data);
        if(data < 195) return extra_5bits(163, data);
        if(data < 227) return extra_5bits(195, data);
        if(data < 258) return extra_5bits(227, data);

        if(data == 258) return getFixedHuffman(285);//length 285

        System.out.println("ERROR: no such length=\""+data+"\"");
        return "";
    }
   
    private static String extra_5bits(int num, int data){
        int inc = (num - 99)/32;
        int x = - (num-32 - data);
        String extra = Integer.toBinaryString(x-32);
        if (extra.length() != 5){//pievieno trukstosas nulles skaitlim
            for (int j = extra.length(); j<5;j++) extra = "0" + extra;
        }
        return getFixedHuffman(data + (312+inc-num) - x) + extra;
    }
    private static String extra_4bits(int num, int data) {
        int inc = (num - 67)/16;
        int x = - (num-16 - data);
        String extra = Integer.toBinaryString(x-16);
        if (extra.length() != 4){//pievieno trukstosas nulles skaitlim
            for (int j = extra.length(); j<4;j++) extra = "0" + extra;
        }
        return getFixedHuffman(data + (293+inc-num) - x) + extra;
    }
    private static String extra_3bits(int num, int data) {
        int inc = (num - 35)/8;
        int x = - (num-8 - data);
        String extra = Integer.toBinaryString(x);
        if (extra.length() != 3){//pievieno trukstosas nulles skaitlim
            for (int j = extra.length(); j<3;j++) extra = "0" + extra;
        }
        return getFixedHuffman(data + (280+inc-num) - x) + extra;
    }

    public static String getFixedHuffman(int data){
        String ObCode;
        if (data < 144){ //(+48)      LITERAL 0 - 143
            ObCode = Integer.toBinaryString(data + 48);

            if (ObCode.length() != 8){//pievieno trukstosas nulles skaitlim
                for (int j = ObCode.length(); j<8;j++) ObCode = "0" + ObCode;
            }
            return ObCode;
        }
        if (data < 256){ //(+256)     LITERAL 144 - 255
            ObCode = Integer.toBinaryString(data + 256);
            if (ObCode.length() != 9){//pievieno trukstosas nulles skaitlim
                for (int j = ObCode.length(); j<9;j++) ObCode = "0" + ObCode;
            }
            return ObCode;
        }
        if (data < 280){ //(-256)      length 256 - 279
            ObCode = Integer.toBinaryString(data - 256);
            if (ObCode.length() != 7){//pievieno trukstosas nulles skaitlim
                for (int j = ObCode.length(); j<7;j++) ObCode = "0" + ObCode;
            }
            return ObCode;
        }
        if (data < 288){ //(-88)      length 280 - 287
            ObCode = Integer.toBinaryString(data - 88);
            if (ObCode.length() != 8){//pievieno trukstosas nulles skaitlim
                for (int j = ObCode.length(); j<8;j++) ObCode = "0" + ObCode;
            }
            return ObCode;
        }
        
        return "";
    }

    public static int Write(String str) {
        //since buffer is LinkedList, you need to track it's length
		len += str.length(); 
		
		for(int i=0; i<str.length(); i++) { //add current number to buffer
			buffer.add(String.valueOf(str.charAt(i)));
		}
        
		if (len > 7) {//you can only write to file 8-bits, so:
			String ans = "";
			for (int i=0; i<8; i++) {
                // System.out.print(buffer.peek() + " ");
				ans += buffer.remove(); //connecting all bits that will 
				len--;
			}
            //returning bin value as integer value
			return Integer.parseInt(ans,2); 
		}
		//you can only write 8-bit value to file, so if not 8-bits, return-1
        return -1; 
	}

}
