package com.ironman.compressiontool;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Compressor {

    private HashMap<Byte,Long> generateFileStatistics(String file) throws IOException {
        HashMap<Byte,Long> map = new HashMap<>();
        DataInputStream dis = new DataInputStream(Files.newInputStream(Paths.get(file)));

        while(dis.available()>0) {
            byte b = dis.readByte();
            map.put(b,map.getOrDefault(b,0L)+1);
        }
        return map;
    }

    private Node constructTree(String file) throws IOException {
        HashMap<Byte, Long> map = generateFileStatistics(file);
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparing(Node::getFrequency));
        for(Byte key : map.keySet()) {
            Node node = new Node(key,map.get(key));
            priorityQueue.add(node);
        }

        while(priorityQueue.size() >= 2) {
            Node child1 = priorityQueue.poll();
            Node child2 = priorityQueue.poll();
            assert child2 != null;
            Node parent = new Node(child1,child2);
            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    private void generateHuffmanCodesHelper(Node node, String code, HashMap<Byte,String> codes){
        if(node == null) return;
        if(node.isLeaf()) {
            codes.put(node.getValue(), code);
        }
        generateHuffmanCodesHelper(node.getLeft(),code+"0" , codes);
        generateHuffmanCodesHelper(node.getRight(),code+"1" , codes);
    }

    private HashMap<Byte,String> generateHuffmanCodes(String file) throws IOException {
        Node root = constructTree(file);
        HashMap<Byte,String> codes = new HashMap<>();
        generateHuffmanCodesHelper(root,"",codes);
        return codes;
    }

    private void writeHeader(Path outputPath,byte padding,HashMap<Byte,String> codes) throws IOException {
        DataOutputStream dos = new DataOutputStream(Files.newOutputStream(outputPath,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING));
        dos.writeShort(codes.size());
        dos.writeByte(padding);
        for(Byte key : codes.keySet()) {
            dos.writeByte(key);
            byte byteCode = (byte) Integer.parseInt(codes.get(key));
            dos.writeByte(byteCode);
        }
    }

    public record FileCodes(byte [] codes, byte padding) {}


    private FileCodes convertFileToCodes(Path inputPath, HashMap<Byte,String> codes) throws IOException {
        DataInputStream dis = new DataInputStream(Files.newInputStream(inputPath));
        StringBuilder stringBuilder = new StringBuilder();

        while(dis.available()>0) {
            byte currentByte = dis.readByte();
            String currentByteCode = codes.get(currentByte);
            stringBuilder.append(currentByteCode);
        }
        dis.close();
        String fileCodes = stringBuilder.toString();
        int numOfBytes = (fileCodes.length() + 7) / 8;
        byte padding = (byte)( (8 - fileCodes.length() % 8) % 8 );
        byte[] bytes = new byte[numOfBytes];
        for(int i = 0; i < numOfBytes; i++) {
            String currentByte = fileCodes.substring(i*8, (i+1)*8);
            bytes[i] = (byte) Integer.parseInt(currentByte,2);
        }

        return new FileCodes(bytes  , padding);
    }

    private void writeFileCodes(Path outputPath,byte [] fileCodes) throws IOException {
        DataOutputStream dos = new DataOutputStream(Files.newOutputStream(outputPath, StandardOpenOption.APPEND));
        for(byte code : fileCodes) {
            dos.writeByte(code);
        }
        dos.close();
    }

    private void huffmanEncodeFile(String file) throws IOException {
        HashMap<Byte,String> codes = generateHuffmanCodes(file);
        Path inputPath = Paths.get(file);
        Path outputPath = Paths.get(file+".huffman");

        FileCodes fileCodes =  convertFileToCodes(inputPath,codes);
        writeHeader(outputPath,fileCodes.padding,codes);
        writeFileCodes(outputPath,fileCodes.codes);
    }

    public void compress(String file) throws IOException {
        huffmanEncodeFile(file);
    }
}
