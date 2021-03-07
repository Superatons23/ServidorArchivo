/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorarchivos;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javie
 */
public class MultiServerClient implements Runnable {

    private static final int BUFFER_SIZE = 8096;
    private DatagramPacket clientpacket;
    private DatagramSocket socket;

    public MultiServerClient(DatagramSocket socket, DatagramPacket clientpacket) {
        this.socket = socket;
        this.clientpacket = clientpacket;
    }

    @Override
    public void run() {
        boolean finished = false;
        try {

            while (!finished) {

                //obtener el archivo a enviar
                System.out.println("obtenino archivo");
                File myFile = new File("descarga0.png");

                //arrelgo de bytes a enviar al client
                byte[] sendBuffer = new byte[this.BUFFER_SIZE];

                //guardar el array del file
                byte[] bytesFile = Files.readAllBytes(myFile.toPath());

                //calcular numero de segmentos a enviar
                int numeroSegmentos = (int) myFile.length() / this.BUFFER_SIZE;

                //obtener el numero de segmentos en un array de bytes
                byte[] np = numeroPaquetesToBytes(numeroSegmentos + 1);

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

                    //crear segmento quesele enviara al cliente
                    clientpacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientpacket.getAddress(), clientpacket.getPort());

                    try {

                        Thread.sleep(300);
                        this.socket.send(clientpacket);
                    } catch (InterruptedException e) {
                    }

                    contadorSegmentos++;
                    System.out.println("enviando paquete " + contadorSegmentos + "/" + (numeroSegmentos + 1));

                    //enviar fragmento faltante, esto se hace porque el ultimo block es muy pequenio
                    if (contadorSegmentos == numeroSegmentos) {

                        byte[] smallPaquete = sendSmallPaquete(from, bytesFile.length, bytesFile);

                        //crear ultimo segmento quesele enviara al cliente
                        clientpacket = new DatagramPacket(smallPaquete, smallPaquete.length, clientpacket.getAddress(), clientpacket.getPort());

                        //enviar ultimo fragmento
                        this.socket.send(clientpacket);
                        System.out.println("enviando paquete " + (contadorSegmentos + 1) + "/" + (numeroSegmentos + 1));
                    }
                }
                finished = true;
            }
        } catch (SocketException ex) {
            Logger.getLogger(MultiServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MultiServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("se termino");
    }

    //metodo que sirve para calcular lo que sobro del file y enviarlo al cliente
    private byte[] sendSmallPaquete(int from, int to, byte[] bytesFile) {
        byte[] array = new byte[to - from];

        int i = 0;
        while (from < to) {
            array[i] = bytesFile[from];
            from++;
            i++;
        }
        return array;

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
