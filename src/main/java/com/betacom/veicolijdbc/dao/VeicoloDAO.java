package com.betacom.veicolijdbc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.betacom.veicolijdbc.exceptions.AcademyException;
import com.betacom.veicolijdbc.models.Bici;
import com.betacom.veicolijdbc.models.Macchina;
import com.betacom.veicolijdbc.models.Moto;
import com.betacom.veicolijdbc.models.Veicolo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class VeicoloDAO {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	private record Filtro(String colonna, Function<String, Object> parser) {}

	private static final Map<String, Filtro> FILTRI = Map.ofEntries(
	        Map.entry("tipo_veicolo",  new Filtro("v.tipo_veicolo", s -> s)),
	        Map.entry("alimentazione", new Filtro("v.alimentazione", s -> s)),
	        Map.entry("categoria",     new Filtro("v.categoria", s -> s)),
	        Map.entry("colore",        new Filtro("v.colore", s -> s)),
	        Map.entry("marca",         new Filtro("v.marca", s -> s)),
	        Map.entry("modello",       new Filtro("v.modello", s -> s)),
	        Map.entry("targa",         new Filtro("v.targa", s -> s)),
	        Map.entry("anno",          new Filtro("v.anno", Integer::valueOf)),
	        Map.entry("ruote",         new Filtro("v.ruote", Integer::valueOf)),
	        Map.entry("cc",            new Filtro("v.cc", Integer::valueOf)),
	        Map.entry("porte",         new Filtro("m.porte", Integer::valueOf)),
	        Map.entry("marce",         new Filtro("b.marce", Integer::valueOf)),
	        Map.entry("corone",        new Filtro("b.corone", Integer::valueOf)),
	        Map.entry("freno",         new Filtro("b.freno", s -> s)),
	        Map.entry("pieghevole",    new Filtro("b.pieghevole", Boolean::valueOf)));

	private static final String SELECT_FULL = """
			SELECT v.*,
			       m.porte,
			       b.corone, b.marce, b.freno, b.sospensione, b.pieghevole,
			       mo.tipo_forcella
			FROM veicolo v
			LEFT JOIN macchina m ON m.id = v.id
			LEFT JOIN bici b     ON b.id = v.id
			LEFT JOIN moto mo	 ON mo.id = v.id
			""";

	public List<Veicolo> findAll() {
		return jdbcTemplate.query(SELECT_FULL + " ORDER BY v.id", this::mapRow);
	}

	public Optional<Veicolo> findById(int id) {
		return jdbcTemplate.query(SELECT_FULL + " WHERE v.id = :id",
				new MapSqlParameterSource("id", id), this::mapRow)
				.stream()
				.findFirst();
	}

	public int insert(Veicolo v) {
		String sql = """
				INSERT INTO veicolo (tipo_veicolo, alimentazione, categoria, colore, marca, modello, anno, ruote, targa, cc)
				VALUES (:tipo_veicolo, :alimentazione, :categoria, :colore, :marca, :modello, :anno, :ruote, :targa, :cc)
				""";
		KeyHolder key = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params(v), key, new String[] { "id" });
		return key.getKey().intValue();
	}

	public int update(Veicolo v) {
		String sql = """
				UPDATE veicolo
				SET tipo_veicolo = :tipo_veicolo, alimentazione = :alimentazione, categoria = :categoria,
				    colore = :colore, marca = :marca, modello = :modello, anno = :anno,
				    ruote = :ruote, targa = :targa, cc = :cc
				WHERE id = :id
				""";
		return jdbcTemplate.update(sql, params(v));
	}

	public int delete(int id) {
		return jdbcTemplate.update("DELETE FROM veicolo WHERE id = :id", new MapSqlParameterSource("id", id));
	}

	private MapSqlParameterSource params(Veicolo v) {
		return new MapSqlParameterSource()
				.addValue("id", v.getId())
				.addValue("tipo_veicolo", v.getTipo_veicolo())
				.addValue("alimentazione", v.getAlimentazione())
				.addValue("categoria", v.getCategoria())
				.addValue("colore", v.getColore())
				.addValue("marca", v.getMarca())
				.addValue("modello", v.getModello())
				.addValue("anno", v.getAnno())
				.addValue("ruote", v.getRuote())
				.addValue("targa", v.getTarga())
				.addValue("cc", v.getCc());
	}

	//sceglie la sottoclasse in base a tipo_veicolo e poi riempie i campi comuni
	private Veicolo mapRow(ResultSet rs, int rowNum) throws SQLException {
		String tipo = rs.getString("tipo_veicolo");
		Veicolo v = switch (tipo == null ? "" : tipo) {
			case "macchina" -> {
				Macchina m = new Macchina();
				m.setPorte(rs.getObject("porte", Integer.class));
				yield m;  //ritorna m in veicolo
			}
			case "bici" -> {
				Bici b = new Bici();
				b.setCorone(rs.getObject("corone", Integer.class));
				b.setMarce(rs.getObject("marce", Integer.class));
				b.setFreno(rs.getString("freno"));
				b.setSospensione(rs.getString("sospensione"));
				b.setPieghevole(rs.getBoolean("pieghevole"));
				yield b;
			}
			case "moto" -> {
				Moto mo = new Moto();
				mo.setTipo_forcella(rs.getString("tipo_forcella"));
				yield mo;
			}
			
			default -> new Veicolo();
		};

		v.setId(rs.getInt("id"));
		v.setTipo_veicolo(tipo);
		v.setAlimentazione(rs.getString("alimentazione"));
		v.setCategoria(rs.getString("categoria"));
		v.setColore(rs.getString("colore"));
		v.setMarca(rs.getString("marca"));
		v.setModello(rs.getString("modello"));
		v.setAnno(rs.getObject("anno", Integer.class));
		v.setRuote(rs.getObject("ruote", Integer.class));
		v.setTarga(rs.getString("targa"));
		v.setCc(rs.getObject("cc", Integer.class));
		return v;
	}
	
	public List<Veicolo> search(Map<String, String> params) {
	    StringBuilder sql = new StringBuilder(SELECT_FULL).append(" WHERE 1=1");
	    MapSqlParameterSource sqlParams = new MapSqlParameterSource();
	    String orderBy = "v.id";

	    for (Map.Entry<String, String> e : params.entrySet()) {
	        String key = e.getKey();
	        String value = e.getValue();

	        if (key.equals("ordina")) {
	            orderBy = filtro(value).colonna();          // anche l'ordinamento passa dalla whitelist
	            continue;
	        }

	        Filtro f = filtro(key);
	        Object converted;
	        try {
	            converted = f.parser().apply(value);
	        } catch (NumberFormatException ex) {
	            throw new AcademyException("Valore non valido per " + key + ": " + value);
	        }

	        if (converted instanceof String) {               // testo: confronto senza maiuscole/minuscole
	            sql.append(" AND LOWER(").append(f.colonna()).append(") = LOWER(:").append(key).append(")");
	        } else {
	            sql.append(" AND ").append(f.colonna()).append(" = :").append(key);
	        }
	        sqlParams.addValue(key, converted);
	    }

	    sql.append(" ORDER BY ").append(orderBy);
	    log.debug("Search SQL: {}", sql);
	    return jdbcTemplate.query(sql.toString(), sqlParams, this::mapRow);
	}

	private Filtro filtro(String key) {
	    Filtro f = FILTRI.get(key);
	    if (f == null) {
	        throw new AcademyException("Filtro non valido: " + key + " (ammessi: " + FILTRI.keySet() + ")");
	    }
	    return f;
	}
	
}
