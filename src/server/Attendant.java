/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author nemesis635
 */
public class Attendant implements Runnable {

    private Socket mySocket;
    private BufferedReader myBufferedReader;
    private PrintStream myPrintStream;
    private boolean initialized;
    private boolean running;
    private Thread myThread;

    public Attendant(Socket mySocket) throws Exception {
        this.mySocket = mySocket;

        this.initialized = false;
        this.running = false;

        open();
    }

    private void open() throws Exception {
        try {
            myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            myPrintStream = new PrintStream(mySocket.getOutputStream());
            initialized = true;
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    private void close() {
        if (myBufferedReader != null) {
            try {
                myBufferedReader.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (myPrintStream != null) {
            try {
                myPrintStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        try {
            mySocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        myBufferedReader = null;
        myPrintStream = null;
        mySocket = null;

        initialized = false;
        running = false;
        myThread = null;
    }

    @Override
    public void run() {
        while (running) {

            try {
                mySocket.setSoTimeout(2500);
                String message = myBufferedReader.readLine();

                System.out.println(
                        "Mensagem recebida do cliente ["
                        + mySocket.getInetAddress().getHostName()
                        + ":" + mySocket.getPort()
                        + "]: " + message);

                if ("quit".equals(message)) {
                    break;
                }
                myPrintStream.println(message);
                
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                System.out.print(e);
                break;
            }
        }

        System.out.println("Encerrando conexão");
        close();
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
        myThread.join();
    }
}
