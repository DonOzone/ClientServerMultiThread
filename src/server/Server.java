/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author nemesis635
 */
public class Server implements Runnable {
    private ServerSocket myServerSocket;
    
    private boolean running;
    private boolean initializing;

    private Thread myThread;
    /**
     * 
     * @param port 
     */
    public Server(int port) throws Exception {
        initializing = false;
        running = false;
        
        openPort(port);
    }
    
    private void openPort(int port) throws Exception {
        myServerSocket = new ServerSocket(port);
        initializing = true;
    }
    
    private void closeConnection() {
        try {
            myServerSocket.close();
        } catch(Exception e) {
            System.out.println(e);
        }
        myServerSocket = null;
        initializing = false;
        running = false;
        
        myThread = null;
    }
    
    public void startServer() {
        if(!initializing || running) {
            return;
        }
        
        running = true;
        myThread = new Thread(this);
        myThread.start();
    }
    
    public void stopServer() throws Exception {
        running = false;
        myThread.join();
    }
    
    @Override
    public void run() {
        // tela servidor exibe 'aguardadndo conexão'
        
        while(running) {
            try {
                myServerSocket.setSoTimeout(2500);
                Socket mySocket = myServerSocket.accept();
                
                // tela servidor exibe 'conexão estabelecida'
            } catch(SocketTimeoutException out) {
                
            } catch(Exception e) {
                System.out.print(e);
                break;
            }
        }
        closeConnection();
    }
    
    public static void main(String[] args) throws Exception {
        
    }

}
