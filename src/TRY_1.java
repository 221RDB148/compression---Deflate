import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TRY_1 {
    public static void main(String[] args) throws IOException{
        String text = "compress this, then decompress";
        // String text = "<!DOCTYPE html>\n<html class=\"client-nojs\"";
        String decoded_text = "";
        
        //binary code output
        String FULL_binary = ""; 
        //binary code output for previewing (has spaces between symbols)
        String EZ_binary = ""; 

        int dec;
        //ENcoding
        System.out.println("encoding:");
        for (int i=0; i<text.length(); i++){
            dec = (int)text.charAt(i);
            System.out.printf("%3d %9s ( %c )\n",dec, getBin(dec), text.charAt(i));
            FULL_binary = FULL_binary + getBin(dec);
            EZ_binary = EZ_binary + getBin(dec) + " ";
        }

        System.out.println(EZ_binary);

        //DEcoding
        System.out.println("\ndecoding:");
        String[] st = EZ_binary.split(" ");
        for(int i=0; i<st.length; i++){
            decoded_text += getSym(st[i]);
        }
        // System.out.println(text);
        // System.out.println(decoded_text);
        /////////////////////////
        
        //input file
        BufferedReader bf = new BufferedReader(new FileReader("text.txt"));
        String line;
        System.out.println("\n");
        while ((line = bf.readLine()) != null) {
            System.out.println(line);
        }
        bf.close();

    }

    static String getBin(int dec){
        String binary_code = "";
        //check if charecter is LITERAL or length

        if (dec>=0 & dec<=143){
            dec = dec + 48;
            binary_code = Integer.toBinaryString(dec);
            //adjusts "0" count in front of binary number
            if (binary_code.length()!=8) {
                for (int j = binary_code.length(); j<8;j++) {
                    binary_code = "0" + binary_code;
                }
            }
            return binary_code;
        }
        
        if (dec>=144 & dec<=255){
            dec = dec + 256;
            binary_code = Integer.toBinaryString(dec);
            //adjusts "0" count in front of binary number
            if (binary_code.length()!=9) {
                for (int j = binary_code.length(); j<9;j++) {
                    binary_code = "0" + binary_code;
                }
            }
            return binary_code;
        }

        if (dec>=256 & dec<=279){
            dec = dec - 256;
            binary_code = Integer.toBinaryString(dec);
            //adjusts "0" count in front of binary number
            if (binary_code.length()!=7) {
                for (int j = binary_code.length(); j<7;j++) {
                    binary_code = "0" + binary_code;
                }
            }
            return binary_code;
        }        
        
        if (dec>=192 & dec<=199){
            dec = dec + 88;
            binary_code = Integer.toBinaryString(dec);
            //adjusts "0" count in front of binary number
            if (binary_code.length()!=8) {
                for (int j = binary_code.length(); j<8;j++) {
                    binary_code = "0" + binary_code;
                }
            }
            return binary_code;
        }
        
        return "";

    }
    
    public static char getSym(String code){
        System.out.print(code);
        //bin to dec
        int dec = Integer.parseInt(code,2);
        // System.out.print("   dec="+dec);
        //dec to symb
        char symb = '[';
        if (dec>=48 & dec<=191){
            symb = (char)(dec-48);
            // System.out.print("   sym="+(dec-48));
        }
        if (dec>=400 & dec<=511){
            symb = (char)(dec-256);
            // System.out.print("   sym="+(dec-256));
        }

        
        // System.out.println("  ("+symb+")");
        return symb;
    }

    
}
