package com.betacom.veicolijdbc.utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.betacom.veicolijdbc.exceptions.AcademyException;

public class GestioneFile {
	private GestioneFile() { }

	public static List<String> leggi(String percorso) {
		Path path = Path.of(percorso);
		try {
			return Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new AcademyException("Impossibile leggere il file " + path.toAbsolutePath()
					+ " (" + e.getClass().getSimpleName() + ")", e);
		}
	}
	
	public static void inizializza(String percorso) {
		Path path = Path.of(percorso);
		try {
			if (path.getParent() != null) {
				Files.createDirectories(path.getParent());
			}
			Files.writeString(path, "", StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new AcademyException("Impossibile preparare il file " + path.toAbsolutePath()
					+ " (" + e.getClass().getSimpleName() + ")", e);
		}
	}
	
	public static void aggiungi(String percorso, List<String> righe) {
		Path path = Path.of(percorso);
		try {
			Files.write(path, righe, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new AcademyException("Impossibile scrivere sul file " + path.toAbsolutePath()
					+ " (" + e.getClass().getSimpleName() + ")", e);
		}
	}
}
