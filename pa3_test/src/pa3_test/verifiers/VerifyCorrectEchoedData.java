/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test.verifiers;

/**
 *
 * @author visoft
 */
public class VerifyCorrectEchoedData extends AbstractVerifier {

    public VerifyCorrectEchoedData() {
        super("Verify if the read data (whatever size) is identical with the sent data");
    }
    
    
    
    @Override
    protected String doVerification() {
        int diff=checkBuffers(0, RWResult.getTotalReadBytes());
        if(diff>=0)
            return "The echoed data does not match the sent data. First difference at: "+diff;
        return "All ok!";
    }
    
}
