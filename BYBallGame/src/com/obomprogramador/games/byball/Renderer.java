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
package com.obomprogramador.games.byball;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;

import com.obomprogramador.games.byball.recs.Records;
import com.obomprogramador.games.modelotela.PosicaoTela;
import com.obomprogramador.games.modelotela.Textura;
import com.obomprogramador.games.openglavancado.OpenGLAvancadoRenderer;
import com.obomprogramador.games.xmloader.Coordenada;
import com.obomprogramador.games.xmloader.GameObject;

public class Renderer extends OpenGLAvancadoRenderer {

	public Activity activity;
	protected float xToque;
	protected float yToque;
	protected float xFinal;
	protected float yFinal;
	protected boolean toqueNaBola;
	protected Textura aviso;
	protected float tempoAviso;
	protected boolean chutou;
	protected float xOrigemBola;
	protected boolean resetarBola;
	public float segundosDeJogo;
	protected int quantidadeNPC;
	protected int quantidadeNPCcalculados;

	protected final int MAXNPC = 10;
	protected boolean acabou;
	protected Calendar inicio;
	protected long diferencaTempo;
	protected int [] qtdOrdem;
	protected boolean trocarNivel;
	protected List<Textura> texturasAviso;
	protected boolean mostrandoAviso;
	
	protected int TEXTURA_TEMPO = 15;
	protected int TEXTURA_PREMIO = 16;
	protected int TEXTURA_PROIBIDO = 120;
	protected int TEXTURA_BTN_REPLAY_LEVEL = 65;
	protected int TEXTURA_BTN_NEXT_LEVEL = 68;
	protected int TEXTURA_BTN_COMPARTILHAR = 70;
	protected int TEXTURA_CARREGANDO = 80; 
	protected int TEXTURA_FUNDO = 9;
	protected int TEXTURA_BTN_AJUDA = 8;
	protected int POSICAO_RELOGIO = 4;
	protected int TEXTURA_OURO = 1;
	protected int TEXTURA_PRATA = 2;
	protected int TEXTURA_BRONZE = 3;
	protected int TEXTURA_NORMAL = 4;
	protected int TEXTURA_CADEADO = 5;
	protected int TEXTURA_PROXIMO = 87;
	protected int TEXTURA_ANTERIOR = 89;
	protected int POSICAO_LINHA1 = 10;
	protected int POSICAO_LINHA2 = 15;
	protected int POSICAO_BTN_PROXIMO = 20;
	protected int POSICAO_BTN_ANTERIOR = 22;
	protected int POSICAO_NUM_NIVEL1 = 30;
	protected int POSICAO_NUM_NIVEL2 = 40;
	protected int POSICAO_CADEADO1 = 90;
	protected int POSICAO_TEMPO1 = 110;
	protected int POSICAO_REPLAY_SOZINHO = 30;
	protected int POSICAO_REPLAY_NEXT = 31;
	protected int POSICAO_COMPARTILHAR = 35;
	protected int POSICAO_NEXT = 32;
	protected PosicaoTela posProximo;
	protected PosicaoTela posAnterior;
	protected Textura textProximo;
	protected Textura textAnterior;
	protected Textura cadeado;
	protected int TEXTURA_SAIR_NIVEL = 13;
	protected int TEXTURA_SAIR_NIVEL_DESTAQUE = 20;
	protected int qtdOrdemErrada;
	protected boolean replayFlag;
	protected boolean flagBolaParada = false;
	protected int contadorFramesBolaParada;
	protected long duracaoNivel = 0;
	
		
	public Renderer(Context context, String nomeGame, String nomeTela) throws Exception {
		super(context, nomeGame, nomeTela);
		
	}

	public void setCena(int cena) {
		this.cena = cena;
	}
	
	public int getCena(int cena) {
		return this.cena;
	}
	
	public void toque(MotionEvent eventoToque) {
		super.toque();
		if (this.resetingGame) {
			return;
		}

		if (simulando) {
			if (eventoToque.getAction() == MotionEvent.ACTION_MOVE && (!cenaCorrente.isParada())) {
				if (toqueNaBola) {
					xFinal = eventoToque.getX() - (larguraViewPort / 2);
					yFinal = (alturaViewPort / 2) - eventoToque.getY();
					return;
				}
			}
			else if (eventoToque.getAction() == MotionEvent.ACTION_UP && toqueNaBola && (!cenaCorrente.isParada())) {
					xFinal = eventoToque.getX() - (larguraViewPort / 2);
					yFinal = (alturaViewPort / 2) - eventoToque.getY();

					aplicarForca();
			}
			else if (eventoToque.getAction() == MotionEvent.ACTION_DOWN) {
					toqueNaBola = false;

						if (!clicouEmTextura(eventoToque)) {
							// Não vale chutar duas vezes
							if ((!chutou)) {
								if (clicouNaBola(eventoToque)) {
									toqueNaBola = true;
								}
							}
							else {
								// Pode ser que o contact listener não tenha identificado
								if (bola.getB2dBody().getPosition().x < 0 && bola.getB2dBody().getPosition().y <= bola.getAltura()) {
									if (clicouNaBola(eventoToque)) {
										toqueNaBola = true;
									}
								}
							}
						}

			}
		}
	}

