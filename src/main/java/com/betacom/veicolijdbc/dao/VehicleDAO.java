package com.betacom.veicolijdbc.dao;

import java.util.Optional;

import com.betacom.veicolijdbc.models.Veicolo;

public interface VehicleDAO<T extends Veicolo> {
	int insert(T v);
	Optional<T> findById(int id);
	int update(T v);
	int delete(int id);
}
