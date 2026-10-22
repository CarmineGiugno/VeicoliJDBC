package com.betacom.veicolijdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.models.Macchina;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MacchinaDAO {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<Macchina> findAll(){
		
		String sql = "select * from macchina, veicolo where macchina.id = veicolo.id ";
		 return jdbcTemplate.query(sql, rs -> {
			 List<Macchina> m = new ArrayList<Macchina>();
			 while(rs.next()) {
				 m.add(Macchina.builder()
						 .id(rs.getInt("id"))
						 .porte(rs.getInt("porte"))
						 .tipo_veicolo(rs.getString("tipo_veicolo"))
						 .alimentazione(rs.getString("alimentazione"))
						 .categoria(rs.getString("categoria"))
						 .colore(rs.getString("colore"))
						 .marca(rs.getString("marca"))
						 .modello(rs.getString("modello"))
						 .anno(rs.getInt("anno"))
						 .ruote(rs.getInt("ruote"))
						 .targa(rs.getString("targa"))
						 .cc(rs.getInt("cc"))
						 .build());
			 }
			 return m;
		 });
	}
	
	public int insert(Macchina m) {
		String sql = """
				WITH m9 AS (
				INSERT INTO veicolo (tipo_veicolo, alimentazione, categoria, colore, marca, modello, anno, ruote, targa, cc) 
				VALUES (:tipo_veicolo, :alimentazione, :categoria, :colore, :marca, :modello, :anno, :ruote,:targa,:cc) RETURNING id
				) INSERT INTO macchina (id, porte) SELECT id, :porte FROM m9;
				""";
		
		SqlParameterSource param = new MapSqlParameterSource()
					.addValue("tipo_veicolo", m.getTipo_veicolo())
					.addValue("alimentazione", m.getAlimentazione())
					.addValue("categoria", m.getCategoria())
					.addValue("colore", m.getColore())
					.addValue("marca", m.getMarca())
					.addValue("modello", m.getModello())
					.addValue("anno", m.getAnno())
					.addValue("ruote", m.getRuote())
					.addValue("targa", m.getTarga())
					.addValue("cc", m.getCc())
					.addValue("porte", m.getPorte());
					
		KeyHolder myKey = new GeneratedKeyHolder();
		int k =  jdbcTemplate.update(sql, param, myKey, new String[] {"id"}); //indicato a jdbc dove si trova la primary key
		log.debug("numero di righe inserite: {}", k); 
		return myKey.getKey().intValue();
	}
	
	public Optional<Macchina> findById(Integer id) {
		String sql = "select * from macchina m join veicolo v ON v.id = m.id where m.id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		
		try {
		Macchina macchina =  jdbcTemplate.queryForObject(sql,  param, (rs, rowNum) ->   //query for object restituisce solo una riga
					Macchina.builder()
					 .id(rs.getInt("id"))
					 .porte(rs.getInt("porte"))
					 .tipo_veicolo(rs.getString("tipo_veicolo"))
					 .alimentazione(rs.getString("alimentazione"))
					 .categoria(rs.getString("categoria"))
					 .colore(rs.getString("colore"))
					 .marca(rs.getString("marca"))
					 .modello(rs.getString("modello"))
					 .anno(rs.getInt("anno"))
					 .ruote(rs.getInt("ruote"))
					 .targa(rs.getString("targa"))
					 .cc(rs.getInt("cc"))
					 .build());
		return Optional.ofNullable(macchina);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	
	public int update(Macchina m) {
	    SqlParameterSource paramV = new MapSqlParameterSource()
	            .addValue("id", m.getId())
	            .addValue("tipo_veicolo", m.getTipo_veicolo())
	            .addValue("alimentazione", m.getAlimentazione())
	            .addValue("categoria", m.getCategoria())
	            .addValue("colore", m.getColore())
	            .addValue("marca", m.getMarca())
	            .addValue("modello", m.getModello())
	            .addValue("anno", m.getAnno())
	            .addValue("ruote", m.getRuote())
	            .addValue("cc", m.getCc())
	    		.addValue("targa", m.getTarga());

	    jdbcTemplate.update("""
	            UPDATE veicolo
	            SET tipo_veicolo = :tipo_veicolo, alimentazione = :alimentazione,
	                categoria = :categoria, colore = :colore, marca = :marca,
	                modello = :modello, anno = :anno, ruote = :ruote, targa =:targa, cc =:cc
	            WHERE id = :id
	            """, paramV);

	    SqlParameterSource paramM = new MapSqlParameterSource()
	            .addValue("id", m.getId())
	            .addValue("porte", m.getPorte());
	            

	    return jdbcTemplate.update("""
	            UPDATE macchina
	            SET porte = :porte
	            WHERE id = :id
	            """, paramM);
	}
	
	public int delete(int id) {
	    SqlParameterSource param = new MapSqlParameterSource("id", id);

	    int deleted = jdbcTemplate.update("DELETE FROM macchina WHERE id = :id", param);
	    if (deleted == 0) {
	        return 0; // nessuna macchina con quell'id: non tocco veicolo
	    }

	    return jdbcTemplate.update("DELETE FROM veicolo WHERE id = :id", param);
	}
	
}
