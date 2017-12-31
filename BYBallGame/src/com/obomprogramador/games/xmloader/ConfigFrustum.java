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

public class ConfigFrustum {
	private Coordenada camera;
	private Coordenada cima;
	private Coordenada direcao;
	private float perto;
	private float longe;
	
	public ConfigFrustum() {
		super();
	}
	
	public ConfigFrustum(Coordenada camera, Coordenada cima,
			Coordenada direcao, float perto, float longe) {
		super();
		this.camera = camera;
		this.cima = cima;
		this.direcao = direcao;
		this.perto = perto;
		this.longe = longe;
	}


	public Coordenada getCamera() {
		return camera;
	}

	public void setCamera(Coordenada camera) {
		this.camera = camera;
	}

	public Coordenada getCima() {
		return cima;
	}

	public void setCima(Coordenada cima) {
		this.cima = cima;
	}

	public Coordenada getDirecao() {
		return direcao;
	}

	public void setDirecao(Coordenada direcao) {
		this.direcao = direcao;
	}

	public float getPerto() {
		return perto;
	}

	public void setPerto(float perto) {
		this.perto = perto;
	}

	public float getLonge() {
		return longe;
	}

	public void setLonge(float longe) {
		this.longe = longe;
	}
	
	
	
	
}
