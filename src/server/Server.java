/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.ClientForm;
import java.awt.ComponentOrientation;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.DefaultListModel;

/**
 *
 * @author nemesis635
 */
public class Server implements Runnable {

    private ServerSocket myServerSocket;
    private boolean running;
    private static boolean initialized;
    private Thread myThread;
    private List<Attendant> listAttendants;
    private static final String serverDir = "D:/projects/java/ClientServerSocket/server/";
    /**
     *
     * @param port
     */
    public Server(int port) throws Exception {
        this.listAttendants = new ArrayList<Attendant>();
        this.initialized = false;
        this.running = false;
        
        openPort(port);
    }

    private void openPort(int port) throws Exception {
        myServerSocket = new ServerSocket(port);
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
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
        initialized = false;
        running = false;

        myThread = null;
    }

    public void start() {
        if (!initialized || running) {
            return;
        }

        running = true;
        myThread = new Thread(this);
        myThread.start();
    }

    public void stop() throws Exception {
        running = false;

        if (myThread != null) {
            myThread.join();
        }
    }

    @Override
    public void run() {
        // tela servidor exibe 'aguardadndo conexão'
        ServerForm.txtAreaOutput.append("Aguardando cliente ...\n");
        DefaultListModel listModel = new DefaultListModel();
        
        while (running) {
            try {
                myServerSocket.setSoTimeout(2500);
                Socket mySocket = myServerSocket.accept();

                // classe Atendente que receberá os clientes (threads)
                Attendant myAttendant = new Attendant(mySocket);
                myAttendant.start();

                listAttendants.add(myAttendant);
                
                ServerForm.txtAreaOutput.append("Cliente conecatado. Porta "+myAttendant.getPort()+"\n");
                
                listModel.addElement("Cliente - Porta "+myAttendant.getPort());
                ServerForm.listClients.setModel(listModel);

            } catch (SocketTimeoutException out) {
            } catch (Exception e) {
                System.out.print(e);
                break;
            }
        }
        close();
    }

    public static void listFiles() {
        DefaultListModel listDefault = new DefaultListModel();
        File directory[] = new File(serverDir).listFiles();
        for (int i = 0; i < directory.length; i++) {
            listDefault.addElement(directory[i].getName());
        }
        ClientForm.listServerFiles.setModel(listDefault);
    }
}
