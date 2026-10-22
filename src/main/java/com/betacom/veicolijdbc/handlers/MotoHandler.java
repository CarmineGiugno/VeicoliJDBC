package com.betacom.veicolijdbc.handlers;

import org.springframework.stereotype.Component;

import com.betacom.veicolijdbc.dao.MotoDAO;
import com.betacom.veicolijdbc.models.Moto;

@Component
public class MotoHandler extends AbstractVehicleHandler<Moto> {

	public MotoHandler(MotoDAO dao) {
		super(dao);
	}

	@Override
	public String type() {
		return "moto";
	}

	@Override
	protected Moto newVehicle() {
		return new Moto();
	}
}
