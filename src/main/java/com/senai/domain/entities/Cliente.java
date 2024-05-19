package com.senai.domain.entities;

import com.senai.domain.Usuarios;

public class Cliente extends Usuarios {

    private String endereco;

    public Cliente() {
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
