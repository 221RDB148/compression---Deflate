import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Run {
    public static void main(String[] args) throws IOException{
        int sBuffS = 32768; //32768 max
        int lBuffS = 258;   //258


        for(int i=3; i<4; i++){

            System.out.print(i + "?=  ");//PRINT

            MakeFile("here.txt", i);
            OOP.Compress.compressWithBlock1("here.txt", "output1.txt",1);
            OOP.Decompress.decompressWithBlock1("output1.txt", "output2.txt",1);
        }

    }
    static void MakeFile(String output, int a) throws IOException{
        DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
        out.writeShort(65);
        out.writeShort(65);
        out.writeShort(65);

        out.writeShort(61440 + a);
        out.writeShort(10);

        out.writeShort(65);
        out.writeShort(65);
        out.writeShort(65);
        out.close();
    }
    private static boolean compareFiles(Path file1, Path file2) throws IOException {
        return Files.mismatch(file1, file2) == -1;
    }
}