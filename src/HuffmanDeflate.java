import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class HuffmanDeflate {
    
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream("text.txt");
        Map<Character, Integer> freqMap = new HashMap<>();

        int data;
        char ch;
        while((data = in.read()) != -1){ //put tuff in Hashmap
            data = data & 0xFF;
            ch = (char)data;
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }
        
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }
        
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        
        Node root = pq.poll();
        Map<Character, String> codes = new HashMap<>();
        generateCodes(root, "", codes);
        
        System.out.println("Symbol\tFrequency\tCode");
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            char c = entry.getKey();
            int freq = entry.getValue();
            String code = codes.get(c);
            System.out.println(c + "\t" + freq + "\t\t" + code);
        }
        in.close();
    }
    
    private static void generateCodes(Node node, String code, Map<Character, String> codes) {
        if (node == null) {
            return;
        }
        if (node.symbol != '\0') {
            codes.put(node.symbol, code);
        }
        generateCodes(node.left, code + "0", codes);
        generateCodes(node.right, code + "1", codes);
    }
    
    private static class Node implements Comparable<Node> {
        char symbol;
        int freq;
        Node left, right;
        
        public Node(char symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
        }
        
        public int compareTo(Node other) {
            return freq - other.freq;
        }
    }
}
