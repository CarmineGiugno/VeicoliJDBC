package com.betacom.veicolijdbc.handlers;

import org.springframework.stereotype.Component;

import com.betacom.veicolijdbc.dao.MacchinaDAO;
import com.betacom.veicolijdbc.models.Macchina;

@Component
public class MacchinaHandler extends AbstractVehicleHandler<Macchina> {

	public MacchinaHandler(MacchinaDAO dao) {
		super(dao);
	}

	@Override
	public String type() {
		return "macchina";
	}

	@Override
	protected Macchina newVehicle() {
		return new Macchina();
	}
}
