import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException, InterruptedException{
        // // FileInputStream in = new FileInputStream("text.txt.lzss");
        // FileInputStream in = new FileInputStream("output1.lzss");
        // int data;
        // String str;
        // int d = 1;
        // while((data = in.read()) != -1){
        //     str = Integer.toBinaryString(data);
        //     for(int i=0; i<str.length(); i++){
        //         if (str.length() != 8)str = "0" + str;
        //     }
        //     // if (d%2 != 0){
        //     //     System.out.printf("%3d %s\n",d, str);
                
        //     // }else{
                
        //     //     System.out.printf("    %s  %c\n",str, (char)Integer.parseInt(str,2));
        //     // }
        //     if (d%2 != 0){
        //         System.out.printf("%3d %s  %c\n",d, str,(char)Integer.parseInt(str,2));
                
        //     }else{
                
        //         System.out.printf("    %s  %c\n",str, (char)Integer.parseInt(str,2));
        //     }
        //     // System.out.println(d + " "+str);
        //     Thread.sleep(5);
        //     d++;
        // }
        // in.close();

        DataOutputStream out = new DataOutputStream(new FileOutputStream("text.txt"));
  
            out.writeShort(65);
            out.writeShort(65);
            out.writeShort(192);
            out.writeShort(65);
            out.writeShort(65);
        // for (int i=3; i<259; i++){
        //     out.writeShort(61440 + i);
        // }
        // for (int i=0; i<255; i++){
        //     out.writeByte(0);
        //     out.writeByte(i);
        // }
        // out.writeByte(0);
        // out.writeByte(65);
        // out.writeByte(65);


        out.close();
    }
}
