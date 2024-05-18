package com.senai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PaginaCliente extends JFrame{

    private InterfaceUsuario interfaceUsuario;

    public PaginaCliente(InterfaceUsuario interfaceUsuario) {
        this.interfaceUsuario = interfaceUsuario;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);

        JPanel clientePanel = new JPanel();
        clientePanel.setLayout(new GridLayout(3, 1));

        JButton btnCadastro = new JButton("Cadastre-se");
        btnCadastro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.cadastroUser();
            }
        });

        JButton btnComprar = new JButton("Comprar");
        btnComprar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.comprar();
            }
        });

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Main telaPrincipal = new Main(interfaceUsuario);
                telaPrincipal.setVisible(true);
            }
        });

        clientePanel.add(btnCadastro);
        clientePanel.add(btnComprar);
        clientePanel.add(btnVoltar);
        add(clientePanel);
    }
    public void mostrar() {
        setVisible(true);
    }

    public static void main(){
        java.util.List<Produtos> listaDeProdutos = new ArrayList<>();
        List<Empresa> listaDeEmpresas = new ArrayList<>();
        Banco banco = new Banco();
        InterfaceUsuario interfaceUsuario = new InterfaceUsuario(listaDeProdutos, listaDeEmpresas, banco);

        PaginaCliente paginaCliente = new PaginaCliente(interfaceUsuario);
        paginaCliente.mostrar();

    }
}
