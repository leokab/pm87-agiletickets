package br.com.caelum.agiletickets.models;

import br.com.caelum.agiletickets.domain.precos.CalculadoraPrecoBallet;
import br.com.caelum.agiletickets.domain.precos.CalculadoraPrecoCinemaOuShow;
import br.com.caelum.agiletickets.domain.precos.CalculadoraPrecoOrquestra;
import br.com.caelum.agiletickets.domain.precos.CalculadoraPrecoTeatro;
import br.com.caelum.agiletickets.domain.precos.CalculadoraPreco;

public enum TipoDeEspetaculo {
	
	CINEMA {
		@Override
		public CalculadoraPreco getCalculadoraPreco(Sessao s) {
			return new CalculadoraPrecoCinemaOuShow(s);
		}
	}, SHOW {
		@Override
		public CalculadoraPreco getCalculadoraPreco(Sessao s) {
			return new CalculadoraPrecoCinemaOuShow(s);
		}
	}, TEATRO {
		@Override
		public CalculadoraPreco getCalculadoraPreco(Sessao s) {
			return new CalculadoraPrecoTeatro(s);
		}
	}, BALLET {
		@Override
		public CalculadoraPreco getCalculadoraPreco(Sessao s) {
			return new CalculadoraPrecoBallet(s);
		}
	}, ORQUESTRA {
		@Override
		public CalculadoraPreco getCalculadoraPreco(Sessao s) {
			return new CalculadoraPrecoOrquestra(s);
		}
	};
	
	public abstract CalculadoraPreco getCalculadoraPreco(Sessao s);
}
