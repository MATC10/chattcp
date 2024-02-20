package com.example.whatsappexamennuevo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    @FXML
    private Button btnConectar;

    @FXML
    private Button btnDesconectar;

    @FXML
    private Button btnEnviar;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblRegistroValido;

    @FXML
    private Label lblAutenticate;

    @FXML
    private TableColumn<Usuario, String> tcNombre;

    @FXML
    private TableView<Usuario> tvNombres;

    @FXML
    private TextField txtMiMensaje;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNombreRegister;

    @FXML
    private TextField txtPuerto;

    @FXML
    private TextField txtServidor;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtPasswordRegister;

    @FXML
    private TextArea txtTodosMensajes;

    RepositorioUsuario repositorioUsuario;
    RepositorioMensaje repositorioMensaje;
    Conexion conexion;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    ComunHilos comunHilos;

    ListenerHilo listenerHilo;

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion=new Conexion();
        repositorioUsuario = new RepositorioUsuario(conexion.conexion);
        repositorioMensaje = new RepositorioMensaje(conexion.conexion);

        txtTodosMensajes.setEditable(false);
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        //inizializo el timeline para refrescar usuarios conectados
        timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), event -> actualizarUsuariosConectados()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void actualizarUsuariosConectados() {
        ObservableList<Usuario> usuariosConectados = repositorioUsuario.obtenerUsuariosConectados();
        tvNombres.setItems(usuariosConectados);
    }

    @FXML
    void onConectar(ActionEvent event) {
        lblRegistroValido.setText("");

        String nombreIngresado = txtNombre.getText();
        String passwordIngresado = txtPassword.getText();
        String servidor = txtServidor.getText();
        int puerto;
        try {
            puerto = Integer.parseInt(txtPuerto.getText());
        } catch (NumberFormatException e) {
            lblAutenticate.setText("Por favor, intrudice un número entero en el Puerto");
            return;
        }

        Usuario usuario = repositorioUsuario.buscarPersonaPorNombre(nombreIngresado);

        if (usuario != null && usuario.getPassword().equals(passwordIngresado)) {

            try {
                socket = new Socket(servidor, puerto);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            }catch (java.net.ConnectException e) {
                lblAutenticate.setText("Error en la conexión con el servidor");
                return;
            } catch (UnknownHostException e) {
                lblAutenticate.setText("Error en la conexión con el servidor");
                return;
            } catch (IOException e) {
                lblAutenticate.setText("Error en la conexión con el servidor");
                return;
            }
            //marcar conectado
            repositorioUsuario.setConectado(usuario,true);

            //nombre y usuaios no se tocan del campo de texto
            btnRegistrar.setDisable(true);
            txtNombre.setDisable(true);
            txtPassword.setDisable(true);
            txtNombreRegister.setDisable(true);
            txtPasswordRegister.setDisable(true);
            txtServidor.setDisable(true);
            txtPuerto.setDisable(true);
            lblEstado.setText("Estado del usuario: CONECTADO - Usuario: " + txtNombre.getText());
            btnConectar.setDisable(true);

            lblAutenticate.setText("Login correcto");

            comunHilos = new ComunHilos(repositorioMensaje);
                comunHilos.addConexion(socket);

                listenerHilo = new ListenerHilo(ois, txtTodosMensajes);
                new Thread(listenerHilo).start();

                //se inicia el timeline
                timeline.play();

                lblAutenticate.setText("Conexión con el servidor exitosa");


        } else {
            lblAutenticate.setText("Nombre de usuario o contraseña incorrectos");
        }
    }

    @FXML
    void onDesconectar(ActionEvent event) {
        listenerHilo.stop();

        comunHilos.delConexion(socket);
        txtNombre.setDisable(false);
        txtPassword.setDisable(false);
        txtNombreRegister.setDisable(false);
        txtPasswordRegister.setDisable(false);
        txtServidor.setDisable(false);
        txtPuerto.setDisable(false);
        lblEstado.setText("Estado del usuario: DESCONECTADO");
        txtTodosMensajes.clear();
        btnRegistrar.setDisable(false);
        btnConectar.setDisable(false);



        //marcar usuario como desconectado
        Usuario usuario = repositorioUsuario.buscarPersonaPorNombre(txtNombre.getText());
        repositorioUsuario.setConectado(usuario,false);
        //limpiar la TableView
        tvNombres.getItems().clear();
        lblAutenticate.setText("");

        //detener el tiempo de refresco de usuarios conectados
        timeline.stop();
    }

    @FXML
    void onEnviar(ActionEvent event) {
        String mensaje = txtMiMensaje.getText();

        if (mensaje.isEmpty()) {
            return;
        }

        txtMiMensaje.clear();
        lblAutenticate.setText("");

        LocalDateTime fechaHora = LocalDateTime.now();

        Usuario emisor = new Usuario();
        emisor.setNombre(txtNombre.getText());
        emisor.setPassword(txtPassword.getText());

        Usuario usuario = repositorioUsuario.buscarPersonaPorNombre(emisor.getNombre());

        Mensaje nuevoMensaje = new Mensaje(usuario, mensaje, fechaHora);
        txtTodosMensajes.clear();
        try {
            if (oos != null) {
                oos.writeObject(nuevoMensaje);
                oos.flush();
            }
        } catch (IOException e) {
        }
    }

    @FXML
    void onRegistrar(ActionEvent event) {
        Usuario usuarioExistente = repositorioUsuario.buscarPersonaPorNombre(txtNombreRegister.getText());
        if (usuarioExistente != null) {
            lblRegistroValido.setText("El usuario ya está registrado");
            txtPasswordRegister.clear();
        } else {
            Usuario user = new Usuario();
            user.setNombre(txtNombreRegister.getText());
            user.setPassword(txtPasswordRegister.getText());

            repositorioUsuario.insertaUsuario(user);
            lblRegistroValido.setText("Registro válido");
            txtPasswordRegister.clear();
            txtNombreRegister.clear();
        }
    }
}