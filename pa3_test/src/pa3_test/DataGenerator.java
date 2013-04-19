/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

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
    
}
