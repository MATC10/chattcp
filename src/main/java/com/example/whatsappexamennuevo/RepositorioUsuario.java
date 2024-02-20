package com.example.whatsappexamennuevo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuario {
    Connection conexion;

    public RepositorioUsuario(Connection miConexion){
        this.conexion=miConexion;
        createTable();
    }

    public void createTable(){
        Statement stmt=null;
        try {
            stmt = conexion.createStatement();
            String CREATE_TABLE_SQL="CREATE TABLE IF NOT EXISTS usuarios (" +
                    "    id               INTEGER AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    nombre           VARCHAR (50),\n" +
                    "    password         VARCHAR (50),\n" +
                    "    conectado        BOOLEAN DEFAULT FALSE\n" +
                    ");";
            stmt.executeUpdate(CREATE_TABLE_SQL);
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    public Usuario buscarPersonaPorNombre (String nombre){
        Usuario usuario = null;
        try {
            PreparedStatement ps=conexion.prepareStatement("SELECT * FROM usuarios WHERE nombre=?");
            ps.setString(1, nombre);

            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPassword(rs.getString("password"));
                usuario.setConectado(rs.getBoolean("conectado"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuario;
    }

    public void insertaUsuario(Usuario usuario){
        PreparedStatement aux = null;
        String sentenciaSql = "INSERT INTO usuarios (nombre, password, conectado) VALUES (?, ?, ?)";
        try {
            aux = conexion.prepareStatement(sentenciaSql, Statement.RETURN_GENERATED_KEYS);
            aux.setString(1, usuario.getNombre());
            aux.setString(2, usuario.getPassword());
            aux.setBoolean(3, usuario.isConectado());
            aux.executeUpdate();

            ResultSet rs = aux.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void setConectado(Usuario usuario, boolean conectado) {
        String sql = "UPDATE usuarios SET conectado = ? WHERE nombre = ?";

        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setBoolean(1, conectado);
            pstmt.setString(2, usuario.getNombre());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Usuario> obtenerUsuariosConectados() {
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
        String sql = "SELECT * FROM usuarios WHERE conectado = true";

        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPassword(rs.getString("password"));
                usuario.setConectado(rs.getBoolean("conectado"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}