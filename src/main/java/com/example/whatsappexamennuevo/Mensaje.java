package com.example.whatsappexamennuevo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Mensaje implements Serializable {
    int id;
    Usuario emisor;
    String mensaje;
    LocalDateTime fechaHora;

    public Mensaje(Usuario emisor, String mensaje, LocalDateTime fechaHora) {
        this.emisor = emisor;
        this.mensaje = mensaje;
        this.fechaHora = LocalDateTime.now();
    }


}
