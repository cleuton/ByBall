package com.obomprogramador.games.modelotela;

import com.obomprogramador.games.xmloader.Coordenada;

public class PosicaoTela implements Comparable<PosicaoTela>, Cloneable {
	private int id;
	private float altura;
	private float largura;
	private float topo;
	private float esquerda;
	private int hVobVertices;
	private float screenTop;
	private float screenLeft;
	private float screenBottom;
	private float screenRight;
	private Coordenada centro;
	private boolean centraVertical;
	private boolean centraHorizontal;
	private int repetirHorizontal;
	private int repetirGrupoVertical;
	private int container;
	private float fracaoTopo;
	private float fracaoEsquerda;
	private boolean centraGrupo;
	private float espaco;
	private String arquivoModelo;
	private String arquivoTela;
	private int exibirTela;
	private int novoNivel;
	
	
	public int getNovoNivel() {
		return novoNivel;
	}
	public void setNovoNivel(int novoNivel) {
		this.novoNivel = novoNivel;
	}
	public String getArquivoModelo() {
		return arquivoModelo;
	}
	public void setArquivoModelo(String arquivoModelo) {
		this.arquivoModelo = arquivoModelo;
	}
	public String getArquivoTela() {
		return arquivoTela;
	}
	public void setArquivoTela(String arquivoTela) {
		this.arquivoTela = arquivoTela;
	}
	public int getExibirTela() {
		return exibirTela;
	}
	public void setExibirTela(int exibirTela) {
		this.exibirTela = exibirTela;
	}
	public float getEspaco() {
		return espaco;
	}
	public void setEspaco(float espaco) {
		this.espaco = espaco;
	}
	public boolean isCentraGrupo() {
		return centraGrupo;
	}
	public void setCentraGrupo(boolean centraGrupo) {
		this.centraGrupo = centraGrupo;
	}
	public int getContainer() {
		return container;
	}
	public void setContainer(int container) {
		this.container = container;
	}
	public float getFracaoTopo() {
		return fracaoTopo;
	}
	public void setFracaoTopo(float fracaoTopo) {
		this.fracaoTopo = fracaoTopo;
	}
	public float getFracaoEsquerda() {
		return fracaoEsquerda;
	}
	public void setFracaoEsquerda(float fracaoEsquerda) {
		this.fracaoEsquerda = fracaoEsquerda;
	}
	public int getRepetirHorizontal() {
		return repetirHorizontal;
	}
	public void setRepetirHorizontal(int repetirHorizontal) {
		this.repetirHorizontal = repetirHorizontal;
	}
	public int getRepetirGrupoVertical() {
		return repetirGrupoVertical;
	}
	public void setRepetirGrupoVertical(int repetirGrupoVertical) {
		this.repetirGrupoVertical = repetirGrupoVertical;
	}
	public boolean isCentraVertical() {
		return centraVertical;
	}
	public void setCentraVertical(boolean centraVertical) {
		this.centraVertical = centraVertical;
	}
	public boolean isCentraHorizontal() {
		return centraHorizontal;
	}
	public void setCentraHorizontal(boolean centraHorizontal) {
		this.centraHorizontal = centraHorizontal;
	}
	public Coordenada getCentro() {
		return centro;
	}
	public void setCentro(Coordenada centro) {
		this.centro = centro;
	}

	private Textura textura;
	
	
	
	
	public float getScreenTop() {
		return screenTop;
	}
	public void setScreenTop(float screenTop) {
		this.screenTop = screenTop;
	}
	public float getScreenLeft() {
		return screenLeft;
	}
	public void setScreenLeft(float screenLeft) {
		this.screenLeft = screenLeft;
	}
	public float getScreenBottom() {
		return screenBottom;
	}
	public void setScreenBottom(float screenBottom) {
		this.screenBottom = screenBottom;
	}
	public float getScreenRight() {
		return screenRight;
	}
	public void setScreenRight(float screenRight) {
		this.screenRight = screenRight;
	}
	public int gethVobVertices() {
		return hVobVertices;
	}
	public void sethVobVertices(int hVobVertices) {
		this.hVobVertices = hVobVertices;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public float getTopo() {
		return topo;
	}
	public void setTopo(float topo) {
		this.topo = topo;
	}
	public float getEsquerda() {
		return esquerda;
	}
	public void setEsquerda(float esquerda) {
		this.esquerda = esquerda;
	}
	
	
	
	public Textura getTextura() {
		return textura;
	}
	public void setTextura(Textura textura) {
		this.textura = textura;
	}
	@Override
	public boolean equals(Object o) {
		return this.getId() == ((PosicaoTela) o).getId();
	}
	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public int compareTo(PosicaoTela another) {
		return this.getId() - another.getId();
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		PosicaoTela pos = new PosicaoTela();
		pos.setAltura(this.getAltura());
		pos.setCentraHorizontal(this.isCentraHorizontal());
		pos.setCentraVertical(this.isCentraVertical());
		pos.setCentro(this.getCentro());
		pos.setEsquerda(this.getEsquerda());
		pos.sethVobVertices(this.gethVobVertices());
		pos.setId(this.getId());
		pos.setLargura(this.getLargura());
		pos.setRepetirGrupoVertical(this.getRepetirGrupoVertical());
		pos.setRepetirHorizontal(this.getRepetirHorizontal());
		pos.setCentraGrupo(this.isCentraGrupo());
		pos.setScreenBottom(this.getScreenBottom());
		pos.setScreenLeft(this.getScreenLeft());
		pos.setScreenRight(this.getScreenRight());
		pos.setScreenTop(this.getScreenTop());
		if (this.getTextura() != null) {
			pos.setTextura(this.getTextura());
		}
		pos.setTopo(this.getTopo());
		pos.setContainer(this.getContainer());
		pos.setFracaoEsquerda(this.getFracaoEsquerda());
		pos.setFracaoTopo(this.getFracaoTopo());
		pos.setEspaco(this.getEspaco());
		pos.setArquivoModelo(this.getArquivoModelo());
		pos.setArquivoTela(this.getArquivoTela());
		pos.setExibirTela(this.getExibirTela());
		pos.setNovoNivel(this.getNovoNivel());
		return pos;
	}
	
	
	
}

