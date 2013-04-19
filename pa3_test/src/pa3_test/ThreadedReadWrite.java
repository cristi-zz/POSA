/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobpipes.AsyncJob;

/**
 * Busy waiting, nasty, unstable, ugly send and receive. Elegant solution, using Async transfer or even a reactor.
 * Implemented maybe after the deadlines ;)
 * 
 * DO NOT DO THIS AT WORK/HOME/EXAM etc!
 * 
 * How to choke a server:
 * Write, but never read.
 * The problem is that the outputStream.write() blocks. It doesn't respond to interrupt events
 * so this piece of test code will block indefitively.
 * That's why a sotp() is sent after timeout, even if is unsafe and deprecated.
 * 
 * 
 * @author visoft
 */
public class ThreadedReadWrite {

    public ThreadedReadWrite(Socket out_socket,byte[] buff, int maxReceived)  {
        this.out_buff = buff;
        if(maxReceived>0)
            this.in_buff= new byte[maxReceived];
        else
            this.in_buff=null;
        totalReadBytes=0;
        totalWrittenBytes=0;
        RWThread=null;
        this.socket=out_socket;
    }

    private class SyncReadWrite extends Thread{

        public SyncReadWrite(Socket socket,int start, int len) {
            this.start = start;
            len=Math.min(len, out_buff.length);
            this.len = len;
            this.socket=socket;
        }
        /**
         * Busy send and busy receive. Elegant solution, using Async transfer or reactor.
         * The write blocks if the server chokes with data.
         * Implemented maybe after the deadlines ;)
         * 
         * DO NOT DO THIS AT WORK/HOME/EXAM etc!
         * 
         * The scope of this method is to write some bytes to the socket and then read some input.
         * This thread is stopped after some time, so 
         */
        @Override
        public void run() {
            InputStream str_in = null;
            OutputStream str_out=null;
            if(socket==null){
                Logger.getLogger(ThreadedReadWrite.class.getName()).log(Level.INFO, "Socket is null");
                return;
            }
            try {
                //Write
                str_out = socket.getOutputStream();
                str_in=socket.getInputStream();
                len=Math.min(start+len, out_buff.length);
                for(int i=start;(i<len) && (!isInterrupted());i++){
                    str_out.write(out_buff,i,1);
                    str_out.flush();
                    totalWrittenBytes++;
                    attemptToRead(str_in);
                }
                //Read
                while(!isInterrupted() && str_in!=null && totalReadBytes<in_buff.length){
                    //Wait till interrupted by the caller thread, or the expected amount of data was read from server
                    sleep(10);
                    attemptToRead(str_in);
                };
            
            } catch (IOException ex) {
                //Logger.getLogger(ThreadedReadWrite.class.getName()).log(Level.INFO, "Socket closed", ex);
                return;
            }catch (InterruptedException ex) {
                //Logger.getLogger(ThreadedReadWrite.class.getName()).log(Level.INFO, "Interrupted, by timeout.", ex);
                return;
            }
        }
        
        private void attemptToRead(InputStream str_in) throws InterruptedException, IOException{
            if(in_buff==null)
                return;
            int k;
            k=str_in.available();
            if(k>0 && totalReadBytes<in_buff.length){
                k=str_in.read(in_buff, totalReadBytes, k);
                totalReadBytes+=k;
            }
        }
        
       int start, len;
       Socket socket;
    }
    
   
    
    /**
     * Main method, execute the send/receive with timeout
     * start and length influence the write operation.
     * Read operation appends to current buffer until it is full.
     * 
     */
    public synchronized void Execute( int start, int length){
        this.start=start;
        this.length=length;
        if(socket==null)
            return;
        killJob();
        RWThread=new SyncReadWrite(socket,start, length);
        RWThread.start();
    }
    
    public void killJob(){
        if(RWThread!=null){
            if(RWThread.isAlive())
                RWThread.interrupt();
            try {
                RWThread.join(1000); //allow to exit gracefuly 
                RWThread.stop();//Because the thread is stuck in a blockable write() 
                
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadedReadWrite.class.getName()).log(Level.SEVERE, "This join shouldn't be interrupted", ex);
            }
            RWThread=null;
        }        
    }
    
    /**
     * Maybe you want to join this thread!
     * @return 
     */
    public synchronized Thread getReadWriteThread() {
        return RWThread;
    }
    
    
    public int getTotalReadBytes() {
        return totalReadBytes;
    }

    public int getTotalWrittenBytes() {
        return totalWrittenBytes;
    }

    public byte[] getOut_buff() {
        return out_buff;
    }

    public byte[] getIn_buff() {
        return in_buff;
    }
    
    private byte[] out_buff;
    private byte[] in_buff;
    private volatile int totalReadBytes, totalWrittenBytes;
    int start,length;
    Thread RWThread;
    Socket socket;
}
