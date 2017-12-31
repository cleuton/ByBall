package com.obomprogramador.games.byball.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.*;
import com.facebook.model.*;

import com.obomprogramador.games.byball.R;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListShareOptions extends ListActivity {
	
	private List<ShareHandler> shareOptions;
	private String assunto;
	private String texto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle options = this.getIntent().getExtras();
		this.assunto = options.getString("assunto");
		this.texto = options.getString("texto");
		getListOfShareOptions();
		ShareArrayAdapter adapter = new ShareArrayAdapter(this.getApplicationContext(),
		        R.layout.entrada_lista_recursos, shareOptions);
		this.setListAdapter(adapter);
		this.setContentView(R.layout.listshareoptions);
		
	}

	private void getListOfShareOptions() {
		shareOptions = new ArrayList<ShareHandler>();
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,  
				this.texto);  
		shareIntent.putExtra(Intent.EXTRA_SUBJECT,  
				this.assunto);
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		for (final ResolveInfo app : activityList) {
			ShareHandler shareHandler = new ShareHandler(app.activityInfo.applicationInfo.loadLabel(pm).toString(), 
					app.activityInfo);
			this.shareOptions.add(shareHandler);
		}
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
	  newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
      super.onConfigurationChanged(newConfig);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String nome = this.shareOptions.get(position).getNome();
		if (!this.isNetworkAvailable()) {
			this.showMsg(String.format(this.getResources().getString(R.string.postnonetwork),nome));
		}
		else {
			try {
				if (nome.toLowerCase().contains("facebook")) {
					shareOnFacebook();
					this.showMsg(this.getResources().getString(R.string.postfacebook));
				}
				else {
					shareUsingIntent(shareOptions.get(position));
					this.showMsg(String.format(this.getResources().getString(R.string.postothers),nome));
				}
			}
			catch (Exception ex) {
				this.showMsg(String.format(this.getResources().getString(R.string.posterror),nome));
			}			
		}
	}

	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	 private void shareUsingIntent(ShareHandler shareHandler) {
		 Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		 shareIntent.setType("text/plain");
		 shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, this.getResources().getString(R.string.posturl)
				 + " " + texto);
		 shareIntent.putExtra(Intent.EXTRA_SUBJECT,            assunto);		 
		 
		 PackageManager pm = this.getPackageManager();
		 List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
	     final ActivityInfo activity = shareHandler.getActivityInfo();
		 final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		 shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		 shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 shareIntent.setComponent(name);
		 this.startActivity(shareIntent);
		
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	        	if (session.isOpened()) {
					publicar(session, state);
				}
	        }
	    };
	
	private void shareOnFacebook() {
		
		// Debug key: 1KeKtava9zwZJ9sFKJ253o9Bn2k=
		// MGK_keystore (bolanoquintal): l3uvBtrGktCkiRlL3ilNEhHwFrA=
		Session.openActiveSession(this, true, callback);
		
	}
	
	protected void publicar(Session session, SessionState state) {
		List<String> PERMISSIONS = Arrays.asList("publish_actions");
		String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
		boolean pendingPublishReauthorization = false;
		List<String> permissions = session.getPermissions();
		if (!isSubsetOf(PERMISSIONS,permissions)) {
			pendingPublishReauthorization = true;
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
			session.requestNewPublishPermissions(newPermissionsRequest);
			return;
		}
		Bundle postParams = new Bundle();
		postParams.putString("name", this.texto);
		postParams.putString("caption", this.texto);
		postParams.putString("description", this.texto);
		postParams.putString("link", "http://indiegamerbrasil.com");
		
		Request.Callback rcallback = new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
				String postId = null;
				try {
					postId = graphResponse.getString("id");
				}
				catch(JSONException jex) {
					
				}
			}
		};
		
		Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, rcallback);
		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
		
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	protected void showMsg(String msg) {
		Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
}
