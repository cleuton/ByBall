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

public class ConfigBox2D {
	
	private float   gravidade = -10.0f;
	private boolean sleep = true;
	private int	    velocityInterations = 6;
	private int     positionInterations = 2;
	private float   proporcaoMetroTela = 5.0f;
	
	public ConfigBox2D() {
		super();
	}

	public ConfigBox2D(float gravidade, boolean sleep, int velocityInterations,
			int positionInterations, float proporcaoMetroTela) {
		super();
		this.gravidade = gravidade;
		this.sleep = sleep;
		this.velocityInterations = velocityInterations;
		this.positionInterations = positionInterations;
		this.proporcaoMetroTela = proporcaoMetroTela;
	}

	public float getGravidade() {
		return gravidade;
	}

	public void setGravidade(float gravidade) {
		this.gravidade = gravidade;
	}

	public boolean isSleep() {
		return sleep;
	}

	public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}

	public int getVelocityInterations() {
		return velocityInterations;
	}

	public void setVelocityInterations(int velocityInterations) {
		this.velocityInterations = velocityInterations;
	}

	public int getPositionInterations() {
		return positionInterations;
	}

	public void setPositionInterations(int positionInterations) {
		this.positionInterations = positionInterations;
	}

	public float getProporcaoMetroTela() {
		return proporcaoMetroTela;
	}

	public void setProporcaoMetroTela(float proporcaoMetroTela) {
		this.proporcaoMetroTela = proporcaoMetroTela;
	}
	
	
}
