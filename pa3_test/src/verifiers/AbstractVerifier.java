/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers;

import java.io.PrintStream;
import java.io.StringWriter;
import jobpipes.AsyncJob;
import jobpipes.Pipe;
import pa3_test.ThreadedReadWrite;
import verifiers.util.VerifyUtils;

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
