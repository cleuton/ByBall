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
package com.obomprogramador.games.openglavancado;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.obomprogramador.games.byball.R;

import com.obomprogramador.games.byball.recs.Records;
import com.obomprogramador.games.modelotela.MediaNiveisSingleton;
import com.obomprogramador.games.modelotela.ModeloTela;
import com.obomprogramador.games.modelotela.ModeloTelaLoader;
import com.obomprogramador.games.modelotela.PosicaoTela;
import com.obomprogramador.games.modelotela.Textura;
import com.obomprogramador.games.xmloader.Cena;
import com.obomprogramador.games.xmloader.ConfigFrustum;
import com.obomprogramador.games.xmloader.Coordenada;
import com.obomprogramador.games.xmloader.GameModel;
import com.obomprogramador.games.xmloader.GameModelLoader;
import com.obomprogramador.games.xmloader.GameObject;
import com.obomprogramador.games.xmloader.GameObject.ALINHAMENTO_GO;

public class OpenGLAvancadoRenderer implements GLSurfaceView.Renderer {

	protected Context context;
	protected final String TAG = "OGLBRenderer";
	protected final int QUANTIDADE_DE_VERTICES = 4;
	protected int cena = 1;
	
    static float textureCoords[] = { 
				0.0f, 1.0f,		// canto superior esquerdo
				0.0f, 0.0f,		// canto inferior esquerdo
				1.0f, 1.0f,		// canto superior direito
				1.0f, 0.0f		// canto inferior direito
	};
    
    // Matrizes de projeção e câmera:
    
    protected float[] matrizProjecao = new float[16];
    protected float[] matrizCamera = new float[16];
    protected float[] matrizModelo = new float[16];
    protected float[] matrizIntermediaria = new float[16];
    
    // Shaders:
    
    protected int programaGLES;
    
    protected final String vertexShaderSource =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "  vTextureCoord = aTextureCoord;\n" +
            "}\n";

        protected final String fragmentShaderSource =
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform float fadefactor;\n" + 
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
            "  gl_FragColor.a *= fadefactor;" + 
            "}\n";
        
        protected int muMVPMatrixHandle;
        protected int maPositionHandle;
        protected int maTextureHandle;
        protected int hTextureVOB;
        protected int maFadeFactor;
        
        // Game Model Loader
        
        protected GameModel gameModel;
        protected Cena cenaCorrente;
        protected Coordenada telaTopLeft;
        protected Coordenada telaBottomRight;
        protected float proporcaoMetroTela;
        protected float diagonalTela;
        
        // Modelo Tela
        
        protected ModeloTela modeloTela;
        protected Map<Integer, Textura> mapaCaracteres = new HashMap<Integer, Textura>();
        
        
        // Box2D
        
        protected World world;
        protected int larguraViewPort;
        protected int alturaViewPort;
        
        // GameLoop
        
        protected boolean simulando;
        protected long ultimo;
        protected long segundo;
        protected long contagemFrames;
        protected long FPSmedio;
        protected long tempo;
        
        // Específicos
        
        protected GameObject bola;
        protected GameObject muro;
        protected String nomeGame;
        protected String nomeTela;
        public boolean pausa;
        protected boolean resetingGame;
       
        protected int [] mediasEsperadas;
        protected boolean [] statusNivel;
        protected Textura relogio;
        protected PosicaoTela relogioCenter;
        protected int ultimoNivel = 1;
        
        protected List<Integer> cinematicos = new ArrayList<Integer>();
        
        protected boolean aplicouForca;
        protected Vec2 forca;
        protected SoundPool pool;
        protected int vidroSoundID;
        protected int bolaSoundID;
        
        protected int ID_MURO = 6;
        public int segundosIniciais = 0;
        protected boolean reinicio = true;
      
	
	public OpenGLAvancadoRenderer(Context context, String nomeGame, String nomeTela) throws Exception {
		
		
		this.context = context;
		this.nomeGame = nomeGame;
		this.nomeTela = nomeTela;
		
		prepareModels();
	}
	
	protected void prepareModels() throws Exception {
		// Carregar modelo do jogo
        
        gameModel = GameModelLoader.loadGameModel(this.context, this.nomeGame);
        MediaNiveisSingleton ms = MediaNiveisSingleton.getMediaSingleton(this.context);
        mediasEsperadas = ms.getMedias();
        
        if (gameModel == null) {
        	throw new Exception("Problema ao carregar Modelo!!!!");
        }
        
       
        this.statusNivel = carregarStatusNiveis();
        
	}
	
	protected boolean [] carregarStatusNiveis() {
		long [] tempos = Records.getNiveis(context);
		
		boolean [] status = new boolean[tempos.length];
		status[0] = true;  // O primeiro nível é sempre aberto
		for (int x=1; x<tempos.length; x++) {
			if (tempos[x] > 0) {
				status[x] = true; // Se o nível já foi jogado, então é aberto
			}
			else {
				if (x > 0) {
					if (status[x-1]) {
						/*
						 * Se o nível anterior foi completado, então este nível tem que ser aberto.
						 */
						if (tempos[x-1] > 0) {
							status[x] = true;
						}
					}
				}
			}
		}
		return status;
	}
	
	protected void resetRenderer() {
		resetModeloGame();
		resetModeloTela();
		resetCenaCorrente();
		this.cenaCorrente = null;
		this.gameModel = null;
		this.modeloTela = null;
		this.world = null;
		this.reinicio = true;
	}
	
	protected void resetModeloGame() {
		for (GameObject go : cenaCorrente.getObjetos()) {
			releaseBuffers(new int [] {go.getVobTextura(), go.getVobVertices()});
			releaseTexture(go.getHandlerTextura());
			if (go.getB2dBody() != null) {
				world.destroyBody(go.getB2dBody());
			}
		}
	}
	
