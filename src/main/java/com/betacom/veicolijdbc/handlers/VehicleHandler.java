package com.betacom.veicolijdbc.handlers;

import java.util.Map;
import java.util.Optional;

import com.betacom.veicolijdbc.models.Veicolo;

public interface VehicleHandler {
	String type();                                   // "macchina", "moto", "bici"
	void add(Map<String, String> params);
	Optional<? extends Veicolo> findById(int id);
	void update(int id, Map<String, String> params);
	void delete(int id);
}
