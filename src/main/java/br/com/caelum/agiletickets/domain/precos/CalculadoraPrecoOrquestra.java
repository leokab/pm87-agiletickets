package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;

public class CalculadoraPrecoOrquestra implements CalculadoraPreco {
	Sessao sessao;
	BigDecimal preco;

	public CalculadoraPrecoOrquestra(Sessao s){
		sessao = s;		
	}
	
	@Override
	public double obterPercentualIngressosRestantes() {
		return (sessao.getTotalIngressos() - sessao.getIngressosReservados()) / sessao.getTotalIngressos().doubleValue();
	}

	@Override
	public BigDecimal calcularPreco() {
		
		//quando estiver acabando os ingressos... 
		if(obterPercentualIngressosRestantes() <= 0.50) { 
			preco = sessao.getPreco().add(sessao.getPreco().multiply(BigDecimal.valueOf(0.20)));
		} else {
			preco = sessao.getPreco();
		}

		if(sessao.getDuracaoEmMinutos() > 60){
			preco = preco.add(sessao.getPreco().multiply(BigDecimal.valueOf(0.10)));
		}

		return preco;
	}

}
