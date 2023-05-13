import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
    public static void main(String[] args){
        long startTime = System.currentTimeMillis();
        String file = "text.txt";

        //////////////////////// COMPRESSING
        System.out.println("compressing ...");
        LZSS.LzssCompress comp = new LZSS.LzssCompress(file, "temp.temp");
		comp.compress();
        OOP.Compress.compressWithBlock1("temp.temp", "temp2.temp",0);
        System.out.println("compressed!");

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        //////////////////////// DECOMPRESSING
        System.out.println("decompressing ...");
        OOP.Decompress.decompressWithBlock1("temp2.temp", "temp.temp",0);
		LZSS.LzssDecompress comp1 = new LZSS.LzssDecompress("temp.temp", "OUT.txt");
		comp1.decompress();
        System.out.println("decompressed!");
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        
        try{
            if(compareFiles(Path.of(file), Path.of("OUT.txt"))) System.out.println("\u001B[32mFilesMatch\u001B[0m");
            else System.out.println("\n\u001B[32mError, files don`t match!\u001B[0m\n");
        } catch(IOException e){System.out.println(e);}


        System.out.print("Original_"); Main.size(file);
        System.out.print("Compress_"); Main.size("temp2.temp");

    }

    static boolean compareFiles(Path file1, Path file2) throws IOException {
        return Files.mismatch(file1, file2) == -1;
    }
}
