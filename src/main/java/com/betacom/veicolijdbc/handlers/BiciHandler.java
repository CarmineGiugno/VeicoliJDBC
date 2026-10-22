package com.betacom.veicolijdbc.handlers;

import org.springframework.stereotype.Component;

import com.betacom.veicolijdbc.dao.BiciDAO;
import com.betacom.veicolijdbc.models.Bici;

@Component
public class BiciHandler extends AbstractVehicleHandler<Bici> {

	public BiciHandler(BiciDAO dao) {
		super(dao);
	}

	@Override
	public String type() {
		return "bici";
	}

	@Override
	protected Bici newVehicle() {
		return new Bici();
	}
}
