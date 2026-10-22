package com.betacom.veicolijdbc.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@ToString
@SuperBuilder
public class Veicolo {
	private Integer id;
	private String tipo_veicolo;
	private String alimentazione;
	private String categoria;
	private String colore;
	private String marca;
	private String modello;
	private Integer anno;
	private Integer ruote;
	private String targa;
	private Integer cc;
}
