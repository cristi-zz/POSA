/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

import pa3_test.ThreadedReadWrite;

/**
 * Verify that the exact no of bytes were received from the server. 
 * No check on the values of the bytes
 * @author visoft
 */
public class VerifyExactReadValue extends AbstractVerifier {

    public VerifyExactReadValue(int expectedReadBytes) {
        super("Expected number of read bytes is "+expectedReadBytes);
        this.expectedReadBytes = expectedReadBytes;
    }

    public VerifyExactReadValue(int expectedReadBytes, ThreadedReadWrite RWResult) {
        this(expectedReadBytes);
        this.RWResult=RWResult;
    }

    @Override
    protected String doVerification() {
        if(RWResult.getTotalReadBytes()==expectedReadBytes)
            return "OK";
        else
            return "Fail! Got instead "+RWResult.getTotalReadBytes()+" bytes";
    }
    int expectedReadBytes;
}
