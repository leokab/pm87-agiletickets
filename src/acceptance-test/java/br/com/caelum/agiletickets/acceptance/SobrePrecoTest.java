package br.com.caelum.agiletickets.acceptance;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.caelum.agiletickets.acceptance.page.EstabelecimentosPage;
import br.com.caelum.agiletickets.acceptance.page.SessaoPage;

public class SobrePrecoTest {

	public static String BASE_URL = "http://localhost:8080";
	private static WebDriver browser;
	private SessaoPage sessoes;

	@BeforeClass
	public static void abreBrowser() {
		browser = new FirefoxDriver();
	}

	@Before
	public void setUp() throws Exception {
		sessoes = new SessaoPage(browser);
	}

	@AfterClass
	public static void teardown() {
		browser.close();
	}

	@Test
	public void sobrePrecoNosUltimosIngressos() throws Exception {
		sessoes.abreListagem();

		sessoes.adicioneEstabelecimento("Caelum", "R. Vergueiro, 3185");

		sessoes.ultimaLinhaDeveConter("Caelum", "R. Vergueiro, 3185");
	}

	@Test
	public void aoAdicionarUmEstabelecimentoSemNomeDeveMostrarErro() throws Exception {
		sessoes.abreListagem();

		sessoes.adicioneEstabelecimento("", "R. Vergueiro, 3185");

		sessoes.deveMostrarErro("O nome não pode ser vazio");
	}

	@Test
	public void aoAdicionarUmEstabelecimentoSemEnderecoDeveMostrarErro() throws Exception {
		sessoes.abreListagem();

		sessoes.adicioneEstabelecimento("Caelum", "");

		sessoes.deveMostrarErro("O endereco não pode ser vazio");
	}

	@Test
	public void mostraQueHaEstacionamentoQuandoCadastramosQueSim() throws Exception {
		sessoes.abreListagem();

		sessoes.adicioneEstabelecimentoComEstacionamento(true);

		sessoes.ultimaLinhaDeveTerEstacionamento(true);
	}

	@Test
	public void mostraQueNaoHaEstacionamentoQuandoCadastramosQueNao() throws Exception {
		sessoes.abreListagem();

		sessoes.adicioneEstabelecimentoComEstacionamento(false);

		sessoes.ultimaLinhaDeveTerEstacionamento(false);
	}
	
}
