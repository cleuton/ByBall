package com.obomprogramador.games.byball.recs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

public class Records {
	private static long [] niveis;
	private static Context context;
	public static int MAXLEVELS = 30;
	protected Records() {
	}
	
	public static long[] getNiveis(Context context) {
		
		Records.context = context;
		if (Records.niveis == null) {
			Records.niveis = loadLevels();
		}
		return Records.niveis;
		
		
	}

	private static long[] loadLevels() {
		long [] levels = new long [Records.MAXLEVELS];
		
		String nome = "records.txt";
		int readPosition = 0;
		try {
			FileInputStream arquivo = Records.context.openFileInput(nome);
			DataInputStream dis = new DataInputStream(arquivo);
			for (int x=0; x < Records.MAXLEVELS; x++) {
				readPosition = x;
				levels[x] = dis.readLong();
			}
			dis.close();
			arquivo.close();
		}
		catch (FileNotFoundException fnf) {
			try {
				FileOutputStream saida = Records.context.openFileOutput(nome, Context.MODE_PRIVATE);
				DataOutputStream dos = new DataOutputStream(saida);
				for (int x=0; x < Records.MAXLEVELS; x++) {
					
					// ************** TEMPORÁRIO
					
					//if (x < 3) {
					//	levels[x] = 12;
					//}
					
					// ************** FIM TEMPORÁRIO - RETIRAR BLOCO
					
					dos.writeLong(levels[x]);
				}
				dos.close();
				saida.close();
			} 
			catch (FileNotFoundException e) {
				Log.e("RECORDS", "Error creating new recors: " + e.getMessage());
			} 
			catch (IOException e) {
				Log.e("RECORDS", "Exception writing records: " + e.getMessage());
			}
		} 
		catch (EOFException eof) {
			Log.d("EOF","Leitura atingiu EOF no ponto: " + readPosition);
		}
		catch (IOException e) {
			Log.e("RECORDS", "Exception reading records: " + e.getMessage());
		}
		return levels;
	}
	
	public static void updateLevels(long [] niveis) {
		
		try {
			context.deleteFile("records.txt");
			FileOutputStream saida = Records.context.openFileOutput("records.txt", Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(saida);
			for (int x=0; x < niveis.length; x++) {
				dos.writeLong(niveis[x]);
			}
			dos.close();
			saida.close();
		} 
		catch (FileNotFoundException e) {
			Log.e("RECORDS", "Error creating new recors: " + e.getMessage());
		} 
		catch (IOException e) {
			Log.e("RECORDS", "Exception writing records: " + e.getMessage());
		}
		
		
	}
}
