
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Huffman {
    HashMap<Byte, String> codemap;
    HashMap<String, Byte> reverse;

    public Huffman() {
        this.codemap = new HashMap<Byte, String>();
        this.reverse = new HashMap<String, Byte>();
    }

    // afunction that returns frequency map given the byte array
    public HashMap<Byte, Integer> GetFreqMap(byte[] arr) {
        HashMap<Byte, Integer> freqMap = new HashMap<Byte, Integer>();
        for (int i = 0; i < arr.length; i++) {

            if (freqMap.containsKey(arr[i])) {
                freqMap.put(arr[i], freqMap.get(arr[i]) + 1);
            } else {
                freqMap.put(arr[i], 1);
            }
        }
        return freqMap;
    }

    // a method that returns a root node to the huffman tree given the frequency map
    public Node BuildHuffmanTree(HashMap<Byte, Integer> frequencyMap) {
        PriorityQueue<Node> nodesMinHeap = new PriorityQueue<Node>(
                (e1, e2) -> Integer.compare(e1.frequency, e2.frequency));
        for (HashMap.Entry<Byte, Integer> e : frequencyMap.entrySet()) {
            nodesMinHeap.add(new Node(e.getKey(), e.getValue(), null, null));
        }
        while (nodesMinHeap.size() >= 2) {

            Node leftChild = nodesMinHeap.poll();
            Node rightChild = nodesMinHeap.poll();
            Node root = new Node(leftChild.frequency + rightChild.frequency, leftChild, rightChild);
            nodesMinHeap.add(root);
        }
        return nodesMinHeap.poll();
    }

    // the main function for the huffman algorithm that reads the input file into a
    // byte array
    // then builds the huffman code tree and then derives the code from the tree
    public void HuffmanAlgorithm(String filePath, String dest) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] byteArr = new byte[(int) (file.length())];
        bis.read(byteArr);
        bis.close();
        // get root of huffman tree
        Node root = BuildHuffmanTree(GetFreqMap(byteArr));
        StoreCode(root, "");
        byte[] compressed = convertCodeMapToByteArray(byteArr);
        WriteFile(compressed,dest);
    }

    public void StoreCode(Node root, String s) {
        if (root.leftChild == null && root.rightChild == null) {
            this.codemap.put(root.value, s);
            this.reverse.put(s, root.value);
        } else {
            StoreCode(root.leftChild, s + "0");
            StoreCode(root.rightChild, s + "1");
        }
    }

    public void WriteFile(byte[] arr, String dest) throws IOException {
        FileOutputStream f = new FileOutputStream(new File(dest));
        BufferedOutputStream bos = new BufferedOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this.reverse);
        oos.writeObject(arr);
        oos.close();
    }

    // a function that returns the byte array to be written in the file
    public byte[] convertCodeMapToByteArray(byte[] bytes) {
        BitSet bitset = new BitSet();
        int index=0;
        for (int i = 0; i < bytes.length; i++) {
            String str = this.codemap.get(bytes[i]);
            for (int j = 0; j < str.length(); j++) {
                if (str.charAt(j) == '1') {
                    bitset.set(index);
                }
                index++;
            }
        }
        byte[] res = bitset.get(0, index+1).toByteArray();
        return res;
    }

    public static void main(String[] args) throws Exception {
        try{
        Huffman h = new Huffman();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter c for compression or d for decompression then press enter:");
        String compression = sc.nextLine();
        while(!compression.equals("c")&&!compression.equals("d")){
            System.out.println("Invalid choice!!");
            System.out.println("Enter c for compression or d for decompression then press enter:");
            compression = sc.nextLine();
        }
        System.out.println("Enter the file path:");
        String pathOfFile = sc.nextLine();
        Path path =  Paths.get(pathOfFile);
        if(compression.equals("c")){
            String destination = path.getParent().toString() + "\\compressed-" + path.getFileName().toString() + ".hc";
            long start = System.nanoTime();
            h.HuffmanAlgorithm(pathOfFile,destination);
            long End = System.nanoTime();
            File in = new File(pathOfFile);
            File out = new File(destination);
            System.out.println("Compression successful!");
            System.out.println("time taken in millieseconds: "+(double)(End-start)/1000000);
            System.out.println("time taken in seconds: "+(double)(End-start)/1000000000);
            System.out.println("Compression ratio: "+((double)out.length()/in.length()));
        }else {
            String fileName = path.getFileName().toString().substring(0, path.getFileName().toString().length() - 3);
            String destination = path.getParent().toString() + "\\extracted-" + fileName;
            long start = System.nanoTime();
            HuffmanDecompression hd = new HuffmanDecompression();
            hd.DecompressFile(pathOfFile, destination);
            long End = System.nanoTime();
            System.out.println("Decompression successful!");
            System.out.println("time taken in nanoseconds: " + (End - start));
            System.out.println("time taken in millieseconds: " + (double) (End - start) / 1000000);
            System.out.println("time taken in seconds: " + (double) (End - start) / 1000000000);
        }
        }catch (Exception e){
            System.out.println("Invalid Path or file!!");
        }
    }
}
