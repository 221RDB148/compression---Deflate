import java.nio.file.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        String file = "File2.html";
        int sBuffS = 32768; //32768 max
        int lBuffS = 258;   //258
        
        LzssCompress comp = new LzssCompress(file,sBuffS, lBuffS);
        Log.print(Log.GREEN + "compressing... " + Log.RESET);
        comp.compress();
        Log.println(Log.GREEN + " done" + Log.RESET);

        LzssDecompress decomp = new LzssDecompress(file + ".lzss", file + ".out", sBuffS + lBuffS);
        Log.print(Log.GREEN + "decompressing..." + Log.RESET);
        decomp.decompress();
        Log.println(Log.GREEN + " done" + Log.RESET);
        
        try{
            if(compareFiles(Path.of(file), Path.of(file + ".out"))) Log.println(Log.GREEN + "\nFiles match!\n" + Log.RESET);
            else System.out.println("\nError, files don`t match!\n");
        } catch(IOException e){System.out.println(e);} 
    }
// --------------------------------------------------------------------------------------
// LZSS compressor
// uses ByteRingBuffer, 
// --------------------------------------------------------------------------------------
    public static class LzssCompress{
        ByteRingBuffer searchBuffer;
        ByteRingBuffer lookAheadBuffer;
        FileInputStream inputf;
        FileOutputStream outputf;

        LzssCompress(String inputFile, int searchBufferSize, int lookAheadBufferSize){
            this.searchBuffer = new ByteRingBuffer(searchBufferSize);
            this.lookAheadBuffer = new ByteRingBuffer(lookAheadBufferSize);
            try{
                inputf = new FileInputStream(inputFile);
                int data;
                while(!lookAheadBuffer.isFull()){
                    data = inputf.read();
                    if(data == -1) break;
                    lookAheadBuffer.add((byte)(data & 0xFF));
                }
            } catch (IOException e){System.out.println("Couldn`t open file" + inputFile);}
            catch (Exception e){System.out.println(e);}

            try{outputf = new FileOutputStream(inputFile + ".lzss", false);}
            catch (FileNotFoundException e){System.out.println("Couldn`t create lzss temp file");}     
        }
        // iet cauri failam, kamēr sasniedz beigas
        public void compress(){
            compress(false);
        }

        public void compress(boolean printToConsole){
            int[] match; //[len, offset]
            try{
                while(!lookAheadBuffer.isEmpty()){
                    match = getLongestMatch();
                    if(match[0] == 0){
                        if(printToConsole) Log.print((char)lookAheadBuffer.get(0));
                        outputf.write(0);
                        outputf.write(lookAheadBuffer.get(0));
                        shiftBuffers(1);
                    } else {
                        if(printToConsole) Log.print(Log.GREEN + "<" + match[0] + ":" + match[1] + ">" + Log.RESET);
                        shiftBuffers(match[0]);
                        int lengthCode = 0xF000 + match[0];
                        outputf.write((byte)((lengthCode>>8) & 0xFF));
                        outputf.write((byte)(lengthCode & 0xFF));
                        outputf.write((byte)((match[1]>>8) & 0xFF));
                        outputf.write((byte)(match[1] & 0xFF));                        
                    }
                }
                outputf.close();
                inputf.close();
            } catch(IOException e){System.out.println(e);}
        }
        //buferu pārkopēšana un jaunu simbola ievietošana
        private void shiftBuffers(int pos){
            int literal;
            for(; pos > 0; pos--){
                searchBuffer.add(lookAheadBuffer.remove());
                try{
                    literal = inputf.read();
                    if(literal != -1){
                        lookAheadBuffer.add((byte)literal);
                    }
                }
                catch(IOException e){
                    System.out.println("Couldn`t read the file!");
                }
            }
        }

        public int[] getLongestMatch(){
            int[] result = new int[]{0, 0}; //[len, offset]
            if(searchBuffer.isEmpty())return result;
            nextMatch: for(int sBuffInd = searchBuffer.size()-1; sBuffInd >= 0; sBuffInd--){
                if(searchBuffer.get(sBuffInd) == lookAheadBuffer.get(0)){
                    int matchCount = 1;
                    //longest match in search buffer
                    while((sBuffInd+matchCount) < searchBuffer.size() && matchCount < lookAheadBuffer.size()){
                        if(searchBuffer.get(sBuffInd+matchCount) == lookAheadBuffer.get(matchCount)){
                            matchCount++;
                            if(matchCount > result[0]){
                                result[0] = matchCount; result[1] = searchBuffer.size()-sBuffInd;
                            }
                        }
                        else break nextMatch;
                    }
                    //add lookahead buffer if possible
                    if((sBuffInd+matchCount) >= searchBuffer.size() && matchCount < lookAheadBuffer.size()){
                        int lBuffInd = 0;
                        while(matchCount < lookAheadBuffer.size()){
                            if(lookAheadBuffer.get(lBuffInd) == lookAheadBuffer.get(matchCount)){
                                matchCount++;
                                lBuffInd++;
                                if(matchCount > result[0]){
                                    result[0] = matchCount; result[1] = searchBuffer.size()-sBuffInd;
                                }
                            }
                            else break;
                        }

                    }
                }
            }
            if(result[0] < 3){
                result[0]=0; result[1]=0;
            }
               
            return result;
        }
        public void printBuffers(){
            Log.println("searchBuffer:");
            searchBuffer.printHeader();
            searchBuffer.printData();
            Log.println("lookAheadBuffer:");
            lookAheadBuffer.printHeader();
            lookAheadBuffer.printData();

        }

    }