	private void aplicarForca() {

		// Comanda a aplicação de forças

		GameObject go = new GameObject();
		go.setId(1);
		int inx = cenaCorrente.getObjetos().indexOf(go);
		go = cenaCorrente.getObjetos().get(inx);
		Body bolaBody = go.getB2dBody();
		
		this.forca = 
				new Vec2((xFinal - bola.getCentro().getX() * proporcaoMetroTela) * bolaBody.getMass(), 
						(yFinal - bola.getCentro().getY() * proporcaoMetroTela) * bolaBody.getMass());
		
		//Vec2 posicao = bolaBody.getWorldCenter();
		/*
		bolaBody.setAwake(true);
		bolaBody.applyForce(forca, posicao);
		*/
		this.aplicouForca = true;
		if (yFinal > bola.getB2dBody().getPosition().y * proporcaoMetroTela) {

			/*
			 * Como eu só deixo chutar novamente se a bola cair no chão, eu preciso saber
			 * se a bola vai sair do chão ou não. Se não sair, eu não considero o chute.
			 */
			this.chutou = true;
		}

	}

	private boolean clicouNaBola(MotionEvent eventoToque) {
		boolean retorno = false;
		if (cenaCorrente.isParada()) {
			return false;
		}
		float toqueX = eventoToque.getX() - (larguraViewPort / 2);
		float toqueY = (alturaViewPort / 2) - eventoToque.getY();
		float screenLeft = (bola.getB2dBody().getPosition().x * proporcaoMetroTela) 
				- ((bola.getLargura()/2) * proporcaoMetroTela);
		float screenTop = (bola.getB2dBody().getPosition().y * proporcaoMetroTela) 
				+ ((bola.getAltura()/2) * proporcaoMetroTela);
		float screenBottom = (bola.getB2dBody().getPosition().y * proporcaoMetroTela) 
				- ((bola.getAltura()/2) * proporcaoMetroTela);
		float screenRight = (bola.getB2dBody().getPosition().x * proporcaoMetroTela) 
				+ ((bola.getLargura()/2) * proporcaoMetroTela);
		
	    if ((toqueX >= screenLeft && toqueX <= screenRight) &&
	            (toqueY >= screenBottom && toqueY <= screenTop)) {
	            retorno = true;
	            
	    }
		
		return retorno;
	}

	private boolean clicouEmTextura(MotionEvent eventoToque) {
		boolean resultado = false;
		Coordenada toque = new Coordenada (
						eventoToque.getX(),
						eventoToque.getY(),
						0.0f); 

		if (this.cenaCorrente.isParada()) {
			for (int x = 10; x < 20; x++) {
				PosicaoTela pt = this.getPosicao(x);
				Textura t = new Textura();
				t.setPosicaoAtual(pt);
				t.setArquivoModelo(pt.getArquivoModelo());
				t.setArquivoTela(pt.getArquivoTela());
				t.setNovoNivel(pt.getNovoNivel());
				if (toqueDentro(t,toque)) {
					processarAcao(t);
					this.pausa = false;
					resultado = true;
					return resultado;
				}
				t = null;
			}
		}
		
		for (Textura t : modeloTela.getTexturas()) {
			if (t.isClicavel() && t.isVisivel()) {
				if (toqueDentro(t,toque)) {
					this.pausa = false;
					resultado = true;
					
					if (t.getTipoAcao() == Textura.TIPO_ACAO.TIPO_ACAO_CARREGAR_NIVEL.ordinal()) {
						processarAcao(t);
					}
					else if (t.getTipoAcao() == Textura.TIPO_ACAO.TIPO_ACAO_EXIBIR_TELA.ordinal()) {
							exibirTela(t);
					}
					else if (t.getTipoAcao() == Textura.TIPO_ACAO.TIPO_ACAO_MANUAL.ordinal()) {
							if (t.getId() == this.TEXTURA_BTN_AJUDA) {
								exibirAjuda();
							} 
							else
								if (t.getId() == this.TEXTURA_ANTERIOR) {
									this.ultimoNivel -= 10;
									this.configurarPropriedadesNivel();
								}
								else
									if (t.getId() == this.TEXTURA_PROXIMO) {
										this.ultimoNivel += 10;
										this.configurarPropriedadesNivel();
									}
									else if (t.getId() == this.TEXTURA_BTN_COMPARTILHAR) {
											// Compartilhar
											this.compartilhar();
									}
					}
					break;
				}
			}
		}
		

		
		return resultado;
	}
	
	private void compartilhar() {
		Resources res = context.getResources();
		String assunto = res.getString(R.string.sharesubject);
 		String texto = String.format(res.getString(R.string.sharetext), cenaCorrente.getNumero(), (int) this.duracaoNivel);
		((GameActivity) this.activity).compartilhar(assunto, texto);
										
		
	}
	
