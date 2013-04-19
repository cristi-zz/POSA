/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobpipes;

/**
 *
 * @author visoft
 */
public interface AsyncJob {
    void StartTheJob();
    void WaitToFinish(int milisec);
    void Register(Pipe pipe);
    
}