// --------------------------------------------------------------------------------------
// LZSS decompressor
// uses ByteRingBuffer
// --------------------------------------------------------------------------------------
    public static class LzssDecompress{
        ByteRingBuffer inputBuffer;
        FileInputStream inputf;
        FileOutputStream outputf;

        LzssDecompress(String inputFile, String outputFile, int BufferSize){
            this.inputBuffer = new ByteRingBuffer(BufferSize);
            try{
                inputf = new FileInputStream(inputFile);
            } catch(IOException e) {
                System.out.println("Couldn`t open file " + inputFile);
            }
            try{
                outputf = new FileOutputStream(outputFile, false);
            } catch(FileNotFoundException e){
                System.out.println("Couldn`t create file " + outputFile);
            }
        }

        public void decompress(){
            int length, offset;
            try{
                int nextByte = inputf.read();
                while(nextByte != -1){
                    if(nextByte > 0){ // this is reference
                        length = ((nextByte << 8) + inputf.read()) & 0xFFF;
                        offset = inputBuffer.size() - (((inputf.read() << 8) + inputf.read()) & 0xFFFF);
                        for(int i = offset; i < (offset + length); i ++){
                            
                            try{outputf.write(inputBuffer.get(i));}
                            catch(IOException e){System.out.println(e);}

                            if(inputBuffer.isFull()){
                                inputBuffer.add(inputBuffer.get(i));
                                i--;
                                offset--;
                            } else{
                                inputBuffer.add(inputBuffer.get(i));
                            }
                            //Log.print((char)inputBuffer.get(i));
                        }
                        //Log.print("<" + length + ":" + offset + ">");
                    } else { // this literal
                        inputBuffer.add((byte)(inputf.read() & 0xFF));
                        //Log.print((char)inputBuffer.get(inputBuffer.size()-1));
                        try{outputf.write(inputBuffer.get(inputBuffer.size()-1));}
                        catch(IOException e){System.out.println(e);}
                    }
                    nextByte = inputf.read();
                }
                inputf.close();;
                outputf.close();
            } catch(IOException e){System.out.println(e);}
        }
    }
// --------------------------------------------------------------------------------------
// Ring Buffer
// --------------------------------------------------------------------------------------
    public static class ByteRingBuffer{
        private int head = 0;
        private int tail = -1;
        private int capacity = 0;
        private int size = 0;
        private byte[] data;

        ByteRingBuffer(int capacity){
            this.capacity = capacity;
            this.data = new byte[this.capacity];
        }

        public void add(byte dataByte){
            int index = (tail + 1) % capacity;
            if (this.isFull()) {
                this.remove();
            }
            data[index] = dataByte;
            size++;
            tail = index;//tail++;
        }

        public byte remove(){
            if (this.isEmpty()) {
                System.out.println("Error: Empty Ring Buffer");
            }
            int index = head % capacity;
            byte element = data[index];
            head = (head + 1) % capacity; //head++;
            size--;
            return element;
        }

        public byte get(int index){
            if(this.isEmpty()) {
                System.out.println("Error: Empty Ring Buffer");
            }
            if(index >= size || index < 0){
                System.out.println("Error: Ring Buffer index out of range (" + index + ")");
            }
            int arrayIndex = (head + index) % capacity;
            return data[arrayIndex];
        }

        public boolean isEmpty(){
            if(size == 0) return true;
            else return false;
        }

        public boolean isFull(){
            if(size == capacity) return true;
            else return false;
        }

        public int size(){
            return this.size;
        }

        public void printHeader(){
            System.out.println("\tCapacity: " + capacity);
            System.out.println("\tSize: " + size);
            System.out.println("\tHead: " + head);
            System.out.print("\tTail: " + tail + "\n");
            // for(int i = 0; i < size; i++){
            //     try{
            //         System.out.print(this.get(i));
            //         if(i < size - 1) System.out.print(", ");
            //     }
            //     catch (Exception e){
            //         System.out.println(e);
            //     }   
            // }
            // System.out.println("]");
        }
        public void printData(){
            for(int i = 0; i < size; i++){
                try{
                    byte ch = this.get(i);
                    System.out.print((char)ch + "");
                    //if(i < size - 1) System.out.print(", ");
                }
                catch (Exception e){
                    System.out.println(e);
                }   
            }
            System.out.println();
        }
    }
// --------------------------------------------------------------------------------------
// Log (functions for debug printing)
// --------------------------------------------------------------------------------------
public static class Log{
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String GREEN = "\033[1;32m";   // GREEN

    public static void println(Object object){
        System.out.println(object);
    }
    public static void print(Object object){
        System.out.print(object);
    }
}
// --------------------------------------------------------------------------------------
// Utilities
// --------------------------------------------------------------------------------------
    private static boolean compareFiles(Path file1, Path file2) throws IOException {
        return Files.mismatch(file1, file2) == -1;
    }
}

