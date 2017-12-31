package com.obomprogramador.games.byball;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ScrollView;

public class AjudaActivity extends Activity {

	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ajuda);
        webView = (WebView) this.findViewById(R.id.wvajuda);
        webView.loadData(this.getResources().getString(R.string.textoajuda), "text/html", "utf-8");
        ScrollView sv = (ScrollView) this.findViewById(R.id.scrollajuda);
        //sv.setBackgroundColor(0x90000000);
        //webView.setBackgroundColor(0x00000000);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
	  newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
      super.onConfigurationChanged(newConfig);
    }
	
	public void sair (View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	

}
