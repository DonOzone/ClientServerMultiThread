/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author nemesis635
 */
public class Server implements Runnable {

    private ServerSocket myServerSocket;
    private boolean running;
    private boolean initalized;
    private Thread myThread;
    private List<Attendant> listAttendants;

    /**
     *
     * @param port
     */
    public Server(int port) throws Exception {
        this.listAttendants = new ArrayList<Attendant>();
        this.initalized = false;
        this.running = false;

        openPort(port);
    }

    private void openPort(int port) throws Exception {
        myServerSocket = new ServerSocket(port);
        initalized = true;
    }

    private void close() {
        for (Attendant attendant : listAttendants) {
            try {
                attendant.stop();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        try {
            myServerSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        myServerSocket = null;
        initalized = false;
        running = false;

        myThread = null;
    }

    public void start() {
        if (!initalized || running) {
            return;
        }

        running = true;
        myThread = new Thread(this);
        myThread.start();
    }

    public void stop() throws Exception {
        running = false;
        myThread.join();
    }

    @Override
    public void run() {
        // tela servidor exibe 'aguardadndo conexão'
        System.out.println("Aguardando conexão...");

        while (running) {
            try {
                myServerSocket.setSoTimeout(2500);
                Socket mySocket = myServerSocket.accept();

                // tela servidor exibe 'conexão estabelecida'
                System.out.println("Conexão estabelecida...");

                // classe Atendente que receberá os clientes (threads)
                Attendant myAttendant = new Attendant(mySocket);
                myAttendant.start();

                listAttendants.add(myAttendant);

            } catch (SocketTimeoutException out) {
            } catch (Exception e) {
                System.out.print(e);
                break;
            }
        }
        close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando servidor...");
        Server myServer = new Server(2525);
        myServer.start();

        System.out.println("ENTER para encerrar o servidor");
        new Scanner(System.in).nextLine();

        System.out.println("Encerrando servidor...");
        myServer.stop();
    }
}
