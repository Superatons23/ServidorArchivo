/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorarchivos;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author javie
 */
public class MultiServerRunnable implements Runnable {

    private final int PORT;
    private DatagramSocket socket;
    private static final int BUFFER_SIZE = 8096;

    public MultiServerRunnable(int port) {
        this.PORT = port;

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

                //obtener el archivo a enviar
                File myFile = new File("descarga0.jpg");

                //arrelgo de bytes a enviar al client
                byte[] sendBuffer = new byte[this.BUFFER_SIZE];

                //guardar el array del file
                byte[] bytesFile = Files.readAllBytes(myFile.toPath());

                //calcular numero de segmentos a enviar
                int numeroSegmentos = (int) myFile.length() / this.BUFFER_SIZE;

                //obtener el numero de segmentos en un array de bytes
                byte[] np = numeroPaquetesToBytes(numeroSegmentos);

                //empaqutar el numero de segmentos al client que enviara el server
                clientpacket = new DatagramPacket(np, np.length, clientpacket.getAddress(), clientpacket.getPort());

                //enviar el numero de segmentos al client que enviara el server
                this.socket.send(clientpacket);

                //numero de segmentos enviados
                int contadorSegmentos = 0;

                while (contadorSegmentos < numeroSegmentos) {

                    int from = contadorSegmentos * this.BUFFER_SIZE;
                    int to = (contadorSegmentos + 1) * this.BUFFER_SIZE;
                    int i = 0;
                      
                    //se crea un segmento
                    while (from < to) {
                        sendBuffer[i] = bytesFile[from];
                        from++;
                        i++;
                    }
                    
                    i = 0;
                    contadorSegmentos++;
                    if(contadorSegmentos<numeroSegmentos){
                        System.out.println("es el ultimo segmento");
                    }
                    clientpacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientpacket.getAddress(), clientpacket.getPort());
                    
                    try {

                        Thread.sleep(500);
                        this.socket.send(clientpacket);
                    } catch (InterruptedException e) {
                    }

                    System.out.println("enviando paquete " + contadorSegmentos + "/" + numeroSegmentos);
                }

                // System.out.println("image download");
                // this.socket.send(ServerPacket);
            }
        } catch (SocketException ex) {
            Logger.getLogger(MultiServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MultiServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //convertir el numero de paquetes a enviar a bytes
    private byte[] numeroPaquetesToBytes(int value) {
        byte[] encodedValue = new byte[Integer.SIZE / Byte.SIZE];
        encodedValue[3] = (byte) (value >> Byte.SIZE * 3);
        encodedValue[2] = (byte) (value >> Byte.SIZE * 2);
        encodedValue[1] = (byte) (value >> Byte.SIZE);
        encodedValue[0] = (byte) value;
        return encodedValue;
    }

}
