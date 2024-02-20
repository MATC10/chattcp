package com.example.whatsappexamennuevo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args){
        int puerto = 44444;
        ServerSocket servidor=null;

        Conexion conexion=new Conexion();
        RepositorioMensaje repositorioMensaje = new RepositorioMensaje(conexion.conexion);
        ComunHilos comun = new ComunHilos(repositorioMensaje);

        try {
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor en marcha...");

            while (true) {
                Socket socket = new Socket();
                socket = servidor.accept();// esperando cliente

                comun.addConexion(socket);

                HiloServidorChat hilo = new HiloServidorChat(socket, comun);
                hilo.start();
            }
            //servidor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servidor iniciado...");
    }
}
