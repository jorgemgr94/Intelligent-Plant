package com.example.plantainteligente;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Regar extends Fragment{
static View view;
	
	static Activity activity;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Hola","Cuando cambias de tab se ejecuta esto");
		if(!MainActivity.arduino_on){
			new Thread(){
		        public void run(){
		        	getActivity().runOnUiThread(new Runnable() {
			   	     @Override
			   	     public void run() {
			   	    	ImageButton imb = (ImageButton) getActivity().findViewById(R.id.btnRegar);
			   	    	imb.setEnabled(false);
			   			imb.setImageAlpha(100);
			   	     }
		        	});
		        }
			}.start();
		}else{
			new Thread(){
		        public void run(){
		        	getActivity().runOnUiThread(new Runnable() {
			   	     @Override
			   	     public void run() {
			   	    	ImageButton imb = (ImageButton) getActivity().findViewById(R.id.btnRegar);
			   	    	imb.setEnabled(true);
			   			imb.setImageAlpha(500);
			   	     }
		        	});
		        }
			}.start();
		}
	}
		
	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                  Bundle savedInstanceState) {	
		 	if(view == null){
				 view = inflater.inflate(R.layout.activity_regar, container, false);
			}

		 	ViewGroup parent = (ViewGroup) view.getParent();
			
		 	if (parent != null){
				parent.removeView(view);
			}
			return view;
	 }
	 
	 public static void setText(String text){
         TextView textView = (TextView) view.findViewById(R.id.txtHumedad);
         textView.setText(text);
     }
	 
	 public static void setText2(String text){
         TextView textView = (TextView) view.findViewById(R.id.txtLuminosidad);
         textView.setText(text);         
     }
	 
	 public static void setText3(String text){
         TextView textView = (TextView) view.findViewById(R.id.txtTemperatura);
         textView.setText(text);         
     }
}