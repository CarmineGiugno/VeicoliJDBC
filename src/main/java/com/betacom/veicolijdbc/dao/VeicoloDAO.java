package com.betacom.veicolijdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.models.Veicolo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class VeicoloDAO {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	public List<Veicolo> findAll(){
			
			String sql = "select * from veicolo";
			 return jdbcTemplate.query(sql, rs -> {
				 List<Veicolo> v = new ArrayList<Veicolo>();
				 while(rs.next()) {
					 v.add(Veicolo.builder()
							 .id(rs.getInt("id"))
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
				 return v;
			 });
	}
	
	
	
}

