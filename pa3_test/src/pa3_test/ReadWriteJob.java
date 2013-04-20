/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import java.io.StringWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobpipes.AsyncJob;
import jobpipes.Pipe;

/**
 * Wrapper for the ThreadedReadWrite.
 * @author visoft
 */
public class ReadWriteJob implements AsyncJob{

    public ReadWriteJob(int start, int length) {
        this.start = start;
        this.length = length;
        this.RWThread=null;
    }
    
    @Override
    public void StartTheJob() {
        out.append("Starting read/write from position "+start+" to "+(start+length)+" size: "+length+"\n");
        RWThread.Execute(start, length);
    }


    @Override
    public void WaitToFinish(int milisec) {
        if(RWThread==null)
            return;
        while(true){
            try {
                RWThread.getReadWriteThread().join(milisec);
                RWThread.killJob();
                break;
            } catch (InterruptedException ex) {
            }
        }
    }


    @Override
    public void Register(Pipe pipe) {
        RWThread=pipe.getReadWriteThread();
        out=pipe.getOutputStream();
    }
    
    private ThreadedReadWrite RWThread;
    private int start, length, timeToWait;
    private StringWriter out;
}
