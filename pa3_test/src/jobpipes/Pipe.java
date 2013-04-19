/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobpipes;

import java.io.StringWriter;
import java.net.Socket;
import pa3_test.ThreadedReadWrite;

/**
 *
 * @author visoft
 */
public interface Pipe {
    int getPort();
    Socket getSocket();
    StringWriter getOutputStream();
    ThreadedReadWrite getReadWriteThread();
    
    void addJob(AsyncJob job);
    void startPipe();
    void waitForFinish();
    String getResults();
}
