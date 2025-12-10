package com.ironman.compressiontool;

public class Node {
    private final byte value;
    private final long frequency;
    private final Node left;
    private final Node right;

    public Node(byte value, long frequency) {
        this.value = value;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    public Node( Node left, Node right) {
        this.value = 0;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    public byte getValue() {
        return value;
    }

    public long getFrequency() {
        return frequency;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public Node getLeft() {
        return left;
    }
    public Node getRight() {
        return right;
    }
}
