/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package verifiers.util;

import java.net.Socket;
import pa3_test.ThreadedReadWrite;

/**
 *
 * @author visoft
 */
public class mockThreadRW extends ThreadedReadWrite {

    public mockThreadRW(byte[] recv_buff, byte sent_buff[]) {
        super(null,null, 0);
        this.recv_buff=recv_buff;
        this.sent_buff=sent_buff;
    }

    @Override
    public byte[] getOut_buff() {
        return sent_buff;
    }

    @Override
    public byte[] getIn_buff() {
        return recv_buff;
    }
    
    private byte[] recv_buff;
    private byte sent_buff[];
}
