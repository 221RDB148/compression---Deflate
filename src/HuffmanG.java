import java.util.*;

public class HuffmanG {
    // Node class for the Huffman tree
    static class Node implements Comparable<Node> {
        char data;
        int frequency;
        Node left, right;

        public Node(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        public Node(Node left, Node right) {
            this.data = '\0';
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return (left == null && right == null);
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency - o.frequency;
        }
    }

    // Function to build the Huffman tree
    public static Node buildHuffmanTree(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();

        // Count the frequency of each character in the text
        for (char c : text.toCharArray()) {
            if (frequencies.containsKey(c)) {
                frequencies.put(c, frequencies.get(c) + 1);
            } else {
                frequencies.put(c, 1);
            }
        }

        // Build the initial priority queue of leaf nodes
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Build the Huffman tree by merging nodes from the priority queue
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.add(new Node(left, right));
        }

        // Return the root of the Huffman tree
        return queue.poll();
    }

    // Function to generate Huffman codes for each character in the tree
    public static Map<Character, String> generateHuffmanCodes(Node root) {
        Map<Character, String> codes = new HashMap<>();

        if (root == null) {
            return codes;
        }

        generateHuffmanCodesHelper(root, "", codes);

        return codes;
    }

    // Helper function to recursively generate Huffman codes for each character
    private static void generateHuffmanCodesHelper(Node node, String code, Map<Character, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.data, code);
        } else {
            generateHuffmanCodesHelper(node.left, code + "0", codes);
            generateHuffmanCodesHelper(node.right, code + "1", codes);
        }
    }

    // Function to output the prefix code lengths for each character
    public static void outputPrefixCodeLengths(Map<Character, String> codes) {
        List<Integer> codeLengths = new ArrayList<>();

        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            char c = entry.getKey();
            String code = entry.getValue();
            codeLengths.add(code.length());
            System.out.println(c + ": " + code.length());
        }
    }

    // Function to regenerate prefix codes based on the prefix code lengths
    public static Map<Character, String> regeneratePrefixCodes(List<Integer> codeLengths) {
        Map<Character, String> codes = new HashMap<>();

        // Generate all possible binary codes of the given lengths
        List<String> binaryCodes = generateBinaryCodes(codeLengths);

        // Assign each character to a binary code
        int i = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            codes.put(c, binaryCodes.get(i));
            i++;
            }
            return codes;
        }
        
        // Function to generate all possible binary codes of a given length
        private static List<String> generateBinaryCodes(List<Integer> codeLengths) {
            List<String> binaryCodes = new ArrayList<>();
        
            // Generate all possible binary codes of the given lengths recursively
            generateBinaryCodesHelper("", 0, codeLengths, binaryCodes);
        
            return binaryCodes;
        }
        
        // Helper function to recursively generate binary codes of a given length
        private static void generateBinaryCodesHelper(String code, int index, List<Integer> codeLengths, List<String> binaryCodes) {
            if (index == codeLengths.size()) {
                binaryCodes.add(code);
                return;
            }
        
            int length = codeLengths.get(index);
            for (int i = 0; i < length; i++) {
                generateBinaryCodesHelper(code + "0", index + 1, codeLengths, binaryCodes);
                generateBinaryCodesHelper(code + "1", index + 1, codeLengths, binaryCodes);
            }
        }
        
        // Main function to test the Huffman coding implementation
        public static void main(String[] args) {
            // Input text
            String text = "hello world";
        
            // Build the Huffman tree
            Node root = buildHuffmanTree(text);
        
            // Generate Huffman codes for each character
            Map<Character, String> codes = generateHuffmanCodes(root);
        
            // Output prefix code lengths for each character
            System.out.println("Prefix code lengths:");
            outputPrefixCodeLengths(codes);
        
            // Regenerate prefix codes based on the prefix code lengths
            List<Integer> codeLengths = Arrays.asList(1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4);
            Map<Character, String> regeneratedCodes = regeneratePrefixCodes(codeLengths);
        
            // Output regenerated prefix codes
            System.out.println("Regenerated prefix codes:");
            for (Map.Entry<Character, String> entry : regeneratedCodes.entrySet()) {
                char c = entry.getKey();
                String code = entry.getValue();
                System.out.println(c + ": " + code);
            }
        }
    }        