	private void exibirAjuda() {
		Intent i = new Intent(context, AjudaActivity.class);
		this.activity.startActivity(i);
	}

	private void processarAcao(Textura t) {
		this.segundosIniciais = 0; // Reseta os segundos iniciais
		if (this.mostrandoAviso) {
			this.exibirTela(this.getTextura(this.TEXTURA_FUNDO));
			return;
		}
		if (t.getId() == this.TEXTURA_BTN_REPLAY_LEVEL) {
			this.replayFlag = true;
			return;
		}
		if (t.getId() == this.TEXTURA_SAIR_NIVEL) {
			Textura tDestaque = this.getTextura(this.TEXTURA_SAIR_NIVEL_DESTAQUE);
			tDestaque.setPosicaoAtual(t.getPosicaoAtual());
			tDestaque.setPosicaoFixa(t.getPosicaoFixa());
			t.setPosicaoAtual(null);
			t.setPosicaoFixa(0);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		
		long [] tempos = Records.getNiveis(context);
		if (t.getNovoNivel() != 0 && (!this.statusNivel[t.getNovoNivel() - 1])) {
			return;
		}
		this.resetingGame = true;
		if (cenaCorrente.getNumero() == 0) {
			if (this.relogio == null) {
				relogio = (Textura) this.getTextura(this.TEXTURA_CARREGANDO);
				relogioCenter = (PosicaoTela) this.getPosicao(this.POSICAO_RELOGIO);
			}
			
			
			relogio.setVisivel(true);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		this.cena = t.getNovoNivel();
		this.nomeGame = t.getArquivoModelo();
		this.nomeTela = t.getArquivoTela();
		this.trocarNivel = true;
	}
	
	protected void replayLevel() {
		// Temos que resetar apenas os objetos e o tempo
		
		this.chutou = false;
		Textura txtReplay = this.getTextura(this.TEXTURA_BTN_REPLAY_LEVEL);
		Textura txtProximo = this.getTextura(this.TEXTURA_BTN_NEXT_LEVEL);
		txtReplay.setVisivel(false);
		txtReplay.setPosicaoAtual(null);
		txtProximo.setVisivel(false);
		txtProximo.setPosicaoAtual(null);
		Textura txtShare = this.getTextura(this.TEXTURA_BTN_COMPARTILHAR);
		txtShare.setVisivel(false);	
		txtShare.setPosicaoAtual(null);
			
		
		this.quantidadeNPC = 0;
		this.world.setContactListener(null);
		this.texturasAviso = new ArrayList<Textura>();
		xOrigemBola = bola.getCentro().getX();
		this.inicio = Calendar.getInstance();
		this.segundosDeJogo = 0;
		this.reinicio = true;
	
		for (int x=0; x<this.qtdOrdem.length; x++) {
			this.qtdOrdem[x] = 0;
		}
		
		for (GameObject go : cenaCorrente.getObjetos()) {
			Vec2 v = new Vec2(0.0f,0.0f);
			if (go.getGameObject() == GameObject.GAMEOBJECT_NPC) {
				go.setVisivel(false);
				go.getCentro().setX(go.getxOriginal());
				go.getCentro().setY(go.getyOriginal());
				if (go.getB2dBody() == null) {
					this.createBody(go);
					this.ajustaUmObjeto(go);
				}
				go.setVisivel(true);
				this.reajustaUmObjeto(go);
				go.getB2dBody().setLinearVelocity(v);
				go.getB2dBody().setAngularVelocity(0.0f);
				go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
						0.0f);
				if (!go.isProibido()) {
					this.quantidadeNPC++;
				}
			}
			

		}
		this.acabou = false;
		this.resetarBola = true;
		this.world.setContactListener(new Contato());
		iniciarCinematicos();
	}

	protected void iniciarCinematicos() {
		for (Integer ix : this.cinematicos) {
        	GameObject go = this.cenaCorrente.getObjetos().get(ix);
   			go.getB2dBody().setLinearVelocity(new Vec2(go.getVelocidade()/10f,0.0f));
        }
		
	}

	protected void exibirTela(Textura t) {
		if (this.mostrandoAviso) {
			t = this.getTextura(7);
		}		
		if (t.getExibirTela() == 1) {
			// Tela de pontuação
			mostrandoAviso = true;
			PosicaoTela pt = this.getPosicao(6);
			Textura fundo = this.getTextura(7);
			fundo.setPosicaoAtual(pt);
			fundo.setPosicaoFixa(pt.getId());
			fundo.setVisivel(true);
			pt = this.getPosicao(7);
			Textura rotulo = this.getTextura(8);
			rotulo.setPosicaoAtual(pt);
			rotulo.setPosicaoFixa(7);
			rotulo.setVisivel(true);
			this.texturasAviso = new ArrayList<Textura>();
			this.texturasAviso.add(rotulo);
			this.texturasAviso.add(fundo);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		else if (t.getExibirTela() == 0) {
				// Fechar tela de aviso
				for (Textura tex : this.texturasAviso) {
					tex.setPosicaoAtual(null);
					tex.setPosicaoFixa(0);
					tex.setVisivel(false);
				}
				this.texturasAviso = null;
				this.mostrandoAviso = false;
						}
	}
	
	protected void resetarGame() {
		
		this.resetRenderer();
		this.acabou = false;
		this.chutou = false;

		this.contagemFrames = 0;
		this.inicio = Calendar.getInstance();
		this.resetarBola = false;
		this.quantidadeNPC = 0;
		this.quantidadeNPCcalculados = 0;
		this.texturasAviso = null;
		this.mostrandoAviso = false;
		
		this.relogio.setVisivel(false);
		this.qtdOrdemErrada = 0;
		this.cinematicos = new ArrayList<Integer>();

		
		try {
			this.prepareModels();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
		this.carregarCena(this.cena);

		this.ajustarObjetos();
		this.reajustarObjetos();
		

	
		resetarVariaveis();
		this.resetingGame = false;
		
	}
	
	private void resetarVariaveis() {

		if (this.cena == 0) {
			this.posAnterior = this.getPosicao(POSICAO_BTN_ANTERIOR);
			this.posProximo = this.getPosicao(POSICAO_BTN_PROXIMO);
			this.textAnterior = this.getTextura(TEXTURA_ANTERIOR);
			this.textProximo = this.getTextura(TEXTURA_PROXIMO);
			this.cadeado = this.getTextura(TEXTURA_CADEADO);
			relogio = (Textura) this.getTextura(this.TEXTURA_CARREGANDO);
			relogioCenter = (PosicaoTela) this.getPosicao(this.POSICAO_RELOGIO);
		}
	}

	private boolean toqueDentro(Textura t, Coordenada toque) {
		boolean resultado = false;
		PosicaoTela p = t.getPosicaoAtual();
		
		float toqueX = toque.getX() - (larguraViewPort / 2);
		float toqueY = (alturaViewPort / 2) - toque.getY();
		
		
	    if ((toqueX >= p.getScreenLeft() && toqueX <= p.getScreenRight()) &&
	            (toqueY >= p.getScreenBottom() && toqueY <= p.getScreenTop())) {
	            resultado = true;
	    }
	    
		return resultado;
	}

	@Override
	protected void desenharTextos() {
		super.desenharTextos();
		if (!cenaCorrente.isParada()) {
			if (!this.pausa) {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				if (!acabou) {
					Calendar cal = Calendar.getInstance();
					//diferencaTempo = cal.getTime().getTime() - inicio.getTime().getTime();
					diferencaTempo = (long) (segundosDeJogo * 1000);
				}
				String saida = sdf.format(diferencaTempo);
				desenharString(saida, 2);
			}
		}
		else {
			// Textos de aviso da cena parada (inicial)
			
			long [] tempos = Records.getNiveis(this.context);
			
		
			if (this.textProximo == null) {
				this.posAnterior = this.getPosicao(POSICAO_BTN_ANTERIOR);
				this.posProximo = this.getPosicao(POSICAO_BTN_PROXIMO);
				this.textAnterior = this.getTextura(TEXTURA_ANTERIOR);
				this.textProximo = this.getTextura(TEXTURA_PROXIMO);
			}
			
			if (this.cadeado == null) {
				this.cadeado = this.getTextura(TEXTURA_CADEADO);
			}
			
			// Verifica se o botão ANTERIOR deve estar habilitado
			
			if (this.ultimoNivel == 1) {
				this.textAnterior.setVisivel(false);
				this.textAnterior.setPosicaoAtual(null);
				this.textAnterior.setPosicaoFixa(0);
			}
			else {
				this.textAnterior.setVisivel(true);
				this.textAnterior.setPosicaoAtual(posAnterior);
				this.textAnterior.setPosicaoFixa(posAnterior.getId());
			}
			
			// Verifica se o botão PRÓXIMO deve estar habilitado
			
			if ((this.ultimoNivel + 10) > mediasEsperadas.length) {
				this.textProximo.setVisivel(false);
				this.textProximo.setPosicaoAtual(null);
				this.textProximo.setPosicaoFixa(0);
			}
			else {
				this.textProximo.setVisivel(true);
				this.textProximo.setPosicaoAtual(posProximo);
				this.textProximo.setPosicaoFixa(posProximo.getId());
			}
			
			
			int [] linhas = new int[mediasEsperadas.length];
			for (int x = 0; x < (tempos.length); x++) {
				long tempo = tempos[x];
				long media = mediasEsperadas[x];
				int idTextura = 0;
				if (statusNivel[x]) {
					if (tempo == 0) {
						idTextura = this.TEXTURA_NORMAL;
					}
					else if (tempo > media) {
						idTextura = this.TEXTURA_BRONZE;
					}
					else if (tempo == media) {
							idTextura = this.TEXTURA_PRATA;
					}
					else {
						idTextura = this.TEXTURA_OURO;
					}
				}
				else {
					idTextura = this.TEXTURA_NORMAL;
				}
				linhas[x] = idTextura;
			}
			
			desenharLinhaNivel(this.POSICAO_LINHA1,this.ultimoNivel, linhas, tempos.length);
			if (tempos.length > 5) {
				desenharLinhaNivel(this.POSICAO_LINHA2,this.ultimoNivel+5, linhas, tempos.length);
			}
			DecimalFormat df = new DecimalFormat("00");
			
			int posIni = POSICAO_NUM_NIVEL1;
			int posCadeado = this.POSICAO_CADEADO1;
			int qtd = 0;
			int posTempos = this.POSICAO_TEMPO1;
			for (int x=(ultimoNivel - 1); x < tempos.length; x++) {
				qtd++;
				if (qtd > 10) {
					break;
				}

				
				
				String stnivel = df.format(x+1);
				desenharString(stnivel,posIni);
				posIni += 2;
				
				if (!statusNivel[x]) {
					desenharCadeado(posCadeado, this.cadeado);
				}
				else {
					if (tempos[x] > 0) {
						stnivel = this.converterRecords(tempos[x]);
						desenharString(stnivel, posTempos);
					}
				}
				
				posCadeado += 1;
				posTempos +=5;
				
			}
			
			if (this.relogio != null && this.relogio.isVisivel()) {
				this.desenharCadeado(this.relogioCenter.getId(), this.relogio);
			}
			
		}
		
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {

Log.d("PAUSA", "PAUSA onSurfaceChanged Renderer. Pausa = " + this.pausa);			

		// Há um bug no Android, que faz o "onSurfaceChaged()" ser invocado duas vezes
		// https://groups.google.com/forum/?fromgroups=#!topic/android-developers/k6-7g3TkEG4

		super.onSurfaceChanged(gl, width, height);
		
		if (!cenaCorrente.isParada()) {
			reajustarObjetos();
			
		}
		else {
			this.resetarVariaveis();
		}
		 
		this.resetingGame = false; 

	}

	
	protected void reajustarObjetos() {
		
		if (this.pausa) {
			this.pausa = false;
		}
		
		
		this.texturasAviso = new ArrayList<Textura>();
		xOrigemBola = bola.getCentro().getX();
		this.inicio = Calendar.getInstance();
		this.segundosDeJogo = this.segundosIniciais;
		this.quantidadeNPC = 0;

		int maxOrdem = 0;
		for (GameObject go : cenaCorrente.getObjetos()) {
			if (!go.isVisivel()) {
				continue;
			}
			if ((!go.isProibido()) && go.getOrdem() > maxOrdem) {
				maxOrdem = go.getOrdem();
			}
			if (go.getGameObject() == GameObject.GAMEOBJECT_NPC && (!go.isProibido())) {
				this.quantidadeNPC++;
			}
			if (go.getAlinhamento() == GameObject.ALINHAMENTO_GO.ALINHAMENTO_BASE_CHAO) {
				
				float yChao = -1 * ((this.alturaViewPort / 2) / proporcaoMetroTela);
				yChao = yChao + go.getAltura() / 2.0f;
				
				go.getCentro().setY(yChao);
				
				go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
						0.0f);
			}
		}
		
		this.quantidadeNPCcalculados = this.quantidadeNPC;
		
		// A ordem começa em zero
		this.qtdOrdem = new int[maxOrdem+1];
		
		// Agora, alinha os GOs que estão sobre outros
		
		for (GameObject go : cenaCorrente.getObjetos()) {
			this.reajustaUmObjeto(go);
		}
		
		this.resetarVariaveis();
		
		this.world.setContactListener(new Contato());
		
		iniciarCinematicos();

	}
	
	protected void reajustaUmObjeto(GameObject go) {
		
		if(go.getB2dBody() == null) {
			return;
		}
		if (!go.isVisivel()) {
			return;
		}
		if (go.getGameObject() == GameObject.GAMEOBJECT_NPC && (!go.isProibido())) {
			this.qtdOrdem[go.getOrdem()]++;
		}

		if (go.getAlinhamento() == GameObject.ALINHAMENTO_GO.ALINHAMENTO_SOBRE_OUTRO) {
			GameObject goBase = new GameObject();
			goBase.setId(go.getSobre());
			int idBase = cenaCorrente.getObjetos().indexOf(goBase);
			goBase = cenaCorrente.getObjetos().get(idBase);
			
			float yChao = goBase.getCentro().getY() + goBase.getAltura() / 2;
			yChao = yChao + go.getAltura() / 2.0f;
			go.getCentro().setY(yChao);
			go.setyOriginal(go.getCentro().getY());
			go.setxOriginal(go.getCentro().getX());
			go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
					0.0f);
		}
		else if (go.getAlinhamento() == GameObject.ALINHAMENTO_GO.ALINHAMENTO_NENHUM) {
				go.setyOriginal(go.getCentro().getY());
				go.setxOriginal(go.getCentro().getX());
		}
		
	}

	@Override
	protected void update(float deltaT) {
		

		if (this.replayFlag) {
			this.replayFlag = false;
			this.replayLevel();
this.displayObject(this.getObject(9));  //**************************************
			return;
		}
		
		if (this.pausa) {
			return;
		}
		
		if (this.trocarNivel) {
			this.trocarNivel = false;
			this.resetarGame();
			return;
		}

		
		if (this.resetingGame) {
			return;
		}
		
		super.update(deltaT);
		
		
		
		
		if (cenaCorrente.isParada()) {
			// **** Se estamos em uma cena parada, então não podemos prosseguir no update ***
			return;
		}
		
		if (!acabou) {
			segundosDeJogo += deltaT;
		}
		
		verCinematicos();
		this.reinicio = false;
		
		// Trata o caso da bola presa na esquerda:
		
		if (bolaParadaNaEsquerda()) {
			flagBolaParada = true;
		}
		else {
			contadorFramesBolaParada = 0;
			flagBolaParada = false;
		}
		
		if (flagBolaParada) {
			contadorFramesBolaParada++;
		}
		
		if (contadorFramesBolaParada >= cenaCorrente.getFps()) {
			contadorFramesBolaParada = 0;
			this.resetBola();
		}
		
		// Fim tratamento de bola presa na esquerda

		if (!acabou && segundosDeJogo >= cenaCorrente.getLimitesegundos()) {
			// O jogo acabou...
			this.tempoAviso = 3;
			this.resetBola();
			chutou = true;
			acabou = true;
			pararCinematicos();
			trocarAviso(this.TEXTURA_TEMPO, true);
		}
 		if (this.aviso != null) {
			tempoAviso -= deltaT;
			if (tempoAviso <= 0) {
				trocarAviso(0, true);
			}
		}

		if (!acabou) {
			tratarColisaoNPC();
		}

		if (resetarBola) {
			this.resetBola();
		}
	}

	protected boolean bolaParadaNaEsquerda() {
		boolean resultado = false;
		
		if (bola.getB2dBody().getPosition().x >= muro.getB2dBody().getPosition().x) {
			// A bola está no lado esquerdo
			if (bola.getB2dBody().getAngularVelocity() == 0 
					|| (bola.getB2dBody().getLinearVelocity().x == 0)
					|| (bola.getB2dBody().getLinearVelocity().y == 0)) {
				resultado = true;
			}
		}
		
		return resultado;
	}



	protected void verCinematicos() {
		if (!acabou) {
			for (Integer x : this.cinematicos) {
				GameObject go = cenaCorrente.getObjetos().get(x);
				float posX = go.getB2dBody().getPosition().x;
				float deslocamento = go.getLargura() * 0.45f;
				float limiteEsquerdo = go.getxOriginal() - deslocamento;
				float limiteDireito = go.getxOriginal() + deslocamento;
				if (posX <= limiteEsquerdo) {
					go.setVelocidade(Math.abs(go.getVelocidade()));
					go.getB2dBody().setLinearVelocity(new Vec2(go.getVelocidade(), 0.0f));
				}
				else if (posX >= limiteDireito) {
						go.setVelocidade(Math.abs(go.getVelocidade()) * -1);
						go.getB2dBody().setLinearVelocity(new Vec2(go.getVelocidade(), 0.0f));
				}
				if (this.reinicio) {
					go.getB2dBody().setLinearVelocity(new Vec2(go.getVelocidade(), 0.0f));
				}
			}
			
			if (this.reinicio) {
				for (Integer x : this.cinematicos) {
					GameObject go = cenaCorrente.getObjetos().get(x);
					reposicionarElementos(go);
				} 
			}
			
		}
	}
	
	private void reposicionarElementos(GameObject go) {

		float deslocamento = go.getB2dBody().getPosition().x - go.getxOriginal();
		for (GameObject goSobre : go.getEstaoSobre()) {
			float novoX = goSobre.getxOriginal();
			novoX += deslocamento;
			goSobre.getCentro().setX(novoX);
			goSobre.getB2dBody().
				setTransform(new Vec2(goSobre.getCentro().getX(), goSobre.getCentro().getY()),  
					0.0f);
		}
	}

	protected void tratarColisaoNPC() {
		if (acabou) {
			return;
		}
		if (this.segundosDeJogo < 0.60) {
			return;
		}
		boolean atingiuProibido = false;
		for (GameObject go : cenaCorrente.getObjetos()) {
			if (go.isVisivel() && go.getGameObject() == GameObject.GAMEOBJECT_NPC) {
				if (go.getB2dBody().getPosition().y < go.getyOriginal()) {

					// O go caiu...
					
					// É um go proibido?
					if (go.isProibido() && 
							this.segundosDeJogo >= 0.60) {
								atingiuProibido = true;
								break;
					}

					
					// Vamos verificar a ordem:
					if (ordemErrada(go)) {
						this.tempoAviso = 0.50f;
						trocarAviso(17, true);
						chutou = false;
						resetarBola = true;
						float newX = verSeMoveu(go);
						go.getCentro().setX(newX);
						go.getCentro().setY(go.getyOriginal());
						go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
								0.0f);
						go.getB2dBody().setLinearVelocity(new Vec2(0.0f,0.0f));
						go.getB2dBody().setAngularVelocity(0.0f);
						int qtdNPCErrados = cenaCorrente.getRetornoNPC();
						if (qtdNPCErrados > 0) {
							qtdOrdemErrada++;
							if (qtdOrdemErrada >= qtdNPCErrados) {
								reverterNPC();
								qtdOrdemErrada = 0;
							}
						}
					}
					else {
						this.quantidadeNPC--;
						//som();
						if (this.quantidadeNPC == 0) {
							break;
						}
						go.setVisivel(false);
						world.destroyBody(go.getB2dBody());
						go.setB2dBody(null);
						go.setFixture(null);
						this.qtdOrdemErrada =0;
						this.qtdOrdem[go.getOrdem()]--;
					}

				}
			}
		}
		if (!acabou && atingiuProibido) {
			// Acabou porque atingiu um objeto proibido
			this.tempoAviso = 3;
			this.resetBola();
			chutou = true;			
			acabou = true;
			this.pararCinematicos();
			for (GameObject go : cenaCorrente.getObjetos()) {
				if (go.getGameObject() == GameObject.GAMEOBJECT_NPC) {
					if (go.getB2dBody() == null) {
						this.createBody(go);
					}
					float newX = verSeMoveu(go);
					go.getCentro().setX(newX);
					go.getCentro().setY(go.getyOriginal());
					go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
							0.0f);
					go.getB2dBody().setLinearVelocity(new Vec2(0.0f,0.0f));
					go.getB2dBody().setAngularVelocity(0.0f);					
					this.ajustaUmObjeto(go);
					this.reajustaUmObjeto(go);
					go.setVisivel(true);
				}
			}
			trocarAviso(this.TEXTURA_PROIBIDO, true);
		}
		else if (this.quantidadeNPC == 0) {
				// Acabou!
				this.tempoAviso = 3;
				this.resetBola();
				chutou = true;			
				acabou = true;
				this.pararCinematicos();
				trocarAviso(this.TEXTURA_PREMIO, true);
				long [] niveis = Records.getNiveis(context);
				long numeroSegundos = diferencaTempo / 1000;
				this.duracaoNivel = numeroSegundos;
				if (niveis[cenaCorrente.getNumero() - 1] == 0 
						|| numeroSegundos < niveis[cenaCorrente.getNumero() - 1]) {
					niveis[cenaCorrente.getNumero() - 1] = numeroSegundos;
				}
				Records.updateLevels(niveis);
				if (cenaCorrente.getNumero() < this.mediasEsperadas.length) {
					// Se o nível é menor que o último, abra o próximo
					// O statusNivel começa em zero
					this.statusNivel[cenaCorrente.getNumero()] = true;
				}
		}
	}

