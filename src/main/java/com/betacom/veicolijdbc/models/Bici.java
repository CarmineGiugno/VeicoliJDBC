package com.betacom.veicolijdbc.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class Bici extends Veicolo{
	private Integer id;
	private Integer corone;
	private Integer marce;
	private String freno;
	private String sospensione;
	private boolean pieghevole;
}
