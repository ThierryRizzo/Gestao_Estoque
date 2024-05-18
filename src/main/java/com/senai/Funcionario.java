package com.senai;

public class Funcionario extends Usuarios {

    private long nit;
    private Empresa empresa;


    public Funcionario() {
    }

    public long getNit() {
        return nit;
    }

    public void setNit(long nit) {
        this.nit = nit;
    }

    public String getEmpresa() {
        return empresa.getNome();
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

}