	protected float verSeMoveu(GameObject go) {

		float novoX = go.getxOriginal();
		GameObject go2 = this.getObject(go.getSobre());
		if (go2.getTipo() == GameObject.TIPO_GO.TIPO_GO_CINEMATICO) {
			float diferenca = go2.getB2dBody().getPosition().x - go2.getxOriginal();
			novoX += diferenca;
		}
		
		return novoX;
	}

	protected void reverterNPC() {
		if (this.quantidadeNPC < this.quantidadeNPCcalculados) {
			// Temos que saber se já derrubou pelo menos 1 NPC...
			// Vamos voltar um NPC já derrubado, como penalidade.
			for (GameObject go : cenaCorrente.getObjetos()) {
				if ((go.getGameObject() == GameObject.GAMEOBJECT_NPC)
						&& (!go.isVisivel())) {
		
					this.createBody(go);
					this.ajustaUmObjeto(go);
					this.reajustaUmObjeto(go);
					this.quantidadeNPC++;
					go.setVisivel(true);
					break;
				}
			}
		}
	}
	
	protected boolean ordemErrada(GameObject go) {
		boolean retorno = false;
		/*
		 * Verifica se existem objetos de menor ordem. Se existirem, então
		 * derrubou na ordem errada.
		 */
		if (go.getOrdem() > 0) {
			for (int x=(go.getOrdem() - 1); x >= 0; x--) {
				if (this.qtdOrdem[x] > 0) {
					retorno = true;
					break;
				}					
			}
		}
		
		return retorno;
	}

