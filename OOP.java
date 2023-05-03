import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

public class OOP {
    static Deque<String> buffer = new LinkedList<>();
    static int len = 0;
    
    public static void main(String[] args) throws IOException {
        // for(int i=3; i<200; i++){
        //     System.out.printf("%3d %s\n",i,getDISTANCEfixedHuffman(i));
        // }
        
        Compress.compressWithBlock1("test.txt.lzss", "output1.txt",0);
        Decompress.decompressWithBlock1("output1.txt", "output2.txt", 0);
        // CompressWithBlock1("test.txt.lzss", "output1.txt",2);

    }
    //int print:    0-dont_print 1-println 2-regular
    //int  kind:    1-charecter 2-length 3-distance
    static void Print(int print, int kind, int printable){

        switch(print){
            case 1:
                switch(kind){
                    case 1: System.out.printf("%-3d(%c)\n",printable,(char)printable);return;
                    case 2: System.out.println("\u001B[33mlength="+printable);return;
                    case 3: System.out.println("distan="+printable+"\033[0m");return;
                }
            case 2:
                switch(kind){
                    case 1:System.out.print((char)printable);return;
                    case 2:System.out.print("\u001B[33m<"+printable+":");return;
                    case 3:System.out.print(printable+">\033[0m");return;
                }
        }
    }
    
    public class Compress {
    
        public static void compressWithBlock1(String input, String output, int print) {
            try{
                DataInputStream in = new DataInputStream(new FileInputStream(input));
                DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
        
                System.out.println("\u001B[33mcompressing file \""+input+"\"\033[0m");
                len = 0;
                buffer.clear();
                int redData, a;
                String outStr = "", bin_code = "";

                while (true) {
                    bin_code = "";
                    //if buffer is too big -> output, begin loop again
                    if(len>15){
                        outStr = "";
                        for(int i=0; i<16; i++){
                            outStr = outStr + buffer.remove();
                            len--;
                        }
                        out.writeShort(Integer.parseInt(outStr,2));
                        continue;
                    }
                    
                    if((redData = in.read()) == -1){break;}

                    if (redData == 0){         //LITERAL
                        redData = (in.read()& 0xFF);
                        if(redData < 144){
                            redData = redData + 48;
                            bin_code = Integer.toBinaryString(redData);
                            if(bin_code.length()!=8) for(int i=bin_code.length(); i<8; i++) bin_code = "0" + bin_code;
                            Print(print,1,redData-48);//Print
                            addToBuffer(bin_code);
                        }
                        else if(redData < 256) {
                            redData = redData + 256;
                            bin_code = Integer.toBinaryString(redData);
                            if(bin_code.length()!=9) for(int i=bin_code.length(); i<9; i++) bin_code = "0" + bin_code;
                            Print(print,1,redData - 256);//Print
                            addToBuffer(bin_code);
                        }
                    }
                    else{                 //length + distance
                        a = 0;
                        if((redData-240) == 1) a = 256;
                        redData = (in.read() & 0xFF) + a;
                        
                        bin_code = getLengthFixedHuffman(redData);
                        Print(print,2,redData);//Print
                        addToBuffer(bin_code);

                        
                        redData = in.readShort() & 0xFFFF;
                        bin_code = getDISTANCEfixedHuffman(redData);
                        Print(print,3,redData);//Print
                        addToBuffer(bin_code);
                    }
                }
                addToBuffer("0000000");
                while(len>7){
                    outStr = "";
                    for(int i=0; i<8; i++) {outStr = outStr + buffer.remove();len--;}
                    out.writeByte(Integer.parseInt(outStr));
                }
                out.writeByte(0);
                outStr = "";
                System.out.println("\u001B[33mCompressed in file \""+output+"\"\033[0m");
                in.close();
                out.close();
            } catch (IOException e){System.out.println(e);}
        }
        
