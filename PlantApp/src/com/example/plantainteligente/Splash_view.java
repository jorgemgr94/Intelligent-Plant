package com.example.plantainteligente;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;

public class Splash_view extends Activity {
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.splash);
	    new Thread(new Runnable() {
	        public void run() {
		        try {
		        	//LinearLayout rl = (LinearLayout) findViewById(R.id.splash);
		        	//for(int i=0;i<12;i++){
						//rl.setBackgroundResource(R.drawable.aa);
		        		Thread.sleep(3000);
		        	//}
		            finish();
		          } catch (Exception e) {
		            e.getLocalizedMessage();
		          }
	        }
	      }).start();
	  }
}