	protected void trocarAviso (int id, boolean forcar) {
		if (id == 0) {
			// Desligar aviso
			int avisoAnterior = aviso.getId();
			aviso.setPosicaoAtual(null);
			aviso.setPosicaoFixa(0);
			aviso.setVisivel(false);
			aviso = null;
			tempoAviso = 0;
			if (this.acabou) {
				// Mostrar botão REPLAY e talvez o NEXT
				this.mostrarNextReplay(avisoAnterior);
			}
		}
		else {
			if (aviso != null) {
				if (forcar) {
					aviso.setPosicaoAtual(null);
					aviso.setPosicaoFixa(0);
					aviso.setVisivel(false);
				}
				else {
					return;
				}
			}
			aviso = getTextura(id);
			aviso.setPosicaoAtual(getPosicao(11));
			aviso.setPosicaoFixa(aviso.getPosicaoAtual().getId());
			aviso.setVisivel(true);
		}
	}
	
	protected void mostrarNextReplay(int avisoAnterior) {
		int nivel = cenaCorrente.getNumero() - 1;
		Textura txtReplay = this.getTextura(this.TEXTURA_BTN_REPLAY_LEVEL);
		int iPosReplay = this.POSICAO_REPLAY_SOZINHO;
		if (nivel < (this.mediasEsperadas.length - 1)) {
			if (statusNivel[nivel+1] || avisoAnterior == this.TEXTURA_PREMIO) {
				Textura txtProximo = this.getTextura(this.TEXTURA_BTN_NEXT_LEVEL);
				iPosReplay = this.POSICAO_REPLAY_NEXT;
				PosicaoTela posNext = this.getPosicao(this.POSICAO_NEXT);
				txtProximo.setPosicaoAtual(posNext);
				txtProximo.setVisivel(true);
			}
		}
		PosicaoTela posReplay = this.getPosicao(iPosReplay);
		txtReplay.setPosicaoAtual(posReplay);
		txtReplay.setVisivel(true);
		if (avisoAnterior == this.TEXTURA_PREMIO) {
			// O jogador concluiu o nível. Vamos mostrar o botão de compartilhamento
			PosicaoTela posShare = this.getPosicao(this.POSICAO_COMPARTILHAR);
			Textura txtShare = this.getTextura(this.TEXTURA_BTN_COMPARTILHAR);
			txtShare.setPosicaoAtual(posShare);
			txtShare.setVisivel(true);
		}
		
	}