        private static String getDISTANCEfixedHuffman(int data) {
            String bin = ""; 
            if(data < 5) {
                bin = Integer.toBinaryString(data-1);
                if (bin.length() != 5){for (int j = bin.length(); j<5;j++) bin = "0" + bin;}
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
            
            System.out.print("ERROR:Distance_\""+data+"\"_not_found");
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
            // for (int j = 0; j<bin_len;j++) {
            //     if (extra.length() != bin_len){
            //         extra = "0" + extra;
            //     }
            // }
            for(int j=extra.length(); j<bin_len; j++) {extra = "0" + extra;}
            // System.out.println("bin="+(bin + extra));
                
            
            // System.out.print("distance bin="+bin + " "+extra);
            return (bin + extra);
        }
        
        public static void addToBuffer(String str){
            for(int i=0; i<str.length(); i++){
                buffer.add(String.valueOf(str.charAt(i)));
                len++;//track length
            }
        }
        
        private static String getLengthFixedHuffman(int data) {
            String extra;
            //length 3 - 10
            if(data < 11){ 
                data = data - 2;
                extra = Integer.toBinaryString(data);
                for(int i=extra.length(); i<7; i++) extra = "0"+extra;
                return extra;
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
            if(data < 131) {
                extra = Integer.toBinaryString(data - 115);
                for(int i=extra.length(); i<4; i++){extra = "0"+extra;}
                return "11000000" + extra;
            }
            
            //length 131 - 257
            if(data < 163) return extra_5bits(131, data);
            if(data < 195) return extra_5bits(163, data);
            if(data < 227) return extra_5bits(195, data);
            if(data < 258) return extra_5bits(227, data);
    
            if(data == 258) return "11000101";//length 285
    
            System.out.print("!!!!!ERROR:wrong_length=\""+data+"\"");
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
 
        private static String getFixedHuffman(int data){
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

    }

    public class Decompress {

        public static void decompressWithBlock1(String input, String output, int print) {
            try{
                DataInputStream in = new DataInputStream(new FileInputStream(input));
                DataOutputStream out = new DataOutputStream(new FileOutputStream(output));

                System.out.println("\u001B[33mdecompressing file \""+input+"\"\033[0m");
                len = 0;
                buffer.clear();
                int redData = 0;
                String bin_code = "";
        
                while(true){
                    if (len<50){
                        if((redData = in.read()) == -1){if(len<8){break;}
                        } else {
                            addToBuffer8_bitVal(redData);
                            continue;
                        }
                    }
                    bin_code = "";
                    if(len<8){break;}
                    for(int i=0; i<8; i++){ bin_code = bin_code + buffer.remove(); }
                    len-=8;
                    
                    redData = Integer.parseInt(bin_code,2);

                    if(redData > 47 & redData < 192){   //8-bit Literal
                        out.writeShort(redData-48);
                        Print(print, 1, redData-48);//PRINT
                        continue;
                    }
                    if(redData > 199 & redData < 256){  //9-bit Literal
                        bin_code = bin_code + buffer.remove();len--;
                        redData = Integer.parseInt(bin_code,2);
                        out.writeShort(redData-256);
                        Print(print, 1, redData-256);//PRINT
                        continue;
                    }
                
                    if(redData < 48 & redData > -1){                   //7-bit Lengths
                        buffer.addFirst(Integer.toString((redData & 1)));len++;
                        redData = redData >> 1;
                        redData = decodeLength(redData);
                        if(redData == 0){break;}
                        out.writeShort(61440 + redData);
                        Print(print, 2, redData);//PRINT


                        redData = decodeDistance();
                        out.writeShort(redData);
                        //System.out.println(redData);////////////////////////////////
                        Print(print, 3, redData);//PRINT
                        continue;

                    }
                    
                    if(redData > 191 & redData < 200){  //8-bit Lengths
                        redData = decodeLength(redData);
                        
                        out.writeShort(61440 + redData);
                        Print(print, 2, redData);//PRINT
                        

                        redData = decodeDistance();
                        out.writeShort(redData);
                        //System.out.println(redData);////////////////////////////////
                        Print(print, 3, redData);//PRINT
                        continue;
                    
                    }
                }

                in.close();
                out.close();
                System.out.println("\u001B[33mDecompressed in file \""+output+"\"\033[0m");
            } catch (IOException e){System.out.println(e);}
        }

        private static int decodeDistance(){

            String bin_code = "";
            for(int i=0; i<5; i++){bin_code = bin_code + buffer.remove();}
            len-=5;
            int redData = Integer.parseInt(bin_code,2);

            int inc;
            String ex = "";
            switch (redData){
                case 0,1,2,3:
                    return redData+1;
                case 4,5:
                    inc = redData - 4;
                    ex = buffer.remove();
                    len--;
                    redData = 5 + (2*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 6,7://2-bit ex
                    inc = redData - 6;
                    ex = buffer.remove() + buffer.remove();
                    len--;len--;
                    redData = 9 + (4*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 8,9://3-bit ex
                    inc = redData - 8;
                    ex = buffer.remove() + buffer.remove() + buffer.remove();
                    len-=3;
                    redData = 17 + (8*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 10,11://4-bit ex
                    inc = redData - 10;
                    for(int i=0; i<4; i++) ex = ex + buffer.remove();
                    len-=4;
                    redData = 33 + (16*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 12,13://5-bit ex
                    inc = redData - 12;
                    for(int i=0; i<5; i++) ex = ex + buffer.remove();
                    len-=5;
                    redData = 65 + (32*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 14,15://6-bit ex
                    // System.out.print("(14, 15)");
                    inc = redData - 14;
                    for(int i=0; i<6; i++) ex = ex + buffer.remove();
                    len-=6;
                    redData = 129 + (64*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 16,17://7-bit ex
                    inc = redData - 16;
                    for(int i=0; i<7; i++) ex = ex + buffer.remove();
                    len-=7;
                    redData = 257 + (128*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 18,19://8-bit ex
                    inc = redData - 18;
                    for(int i=0; i<8; i++) ex = ex + buffer.remove();
                    len-=8;
                    redData = 513 + (256*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 20,21://9-bit ex
                    inc = redData - 20;
                    for(int i=0; i<9; i++) ex = ex + buffer.remove();
                    len-=9;redData = 1025 + (512*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 22,23://10-bit ex
                    inc = redData - 22;
                    for(int i=0; i<10; i++) ex = ex + buffer.remove();
                    len-=10;
                    redData = 2049 + (1024*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 24,25://11-bit ex
                    inc = redData - 24;
                    for(int i=0; i<11; i++) ex = ex + buffer.remove();
                    len-=11;
                    redData = 4097 + (2048*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 26,27://12-bit ex
                    inc = redData - 26;
                    for(int i=0; i<12; i++) ex = ex + buffer.remove();
                    len-=12;
                    redData = 8193 + (4096*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 28,29://13-bit ex
                    inc = redData - 28;
                    // System.out.print("len="+len+" ");
                    for(int i=0; i<13; i++) ex = ex + buffer.remove();
                    len-=13;
                    redData = 16385 + (8192*inc) + Integer.parseInt(ex,2);
                    return redData;
            }
            return -1;
            
        }
        private static int decodeLength(int redData){
            int inc;
            String ex = "";
            switch(redData){
                case 0:
                    return 0;
                case 1,2,3,4,5,6,7,8:
                    return redData+2;
                case 9,10,11,12:
                    inc = redData - 9;
                    ex = buffer.remove();
                    len--;
                    redData = 11 + (2*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 13,14,15,16:
                    inc = redData - 13;
                    ex = buffer.remove() + buffer.remove();
                    len--;len--;
                    redData = 19 + (4*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 17,18,19,20:
                    inc = redData - 17;
                    ex = buffer.remove() + buffer.remove() + buffer.remove();
                    len-=3;
                    redData = 35 + (8*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 21,22,23:
                    inc = redData - 21;
                    ex = buffer.remove() + buffer.remove() + buffer.remove() + buffer.remove();
                    len-=4;
                    redData = 67 + (16*inc) + Integer.parseInt(ex,2);
                    return redData;
                case 192:
                    ex=buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove();
                    len-=4;
                    redData = 115 + Integer.parseInt(ex,2);
                    return redData;
                case 193,194,195,196:
                    inc = redData - 193;
                    ex=buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove()+buffer.remove();
                    len-=4;
                    redData = 131+(32*inc) + Integer.parseInt(ex,2);
                    // System.out.print((redData)+">error?");
                    return redData;
                case 197:
                    return 258;
            }
            return -1;
        }

        private static void addToBuffer8_bitVal(int a){
            String str = Integer.toBinaryString(a);
            for(int i=str.length(); i<8; i++){
                str = "0" + str;
            }
            for(int i=0; i<8; i++){
                buffer.add(String.valueOf(str.charAt(i)));
                len++;//track length
            }
        }
        
    }

}