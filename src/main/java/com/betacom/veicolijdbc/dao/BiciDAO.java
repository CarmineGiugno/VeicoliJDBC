package com.betacom.veicolijdbc.dao;

import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.models.Bici;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BiciDAO implements VehicleDAO<Bici> {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final VeicoloDAO veicoloDAO;

	@Override
	public int insert(Bici b) {
		int id = veicoloDAO.insert(b);
		b.setId(id);
		jdbcTemplate.update("""
				INSERT INTO bici (id, corone, marce, freno, sospensione, pieghevole)
				VALUES (:id, :corone, :marce, :freno, :sospensione, :pieghevole)
				""", params(b));
		return id;
	}

	@Override
	public Optional<Bici> findById(int id) {
		return veicoloDAO.findById(id)
				.filter(Bici.class::isInstance)
				.map(Bici.class::cast);
	}

	@Override
	public int update(Bici b) {
		veicoloDAO.update(b);
		return jdbcTemplate.update("""
				UPDATE bici
				SET corone = :corone, marce = :marce, freno = :freno,
				    sospensione = :sospensione, pieghevole = :pieghevole
				WHERE id = :id
				""", params(b));
	}

	@Override
	public int delete(int id) {
		int deleted = jdbcTemplate.update("DELETE FROM bici WHERE id = :id", new MapSqlParameterSource("id", id));
		return deleted == 0 ? 0 : veicoloDAO.delete(id);
	}

	private MapSqlParameterSource params(Bici b) {
		return new MapSqlParameterSource()
				.addValue("id", b.getId())
				.addValue("corone", b.getCorone())
				.addValue("marce", b.getMarce())
				.addValue("freno", b.getFreno())
				.addValue("sospensione", b.getSospensione())
				.addValue("pieghevole", b.isPieghevole());
	}
}
