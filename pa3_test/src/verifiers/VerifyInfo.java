/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

/**
 * Show some info about the transfer.
 * @author visoft
 */
public class VerifyInfo extends AbstractVerifier{

    public VerifyInfo() {
        super("Info about the stream: ");
    }

    @Override
    protected String doVerification() {
        return "No of sent bytes "+RWResult.getTotalWrittenBytes()+" No of received bytes "+RWResult.getTotalReadBytes();
    }
    
}
