package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.agiletickets.models.TipoDeEspetaculo;

public class CalculadoraDePrecos {

	public static BigDecimal calcula(Sessao sessao, Integer quantidade) {
		BigDecimal preco;
		
		preco = sessao.getEspetaculo().getTipo().getCalculadoraPreco(sessao).calcularPreco();
		
		return preco.multiply(BigDecimal.valueOf(quantidade));
	}

}