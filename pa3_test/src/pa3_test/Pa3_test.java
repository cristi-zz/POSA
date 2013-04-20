/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import pa3_test.verifiers.VerifyExactReadValue;
import pa3_test.verifiers.VerifyBlockMultiplicity;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobpipes.Pipe;
import jobpipes.ServerTestPipe;
import pa3_test.verifiers.VerifyCorrectEchoedData;
import pa3_test.verifiers.VerifyInfo;

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
        pipe.addJob(new VerifyCorrectEchoedData());
        pipe.addJob(new ReadWriteJob(ChunkSize-1, 1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyBlockMultiplicity(ChunkSize));
        pipe.addJob(new VerifyExactReadValue(ChunkSize));
        pipe.addJob(new VerifyCorrectEchoedData());
        pipe.addJob(new ReadWriteJob(ChunkSize, ChunkSize+1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyExactReadValue(2*ChunkSize));
        pipe.addJob(new VerifyBlockMultiplicity(ChunkSize));
        pipe.addJob(new VerifyCorrectEchoedData());

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
        pipe.addJob(new VerifyCorrectEchoedData());
        
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
        
        
       //Fill in some jobs
        Pipe[] jobs={
                chokeTheServer(port,100000,5000),
                smallChunkTest(port, serverChunkSize, 1000),
                largeBufferTransfer(port, 1000000, 15000),
                smallChunkTest(port, serverChunkSize, 1000),
                largeBufferTransfer(port, 1000000, 15000)
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
