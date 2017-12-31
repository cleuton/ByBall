package com.obomprogramador.games.modelotela;

import java.util.ArrayList;
import java.util.List;

public class ModeloTela {
	private List<PosicaoTela> posicoes;
	private List<Textura> texturas;

	public ModeloTela() {
		super();
		posicoes = new ArrayList<PosicaoTela>();
		texturas = new ArrayList<Textura>();
	}

	public List<PosicaoTela> getPosicoes() {
		return posicoes;
	}

	public void setPosicoes(List<PosicaoTela> posicoes) {
		this.posicoes = posicoes;
	}

	public List<Textura> getTexturas() {
		return texturas;
	}

	public void setTexturas(List<Textura> texturas) {
		this.texturas = texturas;
	}
	
	
}
