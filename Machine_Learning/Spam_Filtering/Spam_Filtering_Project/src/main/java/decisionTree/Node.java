package decisionTree;

import java.util.List;

public class Node {

    private ID3Feature feature;
    private Node leftChild;
    private Node rightChild;
    private String className;
    private Boolean isLeaf;
    private List<Integer> emailIndexed;

    Node(List<Integer> emailIndexes) {
        this.isLeaf = false;
        this.leftChild = null;
        this.rightChild = null;
        this.emailIndexed = emailIndexes;
    }

    public Node getLeftChild() {
        return this.leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return this.rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    public ID3Feature getFeature() {
        return this.feature;
    }

    public void setFeature(ID3Feature feature) {
        this.feature = feature;
    }

    public void setLeaf(Boolean leaf) {
        this.isLeaf = leaf;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Integer> getEmailIndexed() {
        return this.emailIndexed;
    }
}
