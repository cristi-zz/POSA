/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pa3_test;

/**
 *
 * @author visoft
 */
public class Pa3_test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port=1000;
        if(args.length>=1)
            try{port=Integer.parseInt(args[0]);}catch(Exception e){};
        System.out.println("Start testing server on port "+port);
    }
}
