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

import android.content.Context;
import android.opengl.GLSurfaceView;

public class OpenGLAvancadoView extends GLSurfaceView  {
	
	private OpenGLAvancadoRenderer mRenderer;
	
	public OpenGLAvancadoRenderer getRenderer() {
		return mRenderer;
	}

	public OpenGLAvancadoView(Context context, OpenGLAvancadoRenderer renderer) throws Exception {
		super(context);
        setEGLContextClientVersion(2);
        mRenderer = renderer;
        setRenderer(mRenderer);
	}
	
	public OpenGLAvancadoView(Context context) throws Exception {
		super(context);
        setEGLContextClientVersion(2);
        mRenderer = null;
        setRenderer(mRenderer);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	

}
