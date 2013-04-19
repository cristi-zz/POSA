/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test.verifiers;

import java.io.PrintStream;
import java.io.StringWriter;
import jobpipes.AsyncJob;
import jobpipes.Pipe;
import pa3_test.ThreadedReadWrite;

/**
 * Verify the results of a send/receive
 * 
 * @author visoft
 */
public abstract class AbstractVerifier implements AsyncJob {
   protected abstract String doVerification();

    public AbstractVerifier(String name) {
        this.name = name;
        this.RWResult = null;
    }

    public AbstractVerifier(String name, ThreadedReadWrite RWResult) {
        this.name = name;
        this.RWResult = RWResult;
    }
   
   /**
    * Template method that does the verification, Outputs by default to stdout.
    * Change the behavior with setStdout()
    * @return 
    */
   public void Verify(){
       String rezult;
       rezult=doVerification();
       out.append(name+" : "+rezult+"\n");
   }
   
   /**
    * Verifies the identity of the two buffers.
    * -1 in case of match, the position of the first difference in case of fail.
    * @param start
    * @param length
    * @return 
    */
   protected int checkBuffers(int start, int length){
       if(RWResult==null)
           throw new RuntimeException("ThreadReadWrite is null");
       byte[] buff1, buff2;
       buff1=RWResult.getIn_buff();
       buff2=RWResult.getOut_buff();
       length=Math.min(Math.min(buff1.length,buff2.length),start+length);
       for(int i=start;i<length;i++){
           if(buff1[i]!=buff2[i])
               return i;
       }
       return -1;
   }

   /**
    * Maybe you want to write a log, or to group results together, expecially in multithreading env.
    * @param out 
    */
    public void setStdout(StringWriter out) {
        this.out = out;
    }

    @Override
    public void StartTheJob() {
        Verify();
    }

    @Override
    public void WaitToFinish(int milisec) {
        return;
    }

    @Override
    public void Register(Pipe pipe) {
        this.RWResult=pipe.getReadWriteThread();
        this.out=pipe.getOutputStream();
    }

    public void setRWResult(ThreadedReadWrite RWResult) {
        this.RWResult = RWResult;
    }
   
   
   protected String name;
   protected ThreadedReadWrite RWResult;
   protected StringWriter out;
}
