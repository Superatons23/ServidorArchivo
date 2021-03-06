/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorarchivos;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Executor service = Executors.newFixedThreadPool(2);
        int port = 4444;
      

            
                
                service.execute(new MultiServerRunnable(port));
            

        

    }

}
