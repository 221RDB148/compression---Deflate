import java.util.*;

public class Final {

    // Node class for building the Huffman tree
    static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        // Compare nodes based on frequency for priority queue
        public int compareTo(Node other) {
            return Integer.compare(freq, other.freq);
        }
    }

    // Build the Huffman tree from the character-frequency map
    public static Node buildHuffmanTree(Map<Character, Integer> charFreqs) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char ch : charFreqs.keySet()) {
            int freq = charFreqs.get(ch);
            pq.add(new Node(ch, freq, null, null));
        }

        while (pq.size() > 1) {
            Node left = pq.remove();
            Node right = pq.remove();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }

        return pq.remove();
    }

    // Recursively generate prefix codes from the Huffman tree
    public static void generateCodes(Node node, String prefix, Map<Character, String> codes) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            codes.put(node.ch, prefix);
        }
        generateCodes(node.left, prefix + "0", codes);
        generateCodes(node.right, prefix + "1", codes);
    }

    public static void main(String[] args) {
        Map<Character, Integer> charFreqs = new HashMap<>();
        charFreqs.put('a', 10);
        charFreqs.put('b', 10);
        charFreqs.put('c', 8);
        charFreqs.put('d', 6);
        charFreqs.put('e', 6);
        charFreqs.put('f', 6);
        charFreqs.put('g', 6);
        charFreqs.put('h', 6);
        charFreqs.put('t', 5);
        charFreqs.put('m', 3);
        charFreqs.put('k', 3);
        charFreqs.put('l', 1);

        Node root = buildHuffmanTree(charFreqs);
        Map<Character, String> codes = new TreeMap<>();
        generateCodes(root, "", codes);

        for (char ch : codes.keySet()) {
            System.out.println(ch + " -> " + codes.get(ch));
        }
    }
}
