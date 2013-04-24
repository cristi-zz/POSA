/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author visoft
 */
public class VerifyUtils {
    /**
     * Verifies the identity of the two buffers.
     * -1 in case of match, the position of the first difference in case of fail.
     * The buffers are verified only on length portion, starting from corresponding offsets
     * and continuing towards the end of the buffers
     * @param buff1
     * @param buff2
     * @param offset1
     * @param offset2
     * @param length
     * @return 
     */
    public static int checkBuffers(byte[] buff1, byte buff2[], int offset1, int offset2, int length){
        int len1,len2;
        if(buff1==null && buff2==null)
            return -1;
        if((buff1==null) || ( buff2==null))
            return 0;
        len1=Math.min(offset1+length,buff1.length);
        len2=Math.min(offset2+length,buff2.length);
        for(int i=0;i<length;i++)
            if(buff1[i+offset1]!=buff2[i+offset2])
                return i;
        return -1;
                
    }
    /**
     * Verifies the identity of the two buffers.
     * -1 in case of match, the position of the first difference in case of fail.
     * The buffers are verified only on length portion, starting from corresponding offsets
     * but backwards, to the beginning of the buffer (index 0)
     * @param buff1
     * @param buff2
     * @param offset1
     * @param offset2
     * @param length
     * @return 
     */
    
    public static int checkBuffersReverse(byte[] buff1, byte buff2[], int offset1, int offset2, int length){
        int len;
        if(buff1==null && buff2==null)
            return -1;
        if((buff1==null) || ( buff2==null))
            return 0;
        if(offset1-length+1<0)
            return 0;
        if(offset2-length+1<0)
            return 0;
        if(offset1>=buff1.length)
            offset1=buff1.length-1;
        if(offset2>=buff2.length)
            offset2=buff2.length-1;
        for(int i=0;i<length;i++)
            if(buff1[offset1-i]!=buff2[offset2-i])
                return i;
        return -1;
                
    }
    
    
    /**
     * Returns the indices in buff where a newline appears. Each \n or \r are treated
     * independently. There is no \r\n 
     * @param buff
     * @return 
     */
    public static ArrayList<Integer> markNewlines(byte[] buff){
        ArrayList<Integer> newlines=new ArrayList<>();
        if(buff==null)
            throw new RuntimeException("The buffer is null!");
        for(int i=0;i<buff.length;i++)
            if(buff[i]=='\n' || buff[i]=='\r')
                newlines.add(i);
        
        return newlines;
    }
    /**
     * Simple structure that holds a pair of indexes and a length
     */
    public static class PairIdx{
        public int i,i_ref,len;

