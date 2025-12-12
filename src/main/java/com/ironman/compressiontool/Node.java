package com.ironman.compressiontool;

public class Node {
    private Byte value;
    private final Long frequency;
    private Node left;
    private Node right;

    public Node(byte value, long frequency) {
        this.value = value;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    public Node( Node left, Node right) {
        this.value = null;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    public Node(){
        this.value = null;
        this.frequency = null;
        this.left = null;
        this.right = null;
    }

    public Byte getValue() {
        return value;
    }

    public Long getFrequency() {
        return frequency;
    }

    public boolean isLeaf() {
        return value != null;
    }

    public Node getLeft() {
        return left;
    }
    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setValue(Byte value) {
        this.value = value;
    }
}
