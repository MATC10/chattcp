package com.example.whatsappexamennuevo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMensaje {
    Connection conexion;
    RepositorioUsuario repositorioUsuario;

    public RepositorioMensaje(Connection miConexion) {
        this.conexion = miConexion;
        this.repositorioUsuario = new RepositorioUsuario(miConexion);
        createTable();
    }

    public void createTable(){
        Statement stmt=null;
        try {
            stmt = conexion.createStatement();
            String CREATE_TABLE_SQL="CREATE TABLE IF NOT EXISTS mensajes (" +
                    "    id               INTEGER AUTO_INCREMENT  PRIMARY KEY,\n" +
                    "    emisor           INTEGER,\n" +
                    "    mensaje          VARCHAR(255),\n" +
                    "    fechaHora        TIMESTAMP,\n" +
                    "    FOREIGN KEY (emisor) REFERENCES usuarios(id)\n" +
                    ");";
            stmt.executeUpdate(CREATE_TABLE_SQL);
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    public void insertaMensaje(Mensaje m){
        PreparedStatement aux = null;
        String sentenciaSql = "INSERT INTO mensajes (emisor, mensaje, fechaHora) VALUES (?, ?, ?)";
        try {
            aux = conexion.prepareStatement(sentenciaSql);
            aux.setInt(1, m.getEmisor().getId());
            aux.setString(2, m.getMensaje());
            aux.setTimestamp(3, Timestamp.valueOf(m.getFechaHora()));
            aux.executeUpdate();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    public List<Mensaje> getTodosMensajes() {
        List<Mensaje> listaMensajes = FXCollections.observableArrayList();
        String sentenciaSql = "SELECT m.*, u.nombre FROM mensajes m INNER JOIN usuarios u ON m.emisor = u.id ORDER BY m.fechaHora ASC";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sentenciaSql);
            while (rs.next()) {
                Usuario emisor = new Usuario();
                emisor.setId(rs.getInt("emisor"));
                emisor.setNombre(rs.getString("nombre"));
                String mensaje = rs.getString("mensaje");
                LocalDateTime fechaHora = rs.getTimestamp("fechaHora").toLocalDateTime();
                listaMensajes.add(new Mensaje(emisor, mensaje, fechaHora));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return listaMensajes;
    }


}