	protected void resetModeloTela() {
		for (PosicaoTela pt : modeloTela.getPosicoes()) {
			releaseBuffers(new int [] {pt.gethVobVertices()});
		}
		for (Textura t : modeloTela.getTexturas()) {
			releaseTexture(t.gethTextura());
		}
	}
	
	protected void resetCenaCorrente() {
		releaseBuffers(new int [] {this.hTextureVOB});
		releaseTexture(cenaCorrente.getHandlerTextura());
	}
	
	protected void releaseBuffers(int [] hBuffers) {
		GLES20.glDeleteBuffers(hBuffers.length, hBuffers, 0);
	}
	
	protected void releaseTexture(int hTex) {
		int [] texBuffers = {hTex};
		GLES20.glDeleteTextures(1, texBuffers, 0);
	}

	
	protected void carregarCena(int i) {
		
		if ((cenaCorrente == null) || (!this.pausa)) {
			this.pool = new SoundPool(2,AudioManager.STREAM_MUSIC,0);
			this.vidroSoundID = pool.load(context, R.raw.vidro, 100);
			this.bolaSoundID = pool.load(context, R.raw.bola, 80);
			DisplayMetrics displayMetrics = new DisplayMetrics();
	    	WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	    	wm.getDefaultDisplay().getMetrics(displayMetrics);
	    	larguraViewPort = displayMetrics.widthPixels;
	    	alturaViewPort = displayMetrics.heightPixels;
			
			Cena cena = new Cena();
			cena.setNumero(i);
			int indice = gameModel.getCenas().indexOf(cena);
			cenaCorrente = gameModel.getCenas().get(indice);
			
			tempo = (long) ((1 / cenaCorrente.getFps()) * 1000);
			
			diagonalTela = (float) (Math.pow(alturaViewPort, 2) + Math.pow(larguraViewPort, 2));
	    	diagonalTela = (float) Math.sqrt(diagonalTela);
	    	proporcaoMetroTela = (float) (diagonalTela / cenaCorrente.getBox2d().getProporcaoMetroTela());
	    	
	    	
		}
		/*
		// Reduz a quantidade de interações de velocidade para aparelhos
		// com menor potência de CPU.
		
		float bogoMips = this.ReadBogoMIPS();
		if (bogoMips <= 500) {
			cenaCorrente.getBox2d().setVelocityInterations(5);
			Log.d("BOGOMIPS", "Reduzindo velocity Interations para: " + 5);
		}
		else {
			Log.d("BOGOMIPS", "Velocity Interations: " + cenaCorrente.getBox2d().getVelocityInterations());
		}
		*/
		
	    int [] buffers = new int[1];
        
	    GLES20.glGenBuffers(1, buffers, 0);
	    checkGlError("glGenBuffers 2");
	    hTextureVOB = buffers[0];

	    // Buffer de textura: 
	    
		int [] hTextura = new int[2];
		//Vamos inicializar o byte buffer da textura:
        ByteBuffer b2 = ByteBuffer.allocateDirect(
	    // (# of coordinate values * 4 bytes per float)
        		textureCoords.length * 4);
	    b2.order(ByteOrder.nativeOrder());
	    FloatBuffer texturaQuadrado = b2.asFloatBuffer();
	    texturaQuadrado.put(textureCoords);
	    texturaQuadrado.position(0);
	    
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, hTextureVOB);
	    checkGlError("glBindBuffer 1");
	    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
	    		textureCoords.length * 4,
	            texturaQuadrado,
	            GLES20.GL_STATIC_DRAW);
	    checkGlError("glBufferData 1");

    	
		// Carrega fundo
		
		if (cenaCorrente.getTexturaFundo() != null) {
			
			// Carrega imagem
					
			Bitmap imagem = carregarImagem(cenaCorrente.getTexturaFundo());
			cenaCorrente.setHandlerTextura(carregarCoordenadasTextura(imagem));
			imagem.recycle();
			// Cria coordenadas imagem de fundo

			int hVertex = carregarCoordenadasVertice(new Coordenada(0,0,0),
					2, 
					2);
			cenaCorrente.setVobVertices(hVertex);

		}
		
		// Carregar Game Objects
		
		/*
		 * Só carrega vértices e texturas de objetos renderizáveis.
		 */
		
		for (GameObject go : cenaCorrente.getObjetos()) {
			
			if (go.getArquivoTextura() != null) {
			
				// Carrega Sprite:
				
				Bitmap imagem = carregarImagem(go.getArquivoTextura());
				go.setHandlerTextura(carregarCoordenadasTextura(imagem));
				go.setVobTextura(hTextureVOB);
				imagem.recycle();
				
				// Carrega Vertice:
				
				if (go.isEsticarAltura()) {
					go.setAltura(alturaViewPort);
				}
				if (go.isEsticarLargura()) {
					go.setLargura(larguraViewPort);
				}
				
				Coordenada centro = new Coordenada(go.getCentro().getX() * proporcaoMetroTela, 
						                           go.getCentro().getY() * proporcaoMetroTela, 
						                           go.getCentro().getZ() * proporcaoMetroTela);
				
				int hVertex = carregarCoordenadasVertice(centro,
						go.getAltura() * proporcaoMetroTela, 
						go.getLargura() * proporcaoMetroTela);
				go.setVobVertices(hVertex);
			
			}
		}
		
		if (!this.pausa) {
			// Carregar configurações Box2D
			initBox2D();
		}
		
		
	}

	protected void initBox2D() {
		
	
		// Inicializa o "mundo" e os objetos
		world = new World(new Vec2(0.0f, -10.0f), true);
		
		for (GameObject go : cenaCorrente.getObjetos()) {
			this.createBody(go);
		}
	}

	
	protected void createBody(GameObject go) {
		BodyDef bodyDef = new BodyDef();
		/*
		Vec2 centro = new Vec2(go.getCentro().getX()  * proporcaoMetroTela, 
				go.getCentro().getY() * proporcaoMetroTela);
				*/
		Vec2 centro = new Vec2(go.getCentro().getX(), 
				go.getCentro().getY());
		bodyDef.position.set(centro);
		if (go.getGameObject() == GameObject.GAMEOBJECT_PLAYER) {
			bodyDef.bullet = true;
		}
		Body body = world.createBody(bodyDef);
		switch (go.getTipo()) {
		case TIPO_GO_ESTATICO:
			body.setType(BodyType.STATIC);
			break;
		case TIPO_GO_DINAMICO:
			body.setType(BodyType.DYNAMIC);
			break;
		case TIPO_GO_CINEMATICO:
			body.setType(BodyType.KINEMATIC);
			break;
			default:
				body.setType(BodyType.STATIC);
		}

		// As fixtures com shapes serão criados no método onSurfaceChanged()
		
		go.setB2dBody(body);
		
	}
	
	protected Bitmap carregarImagem(String texturaFundo) {
		Bitmap imagem = null;
		Options opc = new Options();
        opc.inDither = true;
        opc.inScaled = false; 
        opc.inPreferredConfig = Bitmap.Config.ARGB_4444;
		String nome = null;
        int pos = texturaFundo.indexOf('.');
        nome = (pos >= 0) ? texturaFundo.substring(0, pos) : texturaFundo;
        int codigo = this.context.getResources().getIdentifier(
        		nome, "drawable", context.getPackageName());
        imagem = BitmapFactory.decodeResource(this.context.getResources(), codigo, opc);
        /*
        InputStream is = context.getResources().openRawResource(codigo);
        
        try {
            imagem = BitmapFactory.decodeStream(is);

        } finally {
            //Always clear and close
            try {
                is.close();
                is = null;
            } catch (IOException e) {
            }
        } 
        */       
		return imagem;
	}
	
	protected int carregarCoordenadasVertice(Coordenada centro, float altura, float largura) {
		int handlerVertex = 0;
		float squareCoords[] = new float[12]; 
		float metadeLargura = (largura) / 2;
		float metadeAltura  = (altura) / 2;
		
		// Criar coorenadas de vértice
		// Canto inferior esquerdo: (centrox - metadeLargura, centroy - metadeAltura)
		squareCoords[0] = centro.getX() - metadeLargura;
		squareCoords[1] = centro.getY() - metadeAltura;
		squareCoords[2] = centro.getZ();
		// Canto superior esquerdo: (centrox - metadeLargura, centroy + metadeAltura)
		squareCoords[3] = centro.getX() - metadeLargura;
		squareCoords[4] = centro.getY() + metadeAltura;
		squareCoords[5] = centro.getZ();
		// Canto inferior direito: (centrox + metadeLargura, centroy - metadeAltura)
		squareCoords[6] = centro.getX() + metadeLargura;
		squareCoords[7] = centro.getY() - metadeAltura;
		squareCoords[8] = centro.getZ();
		// Canto superior direito: (centrox + metadeLargura, centroy + metadeAltura)
		squareCoords[9] = centro.getX() + metadeLargura;
		squareCoords[10] = centro.getY() + metadeAltura;
		squareCoords[11] = centro.getZ();
		

		
		// Vamos criar um buffer para passar para o OpenGL (que é em C):
		
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
			                squareCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer verticesQuadrado = bb.asFloatBuffer();
		verticesQuadrado.put(squareCoords);
		verticesQuadrado.position(0);

	    // Vamos VOB (na GPU) e mover os dados para a memória controlada por ela:

	    int [] buffers = new int[1];
        
	    GLES20.glGenBuffers(1, buffers, 0);
	    checkGlError("glGenBuffers 2");
	    handlerVertex = buffers[0];

	    // Buffer de textura: 
	    
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, handlerVertex);
	    checkGlError("glBindBuffer 1");
	    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
	    		squareCoords.length * 4,
	    		verticesQuadrado,
	            GLES20.GL_STATIC_DRAW);
	    checkGlError("glBufferData 1");
		
		return handlerVertex;
	}
	

	protected void verificarNovaProporcao(Coordenada centro, float metadeAltura, float metadeLargura) {
		/*
		 * A razão entre altura e largura é menor que 2/3, logo, temos que recalcular o centro
		 */
		float esquerda = centro.getX() - metadeLargura;
		float direita = centro.getX() + metadeLargura;
		float topo = centro.getY() + metadeAltura;
		float baixo = centro.getY() - metadeAltura;
		float limiteEsquerdo = -1 * (larguraViewPort / 2);
		float limiteDireito = larguraViewPort / 2;
		float limiteSuperior = alturaViewPort / 2;
		float limiteInferior = -1 * (alturaViewPort / 2);
		
		if (esquerda < limiteEsquerdo) {
			centro.setX(centro.getX() + (limiteEsquerdo - esquerda));
		}
		else if (direita > limiteDireito) {
			centro.setX(centro.getX() - (direita - limiteDireito));
		}
		
		if (topo > limiteSuperior) {
			centro.setY(centro.getY() - (topo - limiteSuperior));
		}
		else if (baixo < limiteInferior) {
			centro.setY(centro.getY() + (limiteInferior - baixo));
		}
	}

	protected int carregarCoordenadasTextura(Bitmap imagem) {

		int hTextura = 0;
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		int [] tamanho = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, tamanho,0);
		

	    // Vamos criar o handler da textura
	    
		int[] texturas = new int[1];
        GLES20.glGenTextures(1, texturas, 0);  // Podemos criar mais de uma simultaneamente

        hTextura = texturas[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTextura);
       
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_NEAREST);
        
        
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, imagem, 0);
        
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        
        imagem.recycle();
	    
		return hTextura;
	}


	protected void update(float deltaT) {
		/*
		 * Atualiza o mundo Box2D e calcula a projeção
		 */

		if(this.pausa) {
			// Estamos em pausa, logo, não vamos atualizar nada.
			return;
		}
		

		if(cenaCorrente.isParada()) {
			// A cena corrente é estática, sem animações
			return;
		}
		
		if (this.aplicouForca) {
			this.aplicouForca = false;
			Body bolaBody = this.getObject(1).getB2dBody();
			bolaBody.applyForce(this.forca, bolaBody.getWorldCenter());
		}

		
		  world.step(deltaT, 
				cenaCorrente.getBox2d().getVelocityInterations(), 
				cenaCorrente.getBox2d().getPositionInterations());
		

	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		

			// Controle de FPS:
	    	long agora = System.currentTimeMillis();
	    	long intervaloPassado = tempo;
	    	if (ultimo > 0) {
	    		intervaloPassado = agora - ultimo;
	    	}
	
	    	if (intervaloPassado < tempo) {
			// Colocar o Thread em "sleep mode", pois falta
			// algum tempo para iniciar
	        	try {
					Thread.sleep((long) (tempo - intervaloPassado));
					intervaloPassado += (tempo - intervaloPassado);
				} catch (InterruptedException e) {
					Log.e("GAMELOOP", "Interrompido o sleep: " + e.getMessage());
				}
	    	}
	    	
	    	// Verifica se atingiu 1 segundo
	    	contagemFrames++;
	    	segundo += intervaloPassado;
	    	if (segundo >= 1000) {
	    		// atingiu 1 segundo
	    		FPSmedio = contagemFrames;
	    		segundo = 0;
	    		contagemFrames = 1;
	    	}
	    	
	    	ultimo = System.currentTimeMillis();
	    	update(intervaloPassado / 1000.0f);
	    	
			/*
			 * Este é o momento de desenhar um frame:
			 * 1 - Limpamos a tela;
			 * 2 - Informamos o buffer de vértices e textura;
			 * 3 - Calculamos a matriz de posicionamento combinada;
			 * 4 - Desenhamos.
			 */
			
			// Limpar e preparar:
			
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			GLES20.glUseProgram(programaGLES);
	        checkGlError("glUseProgram");
	
	        // Loop de renderização:
	        /*
	         * 1 - Renderizar textura de fundo da cena;
	         * 2 - Para cada GameObject: renderizar textura;
	         */
	        
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cenaCorrente.getHandlerTextura());
	        
	        // Apontamos nossos buffers de dados (que já estão na GPU): 
	        
	        // Vértices: 
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, cenaCorrente.getVobVertices());
	        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
	        GLES20.glEnableVertexAttribArray(maPositionHandle);
	        
	        // Textura: 
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
	        checkGlError("glEnableVertexAttribArray maPositionHandle");
	        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	        		8, 0);
	        GLES20.glEnableVertexAttribArray(maTextureHandle);
	        
			// Matriz de posicionamento da textura, sem rotação nem movimento:
	        
	        float[] matrizProjecaoFundo = new float[16];
	        float[] matrizCameraFundo = new float[16];
	        
			Matrix.setIdentityM(matrizModelo, 0);
			Matrix.setIdentityM(matrizProjecaoFundo,0);
			Matrix.setIdentityM(matrizCameraFundo,0);
	        Matrix.multiplyMM(matrizIntermediaria, 0, matrizCameraFundo, 0, matrizModelo, 0);
	        Matrix.multiplyMM(matrizIntermediaria, 0, matrizProjecaoFundo, 0, matrizIntermediaria, 0);
	        
			// Desenhamos a textura de fundo:
	        
	        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	        GLES20.glDepthMask(false);
	        GLES20.glUniform1f(maFadeFactor, (float) 1.0);
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
	        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
	
	        checkGlError("glDrawArrays");
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	        
	        

	        
	        // Vamos renderizar os Game Objects:
	        
	        
	        
	        for (GameObject go : cenaCorrente.getObjetos()) {
	        	if (go.getArquivoTextura() != null && go.isVisivel()) {
	        		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, go.getHandlerTextura());
	                
	                // Vértices: 
	                
	                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, go.getVobVertices());
	                GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
	                GLES20.glEnableVertexAttribArray(maPositionHandle);
	                
	                // Textura: 
	                
	                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
	                checkGlError("glEnableVertexAttribArray maPositionHandle");
	                GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	                		8, 0);
	                GLES20.glEnableVertexAttribArray(maTextureHandle);
	                
	        		// Matriz de posicionamento da textura, sem rotação nem movimento:
	                
	        		Matrix.setIdentityM(matrizModelo, 0);
	                float posicaoX = go.getB2dBody().getTransform().position.x * proporcaoMetroTela;
	                float posicaoY = go.getB2dBody().getTransform().position.y * proporcaoMetroTela;
	                Matrix.translateM(matrizModelo, 0, posicaoX, posicaoY, 0); 
	                
	                Matrix.rotateM(matrizModelo, 0, (float) (go.getB2dBody().getAngle() * 57.2957795), 
	                		0, 0, 1);
	                Matrix.multiplyMM(matrizIntermediaria, 0, matrizCamera, 0, matrizModelo, 0);
	                Matrix.multiplyMM(matrizIntermediaria, 0, matrizProjecao, 0, matrizIntermediaria, 0);
	                
	        		// Desenhamos a textura de fundo:
	                float [] win = new float[3];
	                int [] mview = {0,0,480,320};
	                GLU.gluProject (posicaoX, 
	                				posicaoY, 
	                				0.0f, 
	                				matrizModelo, 
	                				0, 
	                				matrizProjecao, 
	                				0, 
	                				mview, 
	                				0, 
	                				win, 
	                				0);
	                GLES20.glUniform1f(maFadeFactor, (float) 1.0);
	                GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
	                GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
	
	                checkGlError("glDrawArrays");
	                
	                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	        	}
	        }
	        
	        
	        // Vamos desenhar as texturas do Mapa de tela
	        // e os textos
	        
	        
	        
	        for (Textura t : modeloTela.getTexturas()) {
	        	if ((t.getPosicaoFixa() != 0) || (t.getPosicaoAtual() != null)) {
	        			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.gethTextura());
	
	        	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, t.getPosicaoAtual().gethVobVertices());
	        	        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
	        	        GLES20.glEnableVertexAttribArray(maPositionHandle);
	        	        
	        	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
	        	        checkGlError("glEnableVertexAttribArray maPositionHandle");
	        	        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	        	        		8, 0);
	        	        GLES20.glEnableVertexAttribArray(maTextureHandle);
	        	        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	        	        GLES20.glDepthMask(false);
	        	        
	        	        Matrix.setIdentityM(matrizModelo, 0);
	        	        float posicaoX = t.getPosicaoAtual().getCentro().getX();
	                    float posicaoY = t.getPosicaoAtual().getCentro().getY();
	                    float mFade = 1.0f;
	                    
	                    Matrix.translateM(matrizModelo, 0, 0.0f, 0.0f, 0);
	                    
	                    Matrix.rotateM(matrizModelo, 0, (float) (t.getAngulo() * 57.2957795), 
		                		0, 0, 1);
	                    
	                    
	                    Matrix.multiplyMM(matrizIntermediaria, 0, matrizCamera, 0, matrizModelo, 0);
	                    Matrix.multiplyMM(matrizIntermediaria, 0, matrizProjecao, 0, matrizIntermediaria, 0);
	        	        
	                    if (t.getId() == 4) {
	                    	mFade = 0.60f;
	                    }
	                    
	                    GLES20.glUniform1f(maFadeFactor, mFade);
	        	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
	        	        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
	        	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	        	}
	        }
	        
	        
	        desenharTextos();

	}

	protected void desenharTextos() {
		/*
		 * Sobrescreva este método para desenhar textos usando os caracteres
		 */
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

Log.d("PAUSA", "PAUSA onSurfaceChanged OpenGLAvancadoRenderer. Pausa = " + this.pausa);		
		
		// A View foi redimensionada (pode acontecer no início e se mudar a orientação)
		GLES20.glViewport(0, 0, width, height);
		larguraViewPort = width;
		alturaViewPort = height;
        
        ConfigFrustum frustum = cenaCorrente.getFrustum();
        
		Matrix.setIdentityM(matrizProjecao, 0);      
		Matrix.orthoM(matrizProjecao, 0, -larguraViewPort/2, 
										 +larguraViewPort/2, 
										 -alturaViewPort/2, 
										 +alturaViewPort/2, 
										-1, 
										1);
        
        // Coordenadas do canto superior esquerdo da tela, transformadas em World Coordinates
        this.telaTopLeft = new Coordenada(-width / 2, height / 2);

        // Coordenadas do canto inferior direito da tela, transformadas em World Coordinates
        this.telaBottomRight = new Coordenada( width / 2, -height / 2);
        
       	ajustarObjetos();

	}
	
	protected void pararCinematicos() {
		Vec2 v = new Vec2(0.0f,0.0f);
		for (int igo : this.cinematicos) {
			GameObject go = this.cenaCorrente.getObjetos().get(igo);
			go.getB2dBody().setLinearVelocity(v);
			go.getCentro().setX(go.getxOriginal());
			go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
					0.0f);
		}

		
	}
	
	protected void ajustarObjetos() {
		
Log.d("PAUSA", "PAUSA ajustarObjetos OpenGLAvancadoRenderer. Pausa = " + this.pausa);				

		pararCinematicos();
		this.cinematicos = new ArrayList<Integer>();
		this.reinicio = true;
		//if (!this.pausa) {
            // Ajusta localização dos objetos que possuem alinhamento com relação ao centro
            for (GameObject go : cenaCorrente.getObjetos()) {
            	this.ajustaUmObjeto(go);
            }
        //}
        if (!simulando) {
        	runGameLoop();
        }
        
    
/*         **************************   NÃO TEM INFLUÊNCIA        
        // Verificar velocidade GOs cinemáticos
        for (Integer ix : this.cinematicos) {
        	GameObject go = this.cenaCorrente.getObjetos().get(ix);
   			go.getB2dBody().setLinearVelocity(new Vec2(go.getVelocidade() / 10.0f ,0.0f));
        }
*/        
        
        // Vamos carregar as texturas do modelo de tela, pois dependem das coordenadas reais da tela
        
        carregarModeloTela();

	}
	
	protected GameObject getObject(int id) {
		GameObject g1 = new GameObject();
    	g1.setId(id);
    	int inx = cenaCorrente.getObjetos().indexOf(g1);
    	g1 = cenaCorrente.getObjetos().get(inx);
    	return g1;
	}
	
	protected void ajustaUmObjeto (GameObject go) {

    	if (go.getGameObject() == GameObject.GAMEOBJECT_PLAYER) {
    		bola = go;
    	}
    	else if (go.getId() == ID_MURO) {
    			muro = go;
    	}
    	
    	Body body = go.getB2dBody();
    	
    	if (body == null) {
    		return;
    	}
    		
    		// Posiciona os objetos com alinhamento específico
    		
    		switch(go.getAlinhamento()) {
    		case ALINHAMENTO_CHAO:
    			go.setCentro(
    					new Coordenada(go.getCentro().getX(),
    								   this.telaBottomRight.getY(), 0.0f)
    					);
    			go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), this.telaBottomRight.getY() / proporcaoMetroTela),  
    					go.getB2dBody().getAngle());

    			break;
    		case ALINHAMENTO_TETO:
    			go.setCentro(
    					new Coordenada(go.getCentro().getX(),
    								   this.telaTopLeft.getY(), 0.0f)
    					);
    			go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), this.telaTopLeft.getY() / proporcaoMetroTela),  
    					go.getB2dBody().getAngle());

    			break;
    		case ALINHAMENTO_DIREITA:
    			go.setCentro(
    					new Coordenada(this.telaBottomRight.getX(),
    								   go.getCentro().getY(),0.0f)
    					);
    			go.getB2dBody().setTransform(new Vec2(this.telaBottomRight.getX() / proporcaoMetroTela, go.getCentro().getY()),  
    					0.0f);
    			break;
    		case ALINHAMENTO_ESQUERDA:
    			go.setCentro(
    					new Coordenada(this.telaTopLeft.getX(),
    								   go.getCentro().getY(),0.0f)
    					);
    			go.getB2dBody().setTransform(new Vec2(this.telaTopLeft.getX()  / proporcaoMetroTela, go.getCentro().getY()),  
    					0.0f);
    			break;
    		case ALINHAMENTO_NENHUM:
    				
    				go.setCentro(
        					new Coordenada(go.getCentro().getX(),
        								   go.getCentro().getY(),0.0f)
        					);
        			go.getB2dBody().setTransform(new Vec2(go.getCentro().getX(), go.getCentro().getY()),  
        					0.0f);
        			break;
    		}

    	
    	// Recalcula os tamanhos dos objetos
    	
		if (go.getFixture() != null) {
			try {
				body.destroyFixture(go.getFixture());
			}
			catch (Exception ex) {
				Log.e("PAUSA", "PAUSA Exception destroyfixture: " + ex.getMessage());
			}
		}
		
		float alturaB2D = go.getAltura();
		float larguraB2D = go.getLargura();
		
		if (go.isEsticarAltura()) {
			// Vamos esticar a altura para cobrir a tela toda
			alturaB2D = this.alturaViewPort / proporcaoMetroTela;
		}
		
		if (go.isEsticarLargura()) {
			// Idem com a largura
			larguraB2D = this.larguraViewPort / proporcaoMetroTela;
		}
		
	
		switch(go.getForma()) {
		case FORMA_GO_CIRCULO:
			CircleShape bodyCircleShape = new CircleShape();
			bodyCircleShape.m_radius = (larguraB2D) / 2;      
			FixtureDef bodyFixDef = new FixtureDef();
			bodyFixDef.shape = bodyCircleShape;
			bodyFixDef.friction = go.getAtrito();
			bodyFixDef.density = go.getDensidade();
			bodyFixDef.restitution = go.getCoefRetribuicao();
			go.setFixture(body.createFixture(bodyFixDef));
			body.resetMassData();
			body.setUserData(new Integer(go.getId()));	
			break;
		case FORMA_GO_NENHUM:
		case FORMA_GO_RETANGULO:
			PolygonShape bodyShape = new PolygonShape();
			bodyShape.setAsBox(larguraB2D / 2, alturaB2D / 2);
			FixtureDef bodyFixDef2 = new FixtureDef();
			bodyFixDef2.shape = bodyShape;
			bodyFixDef2.friction = go.getAtrito();
			bodyFixDef2.density = go.getDensidade();
			go.setFixture(body.createFixture(bodyFixDef2));
			body.setUserData(new Integer(go.getId()));
			break;
		}
		
		if (go.getArquivoTextura() != null) {
			// Sempre carrega os objetos no centro e depois desloca
			Coordenada centro = new Coordenada(0.0f, 
					                           0.0f, 
					                           0.0f);
			
			int hVertex = carregarCoordenadasVertice(centro,
					go.getAltura() * proporcaoMetroTela, 
					go.getLargura() * proporcaoMetroTela);
			go.setVobVertices(hVertex);
			if (go.getTipo() == GameObject.TIPO_GO.TIPO_GO_CINEMATICO) {
				
				if (this.cinematicos == null) {
					this.cinematicos = new ArrayList<Integer>();
				}
				int posCin = cenaCorrente.getObjetos().indexOf(go);
				cinematicos.add(new Integer(posCin));
			}

		}
    		
	}

	private void carregarModeloTela() {
		
		mapaCaracteres = new HashMap<Integer, Textura>();
		
    	modeloTela = ModeloTelaLoader.loadXML(context, this.nomeTela,this.alturaViewPort, this.larguraViewPort);

		
		for (PosicaoTela p : modeloTela.getPosicoes()) {
			
			float cTop = (alturaViewPort / 2) - ((p.getTopo() * diagonalTela) / 100);
			float cLeft = - (larguraViewPort / 2) + ((p.getEsquerda() * diagonalTela) / 100);
			float cAltura = (p.getAltura() * diagonalTela) / 100;
			float cLargura = (p.getLargura() * diagonalTela) / 100;
			float metadeAltura = cAltura / 2.0f;
			float metadeLargura = cLargura / 2.0f;

			Coordenada centro = null;

			centro = new Coordenada(cLeft + metadeLargura, 
                    cTop - metadeAltura, 
                    0);
			
			if (p.isCentraHorizontal()) {
				centro.setX(0.0f);
			}
			if (p.isCentraVertical()) {
				centro.setY(0.0f);
			}
			

			
			
			if (((float) alturaViewPort / (float) larguraViewPort) < 0.66f ) {
				// A proporção é menor que 2/3, precisamos verificar os centros
				verificarNovaProporcao(centro, metadeAltura, metadeLargura);
			}
			
			
			p.setScreenTop(centro.getY() + metadeAltura);
	        p.setScreenLeft(centro.getX() - metadeLargura);
	        p.setScreenBottom(centro.getY() - metadeAltura);
	        p.setScreenRight(centro.getX() + metadeLargura);
			
			p.sethVobVertices(carregarCoordenadasVertice(centro,
					cAltura, 
					cLargura));
		}
		
		// Carrega as texturas e obtem os handlers do modelo de tela
		
		for (Textura t : modeloTela.getTexturas()) {
			Bitmap imagem = carregarImagem(t.getImagem());

			// Vamos criar o handler da textura
		    
	        int[] texturas = new int[1];           
	        GLES20.glGenTextures(1, texturas, 0);  
	        t.sethTextura(texturas[0]);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.gethTextura());
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
	                GLES20.GL_LINEAR_MIPMAP_NEAREST);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MAG_FILTER,
	                GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
	                GLES20.GL_REPEAT);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
	                GLES20.GL_REPEAT);
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, imagem, 0);
	        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
	        imagem.recycle();
	        
	        if (t.getAscii() != 0) {
	        	mapaCaracteres.put(t.getAscii(), t);
	        }
	        
	        if (t.getPosicaoFixa() != 0) {
	        	PosicaoTela p = new PosicaoTela();
	        	p.setId(t.getPosicaoFixa());
	        	int ip = modeloTela.getPosicoes().indexOf(p);
	        	p = modeloTela.getPosicoes().get(ip);
	        	t.setPosicaoAtual(p);
	        }
		}
		configurarPropriedadesNivel();
	}
	
	protected void configurarPropriedadesNivel() {
		if (cenaCorrente.getNumero() == 0) {
			int nivel = this.ultimoNivel;
			for (int x = 10; x < 20; x++) {
				PosicaoTela pt = this.getPosicao(x);
				pt.setArquivoModelo("modelonivel" + nivel + ".xml");
				pt.setArquivoTela("tela" + nivel + ".xml");
				pt.setNovoNivel(nivel++);
			}
		}
	}

	protected PosicaoTela getPosicao(int id) {
		PosicaoTela pos = new PosicaoTela();
		pos.setId(id);
		int ix = modeloTela.getPosicoes().indexOf(pos);
		pos = modeloTela.getPosicoes().get(ix);
		return pos;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
Log.d("PAUSA", "PAUSA onSurfaceCreated OpenGLAvancadoRenderer. Pausa = " + this.pausa);
		/*
		 * Temos que fazer algumas coisas:
		 * 1 - Criar um programa combinando um Vertex Shader e um Fragment Shader;
		 * 2 - Criar nossa textura no OpenGL;
		 * 3 - Criar uma matriz de câmera;
		 */

		// Vamos criar o Vertex Shader:
		
		// Isto é por causa do Bug do Android, que chama o "onSurfaceChanged" duplicado. Ele não chama o 
		// "onSurfaceCreated" duas vezes, então movi este código para cá:
		
		if (!this.pausa) {
			this.segundosIniciais = 0;
		}
		
        int iVertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (iVertexShader != 0) {
            GLES20.glShaderSource(iVertexShader, vertexShaderSource);
            GLES20.glCompileShader(iVertexShader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(iVertexShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
            	// Deu erro...
                Log.e(TAG, "Erro ao compilar o Vertex Shader: ");
                Log.e(TAG, GLES20.glGetShaderInfoLog(iVertexShader));
                GLES20.glDeleteShader(iVertexShader);
                iVertexShader = 0;
                return;
            }
        }
        else {
        	Log.e(TAG, "Erro ao criar o Vertex Shader.");
        	return;
        }
        
		// Vamos criar o Fragment Shader:
		
        int iFragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (iFragmentShader != 0) {
            GLES20.glShaderSource(iFragmentShader, fragmentShaderSource);
            GLES20.glCompileShader(iFragmentShader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(iFragmentShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
            	// Deu erro...
                Log.e(TAG, "Erro ao compilar o Fragment Shader: ");
                Log.e(TAG, GLES20.glGetShaderInfoLog(iFragmentShader));
                GLES20.glDeleteShader(iFragmentShader);
                iFragmentShader = 0;
                return;
            }
        }
        else {
        	Log.e(TAG, "Erro ao criar o Fragment Shader.");
        	return;
        }
        
        // Agora, vamos linkeditar os dois em um mesmo programa OpenGL ES 2.0:
        
        programaGLES = GLES20.glCreateProgram();
        if (programaGLES != 0) {
            GLES20.glAttachShader(programaGLES, iVertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(programaGLES, iFragmentShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(programaGLES);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programaGLES, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Não foi possível linkeditar o programa: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(programaGLES));
                GLES20.glDeleteProgram(programaGLES);
                programaGLES = 0;
                return;
            }
        }
        else {
        	Log.e(TAG, "Erro ao criar o Programa.");
        	return;
        }
        
        
        // Vamos apontar variáveis para os atributos de cada Shader que criamos:
        
        maPositionHandle = GLES20.glGetAttribLocation(programaGLES, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("não localizei o atributo aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(programaGLES, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("não localizei o atributo aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(programaGLES, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("não localizei o atributo uMVPMatrix");
        }
        
        maFadeFactor = GLES20.glGetUniformLocation(programaGLES, "fadefactor");
        checkGlError("glGetUniformLocation fadefactor");
        if (maFadeFactor == -1) {
            throw new RuntimeException("não localizei o atributo maFadeFactor");
        }

        carregarCena(this.cena);
       Matrix.setIdentityM(matrizCamera, 0);
		
        
	}
	
    protected void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    
	//  *** Rotinas do Game Loop *** 
	/*
	 * Para maior simplicidade, estamos usando as classes java.util.Timer e
	 * java.util.TimerTask. Além disto, estamos usando renderização passiva.
	 * Se quiser um melhor resultado, veja o capítulo "Framework básico de game".
	 * Este tipo de implementação de Game loop não é muito preciso, servindo apenas
	 * para demonstração do Box2D.
	 */
	public void runGameLoop() {
		simulando = true;

	}
	
	public void toque() {

	}
	
	public void desenharLinhaNivel(int posicaoInicial, int ultimoNivel, int [] linhas, int numNiveis) {
		int pos = posicaoInicial;
		
		for (int ix = 0; ix < 5 && ix < numNiveis; ix++) {
			int posic = ix + ultimoNivel - 1;
			PosicaoTela pTela = new PosicaoTela();
			pTela.setId(posicaoInicial++);
			int ip = modeloTela.getPosicoes().indexOf(pTela);
			pTela = modeloTela.getPosicoes().get(ip);
			
			Textura textura = new Textura();
			textura.setId(linhas[posic]);
			int indx = modeloTela.getTexturas().indexOf(textura);

			textura = modeloTela.getTexturas().get(indx);
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura.gethTextura());
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, pTela.gethVobVertices());
	        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
	        GLES20.glEnableVertexAttribArray(maPositionHandle);
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
	        checkGlError("glEnableVertexAttribArray maPositionHandle");
	        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	        		8, 0);
	        GLES20.glEnableVertexAttribArray(maTextureHandle);
	        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	        GLES20.glDepthMask(false);
	        GLES20.glUniform1f(maFadeFactor, (float) 1.0);
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
	        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void desenharString(String texto, int posicaoInicial) {
		
		int pos = posicaoInicial;
		
		for (int ix = 0; ix < texto.length(); ix++) {
			
			PosicaoTela pTela = new PosicaoTela();
			pTela.setId(posicaoInicial++);
			int ip = modeloTela.getPosicoes().indexOf(pTela);
			
			pTela = modeloTela.getPosicoes().get(ip);

			Textura caracter = mapaCaracteres.get((int)texto.charAt(ix));
			if (caracter == null) {
				continue;
			}
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, caracter.gethTextura());
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, pTela.gethVobVertices());
	        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
	        GLES20.glEnableVertexAttribArray(maPositionHandle);
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
	        checkGlError("glEnableVertexAttribArray maPositionHandle");
	        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	        		8, 0);
	        GLES20.glEnableVertexAttribArray(maTextureHandle);
	        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	        GLES20.glDepthMask(false);
	        GLES20.glUniform1f(maFadeFactor, (float) 1.0);
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
	        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
		

		
	}
	
	protected void desenharCadeado(int posicao, Textura cadeado) {
		PosicaoTela pTela = new PosicaoTela();
		pTela.setId(posicao++);
		int ip = modeloTela.getPosicoes().indexOf(pTela);
		pTela = modeloTela.getPosicoes().get(ip);
	
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cadeado.gethTextura());
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, pTela.gethVobVertices());
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,12, 0);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.hTextureVOB);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
        		8, 0);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(false);
        GLES20.glUniform1f(maFadeFactor, (float) 1.0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrizIntermediaria, 0);
        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, QUANTIDADE_DE_VERTICES);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	protected String converterTempo(Long tempo) {

		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		return sdf.format(tempo);
	}
	
	protected int converterSegundos(Long tempo1) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tempo1);
		int tempo = cal.get(Calendar.MINUTE);
		tempo = tempo * 60 + (cal.get(Calendar.SECOND));
		return tempo;
	}
	
	private float ReadBogoMIPS()
	 {
	  ProcessBuilder cmd;
	  String result="";
	  float bogoMips = 0.0f;
	  
	  try{
	   String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
	   cmd = new ProcessBuilder(args);
	   
	   Process process = cmd.start();
	   InputStream in = process.getInputStream();
	   byte[] re = new byte[1024];
	   while(in.read(re) != -1){
	    result = result + new String(re);
	   }
	   in.close();
	   int pos = result.indexOf("BogoMIPS");
	   if (pos >= 0) {
		   int posIni = result.indexOf(": ", pos);
		   if (posIni >= 0) {
			   int posFim = result.indexOf(".", pos);
			   if (posFim >= 0) {
				   // Achou o final do número
				   bogoMips = Float.parseFloat(result.substring(posIni+2, posFim));
			   }
		   }
	   }
	  } catch(IOException ex){
		  Log.e("BOGOMIPS", "Exception reading CPU Info: " + ex.getMessage());
	  }
	  catch (NumberFormatException nfe) {
		  Log.e("BOGOMIPS", "Invalid BogoMIPS info");
	  }
	  return bogoMips;
	 }
	
}

