/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Target;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.swing.JOptionPane;

/**
 *
 * @author nemesis635
 */
public class Attendant implements Runnable {

    private static final String serverDir = "D:/projects/java/ClientServerSocket/server/";
    private static final String clientDir = "D:/projects/java/ClientServerSocket/client/";
    private Socket mySocket;
    private DataInputStream myDataInputStream;
    private DataOutputStream myDataOutputStream;
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
            myDataInputStream = new DataInputStream(mySocket.getInputStream());
            myDataOutputStream = new DataOutputStream(mySocket.getOutputStream());

            initialized = true;
        } catch (Exception e) {
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
        try {
            mySocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        myDataInputStream = null;
        myDataOutputStream = null;
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

                // ler a flag - 1 upload, 2 download
                // ler o arquivo
                int flag = myDataInputStream.readInt();
                String file = myDataInputStream.readUTF();

                switch (flag) {
                    // upload
                    case 1:
                        uploadFile(file);
                        break;
                    // download
                    case 2:
                        downloadFile(file);
                        break;
                    // shell
//                    case 3:
//                        break;
                }


            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                System.out.print(e);
                break;
            }
        }

        ServerForm.txtAreaOutput.append("Encerrando conexão com o cliente da porta " + mySocket.getPort() + "\n");
        close();
    }

    public int getPort() {
        return mySocket.getPort();
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

    public void uploadFile(String fileName) throws FileNotFoundException, IOException {

        File file = new File(clientDir + fileName);
        FileInputStream fileInputStream = new FileInputStream(clientDir + file);

        ClientForm.txtAreaOutput.append("Enviando arquivo " + file.getName() + "!\n");
        ServerForm.txtAreaOutput.append("Recebendo arquivo " + file.getName() + "!\n");

        final int size = 4096; // buffer máximo de 4KB
        //final int size = 512;
        byte[] codedFile = new byte[size]; // Array de bytes parcial
        int read = -1; // Quantidade de bytes lidos

        while (true) {
            read = fileInputStream.read(codedFile, 0, size);
            if (read == -1) {
                break;
            }
            myDataOutputStream.write(codedFile, 0, read); //Enviando o array de bytes parcial p/ o cliente
        }

        ClientForm.txtAreaOutput.append("Arquivo enviado com sucesso!\n");
        ServerForm.txtAreaOutput.append("Arquivo recebido com sucesso!\n");
    }

    public void downloadFile(String fileName) throws FileNotFoundException, IOException {

        File file = new File(serverDir + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(serverDir + file);

        ClientForm.txtAreaOutput.append("Recebendo arquivo " + file.getName() + "!\n");
        ServerForm.txtAreaOutput.append("Enviando arquivo " + file.getName() + "!\n");


        final int size = 4096; // buffer máximo de 4KB
        //final int size = 8;
        byte codedFile[] = new byte[size]; // Array de bytes parcial
        int read = -1; // quantidade de bytes lidos
        int sum = 0;
        while (true) {
            read = myDataInputStream.read(codedFile, 0, size);
            sum += read;
            if (read == -1) {
                break;
            }
            fileOutputStream.write(codedFile, 0, read); // Gravando array de bytes máximo de 4KB no arquivo 
            System.out.print(read + "\n");
            //jProgressBar1.setValue((int)((sum*100.0)/tam));
        }

        ClientForm.txtAreaOutput.append("Arquivo recebido com sucesso!\n");
        ServerForm.txtAreaOutput.append("Arquivo enviado com sucesso!\n");
    }
}
