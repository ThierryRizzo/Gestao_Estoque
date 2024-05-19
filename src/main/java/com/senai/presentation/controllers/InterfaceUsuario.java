package com.senai.presentation.controllers;

import com.senai.domain.entities.Cliente;
import com.senai.domain.entities.Empresa;
import com.senai.domain.entities.Funcionario;
import com.senai.domain.entities.Produtos;
import com.senai.infraestructure.database.Banco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;

public class InterfaceUsuario {

    public List<Produtos> listaDeProdutos;
    public List<Empresa> listaDeEmpresas;
    private Banco banco;

    public InterfaceUsuario(List<Produtos> ListaDeProdutos, List<Empresa> ListaDeEmpresas, Banco banco) {
        this.listaDeProdutos = ListaDeProdutos;
        this.listaDeEmpresas = ListaDeEmpresas;
        this.banco= banco;
    }

    public void atualizarListaDeProdutos() {
        listaDeProdutos.clear();

        try (Connection conexao = banco.conectar()) {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM produtos");

            while (rs.next()) {
                Produtos produto = new Produtos();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setPreco(rs.getDouble("preco_produto"));
                produto.setEstoqueAtual(rs.getInt("estoque_atual"));
                produto.setTempoRepo(rs.getInt("tempo_repo"));
                produto.setLoteRepo(rs.getInt("lote_repo"));
                produto.setEstoqueMin(rs.getInt("estoque_min"));
                produto.setEstoqueMax(rs.getInt("estoque_max"));
                produto.setVendaMedia(rs.getInt("tempo_repo"));

                listaDeProdutos.add(produto);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void verificarEstoque() {

        atualizarListaDeProdutos();

        if (listaDeProdutos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            atualizarListaDeProdutos();

            JComboBox<String> produtosComboBox = new JComboBox<>();

            for (Produtos produto : listaDeProdutos) {
                produtosComboBox.addItem(produto.getNome());
            }

            int result = JOptionPane.showConfirmDialog(null, produtosComboBox,
                    "Selecione um produto: ",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int indiceProdutoSelecionado = produtosComboBox.getSelectedIndex();
                Produtos produtoSelecionado = listaDeProdutos.get(indiceProdutoSelecionado);

                if (produtoSelecionado == null) {
                    JOptionPane.showMessageDialog(null, "Produto não encontrado!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (produtoSelecionado.getVendaMedia() == 0) {
                    JOptionPane.showMessageDialog(null, "Calcule a venda média diária deste produto " +
                                    "primeiro!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                produtoSelecionado.decisao((int) produtoSelecionado.getEstoqueMin(),
                        (int) produtoSelecionado.getEstoqueMax(),
                        (int) produtoSelecionado.getEstoqueAtual());

                atualizarListaDeProdutos();

                try (Connection connection = banco.conectar()) {

                    String sqlUpdate = "UPDATE produtos SET estoque_min = ?, estoque_max = ? WHERE id_produto = ?";
                    PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate);
                    stmtUpdate.setInt(1, produtoSelecionado.getEstoqueMin());
                    stmtUpdate.setInt(2, produtoSelecionado.getEstoqueMax());
                    stmtUpdate.setInt(3, produtoSelecionado.getId());
                    stmtUpdate.executeUpdate();
                    stmtUpdate.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                JOptionPane.showMessageDialog(null, "Produto: " +
                        produtoSelecionado.getNome() + "\nEstoque Atual: " + produtoSelecionado.getEstoqueAtual() +
                        "\nEstoque Mínimo: " + produtoSelecionado.getEstoqueMin() + "\nEstoque " +
                        "Máximo: " + produtoSelecionado.getEstoqueMax() + "\nTomada de decisão: " + produtoSelecionado.getMensagemCompra());
            }
        }
    }



    public void cadastrarProduto() {
        Produtos produto = new Produtos();


        String nomeProduto =  JOptionPane.showInputDialog("Informe o nome do produto:");
        produto.setNome(nomeProduto);

        int estoqueAtual = Integer.parseInt(JOptionPane.showInputDialog("Informe o estoque " +
                "atual do produto "
                + produto.getNome() + ":"));
        produto.setEstoqueAtual(estoqueAtual);

        int tempoRepo = Integer.parseInt(JOptionPane.showInputDialog("Informe o tempo de " +
                "reposição do produto "
                + produto.getNome() + " em dias:"));
        produto.setTempoRepo(tempoRepo);

        int loteRepo = Integer.parseInt(JOptionPane.showInputDialog("Informe a quantidade no " +
                "lote de reposição do produto "
                + produto.getNome()));
        produto.setLoteRepo(loteRepo);

        double precoProduto = Double.parseDouble(JOptionPane.showInputDialog("Informe o preço do " +
                "produto: " + produto.getNome()));
        produto.setPreco(precoProduto);

        String produtoReview;
        produtoReview =
                ("Produto: " + produto.getNome() + " \n" + "Preço: R$" + produto.getPreco() +
                        " \n" + "Estoque Atual: " + produto.getEstoqueAtual() + ".un" + " \n" +
                        "Lote " +
                        "de Reposição: "
                        + produto.getLoteRepo() + ".un" + " \n" + "Tempo de Reposição: " + produto.getTempoRepo() + " Dias");

        JOptionPane.showMessageDialog(null, "" + produtoReview,
                "Review", JOptionPane.INFORMATION_MESSAGE);


        JOptionPane.showMessageDialog(null,
                "Produto Cadastrado com sucesso!");
        listaDeProdutos.add(produto);

        try (Connection conexao = banco.conectar()) {
            String sql = "INSERT INTO produtos (nome_produto, preco_produto, estoque_atual, " +
                    "tempo_repo, lote_repo) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, produto.getNome());
                stmt.setDouble(2, produto.getPreco());
                stmt.setInt(3, produto.getEstoqueAtual());
                stmt.setInt(4, produto.getTempoRepo());
                stmt.setInt(5, produto.getLoteRepo());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar produto: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void calcVMD() {

        atualizarListaDeProdutos();
        if (listaDeProdutos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            atualizarListaDeProdutos();
            JComboBox<String> produtosComboBox = new JComboBox<>();
            for (Produtos product : listaDeProdutos) {
                produtosComboBox.addItem(product.getNome());
            }

            int result = JOptionPane.showConfirmDialog(null, produtosComboBox,
                    "Selecione um produto: ",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int indiceProdutoSelecionado = produtosComboBox.getSelectedIndex();
                Produtos produtoSelecionado = listaDeProdutos.get(indiceProdutoSelecionado);

                for (int i = 0; i < 3; i++) {
                    int mes123 = Integer.parseInt(JOptionPane.showInputDialog(null,
                            "Unidades vendidas de " + produtoSelecionado.getNome() +
                                    " no " + (i + 1) + "º mês:", "vendas no mês",
                            JOptionPane.INFORMATION_MESSAGE));
                    produtoSelecionado.setMeses(i, mes123);
                }
                produtoSelecionado.calcVendaMedia();
                produtoSelecionado.calcEstoqueMin();
                produtoSelecionado.calcEstoqueMax();

                JOptionPane.showMessageDialog(null,
                        "Venda média diária: \n" + produtoSelecionado.getVendaMedia());

                try (Connection conexao = banco.conectar()) {
                    String sql = "UPDATE produtos SET estoque_min = ?, estoque_max = ?,vmd_produto = ? WHERE id_produto = ?";
                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, produtoSelecionado.getEstoqueMin());
                        stmt.setInt(2, produtoSelecionado.getEstoqueMax());
                        stmt.setDouble(3, produtoSelecionado.getVendaMedia());
                        stmt.setInt(4, produtoSelecionado.getId());
                        stmt.executeUpdate();

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao calcular venda média diária: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }

            }
            atualizarListaDeProdutos();
        }
    }

    public void listagem() {
        try (Connection conexao = banco.conectar()){


            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM produtos");

            StringBuilder produtosCadastrados = new StringBuilder("Produtos cadastrados:\n");
            while (rs.next()) {
                String nome = rs.getString("nome_produto");
                int estoqueAtual = rs.getInt("estoque_atual");
                double preco = rs.getDouble("preco_produto");

                produtosCadastrados.append("\nNome: ").append(nome).append("\nEstoque Atual: ")
                        .append(estoqueAtual).append("\nPreço: R$").append(preco).append("\n");
            }

            JOptionPane.showMessageDialog(null, produtosCadastrados.toString(),
                    "Lista de Produtos", JOptionPane.INFORMATION_MESSAGE);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void atualizarListaDeEmpresas() {
        listaDeEmpresas.clear();

        try (Connection conexao = banco.conectar()) {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Empresa");

            while (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setCnpj(rs.getInt("cnpj"));
                empresa.setNome(rs.getString("nome_empresa"));
                empresa.setEndereco(rs.getString("endereco_empresa"));
                empresa.setTelefone(rs.getLong("telefone_empresa"));
                listaDeEmpresas.add(empresa);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void cadastreUserCompanyEmployee() {
        int opt;

        Object[] option = {"Funcionário", "Empresa"};
        opt = JOptionPane.showOptionDialog(null, "Escolha uma opção",
                "Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, option, null);

        if (opt == 0) {

            atualizarListaDeEmpresas();

            if (listaDeEmpresas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhuma Empresa cadastrada!",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {

                Funcionario funcionario = new Funcionario();
                String nome = JOptionPane.showInputDialog(null,
                        "Informe o seu nome:", "Cadastro",
                        JOptionPane.INFORMATION_MESSAGE);
                funcionario.setNome(nome);

                String sobrenome = JOptionPane.showInputDialog(null,
                        "Informe o seu " +
                                "Sobrenome:", "Cadastro",
                        JOptionPane.INFORMATION_MESSAGE);
                funcionario.setSobrenome(sobrenome);

                long cpf = Long.parseLong(JOptionPane.showInputDialog(null,
                        "Informe o seu CPF:", "Cadastro",
                        JOptionPane.INFORMATION_MESSAGE));
                funcionario.setCpf(cpf);

                long telefone = Long.parseLong(JOptionPane.showInputDialog(null,
                        "Informe o seu Telefone:", "Cadastro",
                        JOptionPane.INFORMATION_MESSAGE));
                funcionario.setTelefone(telefone);

                JComboBox<String> empresasComboBox = new JComboBox<>();
                for (Empresa empresa : listaDeEmpresas) {
                    empresasComboBox.addItem(empresa.getNome());
                }

                int resultado = JOptionPane.showConfirmDialog(null, empresasComboBox,
                        "Selecione a empresa em que o funcionário trabalha:",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (resultado == JOptionPane.OK_OPTION) {
                    int indiceEmpresaSelecionada = empresasComboBox.getSelectedIndex();

                    Empresa empresaSelecionada = listaDeEmpresas.get(indiceEmpresaSelecionada);
                    funcionario.setEmpresa(empresaSelecionada);


                    String userReview;
                    userReview = ("Nome: " + funcionario.getNome() + " \n" + "Sobrenome: " +
                            funcionario.getSobrenome() + " \n" +
                            "Cpf: " + funcionario.getCpf() + "\n" + "Empresa: " + funcionario.getEmpresa()+ "\n" + "Telefone: " + funcionario.getTelefone());
                    JOptionPane.showMessageDialog(null, "" + userReview,
                            "Review", JOptionPane.INFORMATION_MESSAGE);

                    JOptionPane.showMessageDialog(null,
                            "Funcionário Cadastrado com sucesso!");


                    try (Connection conexao = banco.conectar()) {
                        String sql = "INSERT INTO funcionarios (cpf_funcionario, nome_funcionario, " +
                                "sobrenome_funcionario, telefone_funcionario, fk_empresa) VALUES (?, ?, ?, " +
                                "?, ?)";
                        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                            stmt.setLong(1, funcionario.getCpf());
                            stmt.setString(2, funcionario.getNome());
                            stmt.setString(3, funcionario.getSobrenome());
                            stmt.setLong(4, funcionario.getTelefone());
                            stmt.setLong(5, funcionario.getCnpjEmpresa());
                            stmt.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "Erro ao cadastrar Funcionário: " + e.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }


                }
            }




        } else if (opt == 1) {

            Empresa empresa = new Empresa();
            String nome = JOptionPane.showInputDialog(null,
                    "Informe o nome da Empresa:", "Cadastro",
                    JOptionPane.INFORMATION_MESSAGE);
            empresa.setNome(nome);

            String endereco = JOptionPane.showInputDialog(null,
                    "Informe o Endereço:", "Cadastro",
                    JOptionPane.INFORMATION_MESSAGE);
            empresa.setEndereco(endereco);

            long cnpj = Long.parseLong(JOptionPane.showInputDialog(null,
                    "Informe o CNPJ: ", "Cadastro",
                    JOptionPane.INFORMATION_MESSAGE));
            empresa.setCnpj(cnpj);

            long telefone = Long.parseLong(JOptionPane.showInputDialog(null,
                    "Informe o Telefone: ", "Cadastro",
                    JOptionPane.INFORMATION_MESSAGE));
            empresa.setTelefone(telefone);

            String userReview;
            userReview = ("Nome: " + empresa.getNome() + " \n" + "CNPJ: " + empresa.getCnpj() +
                    "\n" + "Endereço:" + empresa.getEndereco() + "\n" + "Telefone:" + empresa.getTelefone());
            JOptionPane.showMessageDialog(null, "" + userReview,
                    "Review", JOptionPane.INFORMATION_MESSAGE);

            JOptionPane.showMessageDialog(null,
                    "Empresa Cadastrada com sucesso!");
            listaDeEmpresas.add(empresa);

            try (Connection conexao = banco.conectar()) {
                String sql = "INSERT INTO Empresa (cnpj, nome_empresa, endereco_empresa, telefone_empresa) " +
                        "VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                    stmt.setLong(1, empresa.getCnpj());
                    stmt.setString(2, empresa.getNome());
                    stmt.setString(3, empresa.getEndereco());
                    stmt.setLong(4, empresa.getTelefone());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar Empresa: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        }

    }





    public void cadastroUser (){
        Cliente cliente = new Cliente();
        String nome = JOptionPane.showInputDialog(null,
                "Informe o seu nome:", "Cadastro",
                JOptionPane.INFORMATION_MESSAGE);
        cliente.setNome(nome);

        String sobrenome = JOptionPane.showInputDialog(null,
                "Informe o seu " +
                        "Sobrenome:", "Cadastro",
                JOptionPane.INFORMATION_MESSAGE);
        cliente.setSobrenome(sobrenome);

        String endereco = JOptionPane.showInputDialog(null,
                "Informe o seu " +
                        "Endereço:", "Cadastro",
                JOptionPane.INFORMATION_MESSAGE);
        cliente.setEndereco(endereco);

        long cpf = Long.parseLong(JOptionPane.showInputDialog(null,
                "Informe o seu CPF:", "Cadastro",
                JOptionPane.INFORMATION_MESSAGE));
        cliente.setCpf(cpf);

        String userReview;
        userReview = ("Nome: " + cliente.getNome() + " \n" + "Sobrenome: " +
                cliente.getSobrenome() + " \n" +
                "Cpf: " + cliente.getCpf() + "\n" + "Endereço: " + cliente.getEndereco());
        JOptionPane.showMessageDialog(null, "" + userReview,
                "Review", JOptionPane.INFORMATION_MESSAGE);

        JOptionPane.showMessageDialog(null,
                "Usuário Cadastrado com sucesso!");


        try (Connection conexao = banco.conectar()) {
            String sql = "INSERT INTO clientes (cpf_cliente, nome_cliente, sobrenome_cliente, " +
                    "endereco_cliente) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setLong(1, cliente.getCpf());
                stmt.setString(2, cliente.getNome());
                stmt.setString(3, cliente.getSobrenome());
                stmt.setString(4, cliente.getEndereco());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar Empresa: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void comprar(){

        atualizarListaDeProdutos();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> productList = new JList<>(listModel);

        JPanel panel = new JPanel(new BorderLayout());


        JFrame frame = new JFrame("Lista de Produtos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(panel);
        frame.setVisible(true);

        for (Produtos produto : listaDeProdutos) {
            listModel.addElement(produto.getNome() + " - R$" + produto.getPreco());
        }



        JButton calculateButton = new JButton("Calcular Total");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedProducts = productList.getSelectedValuesList();
                double totalValue = 0.0;
                for (String selectedProduct : selectedProducts) {
                    String[] parts = selectedProduct.split(" - R\\$");
                    if (parts.length == 2) {
                        totalValue += Double.parseDouble(parts[1]);
                    }
                }
                JOptionPane.showMessageDialog(null, "Valor Total: R$" + totalValue);
            }

        });
        panel.add(new JScrollPane(productList), BorderLayout.CENTER);
        panel.add(calculateButton, BorderLayout.SOUTH);


    }
}