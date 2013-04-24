/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

import pa3_test.ThreadedReadWrite;

/**
 * Verify if the echo have the corresponding block multiplicity
 * @author visoft
 */
public class VerifyBlockMultiplicity extends AbstractVerifier{

    public VerifyBlockMultiplicity(int chunkSize) {
        super("The server obeys the \"Reply a chunk at a time\" rule. Chunk size is: "+chunkSize);
        this.chunkSize = chunkSize;
    }

    public VerifyBlockMultiplicity(int chunkSize, ThreadedReadWrite RWThread) {
        this(chunkSize);
        this.RWResult=RWThread;
    }
    
    @Override
    protected String doVerification() {
        int expectedReadSize;
        int writtenData;
        writtenData=RWResult.getTotalWrittenBytes();
        expectedReadSize=(writtenData/chunkSize)*chunkSize;
        if(expectedReadSize!=RWResult.getTotalReadBytes())
            return "No of read bytes is wrong. Expected "+expectedReadSize+" got:"+RWResult.getTotalReadBytes();
        return "All ok!";

    }
    
    private int chunkSize;
}
