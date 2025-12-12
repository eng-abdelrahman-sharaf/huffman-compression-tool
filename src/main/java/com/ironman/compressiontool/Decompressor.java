package com.ironman.compressiontool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Decompressor {

    private Node constructTreeHelper(Node self, byte key, String code ){
        if(self == null){
            self = new Node();
        }

        if(code.isEmpty()){
            self.setValue(key);
        }
        else{
            if (code.charAt(0)=='0'){
                Node child = constructTreeHelper(self.getLeft(),key,code.substring(1));
                self.setLeft(child);
            }
            else{
                Node child = constructTreeHelper(self.getRight(),key,code.substring(1));
                self.setRight(child);
            }
        }
        return self;
    }

    private Node constructTree(HashMap<Byte,String> codes ){
        Node root = new Node();
        for (byte key : codes.keySet()) {
            constructTreeHelper(root,key , codes.get(key));
        }
        return root;
    }

    private byte getKey(Node root , String code){
        if(root.isLeaf()){
            return root.getValue();
        }
        if(code.charAt(0)=='0'){
            return  getKey(root.getLeft(),code.substring(1));
        }
        else {
            return getKey(root.getRight(),code.substring(1));
        }
    }

    private String convertByteToString(byte b){
        return Integer.toBinaryString(Byte.toUnsignedInt(b));
    }
    private String convertByteTo8DigitString(byte b){
        return String.format("%8s", convertByteToString(b)).replace(' ', '0');
    }

    private void decodeFile(DataInputStream dis,Node root, HashMap<Byte,String> codes ,String decompressedFile, int padding) throws IOException{
        String code = "";
        DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(decompressedFile)));

        while(dis.available()>0){
            byte currentByte = dis.readByte();
            code += convertByteTo8DigitString(currentByte);
            if(dis.available()<=0){
                code = code.substring(0,code.length()-padding);
            }
            do{
                byte key = getKey(root,code);
                dos.writeByte(key);
                int codeLength = codes.get(key).length();
                if(codeLength < code.length()) code = code.substring(codeLength);
                else code = "";
            }while (code.length() >= 8);
        }
        while(!code.isEmpty()){
            byte key = getKey(root,code);
            dos.writeByte(key);
            int codeLength = codes.get(key).length();
            if(codeLength < code.length()) code = code.substring(codeLength);
            else code = "";
        }
    }
    private HashMap<Byte,String> generateCodeMap(DataInputStream dis,int numOfCodes) throws IOException{
        HashMap<Byte,String> map = new HashMap<>();

        for (int i = 0; i < numOfCodes; i++) {
            byte key = dis.readByte();
            byte numOfCodeBits = dis.readByte();
            String code = convertByteToString(dis.readByte());
            // extend code to take its original length
            // e.g. 0 -> 000 if numOfCodeBits = 3
            String extendedCode = String.format("%"+numOfCodeBits+"s", code);
            map.put(key,extendedCode);
        }
        return map;
    }

    public void decompress(String compressedFile) throws IOException {
        String extension = ".huffman";
        String fileSuffix = compressedFile.substring(compressedFile.length()-extension.length());
        String decompressedFile = compressedFile.substring(0, compressedFile.length()-extension.length());
        if(!fileSuffix.equals(".huffman")){
            throw new IOException("Unsupported compressed file format");
        }
        DataInputStream dis = new DataInputStream(Files.newInputStream(Path.of(compressedFile)));
        short numOfCodes = dis.readShort();
        byte padding = dis.readByte();
        HashMap<Byte,String> codes = generateCodeMap(dis,numOfCodes);
        Node root = constructTree(codes);
        decodeFile(dis,root,codes,decompressedFile,padding);
    }

}
