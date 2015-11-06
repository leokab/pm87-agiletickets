package br.com.caelum.agiletickets.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.agiletickets.models.TipoDeEspetaculo;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;

public class EspetaculosControllerTest {

	private @Mock Agenda agenda;
	private @Mock DiretorioDeEstabelecimentos estabelecimentos;
	private @Spy Validator validator = new MockValidator();
	private @Spy Result result = new MockResult();
	
	private EspetaculosController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new EspetaculosController(result, validator, agenda, estabelecimentos);
	}

/*	@Test
	public void criaSessaoDataInicioPosteriorDataFim(){
		Espetaculo espetaculo = new Espetaculo();
		LocalDate inicio = new LocalDate();
		LocalDate fim = inicio.minusDays(5);
		Periodicidade periodicidade = Periodicidade.DIARIA;
		LocalTime horario = new LocalTime();
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertEquals(0, sessoes.size());
	}*/
	
	@Test
	public void cria4SessoesPeriodoValidoPeriodicidadeDiaria(){
		Espetaculo espetaculo = new Espetaculo();
		LocalDate inicio = new LocalDate();
		LocalDate fim = inicio.plusDays(3);
		Periodicidade periodicidade = Periodicidade.DIARIA;
		LocalTime horario = new LocalTime();
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertEquals(4, sessoes.size());
	}
	
	@Test
	public void cria4SessoesPeriodoValidoPeriodicidadeSemanal(){
		Espetaculo espetaculo = new Espetaculo();
		LocalDate inicio = new LocalDate();
		LocalDate fim = inicio.plusDays(21);
		Periodicidade periodicidade = Periodicidade.SEMANAL;
		LocalTime horario = new LocalTime();
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);
		
		Assert.assertEquals(4, sessoes.size());
	}
	
	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemNome() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setDescricao("uma descricao");

		controller.adiciona(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");

		controller.adiciona(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test
	public void deveCadastrarEspetaculosComNomeEDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");
		espetaculo.setDescricao("uma descricao");

		controller.adiciona(espetaculo);

		verify(agenda).cadastra(espetaculo);
	}
	
	@Test
	public void deveRetornarNotFoundSeASessaoNaoExiste() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(null);

		controller.sessao(1234l);

		verify(result).notFound();
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarZeroIngressos() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(new Sessao());

		controller.reserva(1234l, 0);

		verifyZeroInteractions(result);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarMaisIngressosQueASessaoPermite() throws Exception {
		Sessao sessao = new Sessao();
		sessao.setTotalIngressos(3);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reserva(1234l, 5);

		verifyZeroInteractions(result);
	}

	@Test
	public void deveReservarSeASessaoTemIngressosSuficientes() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setTipo(TipoDeEspetaculo.TEATRO);

		Sessao sessao = new Sessao();
		sessao.setPreco(new BigDecimal("10.00"));
		sessao.setTotalIngressos(5);
		sessao.setEspetaculo(espetaculo);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reserva(1234l, 3);

		assertThat(sessao.getIngressosDisponiveis(), is(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDeveriaCriarSessoesDiariasQuandoDataInicioMaiorQueDataFim() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalDate amanha = hoje.plusDays(1);
	  	LocalTime agora = new LocalTime();
	  	Periodicidade diaria = Periodicidade.DIARIA;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(amanha, hoje, agora, diaria);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
	  	//Nao tem asserts pq deveria jogar exception...
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDeveriaCriarSessoesSemanaisQuandoDataInicioMaiorQueDataFim() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalDate amanha = hoje.plusDays(1);
	  	LocalTime agora = new LocalTime();
	  	Periodicidade semanal = Periodicidade.SEMANAL;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(amanha, hoje, agora, semanal);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
	  	//Nao tem asserts pq deveria jogar exception...
	}

	@Test
	public void deveriaCriarApenasUmaSessaoParaPeriodicidadeDiariaComDataInicioIgualDataFim() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalTime agora = new LocalTime();
	  	Periodicidade diaria = Periodicidade.DIARIA;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(hoje, hoje, agora, diaria);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
		Assert.assertEquals(1, sessoes.size());
	  
	  	//Nao basta apenas verificar o size da lista, precisa garantir que criou as sessoes corretamente:
	  	Sessao unica = sessoes.get(0);
	  	Assert.assertEquals(show, unica.getEspetaculo());
	    Assert.assertEquals(hoje.toDateTime(agora), unica.getInicio());
	}

	@Test
	public void deveriaCriarApenasUmaSessaoParaPeriodicidadeSemanalComDataInicioIgualDataFim() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalTime agora = new LocalTime();
	  	Periodicidade semanal = Periodicidade.SEMANAL;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(hoje, hoje, agora, semanal);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
		Assert.assertEquals(1, sessoes.size());
	  
	  	//Nao basta apenas verificar o size da lista, precisa garantir que criou as sessoes corretamente:
	  	Sessao unica = sessoes.get(0);
	  	Assert.assertEquals(show, unica.getEspetaculo());
	    Assert.assertEquals(hoje.toDateTime(agora), unica.getInicio());
	}

	@Test
	public void deveriaCriarCincoSessoesParaPeriodicidadeDiariaComIntervaloDeCincoDias() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalDate daquiQuatroDias = hoje.plusDays(4);
	  	LocalTime agora = new LocalTime();
	  	Periodicidade diaria = Periodicidade.DIARIA;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(hoje, daquiQuatroDias, agora, diaria);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
		Assert.assertEquals(5, sessoes.size());
	  
	  	//Nao basta apenas verificar o size da lista, precisa garantir que criou as sessoes corretamente:
		for(int i = 0; i < sessoes.size(); i++) {
			Assert.assertEquals(show, sessoes.get(i).getEspetaculo());
			Assert.assertEquals(hoje.plusDays(i).toDateTime(agora), sessoes.get(i).getInicio());
	    }
	}

	@Test
	public void deveriaCriarCincoSessoesParaPeriodicidadeSemanalComIntervaloDeCincoSemanas() {
		//DADAS ESTAS ENTRADAS:
		LocalDate hoje = new LocalDate();
	  	LocalDate daquiQuatroSemanas = hoje.plusWeeks(4);
	  	LocalTime agora = new LocalTime();
	  	Periodicidade semanal = Periodicidade.SEMANAL;
	  
	  	//QUANDO EU DISPARAR O PROCESSAMENTO:
	  	Espetaculo show = new Espetaculo();
	  	List<Sessao> sessoes = show.criaSessoes(hoje, daquiQuatroSemanas, agora, semanal);
	  
	  	//ESTAS SAO AS SAIDAS ESPERADAS:
		Assert.assertEquals(5, sessoes.size());
	  
	  	//Nao basta apenas verificar o size da lista, precisa garantir que criou as sessoes corretamente:
		for(int i = 0; i < sessoes.size(); i++) {
			Assert.assertEquals(show, sessoes.get(i).getEspetaculo());
			Assert.assertEquals(hoje.plusWeeks(i).toDateTime(agora), sessoes.get(i).getInicio());
	    }
	}
}
