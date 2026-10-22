package com.betacom.veicolijdbc.dao;

import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.models.Moto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MotoDAO implements VehicleDAO<Moto> {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final VeicoloDAO veicoloDAO;

	@Override
	public int insert(Moto m) {
		int id = veicoloDAO.insert(m);
		m.setId(id);
		// Quando Moto avrà campi propri: aggiungili qui e in params()
		jdbcTemplate.update("INSERT INTO moto (id, tipo_forcella) VALUES (:id, :tipo_forcella)", params(m));
		return id;
	}

	@Override
	public Optional<Moto> findById(int id) {
		return veicoloDAO.findById(id)
				.filter(Moto.class::isInstance)
				.map(Moto.class::cast);
	}

	@Override
	public int update(Moto m) {
		veicoloDAO.update(m);
		return jdbcTemplate.update("UPDATE moto SET tipo_forcella = :tipo_forcella WHERE id = :id", params(m));
	}

	@Override
	public int delete(int id) {
		int deleted = jdbcTemplate.update("DELETE FROM moto WHERE id = :id", new MapSqlParameterSource("id", id));
		return deleted == 0 ? 0 : veicoloDAO.delete(id);
	}

	private MapSqlParameterSource params(Moto m) {
		return new MapSqlParameterSource()
				.addValue("id", m.getId())
				.addValue("tipo_forcella", m.getTipo_forcella());
	}
}
