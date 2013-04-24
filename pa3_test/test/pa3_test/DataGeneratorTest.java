/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author visoft
 */
public class DataGeneratorTest {
    
    public DataGeneratorTest() {
    }

    @Test
    public void testGenerateRandomCharBuffWithNewLine() {
        byte[] buff;
        buff=DataGenerator.generateRandomCharBuffWithNewLine(1000, 10);
        int sum=0;
        for(int i=0;i<buff.length;i++)
            if(buff[i]=='\n' || buff[i]=='\r')
                sum++;
        assertTrue("No newlines were generated",sum>0);
        
    }
}
