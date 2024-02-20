package com.example.whatsappexamennuevo;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HiloServidorChat extends Thread{
    ObjectInputStream ois;
    Socket socket = null;
    ComunHilos comun;
    private volatile boolean running = true;

    public HiloServidorChat(Socket s, ComunHilos comun) {
        this.socket = s;
        this.comun = comun;
        try {
            // CREO FLUJO DE entrada para leer los mensajes
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ERROR DE E/S");
            e.printStackTrace();
        }
    }


    public void run() {

        comun.EnviarMensajesaTodos(comun.getStMensajes());

        while (running) {
            try {
                Mensaje mensaje = (Mensaje) ois.readObject(); // Lee el objeto Mensaje enviado por el cliente
                if (mensaje.getMensaje().trim().equals("*")) { // El cliente se desconecta
                    comun.delConexion(socket);
                    break;
                }

                comun.setStMensajes(mensaje);

            } catch (EOFException e) {
                //el hilo se detuvo antes de que se pudiera leer un objeto, así que salimos del bucle
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } // fin while

        //se cierra el socket del cliente
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }// run

}