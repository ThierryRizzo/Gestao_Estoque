package com.senai.presentation.listeners;

import com.senai.presentation.controllers.InterfaceUsuario;
import com.senai.application.Main;
import com.senai.domain.entities.Empresa;
import com.senai.domain.entities.Produtos;
import com.senai.infraestructure.database.Banco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PaginaEmpresa extends JFrame {
    private InterfaceUsuario interfaceUsuario;

    public PaginaEmpresa(InterfaceUsuario interfaceUsuario) {
        this.interfaceUsuario = interfaceUsuario;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 2));

        JButton cadastreUserCompanyEmployeeButton = new JButton("Cadastre-se");
        cadastreUserCompanyEmployeeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.cadastreUserCompanyEmployee();
            }
        });

        JButton cadastrarProdutoButton = new JButton("Cadastrar Produto");
        cadastrarProdutoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.cadastrarProduto();
            }
        });

        JButton calcVMDButton = new JButton("Calcular VMD");
        calcVMDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.calcVMD();
            }
        });

        JButton verificarEstoqueButton = new JButton("Verificar Estoque");
        verificarEstoqueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.verificarEstoque();
            }
        });

        JButton listaProdutosButton = new JButton("Lista de Produtos");
        listaProdutosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interfaceUsuario.listagem();
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

        menuPanel.add(btnVoltar);
        menuPanel.add(cadastreUserCompanyEmployeeButton);
        menuPanel.add(cadastrarProdutoButton);
        menuPanel.add(calcVMDButton);
        menuPanel.add(verificarEstoqueButton);
        menuPanel.add(listaProdutosButton);

        add(menuPanel);
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {

        Banco banco = new Banco();
        java.util.List<Produtos> listaDeProdutos = new ArrayList<>();
        List<Empresa> listaDeEmpresas = new ArrayList<>();
        InterfaceUsuario interfaceUsuario = new InterfaceUsuario(listaDeProdutos, listaDeEmpresas, banco);

        PaginaEmpresa paginaEmpresa = new PaginaEmpresa(interfaceUsuario);
        paginaEmpresa.mostrar();

    }
}
