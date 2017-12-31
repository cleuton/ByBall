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

import java.util.ArrayList;
import java.util.List;

public class Cena implements Comparable<Cena> {
	private String texturaFundo;
	private ConfigFrustum frustum;
	private ConfigBox2D box2d;
	private List<GameObject> objetos;
	private float fps;
	private int numero;
	private int vobTextura;
	private int handlerTextura;
	private int vobVertices;
	private int limitesegundos;
	private boolean parada;
	private int grupo;
	private float media;
	private int retornoNPC;
	
	
	
	
	public int getRetornoNPC() {
		return retornoNPC;
	}



	public void setRetornoNPC(int retornoNPC) {
		this.retornoNPC = retornoNPC;
	}



	public int getGrupo() {
		return grupo;
	}



	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}



	public float getMedia() {
		return media;
	}



	public void setMedia(float media) {
		this.media = media;
	}



	public boolean isParada() {
		return parada;
	}



	public void setParada(boolean parada) {
		this.parada = parada;
	}



	public int getLimitesegundos() {
		return limitesegundos;
	}



	public void setLimitesegundos(int limitesegundos) {
		this.limitesegundos = limitesegundos;
	}



	public Cena() {
		super();
		this.objetos = new ArrayList<GameObject>();
	}
	


	public Cena(int numero, String texturaFundo, ConfigFrustum frustum, ConfigBox2D box2d,
			List<GameObject> objetos, float fps) {
		super();
		this.texturaFundo = texturaFundo;
		this.frustum = frustum;
		this.box2d = box2d;
		this.objetos = objetos;
		this.fps = fps;
		this.numero = numero;
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



	public int getNumero() {
		return numero;
	}



	public void setNumero(int numero) {
		this.numero = numero;
	}



	public String getTexturaFundo() {
		return texturaFundo;
	}

	public void setTexturaFundo(String texturaFundo) {
		this.texturaFundo = texturaFundo;
	}

	public ConfigFrustum getFrustum() {
		return frustum;
	}

	public void setFrustum(ConfigFrustum frustum) {
		this.frustum = frustum;
	}

	public ConfigBox2D getBox2d() {
		return box2d;
	}

	public void setBox2d(ConfigBox2D box2d) {
		this.box2d = box2d;
	}

	public List<GameObject> getObjetos() {
		return objetos;
	}

	public void setObjetos(List<GameObject> objetos) {
		this.objetos = objetos;
	}

	public float getFps() {
		return fps;
	}

	public void setFps(float fps) {
		this.fps = fps;
	}



	@Override
	public boolean equals(Object o) {
		return this.getNumero() == ((Cena) o).getNumero();
	}



	@Override
	public int hashCode() {
		return this.getNumero();
	}

	@Override
	public int compareTo(Cena outra) {
		return this.getNumero() - outra.getNumero();
	}
	
	
}
