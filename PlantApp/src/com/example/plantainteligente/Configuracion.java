package com.example.plantainteligente;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

public class Configuracion extends Fragment {
	static View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		Log.d("Me ejecute por morro :D","Ejecute");
	    
		
		
		new Thread(){
	        public void run(){
	        	getActivity().runOnUiThread(new Runnable() {
		   	     @Override
		   	     public void run(){
		   	    	boolean a = MainActivity.settings.getBoolean("autoriego", false); 
		   	    	boolean b = MainActivity.settings.getBoolean("notificaciones", false); 
		   	    	Switch swAutoriego = (Switch) view.findViewById(R.id.swAutoriego);
		   	    	Switch swNotif = (Switch) view.findViewById(R.id.snotif_aut);
		   	    	swAutoriego.setChecked(a);
		   	    	swNotif.setChecked(b);
		   	     }
	        	});
	        }
		}.start();
	}

	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                  Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.activity_config, container, false);		
	           return view;
	 }
	 
	 public static boolean isChecked_a(){
		 Switch swAutoriego = (Switch) view.findViewById(R.id.swAutoriego);
		 boolean check = swAutoriego.isChecked();
		 return check;
	 }
	 public static boolean isChecked_b(){
		 Switch swAutoriego = (Switch) view.findViewById(R.id.snotif_aut);
		 boolean check = swAutoriego.isChecked();
		 return check;
	 }
}