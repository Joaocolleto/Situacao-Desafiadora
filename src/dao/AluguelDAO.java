package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Aluguel;
import util.ConnectionFactory;

public class AluguelDAO {

    // ======================================//
    // READ
    // ======================================//
    public List<Aluguel> buscarTodos() {
        
        List<Aluguel> Locacao = new ArrayList<>();

        String sql = "SELECT * FROM locacao";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();) {
            
            while(rs.next()) {
                LocalDateTime dataLocacao = rs.getObject("data_locacao", LocalDateTime.class);

                Aluguel aluguel = new Aluguel (
                    rs.getLong("idlocacao"),
                    rs.getLong("idQuadra"),
                    rs.getLong("idCliente"),
                    
                    dataLocacao
                    );
                Locacao.add(aluguel);
            }
        } catch (Exception e) {
           System.err.println("Erro ao buscar locacoes: " + e.getMessage());
           e.printStackTrace();
        }
        return Locacao;
    }

    // ======================================//
    // READ BY ID
    // ======================================//
   public List<Aluguel> buscarPorQuadraId(Long idQuadra) {

        // O tipo de retorno deve ser List<Aluguel> (e não Locacao/Aluguel singular)
        List<Aluguel> alugueis = new ArrayList<>();

        // SQL: Filtra por quadra_idquadra, que é a chave estrangeira na tabela locacao
        String sql = "SELECT quadra_idquadra, cliente_idcliente, data_locacao FROM locacao WHERE quadra_idquadra = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Define o ID da Quadra no placeholder '?' do SQL
            stmt.setLong(1, idQuadra);

            try (ResultSet rs = stmt.executeQuery()) {
                
                // Itera sobre todos os resultados encontrados para esta quadra
                while (rs.next()) {
                    
                    // Mapeamento correto do DATETIME para LocalDateTime
                    LocalDateTime dataLocacao = rs.getObject("data_locacao", LocalDateTime.class);

                    // 2. Cria o objeto Aluguel usando os dados do ResultSet
                    Aluguel aluguel = new Aluguel(
                        rs.getLong("quadra_idquadra"),
                        rs.getLong("cliente_idcliente"),
                        dataLocacao
                    );
                    
                    // Adiciona o aluguel à lista
                    alugueis.add(aluguel);
                }
            }
        } catch (SQLException e) {
            // Imprime erro com o ID da quadra que estava sendo buscada
            System.err.println("Erro ao buscar aluguéis por Quadra ID: " + idQuadra + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return alugueis; // Retorna a lista de aluguéis
    }

    // ======================================//
    // CREATE
    // ======================================//
    public void inserir(Aluguel aluguel) {

        // usa Statement.RETURN_GENERATED_KEYS para solicitar o ID gerado
        String sql = "INSERT INTO locacao (quadra_idquadra, cliente_idcliente, data_locacao) VALUES (?,?,?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aluguel.get());

            stmt.executeUpdate();   

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // define o ID no objeto Produto que foi passado (importante para a API)
                    categoria.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir categoria: " + categoria.getNome() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE
    // ------------------------------------
    public void atualizar(Categoria categoria) {

        String sql = "UPDATE produtos SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define os parâmetros (os novos valores)
            stmt.setString(1, categoria.getNome());
            // define o ID no WHERE (o último '?')
            stmt.setLong(4, categoria.getId());

            // executa a atualização
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Categoria ID " + categoria.getId() + " atualizado. Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto ID: " + categoria.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) throws SQLIntegrityConstraintViolationException {

        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            // executa a exclusão
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tentativa de deletar Categoria ID " + id + ". Linhas afetadas: " + linhasAfetadas);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLIntegrityConstraintViolationException();
        }

        catch (SQLException e) {
            System.err.println("Erro ao deletar categoria ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
            throw new SQLIntegrityConstraintViolationException();
        }
    }
}
