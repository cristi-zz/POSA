/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobpipes;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pa3_test.ThreadedReadWrite;

/**
 *
 * @author visoft
 */
public class ServerTestPipe implements Pipe, Runnable{

    public ServerTestPipe(String name, int port, byte[] outData, int maxExpecedInputLen,int jobTimeout, StringWriter out) {
        this.name = name;
        this.port = port;
        this.out_buff=outData;
        this.maxExpectedInput=maxExpecedInputLen;
        jobQueue=new ArrayList<>();
        socket=null;
        if(out==null)
            this.out=new StringWriter();
        else
            this.out=out;
        RWThread=null;
        this.jobTimeout=jobTimeout;
    }
    
    
    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public ThreadedReadWrite getReadWriteThread() {
        return RWThread;
    }


    @Override
    public void addJob(AsyncJob job) {
        jobQueue.add(job);
    }

    /**
     * Starts the pipe in its own thread.
     */
    @Override
    public void startPipe() {
        ownThread=new Thread(this);
        ownThread.start();
    }

    @Override
    public void waitForFinish() {
        if(ownThread==null)
            return;
        else
            while(true)
            try {
            ownThread.join();
            break;
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerTestPipe.class.getName()).log(Level.SEVERE, null, ex);
        }
        ownThread=null;
    }
    
    @Override
    public void run() {
        out.append("Starting test pipe: "+name+"\n");
        try {
            //init the data
            out.append("Initializing ... ");
            init();
            out.append("done!\n");
       
            //Process each job, waiting for the results 
            for(AsyncJob job:jobQueue){
                job.StartTheJob();
                job.WaitToFinish(jobTimeout);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerTestPipe.class.getName()).log(Level.SEVERE, "Cannot start the pipe. Halting.", ex);
            out.append("fail\n");
        }finally{
            if(socket!=null)
            try {
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerTestPipe.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        out.append("End test pipe.\n");
    }

    private void init() throws Exception{
        //Connect the socket
        socket=new Socket("127.0.0.1", port);
        //init the read/write
        RWThread=new ThreadedReadWrite(socket, out_buff, maxExpectedInput);
        //register all the components, so each component can take the neccesary info from this pipe
        for(AsyncJob job:jobQueue)
            job.Register(this);
        
    }
    
    
    private String name;
    private int port;
    private final static String host="127.0.0.1";//don't hack somebody else pls!
    private Socket socket;
    private ThreadedReadWrite RWThread;
    private byte[] out_buff;
    private int maxExpectedInput;
    private ArrayList<AsyncJob> jobQueue;
    private StringWriter out;
    private Thread ownThread;
    private int jobTimeout;

    @Override
    public StringWriter getOutputStream() {
        return out;
    }

    @Override
    public String getResults() {
        return out.toString();
    }

    
}
