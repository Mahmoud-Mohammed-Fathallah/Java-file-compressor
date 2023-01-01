import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.BitSet;
import java.util.HashMap;

public class HuffmanDecompression {
    public void DecompressFile(String path, String dest) throws Exception {
        // read header of file first.
        // reading the data
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        HashMap<String, Byte> reversemap=(HashMap<String, Byte>)ois.readObject();
        byte[] byteArr = (byte[])ois.readObject();
        ois.close();
        BitSet bitSet = BitSet.valueOf(byteArr);
        int ind = 0 ;
        StringBuilder sb = new StringBuilder("");
        byte[] data = new byte[bitSet.length()];
        for(int i = 0 ;i< bitSet.length(); i++){
            if(bitSet.get(i)){
                sb.append('1');
            }else{
                sb.append('0');
            }
            Byte b = reversemap.get(sb.toString());
            if(b!=null){
                data[ind] = b;
                ind++;
                sb = new StringBuilder("");
            }
        }
        writeDecompressedFile(data,ind,dest);
    }

    public void writeDecompressedFile(byte[] arr, int ind,String dest) throws Exception {
        FileOutputStream f = new FileOutputStream(new File(dest));
        BufferedOutputStream bos = new BufferedOutputStream(f);
        bos.write(arr, 0, ind);
        bos.close();
    }
}
