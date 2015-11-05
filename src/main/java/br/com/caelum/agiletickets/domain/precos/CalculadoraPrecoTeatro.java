package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public class CalculadoraPrecoTeatro implements CalculadoraPreco {
	Sessao sessao;
	BigDecimal preco;

	public CalculadoraPrecoTeatro(Sessao s){
		sessao = s;		
	}
	
	@Override
	public double obterPercentualIngressosRestantes() {
		return (sessao.getTotalIngressos() - sessao.getIngressosReservados()) / sessao.getTotalIngressos().doubleValue();
	}

	@Override
	public BigDecimal calcularPreco() {
		preco = sessao.getPreco();
		return preco;
	}

}
