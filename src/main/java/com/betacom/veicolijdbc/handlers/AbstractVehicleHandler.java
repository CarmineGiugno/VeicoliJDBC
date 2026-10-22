package com.betacom.veicolijdbc.handlers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.veicolijdbc.dao.VehicleDAO;
import com.betacom.veicolijdbc.exceptions.AcademyException;
import com.betacom.veicolijdbc.models.Veicolo;

import lombok.extern.slf4j.Slf4j;

/**
 * Logica comune a tutti i tipi di veicolo.
 * Le sottoclassi dicono solo quale tipo gestiscono e come creare un oggetto vuoto.
 */
@Slf4j
public abstract class AbstractVehicleHandler<T extends Veicolo> implements VehicleHandler {

	// Campi che non si possono impostare dal file di input
	private static final Set<String> PROTECTED_FIELDS = Set.of("id", "tipo_veicolo");

	private final VehicleDAO<T> dao;

	protected AbstractVehicleHandler(VehicleDAO<T> dao) {
		this.dao = dao;
	}

	protected abstract T newVehicle();

	@Override
	@Transactional
	public void add(Map<String, String> params) {
		T v = newVehicle();
		applyParams(v, params);
		v.setTipo_veicolo(type());
		int id = dao.insert(v);
		log.debug("Inserito {} con id {}", type(), id);
	}

	@Override
	public Optional<T> findById(int id) {
		return dao.findById(id);
	}

	@Override
	@Transactional
	public void update(int id, Map<String, String> params) {
		T v = dao.findById(id)
				.orElseThrow(() -> new AcademyException(type() + " non trovato: " + id));
		applyParams(v, params);
		dao.update(v);
		log.debug("Aggiornato: {}", v);
	}

	@Override
	@Transactional
	public void delete(int id) {
		if (dao.delete(id) == 0) {
			log.warn("Nessun {} trovato con id {}", type(), id);
		} else {
			log.debug("Eliminato {} con id {}", type(), id);
		}
	}

	/** Imposta i campi per nome; BeanWrapper converte da String a Integer/boolean da solo. */
	private void applyParams(T v, Map<String, String> params) {
		BeanWrapper wrapper = new BeanWrapperImpl(v);
		params.forEach((key, value) -> {
			if (PROTECTED_FIELDS.contains(key) || !wrapper.isWritableProperty(key)) {
				log.warn("Attributo non valido per {}: {}", type(), key);
			} else {
				wrapper.setPropertyValue(key, value);
			}
		});
	}
}
