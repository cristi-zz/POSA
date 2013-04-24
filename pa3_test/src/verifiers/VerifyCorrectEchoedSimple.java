/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

import verifiers.util.VerifyUtils;

/**
 * Verify if the echoed buffer is identical with the sent buffer.
 * Only the received bytes are expected to be identical (i.e. ignore the bytes that were sent and not received)
 * This class will expect the messages to be identical. It will fail at PA#4 where the server must send the threadID
 * @author visoft
 */
public class VerifyCorrectEchoedSimple extends AbstractVerifier {

    public VerifyCorrectEchoedSimple() {
        super("Verify if the read data (whatever size) is identical with the sent data. This method will fail for PA#4 where the ThreadID is expected from server");
    }
    
    
    
    @Override
    protected String doVerification() {
        int diff=VerifyUtils.checkBuffers(RWResult.getIn_buff(),RWResult.getOut_buff(),0,0, RWResult.getTotalReadBytes());
        if(diff>=0)
            return "The echoed data does not match the sent data. First difference at: "+diff;
        return "All ok!";
    }
    
}
