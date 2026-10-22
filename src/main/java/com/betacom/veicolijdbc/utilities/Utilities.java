package com.betacom.veicolijdbc.utilities;

import java.util.LinkedHashMap;
import java.util.Map;

import com.betacom.veicolijdbc.exceptions.AcademyException;

public class Utilities {
	
	public static Map<String, String> parseParametri(String[] token, int da) {
		Map<String, String> mappa = new LinkedHashMap<>();
		for (int i = da; i < token.length; i++) {
			String[] parti = token[i].split("=");
			if (parti.length != 2) {
				throw new AcademyException("Parametro non valido: " + token[i]);
			}
			String chiave = parti[0].trim().toLowerCase();
			if (mappa.putIfAbsent(chiave, parti[1].trim()) != null) {
				throw new AcademyException("Parametro duplicato: " + chiave);
			}
		}
		return mappa;
	}

}
