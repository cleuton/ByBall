package com.obomprogramador.games.byball.share;

import android.content.pm.ActivityInfo;

public class ShareHandler {
	private String nome;
	private ActivityInfo activityInfo;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public ActivityInfo getActivityInfo() {
		return activityInfo;
	}
	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
	}
	@Override
	public boolean equals(Object o) {
		return this.getNome().equals(((ShareHandler) o).getNome());
	}
	@Override
	public int hashCode() {
		return this.getNome().hashCode();
	}
	
	public ShareHandler(String nome, ActivityInfo activityInfo) {
		super();
		this.nome = nome;
		this.activityInfo = activityInfo;
	}
	
	public ShareHandler() {
		super();
	}
	
}
