import java.util.*;

public class Huffman4 {
    public static void main(String[] args) {
        Map<Character, Integer> freq = new HashMap<>();
        freq.put('a', 1);
        freq.put('b', 2);
        freq.put('c', 3);
        freq.put('d', 4);
        freq.put('j', 4);
        freq.put('k', 4);
        freq.put('y', 7);
        
        Map<Character, String> prefixCodes = generatePrefixCodes(freq);
        
        // Output the prefix codes
        for (Map.Entry<Character, String> entry : prefixCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
    public static Map<Character, String> generatePrefixCodes(Map<Character, Integer> freq) {
        // First, we create a priority queue of Nodes sorted by frequency and symbol
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            queue.offer(new Node(entry.getKey(), entry.getValue()));
        }
        
        // Then, we build the Huffman tree by repeatedly combining the two smallest Nodes
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.offer(new Node(left, right));
        }
        
        // Finally, we generate the prefix codes by traversing the tree from root to leaf
        Map<Character, String> prefixCodes = new TreeMap<>();
        generateCodes(queue.peek(), "", prefixCodes);
        return prefixCodes;
    }
    
    private static void generateCodes(Node node, String code, Map<Character, String> prefixCodes) {
        if (node.isLeaf()) {
            prefixCodes.put(node.symbol, code);
        } else {
            generateCodes(node.left, code + "0", prefixCodes);
            generateCodes(node.right, code + "1", prefixCodes);
        }
    }
    
    private static class Node implements Comparable<Node> {
        char symbol;
        int frequency;
        Node left;
        Node right;
        
        public Node(char symbol, int frequency) {
            this.symbol = symbol;
            this.frequency = frequency;
        }
        
        public Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
            // Set symbol to '\0' to indicate internal node (no symbol)
            this.symbol = '\0';
        }
        
        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }
        
        @Override
        public int compareTo(Node other) {
            // Nodes are compared first by frequency, then by symbol lexicographically
            int freqCompare = Integer.compare(this.frequency, other.frequency);
            if (freqCompare == 0) {
                return Character.compare(this.symbol, other.symbol);
            } else {
                return freqCompare;
            }
        }
    }
}
