import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

//pagaidām programma tikai saspiež 1.blokā (bez lzss) tikai parastu pliku tekstu(piem text.txt) 


public class Blocks {
    static Deque<String> buffer = new LinkedList<>();
    // static String filename = "File3.html.lzss";
    static String filename = "compress.txt.lzss";
    static String Input_name = "File1.html.lzss";
    static String Output_name = "output1.txt";
	static int len = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        // System.out.println(extraB_forDistance(6,8,9,513));
        Compress_using_Block1(Input_name);
        
        // Decompress_using_Block1(filename + ".lzss.bl1");
        // Decompress_using_Block1("test.txt");

        // int length = 258;
        // System.out.print(getLength(length));
    }

    public static void Decompress_using_Block1(String fileName) {
        System.out.print("decoding with block1 ...\n");
        try (DataInputStream in = new DataInputStream(new FileInputStream("test.txt"))) {
            DataOutputStream out = new DataOutputStream(new FileOutputStream("output1.lzss"));
            buffer.clear();
            String READ;
            String Bcode;
            int Bcode_value;
            int data = 0;

            // //BLOCK HEADER
            // Bcode = Integer.toBinaryString(in.read() & 0xFF);
            // if (Bcode.length() != 8){//pievieno trukstosas nulles skaitlim
            //     for (int j = Bcode.length(); j<8;j++) Bcode = "0" + Bcode;
            // }
            // for(int i=0; i<8; i++) { //add current number to buffer
            //     buffer.add(String.valueOf(Bcode.charAt(i)));
            //     len++;
            // }
            // for(int i=0; i<3; i++){
            //     if(buffer.isEmpty()) break;
            //     System.out.print(buffer.remove());  
            //     len--;
            // }

            READ = Integer.toBinaryString(in.readByte() & 0xFF); //read charecter from file
            if (READ.length() != 8){//add zeros to binary for length to be 8-bit
                for (int j = READ.length(); j<8;j++) READ = "0" + READ;
            }
            for(int i=0; i<READ.length(); i++) { //add current number to buffer
                buffer.add(String.valueOf(READ.charAt(i)));
                len++;
            }
            while((data = in.readByte()) != -1){

                READ = Integer.toBinaryString(data & 0xFF); //read charecter from file
                if (READ.length() != 8){//add zeros to binary for length to be 8-bit
                    for (int j = READ.length(); j<8;j++) READ = "0" + READ;
                }
                for(int i=0; i<READ.length(); i++) { //add current number to buffer
                    buffer.add(String.valueOf(READ.charAt(i)));
                    len++;
                }

                //check current charecter
                Bcode = "";
                for(int i=0; i<8; i++) Bcode = Bcode + buffer.remove(); len--;
                Bcode_value = Integer.parseInt(Bcode,2);
                // System.out.println(Bcode_value);
                // System.out.print("bcode="+Bcode+" value="+Bcode_value);
                System.out.print("     real="+READ+"   ");

                // outputCode = getDecodingValue(Bcode_value);


                if(Bcode_value < 48){ // 7-bit LENGTH
                    buffer.addFirst(Character.toString(Bcode.charAt(7)));
                    len++;
                    Bcode = Bcode.substring(0,7);
                    Bcode_value = Integer.parseInt(Bcode,2);
                    String extra;
                   
                    int inc;
                    switch (Bcode_value){
                        case 0:
                            break;
                        case 1,2,3,4,5,6,7,8:
                            System.out.print("<"+(Bcode_value+2)+":");
                            out.writeShort(61440 + Bcode_value+2);
                            break;
                        case 9,10,11,12:
                            inc = Bcode_value - 9;
                            Bcode_value = 11+(2*inc) + Integer.parseInt(buffer.remove(),2);
                            len--;
                            System.out.print("<"+Bcode_value+":");
                            out.writeShort(61440 + Bcode_value);
                            break;
                        case 13,14,15,16:
                            inc = Bcode_value - 13;
                            extra = buffer.remove() + buffer.remove();
                            len-=2;
                            Bcode_value = 19+(4*inc) + Integer.parseInt(extra,2);
                            System.out.print("<"+Bcode_value+":");
                            out.writeShort(61440 + Bcode_value);
                            break;
                        case 17,18,19,20:
                            inc = Bcode_value - 17;
                            extra = buffer.remove() + buffer.remove() + buffer.remove();
                            len-=3;
                            Bcode_value = 35+(8*inc) + Integer.parseInt(extra,2);
                            System.out.print("<"+Bcode_value+":");
                            out.writeShort(61440 + Bcode_value);
                            break;
                        case 31,32,33:
                            inc = Bcode_value - 31;
                            extra=buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove();
                            len-=4;
                            Bcode_value = 67+(16*inc) + Integer.parseInt(extra,2);
                            System.out.print("<"+Bcode_value+":");
                            out.writeShort(61440 + Bcode_value);
                            break;
                    }

                    //distance
                    READ = Integer.toBinaryString(in.read() & 0xFF); //read charecter from file
                    if (READ.length() != 8){//add zeros to binary for length to be 8-bit
                        for (int j = READ.length(); j<8;j++) READ = "0" + READ;
                    }          
                    for(int i=0; i<8; i++) { //add current number to buffer
                        buffer.add(String.valueOf(READ.charAt(i)));
                        len++;
                    }
                    
                    Bcode = "";
                    for(int i=0; i<5; i++) Bcode = Bcode + buffer.remove();len--;
                    Bcode_value = Integer.parseInt(Bcode,2);
                    extra = "";

                    //distance
                    out.writeShort(FixedHuffmanToDistance(Bcode_value));
                    continue;
                }
                
                if(Bcode_value < 192){// 8-bit LITERAL
                    System.out.println("ch="+(char)(Bcode_value - 48));
                    int value = Bcode_value - 48;
                    out.writeShort(value);
                    continue;
                }

                if(Bcode_value < 199){
                    int inc;
                    String extra;
                    Bcode_value = Bcode_value - 144;
                    //length
                    switch(Bcode_value){
                        case 192:
                            extra=buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove();
                            len-=4;
                            Bcode_value = 115 + Integer.parseInt(extra,2);
                            System.out.print((Bcode_value)+">");
                            out.writeShort(61440 + Bcode_value);
                            break;
                        case 193,194,195,196:
                            inc = Bcode_value - 193;
                            extra=buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove();
                            len-=4;
                            Bcode_value = 131+(32*inc) + Integer.parseInt(extra,2);
                            System.out.print((Bcode_value)+">");
                            out.writeShort(61440 + Bcode_value);
                            break;
                        case 197:
                            out.writeShort(258);
                            System.out.print(258+">  (longest there is)");
                            break;
                    }
                    //distance
                    Bcode = "";
                    for(int i=0; i<5; i++) Bcode = Bcode + buffer.remove(); len--;
                    Bcode_value = Integer.parseInt(Bcode,2);
                    extra = "";
                    out.writeShort(FixedHuffmanToDistance(Bcode_value));
                    continue;
                }
            
                if(Bcode_value < 256){// 9-bit LITERAL
                    READ = buffer.remove();
                    len--;
                    Bcode = Bcode + READ;
                    Bcode_value = Integer.parseInt(Bcode,2);
                    Bcode_value = Bcode_value - 256;
                    System.out.println("\nERROR HERE:"+Bcode_value+"\n");
                    out.writeShort(Bcode_value);

                }
            }

            in.close();
            out.close();
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print("\ndecompressed ...");
        System.out.println("dump buffer:");

        
        print();

        while(!buffer.isEmpty()){
            String str = "";
            for(int i=0; i<8; i++){
                str = str + buffer.remove();
            }
            for(int i=str.length(); i<8; i++){
                str = "0" + str;
            }
            System.out.println("char " + Integer.parseInt(str,2) + " "+ (char)Integer.parseInt(str,2));
            
        }
    }

    public static void Block0() throws IOException {
        FileInputStream in = new FileInputStream(filename);
        FileOutputStream out = new FileOutputStream(filename + ".bl1");

        Write("001"); //block header



        //bufferī ir palikusas liekās vertibas, lets output them + end Bl (256)
        out.write(Write("0000000")); //256 value Huffman tabulā (1blokam)
        
        in.close();
        out.close();
        System.out.print("(bl0)compressed in file: \""+filename+".bl\"");
    }

    public static void print(){
        Iterator<String> iterator = buffer.iterator();
        System.out.println();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println();
    }

    public static void Compress_using_Block1(String fileName) throws IOException, InterruptedException {
        buffer.clear();
        System.out.println("encoding with block1 ...");
        FileInputStream in = new FileInputStream("text.");
        FileOutputStream out = new FileOutputStream("text.txt.lzss.bl1");

        int data = 0;
        String ObCode;
        int val = 0;

        // Write("001"); //block header

        while((data = in.read()) != -1){
            //check if LITERAL or length
            if((data & 0xFF) == 0){                    //LITERAL
                data = in.read();
                ObCode = getFixedHuffman(data);
                System.out.print((char)data);
                //before output, make sure value is 8-bit
                val = Write(ObCode);
                if(val != -1) out.write(val);
                //before output, make sure value is 8-bit
            }
            else {                                     //LENGTH
                val = (data & 0xFF) << 8;
                val += in.read() - 61440;
                ObCode = getLengthFixedHuffman(val);
                System.out.print("<"+val+":");
                
                //before output, make sure value is 8-bit
                val = Write(ObCode);
                if(val != -1) out.write(val);
                //before output, make sure value is 8-bit

                //                                     //DISTANCE
                val = (in.read() & 0xFF) << 8;
                val += (in.read() & 0xFF);
                System.out.print(val+">");
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

        System.out.print("\ncompressed(still stuff in buffer"+len+")\n");
        
        while (len > 8){
            String buffString = "";
            for(int i=0; i<8; i++){
                buffString = buffString + buffer.remove();
                len--;
            }
            System.out.println((char)Integer.parseInt(buffString,2));
        }
        // TODO:
        // - buferī iespējams ir vēl kautkas (to vajag sakt outputot, kad rakstis nakamo bloku)
        // - jauztaisa sistema, kura pievienos lieko bufferi, ja ir pedejais bloks
    }

    public static String getDISTANCEfixedHuffman(int data) {
        String bin = ""; 
        if(data < 5) {
            bin = Integer.toBinaryString(data);

            int a = Integer.parseInt(bin) - 1;
            bin = Integer.toBinaryString(a);
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
        if(data < 32769) return extraB_forDistance(29,13, data,24577);
        
        
        System.out.print("ERROR: distance \""+data+"\" not found");
        return "";
    }

    private static String extraB_forDistance(int bin_v,int bin_len, int data, int start){
        String bin = Integer.toBinaryString(bin_v);

        for (int j = 0; j<5;j++) {
            if (bin.length() != 5){
                bin = "0" + bin;
            }
        }
        String extra = Integer.toBinaryString(data - start);
        for (int j = 0; j<bin_len;j++) {
            if (extra.length() != bin_len){
                extra = "0" + extra;
            }
        }
            
        
        // System.out.print("distance bin="+bin + " "+extra);
        return (bin + extra);
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

        System.out.print("ERROR: no such length=\""+data+"\"");
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
    public static int FixedHuffmanToDistance(int Bcode_value){
        String extra = "";
        int inc;

        switch (Bcode_value){
            case 1,2,3:
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 4,5://1-bit extra
                inc = Bcode_value - 4;
                extra = buffer.remove();
                len--;
                Bcode_value = 5 + (2*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 6,7://2-bit extra
                inc = Bcode_value - 6;
                extra = buffer.remove() + buffer.remove();
                len--;len--;
                Bcode_value = 9 + (4*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 8,9://3-bit extra
                inc = Bcode_value - 8;
                extra = buffer.remove() + buffer.remove() + buffer.remove();
                len-=3;
                Bcode_value = 17 + (8*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 10,11://4-bit extra
                inc = Bcode_value - 10;
                for(int i=0; i<4; i++) extra = extra + buffer.remove();
                len-=4;
                Bcode_value = 33 + (16*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 12,13://5-bit extra
                inc = Bcode_value - 12;
                for(int i=0; i<5; i++) extra = extra + buffer.remove();
                len-=5;
                Bcode_value = 65 + (32*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 14,15://6-bit extra
                inc = Bcode_value - 14;
                for(int i=0; i<6; i++) extra = extra + buffer.remove();
                len-=6;
                Bcode_value = 129 + (64*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 16,17://7-bit extra
                inc = Bcode_value - 16;
                for(int i=0; i<7; i++) extra = extra + buffer.remove();
                len-=7;
                Bcode_value = 257 + (128*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 18,19://8-bit extra
                inc = Bcode_value - 18;
                for(int i=0; i<8; i++) extra = extra + buffer.remove();
                len-=8;
                Bcode_value = 257 + (256*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 20,21://9-bit extra
                inc = Bcode_value - 20;
                for(int i=0; i<9; i++) extra = extra + buffer.remove();
                len-=9;Bcode_value = 1025 + (512*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 22,23://10-bit extra
                inc = Bcode_value - 22;
                for(int i=0; i<10; i++) extra = extra + buffer.remove();
                len-=10;
                Bcode_value = 2049 + (1024*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 24,25://11-bit extra
                inc = Bcode_value - 24;
                for(int i=0; i<11; i++) extra = extra + buffer.remove();
                len-=11;
                Bcode_value = 4897 + (2048*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 26,27://12-bit extra
                inc = Bcode_value - 26;
                for(int i=0; i<12; i++) extra = extra + buffer.remove();
                len-=12;
                Bcode_value = 8193 + (4096*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
            case 28,29://13-bit extra
                inc = Bcode_value - 28;
                for(int i=0; i<13; i++) extra = extra + buffer.remove();
                len-=13;
                Bcode_value = 16385 + (8192*inc) + Integer.parseInt(extra,2);
                System.out.print((Bcode_value)+">");
                return (Bcode_value);
        }
        
        // switch (Bcode_value){
        //     case 1,2,3,4:
        //         System.out.print((Bcode_value+1)+">");
        //         return (Bcode_value+1);
        //     case 5,6://1-bit extra
        //         inc = Bcode_value - 4;
        //         extra = buffer.remove();
        //         Bcode_value = 5 + (2*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 7,8://2-bit extra
        //         inc = Bcode_value - 6;
        //         extra = buffer.remove() + buffer.remove();
        //         Bcode_value = 9 + (4*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 9,10://3-bit extra
        //         inc = Bcode_value - 8;
        //         extra = buffer.remove() + buffer.remove() + buffer.remove();
        //         Bcode_value = 17 + (8*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 11,12://4-bit extra
        //         inc = Bcode_value - 10;
        //         for(int i=0; i<4; i++) extra = extra + buffer.remove();
        //         Bcode_value = 33 + (16*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 13,14://5-bit extra
        //         inc = Bcode_value - 12;
        //         for(int i=0; i<5; i++) extra = extra + buffer.remove();
        //         Bcode_value = 65 + (32*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 15,16://6-bit extra
        //         inc = Bcode_value - 14;
        //         for(int i=0; i<6; i++) extra = extra + buffer.remove();
        //         Bcode_value = 129 + (64*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 17,18://7-bit extra
        //         inc = Bcode_value - 16;
        //         for(int i=0; i<7; i++) extra = extra + buffer.remove();
        //         Bcode_value = 257 + (128*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 19,20://8-bit extra
        //         inc = Bcode_value - 18;
        //         for(int i=0; i<8; i++) extra = extra + buffer.remove();
        //         Bcode_value = 257 + (256*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 21,22://9-bit extra
        //         inc = Bcode_value - 20;
        //         for(int i=0; i<9; i++) extra = extra + buffer.remove();
        //         Bcode_value = 1025 + (512*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 23,24://10-bit extra
        //         inc = Bcode_value - 22;
        //         for(int i=0; i<10; i++) extra = extra + buffer.remove();
        //         Bcode_value = 2049 + (1024*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 25,26://11-bit extra
        //         inc = Bcode_value - 24;
        //         for(int i=0; i<11; i++) extra = extra + buffer.remove();
        //         Bcode_value = 4897 + (2048*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 27,28://12-bit extra
        //         inc = Bcode_value - 26;
        //         for(int i=0; i<12; i++) extra = extra + buffer.remove();
        //         Bcode_value = 8193 + (4096*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        //     case 29,30://13-bit extra
        //         inc = Bcode_value - 28;
        //         for(int i=0; i<13; i++) extra = extra + buffer.remove();
        //         Bcode_value = 16385 + (8192*inc) + Integer.parseInt(extra,2);
        //         System.out.print((Bcode_value)+">");
        //         return (Bcode_value);
        // }
        
        System.err.print("ERROR DISTANCE WRONG="+Bcode_value);
        return -1;
    }

}
