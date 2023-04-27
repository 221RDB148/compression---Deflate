import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

//pagaidām programma tikai saspiež 1.blokā (bez lzss) tikai parastu pliku tekstu(piem text.txt) 


public class Blocks {
    static Deque<String> buffer = new LinkedList<>();
    static String filename = "text.txt";
    // static String filename = "File1.html";
	static int len = 0;

    public static void main(String[] args) throws IOException {
        Block1();
    }

    public static void Block1() throws IOException {
        FileInputStream in = new FileInputStream(filename);
        FileOutputStream out = new FileOutputStream(filename + ".bl1");

        int data = 0;
        String ObCode;
        int val = 0;

        Write("001"); //block header

        while((data = in.read()) != -1){
            ObCode = getFixedHuffman(data);
            // System.out.println(ObCode + " " + (char)Integer.parseInt(ObCode,2));

            //japarliecinas vai value ir 8-bit, vai -1(null) pirms output:
            val = Write(ObCode);
            if(val != -1) out.write(val);
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
            ObCode = Integer.toBinaryString(data - 256);
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
