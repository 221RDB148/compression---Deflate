import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LzssTest {
    public static void main(String[] args) throws Exception {
        LzssCompress compressor = new LzssCompress("File2.html",32768, 258);
        Log.println(Log.GREEN + "compressing... " + Log.RESET);
        compressor.compress(true);
        Log.println(Log.GREEN + "\ndone" + Log.RESET);

        
        
    }
// --------------------------------------------------------------------------------------
// Log
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
// Sliding window
// uses ByteRingBuffer, 
// --------------------------------------------------------------------------------------
    public static class LzssCompress{
        ByteRingBuffer searchBuffer;
        ByteRingBuffer lookAheadBuffer;
        int cursor = 0;
        FileInputStream inputf;
        FileOutputStream outputf;

        LzssCompress(String inputFile, int searchBufferSize, int lookAheadBufferSize){
            this.searchBuffer = new ByteRingBuffer(searchBufferSize);
            this.lookAheadBuffer = new ByteRingBuffer(lookAheadBufferSize);
            try{
                inputf = new FileInputStream(inputFile);
                int data = inputf.read();
                while(!lookAheadBuffer.isFull() && data != -1){
                    lookAheadBuffer.add((byte)(data & 0xFF));
                    data = inputf.read();
                }
            }
            catch (IOException e){
                System.out.println("Couldn`t open file" + inputFile);
            }
            catch (Exception e){
                System.out.println(e);
            }
            try{
                outputf = new FileOutputStream(inputFile + ".lzss", false);
            }
            catch (FileNotFoundException e){
                System.out.println("Couldn`t create lzss temp file");

            }     
        }
        // iet cauri failam, kamēr sasniedz beigas
        public void compress(){
            compress(false);
        }

        public void compress(boolean printToConsole){
            int[] match; //len, offset
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
            }
            catch(IOException e){
                System.out.println("Couldn`t write to lzss temp file");
            }
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
                    System.out.print((char)ch);
                    //if(i < size - 1) System.out.print(", ");
                }
                catch (Exception e){
                    System.out.println(e);
                }   
            }
            System.out.println();
        }
    }

}
