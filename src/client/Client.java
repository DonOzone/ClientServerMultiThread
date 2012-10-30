/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import server.ServerForm;

/**
 *
 * @author nemesis635
 */
public class Client implements Runnable {
    private Socket mySocket;
    private DataInputStream myDataInputStream;
    private DataOutputStream myDataOutputStream;

    public DataInputStream getMyDataInputStream() {
        return myDataInputStream;
    }

    public DataOutputStream getMyDataOutputStream() {
        return myDataOutputStream;
    }
    private boolean initialized;
    private boolean running;
    private Thread myThread;

    public Client(String address, int port) throws Exception {
        initialized = false;
        running = false;
        
        open(address, port);
    }

    private void open(String address, int port) throws Exception {
        try {
            mySocket = new Socket(address, port);

            myDataInputStream = new DataInputStream(mySocket.getInputStream());
            myDataOutputStream = new DataOutputStream(mySocket.getOutputStream());

            initialized = true;
        } catch (Exception e) {
            System.out.print(e);
            close();
            throw e;
        }
    }

    private void close() {
        if (myDataInputStream != null) {
            try {
                myDataInputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (myDataOutputStream != null) {
            try {
                myDataOutputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mySocket != null) {
            try {
                mySocket.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        myDataInputStream = null;
        myDataOutputStream = null;
        mySocket = null;

        initialized = false;
        running = false;
        myThread = null;
    }

    private boolean isRunning() {
        return running;
    }
    
    public void sendFlag(int opt) throws IOException {
        myDataOutputStream.writeInt(opt);
    }
    
    public void sendFile(String file) throws IOException {
        myDataOutputStream.writeUTF(file);
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
        while (running) {

            try {
                mySocket.setSoTimeout(2500);
                String message = myDataInputStream.readUTF();

                if (message == null) {
                    break;
                }

                ServerForm.txtAreaOutput.append(message + "\n");
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                System.out.println(e);
                break;
            }
        }
        close();
    }
   

}
