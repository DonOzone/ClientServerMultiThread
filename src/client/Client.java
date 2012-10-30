/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 *
 * @author nemesis635
 */
public class Client implements Runnable {

    private Socket mySocket;
    private BufferedReader myBufferedReader;
    private PrintStream myPrintStream;
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

            myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            myPrintStream = new PrintStream(mySocket.getOutputStream());

            initialized = true;
        } catch (Exception e) {
            System.out.print(e);
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
        if (mySocket != null) {
            try {
                mySocket.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        myBufferedReader = null;
        myPrintStream = null;
        mySocket = null;

        initialized = false;
        running = false;
        myThread = null;
    }

    public void send(String message) {
        myPrintStream.println(message);
    }

    private boolean isRunning() {
        return running;
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
                String message = myBufferedReader.readLine();

                if (message == null) {
                    break;
                }

                System.out.println(
                        "Mensagem enviada pelo servidor: " + message);
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                System.out.println(e);
                break;
            }
        }
        close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando cliente...");
        System.out.println("Iniciando conexão com o servidor...");

        Client myClient = new Client("localhost", 2525);
        
        System.out.println("Conexão estabelecida...");
        
        myClient.start();

        Scanner myScanner = new Scanner(System.in);

        while (true) {
            System.out.println("Digite uma mensagem...");
            String message = myScanner.nextLine();

            if (!myClient.isRunning()) {
                break;
            }
            myClient.send(message);

            if ("quit".equals(message)) {
                break;
            }
        }
        System.out.println("Encerrando cliente...");
        myClient.stop();
    }
}
