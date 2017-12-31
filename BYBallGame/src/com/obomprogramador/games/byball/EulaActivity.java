package com.obomprogramador.games.byball;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.obomprogramador.games.byball.recs.Records;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class EulaActivity extends Activity {
	
	private CheckBox cbkConcordo;
	private boolean firstTime = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.hasSeenEula()) {
			firstTime = false;
			this.prosseguir(null);
			return;
		}
		
		setContentView(R.layout.eula);
		cbkConcordo = (CheckBox) this.findViewById(R.id.cbkconcordo);
	}

	public void prosseguir(View view) {
		if (this.firstTime) {
			if (this.cbkConcordo.isChecked()) {
				this.writeEulaAgreement();
			}
			else {
				this.finish();
				return;
			}
		}
		Intent i = new Intent(this.getApplicationContext(), GameActivity.class);
		this.startActivity(i);
		this.finish();
	}
	
	
	private boolean hasSeenEula() {
		boolean hasSeen = false;
		
		String nome = "agreedeula.txt";
		try {
			FileInputStream arquivo = this.getApplicationContext().openFileInput(nome);
			DataInputStream dis = new DataInputStream(arquivo);
			int readEula = dis.readInt();
			dis.close();
			arquivo.close();
			hasSeen = true;
		}
		catch (FileNotFoundException fnf) {
		} 
		catch (IOException e) {
		}
		
		return hasSeen;

	}
	
	private void writeEulaAgreement() {
		try {
			String nome = "agreedeula.txt";
			FileOutputStream saida = this.getApplicationContext().openFileOutput(nome, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(saida);
			dos.writeInt(1);
			dos.close();
			saida.close();
		} 
		catch (FileNotFoundException e) {
			Log.e("RECORDS", "Error creating EULA agreement: " + e.getMessage());
		} 
		catch (IOException e) {
			Log.e("RECORDS", "Exception writing EULA agreement: " + e.getMessage());
		}

	}
}
