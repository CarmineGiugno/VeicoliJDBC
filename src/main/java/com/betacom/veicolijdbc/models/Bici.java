package com.betacom.veicolijdbc.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class Bici extends Veicolo {
	private Integer corone;
	private Integer marce;
	private String freno;
	private String sospensione;
	private boolean pieghevole;
}
