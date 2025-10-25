/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.HashMap;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.logic.method.util.InfoNormalizer;
import objectiveethics.values.logic.method.util.InfoUtils;
import java.util.Map;

@BaseValue(
    id = 8,
    category = "information",
    acronym = "IHUFF",
    name = "Huffman Information",
    description = "Calculates information content using Huffman coding algorithm. Builds an optimal " +
                  "binary tree based on symbol frequencies and returns the total number of bits " +
                  "required to encode the data. More frequent symbols get shorter codes, " +
                  "resulting in compression-based information measurement.",
    algorithm = "1. Count frequency of each unique symbol;\n" +
             "2. Create leaf nodes for each symbol with their frequencies;\n" +
             "3. Build Huffman tree by repeatedly merging two lowest frequency nodes;\n" +
             "4. Generate binary codes by traversing tree (left=0, right=1);\n" +
             "5. Calculate total bits by summing (symbol_frequency * code_length)",
    article = "https://objectiveethics.com/values/docs/huffman-information/"
)
public class HuffmanInfo implements Information {

    @Override
    public double value(byte[] values) {
        if (values == null || values.length < 1) {
            return 0;
        }
        
        if (values.length == 1) {
            return 1;
        }
        
        double huffmanInfo = calculateHuffmanInfo(values);
        
        // Calculate min and max Huffman info for normalization
        double minHuffmanInfo = calculateHuffmanInfo(new byte[values.length]); // All zeros
        double maxHuffmanInfo = calculateHuffmanInfo(InfoUtils.generateRandomByteArray(values)); // Random sequence
        
        // Normalize the Huffman information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(huffmanInfo, minHuffmanInfo, maxHuffmanInfo, values);
    }
    
    private double calculateHuffmanInfo(byte[] values) {
        ByteNode root = buildHuffmanTree(values);
        Map<Byte, Integer> codeTable = new HashMap<>();
        buildCodeTable(codeTable, root, new StringBuilder());
        long info = 0;
        for (byte ch : values) {
            info += codeTable.get(ch);
        }
        return info;
    }

    private void buildCodeTable(Map<Byte, Integer> codeTable, ByteNode node, StringBuilder sb) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                codeTable.put(node.ch, sb.length());
            }
            sb.append('0');
            buildCodeTable(codeTable, node.left, sb);
            sb.deleteCharAt(sb.length() - 1);

            sb.append('1');
            buildCodeTable(codeTable, node.right, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private ByteNode buildHuffmanTree(byte[] text) {
        Map<Byte, Integer> freq = new HashMap<>();
        for (byte ch : text) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<ByteNode> pq = new PriorityQueue<>((l, r) -> l.frequency - r.frequency);
        for (var entry : freq.entrySet()) {
            pq.add(new ByteNode(entry.getKey(), entry.getValue()));
        }

        if (freq.size() == 1) {
            // Add an extra node with a dummy character to handle single character input
            pq.add(new ByteNode((byte) 0, 1));
        }

        while (pq.size() > 1) {
            ByteNode left = pq.poll();
            ByteNode right = pq.poll();
            int sum = left.frequency + right.frequency;
            pq.add(new ByteNode((byte) 0, sum, left, right));
        }

        return pq.poll();
    }

    private class ByteNode {

        byte ch;
        int frequency;
        ByteNode left = null, right = null;

        ByteNode(byte ch, int frequency) {
            this.ch = ch;
            this.frequency = frequency;
        }

        ByteNode(byte ch, int frequency, ByteNode left, ByteNode right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }
    
    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() < 1) {
            return 0;
        }
        
        if (values.size() == 1) {
            return 1;
        }
        
        Object[] input = values.toArray();
        double huffmanInfo = calculateHuffmanInfo(input);
        
        // Calculate min and max Huffman info for normalization
        double minHuffmanInfo = calculateHuffmanInfo(new Object[values.size()]); // All nulls
        double maxHuffmanInfo = calculateHuffmanInfo(InfoUtils.generateRandomObjectArray(values)); // Random sequence
        
        // Normalize the Huffman information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(huffmanInfo, minHuffmanInfo, maxHuffmanInfo, values);
    }
    
    private double calculateHuffmanInfo(Object[] values) {
        Node root = buildHuffmanTree(java.util.Arrays.asList(values));
        Map<Object, Integer> codeTable = new HashMap<>();
        buildCodeTable(codeTable, root, new StringBuilder());
        long info = 0;
        for (Object ch : values) {
            info += codeTable.get(ch);
        }
        return info;
    }

    private void buildCodeTable(Map<Object, Integer> codeTable, Node node, StringBuilder sb) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                codeTable.put(node.ch, sb.length());
            }
            sb.append('0');
            buildCodeTable(codeTable, node.left, sb);
            sb.deleteCharAt(sb.length() - 1);

            sb.append('1');
            buildCodeTable(codeTable, node.right, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private Node buildHuffmanTree(Collection<?> text) {
        Map<Object, Integer> freq = new HashMap<>();
        for (Object ch : text) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((l, r) -> l.frequency - r.frequency);
        for (var entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        if (freq.size() == 1) {
            // Add an extra node with a dummy character to handle single character input
            pq.add(new Node(new Object(), 1));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.frequency + right.frequency;
            pq.add(new Node(new Object(), sum, left, right));
        }

        return pq.poll();
    }

    private class Node {

        Object ch;
        int frequency;
        Node left = null, right = null;

        Node(Object ch, int frequency) {
            this.ch = ch;
            this.frequency = frequency;
        }

        Node(Object ch, int frequency, Node left, Node right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }

}
