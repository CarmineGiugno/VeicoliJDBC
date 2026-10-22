package com.betacom.veicolijdbc.dao;

import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.models.Macchina;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MacchinaDAO implements VehicleDAO<Macchina> {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final VeicoloDAO veicoloDAO;

	@Override
	public int insert(Macchina m) {
		int id = veicoloDAO.insert(m);
		m.setId(id);
		jdbcTemplate.update("INSERT INTO macchina (id, porte) VALUES (:id, :porte)", params(m));
		return id;
	}

	@Override
	public Optional<Macchina> findById(int id) {
		return veicoloDAO.findById(id)
				.filter(Macchina.class::isInstance)
				.map(Macchina.class::cast);
	}

	@Override
	public int update(Macchina m) {
		veicoloDAO.update(m);
		return jdbcTemplate.update("UPDATE macchina SET porte = :porte WHERE id = :id", params(m));
	}

	@Override
	public int delete(int id) {
		int deleted = jdbcTemplate.update("DELETE FROM macchina WHERE id = :id", new MapSqlParameterSource("id", id));
		return deleted == 0 ? 0 : veicoloDAO.delete(id);   //nessuna macchina con quell'id: non tocco veicolo
	}

	private MapSqlParameterSource params(Macchina m) {
		return new MapSqlParameterSource()
				.addValue("id", m.getId())
				.addValue("porte", m.getPorte());
	}
}
