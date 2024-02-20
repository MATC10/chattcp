package com.example.whatsappexamennuevo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ListenerHilo implements Runnable {
    private ObjectInputStream ois;

    private TextArea txtTodosMensajes;
    private volatile boolean running = true;

    public ListenerHilo(ObjectInputStream ois, TextArea txtTodosMensajes) {
        this.ois = ois;
        this.txtTodosMensajes = txtTodosMensajes;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    List<Mensaje> listaMensajes = (List<Mensaje>) obj;
                    for (Mensaje mensaje : listaMensajes) {
                        String mensajeStr = mensaje.getFechaHora() + " : " + mensaje.getEmisor().getNombre() + " : " + mensaje.getMensaje();
                        Platform.runLater(() -> txtTodosMensajes.appendText(mensajeStr + "\n"));
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket cerrado");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }
}