package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;

public class ClienteTest {

	private HttpServer server;
	private WebTarget target;
	private Client client;
	
	/*@Before
	public void startaServidor() {
	    ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
	    URI uri = URI.create("http://localhost:8080/");
	    this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
	}*/
	@Before
	public void before() {
		server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8080");
	}
	
	@After
    public void mataServidor() {
        server.stop();
    }
	
	@Test
	public void testaAConexaoComOServidor() {
//		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://www.mocky.io");
		String conteudo = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
		Assert.assertTrue(conteudo.contains("<rua>Rua Vergueiro 3185"));
	}
	
	@Test
	public void testaQueAConexaoComOServidorFunciona() {
//	    Client client = ClientBuilder.newClient();
	    WebTarget target = client.target("http://www.mocky.io");
	    String conteudo = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
	    System.out.println(conteudo);
	    Assert.assertTrue(conteudo.contains("Rua Vergueiro 3185"));
	}
	
	@Test
    public void testaQueAConexaoComOServidorFuncionaNoPathDeProjetos() {
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target("http://localhost:8080");
        String conteudo = target.path("/projetos").request().get(String.class);
        Assert.assertTrue(conteudo.contains("<nome>Minha loja"));
    }
	
	@Test
    public void testaQueBuscarUmCarrinhoTrasUmCarrinho() {
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target("http://localhost:8080");
        Carrinho conteudo = target.path("/carrinhos/1").request().get(Carrinho.class);
//        Carrinho fromXML = (Carrinho) new XStream().fromXML(conteudo);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar",conteudo.getRua());
    }
	
	@Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
       /* ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
        URI uri = URI.create("http://localhost:8080/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);*/

//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target("http://localhost:8080");
        String conteudo = target.path("/carrinhos/1").request().get(String.class);
        Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }
	
	@Test
	public void testaBuscarUmProjeto() {
//		Client client = ClientBuilder.newClient();
//		WebTarget target = client.target("http://localhost:8080");
		Projeto conteudo = target.path("/projetos/1").request().get(Projeto.class);
//		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		Assert.assertEquals("Minha loja", conteudo.getNome());
	}
	
	@Test
	public void testaPost() {
//		Client client = ClientBuilder.newClient();
//		WebTarget target = client.target("http://localhost:8080");
		Carrinho carrinho = new Carrinho();
		carrinho = carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
		carrinho.setRua("Rua Vergueiro");
		carrinho.setCidade("São Paulo");
//		String xml = carrinho.toXML();
		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		Carrinho conteudo = client.target(location).request().get(Carrinho.class);
		Assert.assertEquals("Tablet", conteudo.getProdutos().get(0).getNome());
	}
}