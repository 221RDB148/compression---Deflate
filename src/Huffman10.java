import java.util.HashMap;
import java.util.Map;

public class Huffman10 {
    
    public static Map<Character, String> generateHuffmanCode(int[] codeLengths) {
        Map<Character, String> codes = new HashMap<Character, String>();
        int[] blCount = new int[16];
        int[] nextCode = new int[16];
        
        for (int i = 0; i < codeLengths.length; i++) {
            blCount[codeLengths[i]]++;
        }
        
        int code = 0;
        blCount[0] = 0;
        for (int bits = 1; bits <= 15; bits++) {
            code = (code + blCount[bits-1]) << 1;
            nextCode[bits] = code;
        }
        
        for (int i = 0; i < codeLengths.length; i++) {
            int len = codeLengths[i];
            if (len != 0) {
                String huffCode = toBinaryString(nextCode[len], len);
                codes.put((char)i, huffCode);
                nextCode[len]++;
            }
        }
        
        return codes;
    }
    
    private static String toBinaryString(int num, int len) {
        String binaryString = Integer.toBinaryString(num);
        while (binaryString.length() < len) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }
    
    public static void main(String[] args) {
        // int[] codeLengths = {3,2,2,2,3};
        int[] codeLengths = {2,3,4,4,2,4,4,5,5,4};
        // int[] codeLengths = {3,3,3,3,3,2,4,4};
        Map<Character, String> codes = generateHuffmanCode(codeLengths);
        
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
