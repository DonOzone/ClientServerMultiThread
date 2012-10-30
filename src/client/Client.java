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
import java.util.Scanner;

/**
 *
 * @author nemesis635
 */
public class Client {

    public static void main(String[] args) throws Exception {

        System.out.println("Iniciando cliente...");

        System.out.println("Iniciando conexão com o servidor...");

        Socket mySocket = new Socket("localhost", 2525);

        System.out.println("Conexão estabelecida...");

        InputStream myInputStream = mySocket.getInputStream();
        OutputStream myOutputStream = mySocket.getOutputStream();

        BufferedReader myBufferedReader = new BufferedReader(new InputStreamReader(myInputStream));
        PrintStream myPrintStream = new PrintStream(myOutputStream);

        Scanner myScanner = new Scanner(System.in);

        while (true) {
            System.out.println("Digite uma mensagem...");
            String message = myScanner.nextLine();

            myPrintStream.println(message);

            if ("quit".equals(message)) {
                break;
            }

            System.out.println("Mensagem recebida: " + message);

        }
        System.out.println("Encerrando conexão");
        
        myBufferedReader.close();
        myPrintStream.close();
        mySocket.close();
    }
}
