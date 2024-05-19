package com.senai.domain.entities;

import com.senai.domain.Usuarios;

public class Funcionario extends Usuarios {

    private Empresa empresa;


    public Funcionario() {
    }

    public String getEmpresa() {
        return empresa.getNome();
    }

    public long getCnpjEmpresa() {
        return empresa.getCnpj();
    }


    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

}
