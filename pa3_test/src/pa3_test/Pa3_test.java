/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import verifiers.VerifyExactReadValue;
import verifiers.VerifyBlockMultiplicity;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobpipes.Pipe;
import jobpipes.ServerTestPipe;
import verifiers.VerifyCorrectEchoedNewline;
import verifiers.VerifyCorrectEchoedSimple;
import verifiers.VerifyInfo;
import verifiers.VerifyNewlineMultiplicity;

/**
 * 
 * @author visoft
 */
public class Pa3_test {

    /**
     * This test will write some data and observe the "chunk at a time" behavior
     * @param port
     * @param ChunkSize
     * @return 
     */
    private static Pipe smallChunkTest(int port,int ChunkSize, int timeout){
        int size=2*ChunkSize+1;
        byte[] outData=DataGenerator.generateRandomCharBuff(size);
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Simple test suite", port, outData, size, timeout, null);
        
        pipe.addJob(new ReadWriteJob(0, ChunkSize-1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyExactReadValue(0));
        pipe.addJob(new VerifyBlockMultiplicity(ChunkSize));
        pipe.addJob(new VerifyCorrectEchoedSimple());
        pipe.addJob(new ReadWriteJob(ChunkSize-1, 1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyBlockMultiplicity(ChunkSize));
        pipe.addJob(new VerifyExactReadValue(ChunkSize));
        pipe.addJob(new VerifyCorrectEchoedSimple());
        pipe.addJob(new ReadWriteJob(ChunkSize, ChunkSize+1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyExactReadValue(2*ChunkSize));
        pipe.addJob(new VerifyBlockMultiplicity(ChunkSize));
        pipe.addJob(new VerifyCorrectEchoedSimple());

        return pipe;
    }
    /**
     * Some large buffer is sent,received and verified
     * @param port
     * @param size
     * @param timeout
     * @return 
     */
    private static Pipe largeBufferTransfer(int port,int size,int timeout){
        byte[] outData=DataGenerator.generateRandomCharBuff(size);
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Send and receive a loong buffer", port, outData, size, timeout, null);
        
        pipe.addJob(new ReadWriteJob(0, size));
        pipe.addJob(new VerifyInfo());  
        pipe.addJob(new VerifyCorrectEchoedSimple());
        
        return pipe;
    }
    /**
     * Flood the server with writes, and never read anything.
     * The communication will block at both ends when the OS/network buffers will be full.
     * HOWEVER, the communication will have to block only on THIS socket only and leave the rest
     * of the sockets unaffected!
     * 
     * @param port
     * @param size
     * @param timeout
     * @return 
     */
    private static Pipe chokeTheServer(int port,int size, int timeout){
        byte[] outData=DataGenerator.generateRandomCharBuff(size);
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Choke the server. The total written bytes should be lower than the actual buffer ("+size+" bytes)", port, outData, 0, timeout, null);
        pipe.addJob(new ReadWriteJob(0, size));
        pipe.addJob(new VerifyInfo());  
        
        
        return pipe;
    }
    
    /**
     * 
     * Sends a looong chunk and see if the chunks between newlines are echoed back correctly.
     * Also collects the thread ID's if any.
     * 
     * @param port
     * @param size
     * @param timeout
     * @return 
     */
    private static Pipe LongBufferNewLineServer(int port, int size, int timeout){
        byte[] outData=DataGenerator.generateRandomCharBuffWithNewLine(size, 200);
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Send and receive a loong buffer. Evaluate the newline behavior", port, outData, size, timeout, null);
        
        pipe.addJob(new ReadWriteJob(0, size));
        pipe.addJob(new VerifyInfo());  
        pipe.addJob(new VerifyNewlineMultiplicity());
        pipe.addJob(new VerifyCorrectEchoedNewline());

        return pipe;
        
    }
    
    
    private static Pipe ShortBufferNewlineBehavior(int port, int timeout){
        String s1="AAA\n\rBBB\nCCC\nDDD";
       
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Send and receive a loong buffer. Evaluate the newline behavior", port, s1.getBytes(),500 , timeout, null);
        pipe.addJob(new ReadWriteJob(0,3));
        pipe.addJob(new VerifyInfo());  
        pipe.addJob(new VerifyNewlineMultiplicity());
        pipe.addJob(new ReadWriteJob(3,1));
        pipe.addJob(new VerifyInfo());  
        pipe.addJob(new VerifyNewlineMultiplicity());
        pipe.addJob(new ReadWriteJob(4,s1.length()-4));
        pipe.addJob(new VerifyInfo());  
        pipe.addJob(new VerifyNewlineMultiplicity());
        pipe.addJob(new VerifyCorrectEchoedNewline());
        
        return pipe;
    }
    
    /**
     * Main method. First parameter is the port (default 1000) and the second 
     * the server's chunk dimension (default 1000)
     * 
     * To run, use: java -jar pa3_test.jar port_no chunk_size
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port=1000;
        int serverChunkSize=1000;
        if(args.length>=1)
            try{port=Integer.parseInt(args[0]);}catch(Exception e){};

        if(args.length>=2)
            try{serverChunkSize=Integer.parseInt(args[1]);}catch(Exception e){};
            
            
        System.out.println("Start testing server on port "+port);
        System.out.println("Expected server chunk size: "+serverChunkSize+". Some tests will incorrectly fail if this value is wrong");
        System.out.println("If errors, read the messages.");
        
        
       //Fill in some jobs Feel free to add/remove/create your own sadistic jobs!
        Pipe[] jobs={
                chokeTheServer(port,100000,5000),
                smallChunkTest(port, serverChunkSize, 1000),
                largeBufferTransfer(port, 1000000, 15000),
                smallChunkTest(port, serverChunkSize, 1000),
                largeBufferTransfer(port, 1000000, 15000),
                LongBufferNewLineServer(port,1000000,15000 ),
                ShortBufferNewlineBehavior(port, 1000),
                LongBufferNewLineServer(port,1000000,15000 ),
                ShortBufferNewlineBehavior(port, 1000),
                
        };
        
        System.out.println("\nStarting jobs...\n");
        
        for(Pipe p:jobs)
            p.startPipe();
        
        for(Pipe p:jobs){
            p.waitForFinish();
            System.out.println(p.getResults());
        }

        System.out.println("\nDone.");
     
    }
}
