package model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;

import model.entity.enums.Resultado;
import model.entity.Partida;

public class PartidaRepository implements BaseRepository<Partida> {

	@Override
	public Partida salvar(Partida novaPartida) {
		String query = " INSERT INTO partida (ROUNDS_VENCIDOS_JOGADOR, ROUNDS_VENCIDOS_CPU, ROUNDS_EMPATADOS, RESULTADO, "
				+ "                      DATA, FORCA_UTILIZADA, INTELIGENCIA_UTILIZADA, VELOCIDADE_UTILIZADA) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = Banco.getPreparedStatementWithPk(conn, query);
		try {
			this.preencherValoresParaInsertOuUpdate(pstmt, novaPartida);
			pstmt.execute();
			ResultSet resultado = pstmt.getGeneratedKeys();

			if (resultado.next()) {
				novaPartida.setId(resultado.getInt(1));
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao salvar nova partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return novaPartida;
	}

	private PreparedStatement preencherValoresParaInsertOuUpdate(PreparedStatement pstmt, Partida partida)
			throws SQLException {
		pstmt.setInt(1, partida.getRoundsVencidosJogador());
		pstmt.setInt(2, partida.getRoundsVencidosCpu());
		pstmt.setInt(3, partida.getRoundsEmpatados());
		pstmt.setString(4, partida.getResultado().toString());
		pstmt.setObject(5, partida.getData());
		pstmt.setBoolean(6, partida.isJogouForca());
		pstmt.setBoolean(7, partida.isJogouInteligencia());
		pstmt.setBoolean(8, partida.isJogouVelocidade());
		return pstmt;
	}

	@Override
	public boolean excluir(int id) {
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);
		boolean excluiu = false;
		String query = "DELETE FROM partida WHERE id = " + id;
		try {
			if (stmt.executeUpdate(query) == 1) {
				excluiu = true;
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao excluir partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return excluiu;
	}

	@Override
	public boolean alterar(Partida partidaParaAtualizar) {
		boolean alterou = false;
		String query = " UPDATE partida SET "
				+ "   ROUNDS_VENCIDOS_JOGADOR=?, ROUNDS_VENCIDOS_CPU=?, ROUNDS_EMPATADOS=?, RESULTADO=?, "
				+ "   DATA=?, FORCA_UTILIZADA=?, INTELIGENCIA_UTILIZADA=?, VELOCIDADE_UTILIZADA=? ";
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = Banco.getPreparedStatementWithPk(conn, query);
		try {
			this.preencherValoresParaInsertOuUpdate(pstmt, partidaParaAtualizar);
			alterou = pstmt.executeUpdate(query) == 1;
		} catch (SQLException erro) {
			System.out.println("Erro ao atualizar partida");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return alterou;
	}

	@Override
	public Partida consultarPorId(int id) {
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);

		ResultSet resultado = null;
		Partida partida = new Partida();
		String query = " SELECT * FROM partida WHERE id = " + id;

		try {
			resultado = stmt.executeQuery(query);
			if (resultado.next()) {
				partida = converterDoResultSet(resultado);

			}
		} catch (SQLException erro) {
			System.out.println("Erro ao executar consultar partida com id (" + id + ")");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeResultSet(resultado);
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return partida;
	}

	private Partida converterDoResultSet(ResultSet resultado) throws SQLException {
		Partida p = new Partida();
		p.setId(Integer.parseInt(resultado.getString("ID")));

		// TODO como preencher o jogador?
		// p.setJogador(resultado.getString("NOME"));

		p.setRoundsVencidosJogador(resultado.getInt("ROUNDS_VENCIDOS_JOGADOR"));
		p.setRoundsVencidosCpu(resultado.getInt("ROUNDS_VENCIDOS_CPU"));
		p.setRoundsEmpatados(resultado.getInt("ROUNDS_EMPATADOS"));
		p.setResultado(Resultado.valueOf(resultado.getString("RESULTADO")));

		// Referência: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
		p.setData(resultado.getDate("DATA").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

		p.setJogouForca(resultado.getBoolean("FORCA_UTILIZADA"));
		p.setJogouInteligencia(resultado.getBoolean("INTELIGENCIA_UTILIZADA"));
		p.setJogouVelocidade(resultado.getBoolean(""));

		return p;
	}

	@Override
	public ArrayList<Partida> consultarTodos() {
		ArrayList<Partida> partidas = new ArrayList<>();
		Connection conn = Banco.getConnection();
		Statement stmt = Banco.getStatement(conn);

		ResultSet resultado = null;
		String query = " SELECT * FROM partida";

		try {
			resultado = stmt.executeQuery(query);
			while (resultado.next()) {
				Partida partida = converterDoResultSet(resultado);
				partidas.add(partida);
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao executar consultar todas as partidas");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeResultSet(resultado);
			Banco.closeStatement(stmt);
			Banco.closeConnection(conn);
		}
		return partidas;
	}
}