        public PairIdx(int i, int i_ref, int len) {
            this.i = i;
            this.i_ref = i_ref;
            this.len = len;
        }
        
    }
    /**
     * Mathches the newlines in ref_buff (usually the sent buffer) with
     * the newlines in buff (received buffer).
     * The matching begins at the end of the ref_buff and continues towards the beginning.
     * Unmatched newlines from ref_buff are ignored.
     * The len stored in PairIdx is counted from i_ref towards the beginning.
     * @param buff
     * @param ref_buff
     * @return 
     */
    public static ArrayList<PairIdx> matchNewlinesReverse(byte[] buff, byte[] ref_buff){
        ArrayList<Integer> buff_eol,ref_eol;
        ArrayList<PairIdx> match=new ArrayList<>();
        buff_eol=markNewlines(buff);
        ref_eol=markNewlines(ref_buff);
        
        int i_ref, i1,len;
        i1=buff_eol.size()-1;
        if(i1<0)
            return match;
        for(i_ref=ref_eol.size()-1;i_ref>=0;i_ref--){
            if(i_ref>0)
                len=ref_eol.get(i_ref)-ref_eol.get(i_ref-1);
            else
                len=ref_eol.get(i_ref)+1;
            match.add(new PairIdx(buff_eol.get(i1),ref_eol.get(i_ref),len));
            i1--;
            if(i1<0)
                break;
        }
        
        return match;
    }
    /**
     * 
     * @param rec_buff
     * @param sent_buff
     * @return 
     */
    public static String verifyNewlineMultiplicity(byte[] rec_buff, byte[] sent_buff){
        ArrayList<VerifyUtils.PairIdx> pairs=VerifyUtils.matchNewlinesReverse(rec_buff,sent_buff);
        
         ArrayList<Integer> out_eol=VerifyUtils.markNewlines(sent_buff);
         
         //if there are no pairs, but out_eol sent at least one pair, then the echo server choked with that newline
         if(pairs.size()==0 && out_eol.size()>0)
             return "Fail: The server echoed no newline, while the client sent at least one.";

         //if there is no newline in output, but data in the input, error.
         if(out_eol.size()==0 && rec_buff.length>0)
             return "Fail: The server echoed data before the first newline was sent";
         
         //if there are no pairs and the client sent no newline, cannot do the checking.
         if(pairs.size()==0 && out_eol.size()==0) 
             return "Unkwnown, the client didn't sent any newline.";
         
        //Take the last sent pair (first to be identified) and check if the received data is identical with the sent data
        PairIdx pair=pairs.get(0);

        //if the server echoed more data after the last newline, is not following the specs
        if(pair.i<rec_buff.length-1)
            return "Fail: The server echoed data without encountering newline! Last newline index: "+pair.i+" last received byte index: "+(rec_buff.length-1);
        
        int diff=VerifyUtils.checkBuffersReverse(rec_buff, sent_buff, pair.i, pair.i_ref, pair.len);

        if(diff>=0)
            return "Fail: The last echoed line of data does not match the sent data. Last difference at: "+diff;
        return "All ok!";        
        
    }
    /**
     * Verifies if the echoed buffer is correct, wrt to newlines.
     * Ignores threadID's if any
     * @param rec_buff
     * @param sent_buff
     * @return 
     */
    public static String verifyNewlineEchoedCorrect(byte[] rec_buff, byte[] sent_buff){
        ArrayList<VerifyUtils.PairIdx> pairs=VerifyUtils.matchNewlinesReverse(rec_buff,sent_buff);
         //if there are no pairs and the client sent no newline, cannot do the checking.
         if(pairs.size()==0) 
             return "Unkwnown. No EOL pairs between send and receive buffer. Try to collect more data, or the server behaves incorrectly";
        
         int dif;
         //Check each pair
         for(PairIdx p: pairs){
             dif=VerifyUtils.checkBuffersReverse(rec_buff, sent_buff, p.i, p.i_ref, p.len);
             if(dif>=0)
                 return "Fail: Last received data mistmatch at index: "+(p.i-dif);
         }
         return "All Ok!";
    }
    /**
     * Collects the thread id's that were echoed back to the client.
     * No chekcing if the echoed data is identical or not.
     * @param rec_buff
     * @param sent_buff
     * @return 
     */
    public static ArrayList<String> collectThreadID(byte[] rec_buff, byte[] sent_buff){
        ArrayList<String> threadID=new ArrayList<>();
        ArrayList<VerifyUtils.PairIdx> pairs=VerifyUtils.matchNewlinesReverse(rec_buff,sent_buff);
         //if there are no pairs and the client sent no newline, cannot do the checking.
         if(pairs.size()==0) 
             return threadID;
         PairIdx p;
         int startIdx,endIdx;
         byte[] buf;
         startIdx=0;
         for(int i=pairs.size()-1;i>=0;i--){
             p=pairs.get(i);
             endIdx=p.i-p.len;
             if(endIdx-startIdx>0){
                buf=Arrays.copyOfRange(rec_buff, startIdx, endIdx+1);
                 threadID.add(new String(buf));
             }
             startIdx=p.i+1;
         }         
        
        return threadID;
    }
    
}
