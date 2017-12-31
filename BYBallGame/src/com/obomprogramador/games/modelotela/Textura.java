package com.obomprogramador.games.modelotela;

public class Textura implements Comparable<Textura>, Cloneable {

	
	public static enum TIPO_ACAO {
		TIPO_ACAO_MANUAL,
		TIPO_ACAO_CARREGAR_NIVEL,
		TIPO_ACAO_EXIBIR_TELA
	};
	
	private int id;
	private int ascii;
	private String imagem;
	private boolean visivel;
	private boolean clicavel;
	private PosicaoTela posicaoAtual;
	private int posicaoFixa;
	private int hTextura;
	private int tipoAcao;
	private int novoNivel;
	private String arquivoModelo;
	private String arquivoTela;
	private int exibirTela;
	private float angulo;
		
	public float getAngulo() {
		return angulo;
	}
	public void setAngulo(float angulo) {
		this.angulo = angulo;
	}
	public int getTipoAcao() {
		return tipoAcao;
	}
	public void setTipoAcao(int tipoAcao) {
		this.tipoAcao = tipoAcao;
	}
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
	public int gethTextura() {
		return hTextura;
	}
	public void sethTextura(int hTextura) {
		this.hTextura = hTextura;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAscii() {
		return ascii;
	}
	public void setAscii(int ascii) {
		this.ascii = ascii;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public boolean isVisivel() {
		return visivel;
	}
	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}
	public boolean isClicavel() {
		return clicavel;
	}
	public void setClicavel(boolean clicavel) {
		this.clicavel = clicavel;
	}

	public PosicaoTela getPosicaoAtual() {
		return posicaoAtual;
	}
	public void setPosicaoAtual(PosicaoTela posicaoAtual) {
		this.posicaoAtual = posicaoAtual;
	}

	public int getPosicaoFixa() {
		return posicaoFixa;
	}
	public void setPosicaoFixa(int posicaoFixa) {
		this.posicaoFixa = posicaoFixa;
	}
	@Override
	public int compareTo(Textura another) {
		return this.getId() - another.getId();
	}
	@Override
	public boolean equals(Object o) {
		return this.getId() == ((Textura) o).getId();
	}
	@Override
	public int hashCode() {
		return this.getId();
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		Textura t = new Textura();
		t.setAngulo(this.getAngulo());
		t.setArquivoModelo(this.getArquivoModelo());
		t.setArquivoTela(this.getArquivoTela());
		t.setAscii(this.getAscii());
		t.setClicavel(this.isClicavel());
		t.setExibirTela(this.getExibirTela());
		t.sethTextura(this.gethTextura());
		t.setId(this.getId());
		t.setImagem(this.getImagem());
		t.setNovoNivel(this.getNovoNivel());
		t.setPosicaoAtual(this.getPosicaoAtual());
		t.setPosicaoFixa(this.getPosicaoFixa());
		t.setTipoAcao(this.getTipoAcao());
		t.setVisivel(this.isVisivel());
		
		return t;
	}
	
	
}