	protected Textura getTextura(int id) {
		Textura textura = new Textura();
		textura.setId(id);
		int ix = modeloTela.getTexturas().indexOf(textura);
		textura = modeloTela.getTexturas().get(ix);
		return textura; 
	}
	

	

	class Contato implements ContactListener {

		@Override
		public void beginContact(org.jbox2d.dynamics.contacts.Contact c) {

			int id1 = ((Integer)c.getFixtureA().getBody().getUserData()).intValue();
			int id2 = ((Integer)c.getFixtureB().getBody().getUserData()).intValue();

			// A bola bateu no chão
				
			if ((id1 == bola.getId() &&
					id2 == 2) || (id1 == 2 &&
							id2 == bola.getId())) {
				bolaChao();
			}

			

				
		}

		@Override
		public void endContact(org.jbox2d.dynamics.contacts.Contact arg0) {
			
		}

		@Override
		public void postSolve(org.jbox2d.dynamics.contacts.Contact arg0,
				ContactImpulse arg1) {
			
		}

		@Override
		public void preSolve(org.jbox2d.dynamics.contacts.Contact arg0,
				Manifold arg1) {
		
			
		}
		
	}


	protected void bolaChao() {

		chutou = false;
		if (bola.getB2dBody().getPosition().x >= this.getObject(6).getB2dBody().getPosition().x) {
			resetarBola = true;
		}
	}





