package com.obomprogramador.games.modelotela;

import com.obomprogramador.games.byball.R;

import android.content.Context;

public class MediaNiveisSingleton {

	private static MediaNiveisSingleton me;
	private int [] medias;
	
	protected MediaNiveisSingleton() {
		super();
	}
	
	public static MediaNiveisSingleton getMediaSingleton(Context context) {
		if (me == null) {
			me = new MediaNiveisSingleton();
			me.medias = context.getResources().getIntArray(R.array.medias);
		}
		return me;
	}
	
	public int [] getMedias() {
		return this.medias;
	}
	
}
