package com.example.whatsappexamennuevo;

import javafx.collections.ObservableList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ComunHilos {
    ArrayList<Socket> conexiones=new ArrayList<>();
    ArrayList<Usuario> usuarios=new ArrayList<>();
    RepositorioMensaje repositorioMensaje;

    private HashMap<Socket, ObjectOutputStream> outputStreams = new HashMap<>();

    public ComunHilos(RepositorioMensaje repositorioMensaje){
        this.repositorioMensaje = repositorioMensaje;
    }

    public void addConexion(Socket socket){
        conexiones.add(socket);
        System.out.println(conexiones);
    }

    public void delConexion(Socket socket){
        if(conexiones.size()>0) {
            Iterator<Socket> iterator = conexiones.iterator();
            while (iterator.hasNext()) {
                Socket s = iterator.next();
                if (s.equals(socket)) {
                    iterator.remove();
                    System.out.println(conexiones);
                    try {
                        s.close(); //cierra la conexión de red
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //eliminamos el oos
        ObjectOutputStream oos = outputStreams.get(socket);
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStreams.remove(socket);
        }
        System.out.println(conexiones);
    }



    public synchronized List<Mensaje> getStMensajes() {
        return repositorioMensaje.getTodosMensajes();
    }

    public synchronized void setStMensajes(Mensaje mensaje) {
        repositorioMensaje.insertaMensaje(mensaje);
        List<Mensaje> listaMensajes = getStMensajes();
        EnviarMensajesaTodos(listaMensajes);
    }


    public void EnviarMensajesaTodos(List<Mensaje> listaMensajes) {
        List<Mensaje> serializableList = new ArrayList<>(listaMensajes);
        for(Socket s: conexiones){
            if(!s.isClosed()){
                try {
                    ObjectOutputStream oos = outputStreams.get(s);
                    if (oos == null) {
                        oos = new ObjectOutputStream(s.getOutputStream());
                        outputStreams.put(s, oos);
                    }
                    oos.writeObject(serializableList);
                    oos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}