package com.lehms.ui.clinical.model;

import java.io.Serializable;

public enum BloodSugerLevelInsulinType implements Serializable  {
	None,
	Actrapid,
	Humalog,
	Insulatard,
	Lantus,
	Mixtard_30_70,
	Mixtard_50_50,
	NovoMix,
	NovoRapid;
	
	@Override
	public String toString() {

		switch (this) {
		case Mixtard_30_70:
			return "Mixtard 30/70";
		case Mixtard_50_50:
			return "Mixtard 50/50";
		}
		return super.toString();
	}
}
