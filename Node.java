public class Node {
    int frequency;
    Byte value;
    Node leftChild;
    Node rightChild;
    public Node(byte value,int frequency,Node left,Node right){
        this.value = value;
        this.frequency = frequency;
        this.leftChild = left;
        this.rightChild = right;
    }
    public Node(int frequency,Node left,Node right){
        this.frequency = frequency;
        this.leftChild = left;
        this.rightChild = right;
    }
}
