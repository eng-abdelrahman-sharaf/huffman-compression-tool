package com.ironman.compressiontool;

import java.io.IOException;

public class TestCompressor {
    public static void main(String[] args)  {
        Compressor compressor = new Compressor();
        try {
            compressor.compress("texts.txt");
        }catch (IOException e){
            System.out.println("Error: " + e);
        }
    }
}
