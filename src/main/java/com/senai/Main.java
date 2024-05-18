package com.senai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JFrame {
    private InterfaceUsuario interfaceUsuario;

    public Main(InterfaceUsuario interfaceUsuario) {
        this.interfaceUsuario = interfaceUsuario;

        JButton btnEmpresa = new JButton("Empresa");
        btnEmpresa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                PaginaEmpresa paginaEmpresa = new PaginaEmpresa(interfaceUsuario);
                paginaEmpresa.mostrar();
            }
        });

        JButton btnCliente = new JButton("Cliente");
        btnCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                PaginaCliente paginaCliente = new PaginaCliente(interfaceUsuario);
                paginaCliente.mostrar();
            }
        });

        getContentPane().setLayout(new GridLayout(2, 1));
        getContentPane().add(btnEmpresa);
        getContentPane().add(btnCliente);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);
        List<Produtos> listaDeProdutos = new ArrayList<>();
        List<Empresa> listaDeEmpresas = new ArrayList<>();
        Banco banco = new Banco();
        InterfaceUsuario interfaceUsuario = new InterfaceUsuario(listaDeProdutos, listaDeEmpresas, banco);
        try {
            banco.conectar();
            System.out.println("Conexão bem-sucedida!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main telaPrincipal = new Main(interfaceUsuario);
                telaPrincipal.setVisible(true);
            }
        });
    }
}
