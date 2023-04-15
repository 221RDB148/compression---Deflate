import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class test {


    public static void main(String[] args) throws IOException{
        String line;
        BufferedReader reader = new BufferedReader(new FileReader("File1.html"));
        int i = 0;
        while((line = reader.readLine()) != null){
            if (i==20){
                break;
            }
            System.out.println(line);
            i++;
        }
        reader.close();
        System.out.println("asd");
    }
}
