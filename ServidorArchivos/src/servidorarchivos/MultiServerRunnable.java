/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorarchivos;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author javie
 */
public class MultiServerRunnable implements Runnable {

    private final int PORT;
    private DatagramSocket socket;
    private static final int BUFFER_SIZE = 8096;
    private Executor service;

    public MultiServerRunnable(int port) {
        this.PORT = port;
        this.service = Executors.newFixedThreadPool(3);

    }

    @Override
    public void run() {

        try {
            this.socket = new DatagramSocket(this.PORT);

            while (true) {
                System.out.println("Esperando data from client");
                //almacenar nombre de archivo a descargar
                byte[] buffer = new byte[65000];

                // receive data from cliente
                DatagramPacket clientpacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(clientpacket);
                System.out.println("reciviendo data from client");
                service.execute(new MultiServerClient(this.socket,clientpacket));
            }

        } catch (IOException ex) {
            Logger.getLogger(MultiServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
