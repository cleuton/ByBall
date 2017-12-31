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

public class GameModel {
	
	private List<Cena> cenas = new ArrayList<Cena>();

	public GameModel() {
		super();
	}
	
	public GameModel(List<Cena> cenas) {
		super();
		this.cenas = cenas;
	}

	public List<Cena> getCenas() {
		return cenas;
	}

	public void setCenas(List<Cena> cenas) {
		this.cenas = cenas;
	}
	
	
}
