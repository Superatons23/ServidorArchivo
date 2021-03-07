/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorarchivos;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 *
 * @author javie
 */
public class ServerExecute {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
        Executor service = Executors.newSingleThreadExecutor();
        int port = 4444;
      

            
                
                service.execute(new MultiServerRunnable(port));
            

        

    }

}