	public void som() {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float leftVolume = curVolume/maxVolume;
		float rightVolume = curVolume/maxVolume;
		int priority = 1;
		float normal_playback_rate = 0.5f;
		this.pool.play(this.vidroSoundID, leftVolume, rightVolume, priority, 0, normal_playback_rate);
		
	}

	protected void resetBola() {
		float yChao = -1 * ((this.alturaViewPort / 2) / proporcaoMetroTela);
		yChao = yChao + bola.getAltura() / 2.0f;
		bola.getCentro().setY(yChao);
		bola.getCentro().setX(this.xOrigemBola);
		bola.getB2dBody().setLinearVelocity(new Vec2(bola.getB2dBody().getLinearVelocity().x,0.0f));
		bola.getB2dBody().setTransform(new Vec2(bola.getCentro().getX(), bola.getCentro().getY()), 0.0f);
		bola.getB2dBody().setAwake(true);
		resetarBola = false;
	}

	
	protected void msg(int p) {
		Log.d("DEB***", "PASSO: " + p);
	}
	
	protected String converterRecords(long record) {
		StringBuilder saida = new StringBuilder();
		long numSegundos = (record < 1000) ? record : record / 1000;
		int minutos = (int) (numSegundos / 60);
		int segundos = (int) (numSegundos - (minutos * 60));
		DecimalFormat df = new DecimalFormat("00");
		saida.append(df.format(minutos));
		saida.append(":");
		saida.append(df.format(segundos));
		return saida.toString();
	}
	
	protected void displayObject(GameObject go) {
		Log.d("GO", "GO. " + go.toString());
	}
}

