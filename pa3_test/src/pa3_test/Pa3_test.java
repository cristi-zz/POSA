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
import jobpipes.ServerTestPipe;
import pa3_test.verifiers.VerifyInfo;

/**
 *
 * @author visoft
 */
public class Pa3_test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port=1000;
        if(args.length>=1)
            try{port=Integer.parseInt(args[0]);}catch(Exception e){};
        System.out.println("Start testing server on port "+port);
        
        
        byte[] outData=DataGenerator.generateRandomCharBuff(21);
        
        ServerTestPipe pipe;
        pipe=new ServerTestPipe("Simple test suite", port, outData, 1000, 1000, null);
        
        pipe.addJob(new ReadWriteJob(0, 9));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyExactReadValue(0));
        pipe.addJob(new VerifyBlockMultiplicity(10));
        pipe.addJob(new ReadWriteJob(9, 1));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyBlockMultiplicity(10));
        pipe.addJob(new VerifyExactReadValue(10));
        pipe.addJob(new ReadWriteJob(10, 11));
        pipe.addJob(new VerifyInfo());        
        pipe.addJob(new VerifyExactReadValue(20));
        pipe.addJob(new VerifyBlockMultiplicity(10));
        
        
        ServerTestPipe flood;
        flood=new ServerTestPipe("Flood and block the read buffers", port, DataGenerator.generateRandomCharBuff(1000000), 0, 5000, null);
        flood.addJob(new ReadWriteJob(0, 1000000));
        flood.addJob(new VerifyInfo());
        
        ServerTestPipe longprocess =new ServerTestPipe("Some long buffer", port, DataGenerator.generateRandomCharBuff(1000000), 1000000, 25000, null);
        longprocess.addJob(new ReadWriteJob(0, 1000000));
        longprocess.addJob(new VerifyBlockMultiplicity(10));
        longprocess.addJob(new VerifyInfo());
        
        flood.startPipe();
        longprocess.startPipe();
        pipe.startPipe();
        
        
        
        pipe.waitForFinish();
        flood.waitForFinish();
        longprocess.waitForFinish();
        
        System.out.println(pipe.getResults());
        System.out.println(flood.getResults());
        System.out.println(longprocess.getResults());
        
     
    }
}
