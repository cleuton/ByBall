package com.obomprogramador.games.byball.share;

import java.util.List;

import com.obomprogramador.games.byball.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareArrayAdapter extends ArrayAdapter<ShareHandler> {

	private List<ShareHandler> listaShareHandlers;
	private Context context;
	
	public ShareArrayAdapter(Context context, int textViewResourceId,
			List<ShareHandler> objects) {
		super(context, textViewResourceId, objects);
		this.listaShareHandlers = objects;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.listaShareHandlers.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		PackageManager pm = context.getPackageManager();
		
	    if (v == null) {
	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.entrada_lista_recursos, null);
	    }

	    ShareHandler p = this.listaShareHandlers.get(position);

	    if (p != null) {

	        ImageView list_image = (ImageView) v.findViewById(R.id.ivfoto);
	        TextView list_title = (TextView) v.findViewById(R.id.tvnome);

	        if (list_title != null) {
	            list_title.setText(
	            		p.getActivityInfo().applicationInfo.loadLabel(pm).toString()
	            		);
	        }

	        if (list_image != null) {
	        	list_image.setImageDrawable(p.getActivityInfo().applicationInfo.loadIcon(pm));
	        }
	    }		
		
		return v;
	}


	

}
