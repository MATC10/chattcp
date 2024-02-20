package com.example.whatsappexamennuevo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Data
public class Usuario implements Serializable {
    int id;
    String nombre;
    String password;
    boolean conectado;
}