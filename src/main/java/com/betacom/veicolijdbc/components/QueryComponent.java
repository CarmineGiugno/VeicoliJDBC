package com.betacom.veicolijdbc.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.betacom.veicolijdbc.dao.VeicoloDAO;
import com.betacom.veicolijdbc.exceptions.AcademyException;
import com.betacom.veicolijdbc.handlers.VehicleHandler;
import com.betacom.veicolijdbc.models.Veicolo;
import com.betacom.veicolijdbc.utilities.GestioneFile;
import com.betacom.veicolijdbc.utilities.Utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueryComponent {

	public static final String FILE_INPUT = "input.txt";
	public static final String FILE_OUTPUT = "output/veicoli_output.txt";

	private final Map<String, VehicleHandler> handlers;
	private final VeicoloDAO daoV;

	public QueryComponent(List<VehicleHandler> handlerList, VeicoloDAO daoV) {
		this.handlers = handlerList.stream()
				.collect(Collectors.toMap(VehicleHandler::type, Function.identity()));
		this.daoV = daoV;
		log.debug("Tipi di veicolo gestiti: {}", handlers.keySet());
	}

	// Niente @Transactional qui: ogni comando ha la sua transazione (vedi AbstractVehicleHandler),
	// così un errore SQL su una riga non blocca le righe successive.
	public void execute() {
		log.info("Start Veicoli, file di input: {}", FILE_INPUT);

		List<String> righe;
		try {
			righe = GestioneFile.leggi(FILE_INPUT);
			GestioneFile.inizializza(FILE_OUTPUT);
		} catch (AcademyException e) {
			log.warn(e.getMessage());
			return;
		}
		log.info("Lette {} righe", righe.size());

		for (int n = 0; n < righe.size(); n++) {
			String riga = righe.get(n);
			if (riga.isBlank()) {
				continue;
			}
			String[] token = Arrays.stream(riga.split(","))
					.map(String::trim)
					.toArray(String[]::new);

			try {
				executeLine(token);
			} catch (RuntimeException e) {
				log.warn("Riga {} ignorata ({}): {}", n + 1, riga.trim(), e.getMessage());
			}
		}

		log.info("End Veicoli");
	}

	private void executeLine(String[] t) {
		switch (t[0]) {
			case "add" -> {
				requireTokens(t, 3);
				handlerFor(t[1]).add(Utilities.parseParametri(t, 2));
			}
			case "update" -> {
				requireTokens(t, 4);
				handlerFor(t[1]).update(parseId(t[2]), Utilities.parseParametri(t, 3));
			}
			case "delete" -> {
				requireTokens(t, 3);
				handlerFor(t[1]).delete(parseId(t[2]));
			}
			case "findById" -> {
				requireTokens(t, 2);
				// findById,11 -> veicolo generico | findById,macchina,11 -> tipo specifico
				Optional<? extends Veicolo> found = (t.length == 2)
						? daoV.findById(parseId(t[1]))
						: handlerFor(t[1]).findById(parseId(t[2]));
				found.ifPresentOrElse(
						v -> log.debug("Trovato: {}", v),
						() -> log.warn("Nessun veicolo trovato per: {}", String.join(",", t)));
			}
			case "findAll" -> scriviOutput("Elenco veicoli", daoV.findAll());
			case "search"  -> {
			    Map<String, String> filtri = Utilities.parseParametri(t, 1);
			    scriviOutput("Ricerca " + filtri, daoV.search(filtri));
			}
			default -> throw new AcademyException("Comando sconosciuto: " + t[0]);
		}
	}

	private VehicleHandler handlerFor(String type) {
		VehicleHandler h = handlers.get(type);
		if (h == null) {
			throw new AcademyException("Tipo veicolo sconosciuto: " + type);
		}
		return h;
	}

	private void requireTokens(String[] t, int min) {
		if (t.length < min) {
			throw new AcademyException("Comando '" + t[0] + "' richiede almeno "
					+ min + " parametri, trovati " + t.length);
		}
	}

	private int parseId(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new AcademyException("Id non valido: " + s);
		}
	}
	
	private void scriviOutput(String titolo, List<? extends Veicolo> veicoli) {
	    List<String> output = new ArrayList<>();
	    output.add("=== " + titolo + " (" + veicoli.size() + ") ===");
	    veicoli.forEach(v -> output.add(v.toString()));
	    output.add("");
	    GestioneFile.aggiungi(FILE_OUTPUT, output);
	    log.debug("{}: scritti {} veicoli su {}", titolo, veicoli.size(), FILE_OUTPUT);
	}
}
