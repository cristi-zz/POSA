/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import java.util.ArrayList;
import java.util.Random;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

/**
 * Generate buffers
 * @author visoft
 */
public class DataGenerator {
    public static byte[] generateRandomBuff(int dim){
        byte[] buff=new byte[dim];
        for(int i=0;i<dim;i++)
            buff[i]=(byte)Math.round(Math.random()*255);
        return buff;
    }
    
    public static byte[] generateRandomCharBuff(int dim){
        byte[] buff=new byte[dim];
        for(int i=0;i<dim;i++)
            buff[i]=(byte)Math.round(Math.random()*(126-32)+32);
        return buff;
    }
    /**
     * Randomly generates some buffer, inserting newlines. The mean "length"
     * of a line is meanLineLength.
     * Of course, this is a mean value, the actual values are randomly generated.
     * The random variable is normal, with SD ~= linelength/4
     * 
     * @param dim
     * @param meanLineLength
     * @return 
     */
    public static byte[] generateRandomCharBuffWithNewLine(int dim, int meanLineLength){
        Random rnd=new Random();
        int NoOfRand=dim/meanLineLength;
        byte[] buff=generateRandomCharBuff(dim);
        for(int i=0;i<NoOfRand;i++){
            int pos=Math.max(rnd.nextInt(),0)%dim;
            byte c;
            if(rnd.nextBoolean())
                c='\n';
            else
                c='\r';
            buff[pos]=c;
        }
        return buff;
        
    }
    
    
    
}
