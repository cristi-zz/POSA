/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

import org.junit.Test;
import static org.junit.Assert.*;
import verifiers.util.mockThreadRW;

/**
 *
 * @author visoft
 */
public class VerifyCorrectEchoedNewlineTest {
    


    @Test
    public void testSomeMethod() {
        String s1,s2;
        s1="11aaavvvddd\n\r69ekkfkfeofaweofaer\r11gfjefje\r\n";
        s2="aaavvvddd\n\rekkfkfeofaweofaer\rgfjefje\r\neeekawoekr";
        mockThreadRW mock=new mockThreadRW(s1.getBytes(), s2.getBytes());
        VerifyCorrectEchoedNewline ver=new VerifyCorrectEchoedNewline(mock);
        String rez;
        rez=ver.doVerification();
        assertTrue("The verification should be ok",rez.contains("All "));
        
    }
}
