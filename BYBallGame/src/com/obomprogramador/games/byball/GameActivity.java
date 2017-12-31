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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.obomprogramador.games.byball.share.ListShareOptions;
import com.obomprogramador.games.openglavancado.OpenGLAvancadoRenderer;
import com.obomprogramador.games.openglavancado.OpenGLAvancadoView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity  implements OnTouchListener {

    public OpenGLAvancadoView mView;
    protected OpenGLAvancadoRenderer renderer;
    protected int nivel = 0;
    protected String arquivoModeloGame = "inicio.xml";
    protected String arquivoModeloTela = "telainicial.xml";
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        printHash();
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	renderer = new Renderer(this.getApplicationContext(), arquivoModeloGame, arquivoModeloTela);
        	((Renderer) renderer).setCena(nivel);
        	((Renderer) renderer).activity = this;
			mView = new OpenGLAvancadoView(getApplication(), renderer);
		} catch (Exception e) {
			Log.d("GAMEMODEL", "Exception: " + e.getMessage());
		}

        mView.setOnTouchListener(this);
        setContentView(mView);
        

    }

    protected void printHash() {
        // Add code to print out the key hash
        try {
        	Log.d("KeyHash:", "KeyHash: Vai obter o keyhash");
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.facebook.samples.hellofacebook", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {
        	Log.d("KeyHash:", "KeyHash: exception namenotfound " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
        	Log.d("KeyHash:", "KeyHash: NoSuchAlgorithmException " + e.getMessage());
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        renderer.pausa = true;
        renderer.segundosIniciais = (int) ((Renderer) renderer).segundosDeJogo;
        mView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();

    }
    
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
	  newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
      super.onConfigurationChanged(newConfig);
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		((Renderer) mView.getRenderer()).toque(arg1);
		return true;
	}
	
	public void sair() {
		this.finish();
	}
	
	public void compartilhar(String assunto, String texto) {
		Resources res = getResources();
		Intent sendIntent =   
				new Intent(this.getApplicationContext(),ListShareOptions.class);  
		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sendIntent.putExtra("assunto",  assunto);  
		sendIntent.putExtra("texto",    texto);
		sendIntent.setType("text/plain");  
		startActivity(  
           sendIntent);  
		

	}

}
