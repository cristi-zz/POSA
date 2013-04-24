/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers.util;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import verifiers.util.VerifyUtils.PairIdx;
import static verifiers.util.VerifyUtils.*;

/**
 *
 * @author visoft
 */
public class VerifyUtilsTest {
    
    public VerifyUtilsTest() {
    }

    
    @Test
    public void testCheckBuffReverse(){
        String s1,s2;
        s1="aaabbbcccddd";
        s2="eeefffggaaabbbcccddd";
        int rez;
        rez=checkBuffersReverse(s1.getBytes(),s2.getBytes(),8,16,8);
        assertEquals("There should be no difference", -1,rez);
    }

        @Test
    public void testCheckBuffReverseLastCharWrong(){
        String s1,s2;
        s1="eaaabbbccc";
        s2="aaaaaadaaabbbccc";
        int rez;
        rez=checkBuffersReverse(s1.getBytes(),s2.getBytes(),9,15,10);
        assertEquals("There should detect a difference at offset ", 9,rez);
    }

    
    @Test
    public void testMatchNewlinesReverse() {
        String s1,sref;
        s1="123aaaaaaaaaa\n333bbbbb\n45eeeeeeee\r";
        sref="aaaaaaaaaa\nbbbbb\neeeeeeee\rfffffffff";
        byte[] b1, bref;
        b1=s1.getBytes();
        bref=sref.getBytes();
        ArrayList<VerifyUtils.PairIdx> pairs=VerifyUtils.matchNewlinesReverse(b1, bref);
        assertEquals("The number of pairs is wrong",3, pairs.size());
        String st,st_ref;PairIdx p;
        
        for(int i=0;i<pairs.size();i++){
            p=pairs.get(i);
            st=s1.substring(p.i-p.len+1, p.i+1);
            st_ref=sref.substring(p.i_ref-p.len+1, p.i_ref+1);
            assertEquals("The cutted strings mistmatch",st_ref, st);
        }        
    }
    
    @Test
    public void testMatchNewlinesReverse1() {
        String s1,sref;
        s1="123aaa\n";
        sref="aaa\n";
        byte[] b1, bref;
        b1=s1.getBytes();
        bref=sref.getBytes();
        ArrayList<VerifyUtils.PairIdx> pairs=VerifyUtils.matchNewlinesReverse(b1, bref);
        assertEquals("The number of pairs is wrong",1, pairs.size());
/*        String st,st_ref;PairIdx p;
        
        for(int i=0;i<pairs.size();i++){
            p=pairs.get(i);
            st=s1.substring(p.i-p.len+1, p.i+1);
            st_ref=sref.substring(p.i_ref-p.len+1, p.i_ref+1);
            assertEquals("The cutted strings mistmatch",st_ref, st);
        }        
        */ 
    }

    
    
    
    @Test
    public void testVerifyNewlineMulti_noeco1() {
        String in, out,rez;
        in="asdfasdfa";
        out="aaa\nbbb\nccc\n";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect that server returned no newline",rez.startsWith("Fail: "));
        
    }
    @Test
    public void testVerifyNewlineMulti_noeco2() {
        String in, out,rez;
        in="asdfasdfa";
        out="aaaasdfasdf";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect that server returned without encountering newline",rez.startsWith("Fail: "));
        
    }
    @Test
    public void testVerifyNewlineMulti_ecoafternl() {
        String in, out,rez;
        in="aaa\nbbb\nccc";
        out="aaa\nbbb\ncccdddeee";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect that server returned data after newline",rez.startsWith("Fail: "));
        
    }

    @Test
    public void testVerifyNewlineMulti_mistmatchlasteco() {
        String in, out,rez;
        in="aaa\nbbb\ncccdddee1\n";
        out="aaa\nbbb\ncccdddeee\nfffff";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect that server returned data after newline",rez.startsWith("Fail: "));
        
    }

    @Test
    public void testVerifyNewlineMulti_correct() {
        String in, out,rez;
        in="aaa\nbbb\ncccdddeee\n";
        out="aaa\nbbb\ncccdddeee\nfffff";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect correct echo",rez.startsWith("All ok!"));
        
    }

    @Test
    public void testVerifyNewlineMulti_correctThreadID() {
        String in, out,rez;
        in="aaa\nbbb\n343343cccdddeee\n";
        out="aaa\nbbb\ncccdddeee\nfffff";
        rez=verifyNewlineMultiplicity(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect correct echo",rez.startsWith("All ok!"));
        
    }
    @Test
    public void testverifyNewlineEchoedCorrect() {
        String in, out,rez;
        in="aaa\n234bbb\n3233ccc\nddd\n";
        out="aaa\nbbb\nccc\nddd\neee";
        rez=verifyNewlineEchoedCorrect(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect correct echo",rez.startsWith("All Ok!"));
        
    }
    @Test
    public void testverifyNewlineEchoedCorrect_fail() {
        String in, out,rez;
        in="aaa\n234cbb\n3233ccc\nddd\n";
        out="aaa\nbbb\nccc\nddd\neee";
        rez=verifyNewlineEchoedCorrect(in.getBytes(),out.getBytes());
        
        assertTrue("The method should detect wrong echo",rez.startsWith("Fail: "));
        
    }

    @Test
    public void testcollectThreadID1() {
        String in, out,rez;
        in="aaa\n234cbb\n3233ccc\nddd\n";
        out="aaa\nbbb\nccc\nddd\neee";
        ArrayList<String> id=collectThreadID(in.getBytes(), out.getBytes());
        
        assertEquals("The number of id's is wrong",2,id.size());
        assertEquals("The first id is wrong","234",id.get(0));
        assertEquals("The second id is wrong","3233",id.get(1));
                
        
    }

    @Test
    public void testcollectThreadID2() {
        String in, out,rez;
        in="11abc\n\r69def\r11ghi\r\n";
        out="abc\n\rdef\rghi\r\neeekawoekr";
        ArrayList<String> id=collectThreadID(in.getBytes(), out.getBytes());
        
        assertEquals("The number of id's is wrong",3,id.size());
        assertEquals("The first id is wrong","11",id.get(0));
        assertEquals("The first id is wrong","69",id.get(1));
        assertEquals("The second id is wrong","11",id.get(2));
                
        
    }    
}
