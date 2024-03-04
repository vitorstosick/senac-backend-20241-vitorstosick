package model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.entity.CartaNaPartida;

public class CartaPartidaRepository implements BaseRepository<CartaNaPartida> {

	@Override
	public CartaNaPartida salvar(CartaNaPartida novaCartaNaPartida) {
		String query = " INSERT INTO CARTA_PARTIDA (ID_PARTIDA, ID_CARTA, DO_JOGADOR, UTILIZADA) "
				     + " VALUES (?, ?, ?, ?)";
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = Banco.getPreparedStatementWithPk(conn, query);
		try {
			this.preencherValoresParaInsertOuUpdate(pstmt, novaCartaNaPartida);
			pstmt.execute();
			ResultSet resultado = pstmt.getGeneratedKeys();
			
			if(resultado.next()) {
				novaCartaNaPartida.setId(resultado.getInt(1));
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao salvar carta associada à partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return novaCartaNaPartida;
	}

	private PreparedStatement preencherValoresParaInsertOuUpdate(PreparedStatement pstmt, CartaNaPartida cartaNaPartida) throws SQLException {
		pstmt.setInt(1, cartaNaPartida.getIdPartida());
		pstmt.setInt(2, cartaNaPartida.getCarta().getId());
		pstmt.setBoolean(3, cartaNaPartida.isPertenceAoJogador());
		pstmt.setBoolean(4, cartaNaPartida.isUtilizada());
		return pstmt;
	}

	@Override
	public boolean excluir(int id) {
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);
		boolean excluiu = false;
		String query = "DELETE FROM carta_partida WHERE id = " + id;
		try {
			if(stmt.executeUpdate(query) == 1) {
				excluiu = true;
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao excluir carta da partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return excluiu;
	}

	@Override
	public boolean alterar(CartaNaPartida partidaParaAtualizar) {
		boolean alterou = false;
		String query = " UPDATE carta_partida SET "
				     + "   ID_PARTIDA=?, ID_CARTA=?, DO_JOGADOR=?, UTILIZADA=? "
				     + " WHERE ID = " + partidaParaAtualizar.getId();
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = Banco.getPreparedStatement(conn, query);
		try {
			this.preencherValoresParaInsertOuUpdate(pstmt, partidaParaAtualizar);
			alterou = pstmt.executeUpdate(query) == 1;
		} catch (SQLException erro) {
			System.out.println("Erro ao atualizar carta na partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return alterou;
	}

	@Override
	public CartaNaPartida consultarPorId(int id) {
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);
		
		ResultSet resultado = null;
		CartaNaPartida partida = new CartaNaPartida();
		String query = " SELECT * FROM carta_partida WHERE id = " + id;
		
		try{
			resultado = stmt.executeQuery(query);
			if(resultado.next()){
				partida = converterDoResultSet(resultado);
			}
		} catch (SQLException erro){
			System.out.println("Erro ao executar consultar carta na partida com id (" + id + ")");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeResultSet(resultado);
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return partida;
	}

	private CartaNaPartida converterDoResultSet(ResultSet resultado) throws SQLException {
		CartaNaPartida cartaPartida = new CartaNaPartida();
		cartaPartida.setId(Integer.parseInt(resultado.getString("ID")));
		cartaPartida.setIdPartida(resultado.getInt("ID_PARTIDA"));
		
		//TODO como preencher a carta?
		//p.setCarta(resultado.getInt("ID_CARTA"));

		cartaPartida.setPertenceAoJogador(resultado.getBoolean("DO_JOGADOR"));
		cartaPartida.setUtilizada(resultado.getBoolean("UTILIZADA"));

		return cartaPartida;
	}

	@Override
	public ArrayList<CartaNaPartida> consultarTodos() {
		ArrayList<CartaNaPartida> partidas = new ArrayList<>();
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);
		
		ResultSet resultado = null;
		String query = " SELECT * FROM carta_partida ";
		
		try{
			resultado = stmt.executeQuery(query);
			while(resultado.next()){
				CartaNaPartida partida = converterDoResultSet(resultado);
				partidas.add(partida);
			}
		} catch (SQLException erro){
			System.out.println("Erro ao executar consultar todas as cartas utilizadas em cada partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeResultSet(resultado);
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return partidas;
	}
}
