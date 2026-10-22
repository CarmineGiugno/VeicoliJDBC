package com.betacom.veicolijdbc.components;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.veicolijdbc.dao.MacchinaDAO;
import com.betacom.veicolijdbc.dao.VeicoloDAO;
import com.betacom.veicolijdbc.exceptions.AcademyException;
import com.betacom.veicolijdbc.models.Macchina;
import com.betacom.veicolijdbc.models.Veicolo;
import com.betacom.veicolijdbc.utilities.GestioneFile;
import com.betacom.veicolijdbc.utilities.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class QueryComponent {
	
	public final String FILE_INPUT  = "input.txt";
	private final MacchinaDAO daoM;
	private final VeicoloDAO daoV;
 
	@Transactional
	public void execute() {
		
		/*
		log.debug("Begin QueryComponent");
		List<Dipendenti> lD = daoD.findAll();
		
		lD.forEach(d -> log.debug(d.toString()));
		
		log.debug("+++++ byId ******");
		Optional<Dipendenti> dopt = daoD.findById(13);
		if(dopt.isEmpty())
			log.debug("record non trovato");
		else
			log.debug(dopt.toString());
		
		log.debug("*** stipendio > 1300");
		lD = daoD.findBySalaryGreater(1300.0);
		lD.forEach(d -> log.debug(d.toString()));
		
		log.debug("*** count stipendio > 1500");
		log.debug("numero trovato {}", daoD.countBySalaryGreater(1500.0)); */
		
		
		//public static final String FILE_OUTPUT = "output/veicoli_output.txt";
		//public static final String FILE_JSON   = "output/veicoli.json";
		
		String percorso = FILE_INPUT;
		log.info("Start Veicoli, file di input: {}", percorso);
		try {
			List<String> righe = GestioneFile.leggi(percorso);
			log.info("Lette {} righe", righe.size());
			//GestioneFile.inizializza(Configurazione.FILE_OUTPUT);
			
			for (int n = 0; n < righe.size(); n++) {
				String riga = righe.get(n);
				
				String[] token = Arrays.stream(riga.split(","))
						.map(String::trim)
						.toArray(String[]::new);
				
				switch(token[0]) {
					case "add" -> {
						if(token[1].equals("macchina")) {
							Map<String, String> parametri = Utilities.parseParametri(token, 2);
							Macchina m = Macchina.builder()
							.tipo_veicolo(token[1])
							.alimentazione(parametri.get("alimentazione"))
							.categoria(parametri.get("categoria"))
							.colore(parametri.get("colore"))
							.marca(parametri.get("marca"))
							.modello(parametri.get("modello"))
							.anno(Integer.parseInt(parametri.get("anno")))
							.ruote(Integer.parseInt(parametri.get("ruote")))
							.targa(parametri.get("targa"))
							.porte(Integer.parseInt(parametri.get("porte")))
							.cc(Integer.parseInt(parametri.get("cc")))
							.build();
							
							int idMacchina = daoM.insert(m);
							log.debug("Id Auto inserita: {}", idMacchina);
						}
					}
					case "findAll" -> {
						if(token[1].equals("veicolo")){
							List<Veicolo> lista = daoV.findAll();
							lista.forEach(v -> log.debug(v.toString()));
						}
					}
					case "update" -> {
					    if (token[1].equals("macchina")) {
					        int idMacchina = Integer.parseInt(token[2]);
					        Map<String, String> parametri = Utilities.parseParametri(token, 3);

					        Macchina m = daoM.findById(idMacchina)
					                .orElseThrow(() -> new AcademyException("Macchina non trovata: " + idMacchina));

					        BeanWrapper wrapper = new BeanWrapperImpl(m);
					        parametri.forEach((key, value) -> {
					            if (wrapper.isWritableProperty(key)) {
					                wrapper.setPropertyValue(key, value);
					            } else {
					                log.warn("Attributo non valido per macchina: {}", key);
					            }
					        });

					        daoM.update(m);
					        log.debug("Macchina aggiornata: {}", m);
					    }
					}
					case "delete" -> {
					    if (token[1].equals("macchina")) {
					        int idMacchina = Integer.parseInt(token[2]);
					        int result = daoM.delete(idMacchina);

					        if (result == 0)
					            log.warn("Nessuna macchina trovata con id {}", idMacchina);
					        else
					            log.debug("Macchina {} eliminata", idMacchina);
					    }
					}
				}	
			}		
		} catch (AcademyException e) {
			log.warn(e.getMessage());
		}
		log.info("End Veicoli");	
	}
}
