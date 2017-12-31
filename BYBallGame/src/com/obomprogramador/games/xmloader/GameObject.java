/*
 * Parte do livro "Manual do Indie Gamer", de Cleuton Sampaio, 
 * doravante denominado "O Autor".
 * Este codigo é distribuído sob a licença Apache 2.0, cujo texto pode ser lido em:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Se você for utilizar este código em seus produtos, por favor mencione a autoria
 * original, citando o livro e o nome do Autor.
 * 
 * O código  é disponibilizado "AS IS", e não há nenhum compromisso de suporte ou
 * garantia por parte do Autor.
 * 
 */
package com.obomprogramador.games.xmloader;

import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class GameObject implements Comparable<GameObject> {
	
	public static enum TIPO_GO {
		TIPO_GO_NENHUM,
		TIPO_GO_ESTATICO,
		TIPO_GO_DINAMICO,
		TIPO_GO_CINEMATICO
	};
	
	public static enum FORMA_GO {
		FORMA_GO_NENHUM,
		FORMA_GO_CIRCULO,
		FORMA_GO_RETANGULO
	};
	
	public static enum ALINHAMENTO_GO {
		ALINHAMENTO_NENHUM,
		ALINHAMENTO_CHAO,
		ALINHAMENTO_ESQUERDA,
		ALINHAMENTO_DIREITA,
		ALINHAMENTO_TETO,
		ALINHAMENTO_BASE_CHAO,
		ALINHAMENTO_SOBRE_OUTRO
	};
	
	public static final int GAMEOBJECT_PLAYER = 1;
	public static final int GAMEOBJECT_NPC = 2;
	public static final int GAMEOBJECT_CENARIO = 3;
	
	private int id;
	private TIPO_GO tipo;
	private FORMA_GO forma;
	private ALINHAMENTO_GO alinhamento;
	private Coordenada centro;
	private float altura;
	private boolean esticarAltura;
	private float largura;
	private boolean esticarLargura;
	private String arquivoTextura;
	private float densidade;
	private float atrito;
	private float coefRetribuicao;
	private int vobTextura;
	private int handlerTextura;
	private int vobVertices;
	private Body b2dBody;
	private Fixture fixture;
	private int ordem;
	private int sobre;
	private boolean visivel;
	private float yOriginal;
	private float xOriginal;
	private float velocidade;
	private boolean proibido;
	private List<GameObject> estaoSobre;
	
	
	
	public List<GameObject> getEstaoSobre() {
		return estaoSobre;
	}

	public void setEstaoSobre(List<GameObject> estaoSobre) {
		this.estaoSobre = estaoSobre;
	}

	public boolean isProibido() {
		return proibido;
	}

	public void setProibido(boolean proibido) {
		this.proibido = proibido;
	}

	public float getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(float velocidade) {
		this.velocidade = velocidade;
	}

	public float getxOriginal() {
		return xOriginal;
	}

	public void setxOriginal(float xOriginal) {
		this.xOriginal = xOriginal;
	}

	public float getyOriginal() {
		return yOriginal;
	}

	public void setyOriginal(float yOriginal) {
		this.yOriginal = yOriginal;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public int getSobre() {
		return sobre;
	}

	public void setSobre(int sobre) {
		this.sobre = sobre;
	}

	// 1 = PLAYER, 2 = NPC, 3 = CENÁRIO
	private int gameObject;
	
	
	
	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public int getGameObject() {
		return gameObject;
	}

	public void setGameObject(int gameObject) {
		this.gameObject = gameObject;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
	}

	public boolean isEsticarAltura() {
		return esticarAltura;
	}

	public void setEsticarAltura(boolean esticarAltura) {
		this.esticarAltura = esticarAltura;
	}

	public boolean isEsticarLargura() {
		return esticarLargura;
	}

	public void setEsticarLargura(boolean esticarLargura) {
		this.esticarLargura = esticarLargura;
	}

	public Body getB2dBody() {
		return b2dBody;
	}

	public void setB2dBody(Body b2dBody) {
		this.b2dBody = b2dBody;
	}

	public int getVobTextura() {
		return vobTextura;
	}

	public void setVobTextura(int vobTextura) {
		this.vobTextura = vobTextura;
	}

	public int getHandlerTextura() {
		return handlerTextura;
	}

	public void setHandlerTextura(int handlerTextura) {
		this.handlerTextura = handlerTextura;
	}

	public int getVobVertices() {
		return vobVertices;
	}

	public void setVobVertices(int vobVertices) {
		this.vobVertices = vobVertices;
	}

	public GameObject() {
		super();
	}
	
	public GameObject(int id, TIPO_GO tipo, FORMA_GO forma, Coordenada centro,
			float altura, float largura, String arquivoTextura,
			float densidade, float atrito, float coefRetribuicao) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.forma = forma;
		this.centro = centro;
		this.altura = altura;
		this.largura = largura;
		this.arquivoTextura = arquivoTextura;
		this.densidade = densidade;
		this.atrito = atrito;
		this.coefRetribuicao = coefRetribuicao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	
	public ALINHAMENTO_GO getAlinhamento() {
		return alinhamento;
	}

	public void setAlinhamento(ALINHAMENTO_GO alinhamento) {
		this.alinhamento = alinhamento;
	}

	public TIPO_GO getTipo() {
		return tipo;
	}

	public void setTipo(TIPO_GO tipo) {
		this.tipo = tipo;
	}

	public FORMA_GO getForma() {
		return forma;
	}

	public void setForma(FORMA_GO forma) {
		this.forma = forma;
	}

	public Coordenada getCentro() {
		return centro;
	}

	public void setCentro(Coordenada centro) {
		this.centro = centro;
	}

	public float getAltura() {
		return altura;
	}

	public void setAltura(float altura) {
		this.altura = altura;
	}

	public float getLargura() {
		return largura;
	}

	public void setLargura(float largura) {
		this.largura = largura;
	}

	public String getArquivoTextura() {
		return arquivoTextura;
	}

	public void setArquivoTextura(String arquivoTextura) {
		this.arquivoTextura = arquivoTextura;
	}

	public float getDensidade() {
		return densidade;
	}

	public void setDensidade(float densidade) {
		this.densidade = densidade;
	}

	public float getAtrito() {
		return atrito;
	}

	public void setAtrito(float atrito) {
		this.atrito = atrito;
	}

	public float getCoefRetribuicao() {
		return coefRetribuicao;
	}

	public void setCoefRetribuicao(float coefRetribuicao) {
		this.coefRetribuicao = coefRetribuicao;
	}

	@Override
	public boolean equals(Object o) {
		return this.getId() == ((GameObject) o).getId();
	}

	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public int compareTo(GameObject outro) {
		return this.getId() - outro.getId();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n[id: " + this.getId());
		sb.append("\r\nVisivel: " + this.isVisivel());
		sb.append("\r\nProibido: " + this.isProibido());
		if (this.getB2dBody() != null) {
			sb.append("\r\nB2X: " + this.getB2dBody().getPosition().x);
			sb.append("\r\nB2Y: " + this.getB2dBody().getPosition().y);
			sb.append("\r\nVLinX: " + this.getB2dBody().getLinearVelocity().x);
			sb.append("\r\nVLinY: " + this.getB2dBody().getLinearVelocity().y);
			sb.append("\r\nVAng: " + this.getB2dBody().getAngularVelocity());
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	
	
}
