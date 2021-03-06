/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteudp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author javie
 */
public class ClienteUDP implements Runnable {

    private JFrame jframe;
    InetAddress adress;
    int port;

    DatagramSocket udpSocket;

    public ClienteUDP(JFrame frame, InetAddress adress, int port) {
        this.jframe = frame;
        this.port = port;
        this.adress = adress;

    }

    @Override
    public void run() {

        try {
            udpSocket = new DatagramSocket();
            byte[] buffer = new byte[8096];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adress, port);

                // recibir el numero de segmentos a recibir
                udpSocket.receive(packet);

                //convertir el numero de paquetes de byte to int
                int np = bytesToNumeroPaquetes(packet.getData());

                //numero de paquetes recibidos
                int npr = 0;

                //var para ir almacenando los bytes que recibamos desde el server
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              
                //recibir paquetes
                while (npr < np) {

                
                    udpSocket.receive(packet);

                    //guardar arrays de bytes en outputStream
                    outputStream.write(packet.getData());

                    npr++;
                    System.out.println("recibiendo paquete " + npr + "/" + np);
                }
                System.out.println("creando img");
                ByteArrayInputStream myStream = new ByteArrayInputStream(outputStream.toByteArray());
                BufferedImage bImage = ImageIO.read(myStream);
                ImageIO.write(bImage, "jpg", new File("picture1.jpg"));
                System.out.println("image created");
                
                
                
//                System.out.println("reciviendo data from server");
//                buffer = packet.getData();
//                ByteArrayInputStream myStream = new ByteArrayInputStream(buffer);
//                BufferedImage bImage = ImageIO.read(myStream);
//                ImageIO.write(bImage, "jpg", new File("C:\\Users\\javie\\Desktop\\6TO-SEMESTRE\\Sistemas-Distribuidos\\Parcial-Dos\\ServidorDeArchivos\\picture1.jpg"));
//                System.out.println("image download");
            }
            //udpSocket.close();
        } catch (SocketException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send(String message) {

        try {
            byte[] buffer = new byte[450];

            buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adress, port);
            System.out.println("enviando data to server");
            udpSocket.send(packet);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //convertir el numero de paquetes a enviar a bytes
    private int bytesToNumeroPaquetes(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE * 1);
        value |= (encodedValue[0] & 0xFF);

        return value;

    }
}
