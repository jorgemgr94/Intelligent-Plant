package com.example.plantainteligente;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity{
	public static Context context;
	public static BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();
	public static Menu menu_instance;
	ActionBar abar;
	ActionBar.Tab tab2,tab1;
	Handler h;
	final int RECIEVE_MESSAGE = 1; 
	private ConnectedThread mConnectedThread;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "20:13:11:01:11:48";
	static ActionBar.Tab currentTab;
	public static final String PREFS_NAME = "myprefs";
	boolean autoriego = false;
	static Boolean bluetooth_on= true;
	static Boolean bluetooth_off = false;
	public static boolean arduino_on=false;
	Builder mBuilder;
	NotificationManager mNotifyManager;
	static SharedPreferences settings;
	static int tab_active=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent Splasintent = new Intent().setClass(this, Splash_view.class);
        startActivity(Splasintent);
        
		super.onCreate(savedInstanceState);

		settings= getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		//Obtenemos una referencia a la actionbar
		abar = getActionBar();
		//Establecemos el modo de navegaci�n por pesta�as
	    abar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    
	    
	    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    isBluetoothAvailable();
	    
	    tab1 = abar.newTab().setText("Conectar");
	    tab2 = abar.newTab().setText("Estatus");
        ActionBar.Tab tab3 = abar.newTab().setText("Configuración");
        
        tab1.setTabListener(new TabListener(new Home(),context));
        tab2.setTabListener(new TabListener(new Regar(),context));
        //Config.class, null);
        tab3.setTabListener(new TabListener(new Configuracion(),context));
        abar.addTab(tab1);
        abar.addTab(tab2);
        abar.addTab(tab3);
	}
	
	
	public void guardarCambios(View v){
		boolean a = MainActivity.settings.getBoolean("autoriego", false); 
	    boolean b = MainActivity.settings.getBoolean("notificaciones", false); 
 		String msg1 = (a)?"b":"c";
 		String msg2=(b)?"d":"e";
 		mConnectedThread.write(msg1); 
 		mConnectedThread.write(msg2); 
 		boolean auto = Configuracion.isChecked_a();
	   	boolean notif = Configuracion.isChecked_b();
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("autoriego", auto);
	    editor.putBoolean("notificaciones", notif);
	    editor.commit();	
	    Toast.makeText(getBaseContext(), "Cambios guardados", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu_instance=menu;
		menu.findItem(R.id.enabled_bluetooth).setVisible(bluetooth_on);
		menu.findItem(R.id.disabled_bluetooth).setVisible(bluetooth_off);
		menu.findItem(R.id.ic_arduino).setVisible(arduino_on);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void conectar_arduino(View v){
		
		if(!arduino_on){
			if(isBluetoothAvailable()){
				//Toast_length("Conectando..");
				loading_show_hide(1);
				RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_home);
				rl.setBackgroundResource(R.drawable.fondonoche);
				ImageButton imb = (ImageButton) findViewById(R.id.imageButton1);
				imb.setEnabled(false);
				imb.setImageAlpha(100);
				new Thread(){
			        public void run(){
			        	// Variable que apunta al arduino
						BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
					    
					    //Se logra la conexión con el dispositivo antes definido 
					    try {
					        btSocket = createBluetoothSocket(device);
					    } catch (IOException e) {
					        Log.d("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
					    }
					    
					    // Discovery is resource intensive.  Make sure it isn't going on
					    // when you attempt to connect and pass your message.
					    bluetoothAdapter.cancelDiscovery();
					    
					    // Establish the connection.  This will block until it connects.    
					    try {
					      btSocket.connect();
					      //Aqui hago aparecer el icono :)
					      //Aqui determino que no se conecto con arduino o no esta disponible
					    	runOnUiThread(new Runnable() {
					    	     @Override
					    	     public void run(){
					    	    	arduino_on=true;
					    	 		menu_instance.findItem(R.id.ic_arduino).setVisible(arduino_on);
					    	 		loading_show_hide(0);
					    	 		abar.selectTab(tab2);
					    	 		boolean a = MainActivity.settings.getBoolean("autoriego", false); 
						   	    	boolean b = MainActivity.settings.getBoolean("notificaciones", false); 
					    	 		String msg1 = (a)?"b":"c";
					    	 		String msg2=(b)?"d":"e";
					    	 		mConnectedThread.write(msg1); 
					    	 		mConnectedThread.write(msg2); 
					    	    }
					    	});
					    } catch (IOException e) {
					    	runOnUiThread(new Runnable() {
					    	     @Override
					    	     public void run() {
					    	    	ImageButton imb = (ImageButton) findViewById(R.id.imageButton1);
							    	imb.setEnabled(true);
									imb.setImageAlpha(500);
									loading_show_hide(0);
									Toast_length("No se pudo conectar con Arduino");
					    	    }
					    	});
					    	Log.d("Error", "NO se conecto :( ?" +e.getLocalizedMessage());
					      try {
					        btSocket.close();
					      } catch (IOException e2) {
					        Log.d("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
					      }
					    }
					    // Create a data stream so we can talk to server.        
					    mConnectedThread = new ConnectedThread(btSocket);
					    mConnectedThread.start();
			        }
				}.start();
				//y hacer escuchar al arduino 
			    escucha_arduino();
			}else{
				Toast_short("Active su bluetooth primero");
			}
		}else{
			Toast_short("Ya hay un arduino conectado");
		}
	}
	
	public void regar(View v){
		if(arduino_on){
			ImageButton imb = (ImageButton) findViewById(R.id.btnRegar);
			imb.setImageAlpha(100);
			imb.setEnabled(false);
			mConnectedThread.write("a"); 
		}
	}
	
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
	      if(Build.VERSION.SDK_INT >= 10){
	          try {
	              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	              return (BluetoothSocket) m.invoke(device, MY_UUID);
	          } catch (Exception e) {
	              Log.e("-->", "Could not create Insecure RFComm Connection",e);
	          }
	      }
	      return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.enabled_bluetooth){
			Toast_short("Bluetooth Desactivado");
			item.setVisible(false);
			bluetoothAdapter.disable();
			menu_instance.findItem(R.id.disabled_bluetooth).setVisible(true);
			return true;
		}else if (id == R.id.disabled_bluetooth){
			Toast_short("Bluetooth Activado");
			enableBluetooth(false);
			item.setVisible(false);
			bluetoothAdapter.enable();
			menu_instance.findItem(R.id.enabled_bluetooth).setVisible(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint({ "NewApi", "HandlerLeak" })
	public void escucha_arduino(){
		//Se leen los datos que envía el arduino
	     h = new Handler() {
	    	 public void handleMessage(android.os.Message msg) {
	    		 
	    		 switch (msg.what){
	             case RECIEVE_MESSAGE:
	                 byte[] readBuf = (byte[]) msg.obj;
	                 String strIncom = new String(readBuf, 0, msg.arg1);  
	                 sb.append(strIncom);
	                 int endOfLineIndex = sb.indexOf("\r\n");   
	                 if (endOfLineIndex > 0) {  
	                	 //mensajs del arudino
	                	 Log.d("Llega: ", strIncom);
	                	 
	                	 if(strIncom.indexOf("h")!=-1){
	                		Log.d("Enabled denuevo :D ","lkjlkjkj");
	                		ImageButton imb=(ImageButton)findViewById(R.id.btnRegar);
	 			   	    	imb.setEnabled(true);
	 			   			imb.setImageAlpha(500);
		 			   		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		 					mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 					mBuilder = new Notification.Builder(context);
		 					mBuilder.setContentTitle("Gracias por regarme :D")
		 					    .setSmallIcon(R.drawable.phone_white).setSound(soundUri);
		 					mNotifyManager.notify(1, mBuilder.build());
	                	 }else if(strIncom.indexOf("f")!=-1){
	                		 Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			 					mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			 					mBuilder = new Notification.Builder(context);
			 					mBuilder.setContentTitle("Me falta agua :(")
			 					    .setSmallIcon(R.drawable.phone_white).setSound(soundUri);
			 					mNotifyManager.notify(2, mBuilder.build());
	                	 }else{
	                		 String[] values = strIncom.split("-");
		                	 Log.d("Lenght",""+values.length+"");
		                	 if(values.length==3){
			                	 Log.d("Valor[0]: ",values[0]);
			                	 Log.d("Valor[1]: ",values[1]);
			                	 Log.d("Valor[2]: ",values[2]);
			                	 
			                	 ///0-20 me fala agua
			                	  //20-60 regular
			                	  //+60 humedad satistactoria
			                	 int h;
			                	 int l;
			                	 try{h = Integer.parseInt(values[0]);}
			                	 catch(Exception e){h=0;}
			                	 try{l = Math.abs((Integer.parseInt(values[1])*10/100));}
			                	 catch(Exception e){l=0;}
			                	 
			                	 if(tab_active==2){
				                	 String humedad;
				                	 ImageView im = (ImageView)findViewById(R.id.imageView1);
				                	 if(h<20){
				                		 humedad="Me falta agua";
					                	 im.setImageResource(R.drawable.minagua);
				                	 }else if(h>20 && h<60){
				                		 humedad="Me siento bien";
					                	 im.setImageResource(R.drawable.medagua);
				                	 }else{
				                		 humedad="Suficiente agua";
					                	 im.setImageResource(R.drawable.maxagua);
				                	 }
				                	 
				                	 RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_regar);
				                	 if(l<50){
					                	 rl.setBackgroundResource(R.drawable.fondonoche);
				                	 }else{
					                	 rl.setBackgroundResource(R.drawable.fondodia);
				                	 }
				                	 
				                	 Regar.setText(humedad+"("+h+")");
				                	 Regar.setText2(l+"%");
				                	 Regar.setText3(Math.abs((Float.parseFloat(values[2])))+" °C");
			                	 }
			                } 
	                	 }
	                 }
	                 break;
	             }
	         };
	     };
	}
	
	
	
	public void Toast_short(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	public void Toast_length(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
	public static void enableBluetooth(Boolean a){
		if(a){
			bluetooth_on=true;	bluetooth_off=false;
		}else{
			bluetooth_on=false;	bluetooth_off=true;
		}
	}
	
	public static boolean isBluetoothAvailable(){
       if (bluetoothAdapter.isEnabled()) {
         enableBluetooth(true);
         return true;
       }else {
    	 enableBluetooth(false);
    	 return false;
       }
	}
	
	private class ConnectedThread extends Thread {
	       private final InputStream mmInStream;
	       private final OutputStream mmOutStream;
	     
	       public ConnectedThread(BluetoothSocket socket) {
	           InputStream tmpIn = null;
	           OutputStream tmpOut = null;
	     
	           // Get the input and output streams, using temp objects because
	           // member streams are final
	           try {
	               tmpIn = socket.getInputStream();
	               tmpOut = socket.getOutputStream();
	           } catch (IOException e) { 
	        	   Log.d("Se fue","se fue :c");
	           }
	     
	           mmInStream = tmpIn;
	           mmOutStream = tmpOut;
	       }
	     
	       public void run() {
	           byte[] buffer = new byte[256];  // buffer store for the stream
	           int bytes; // bytes returned from read()
	           // Keep listening to the InputStream until an exception occurs
	           while (true) {
	        	   try {
	                   // Read from the InputStream
	                   bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
	                   h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
	               } catch (IOException e) {
	            	   //Aqui escondo el icono del arduino porque es cuando y ano hay conexion
	            	   			//Talves un reset a los textos de regar
	            	   runOnUiThread(new Runnable() {
				    	     @Override
				    	     public void run(){    	
				    	    	arduino_on=false;
				    	 		menu_instance.findItem(R.id.ic_arduino).setVisible(arduino_on);
				    	    	abar.selectTab(tab1);
				    	     }
	            	   });
	            	   Log.d("ya no","Ya no leo");
	                   break;
	               }
	           }
	       }
	     
	       /* Call this from the main activity to send data to the remote device */
	       public void write(String message) {
	           Log.d("", "...Data to send: " + message + "...");
	           byte[] msgBuffer = message.getBytes();
	           try {
	               mmOutStream.write(msgBuffer);
	           } catch (IOException e) {
	               Log.d("", "...Error data send: " + e.getMessage() + "...");     
	           }
	       }
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("lklkjklj","jkjlkj");
	};
	
	@SuppressLint("NewApi")
	public void disable_connect() {
		   	    	ImageButton imb = (ImageButton) findViewById(R.id.imageButton1);
					imb.setEnabled(false);
					imb.setImageAlpha(100);
					Toast_length("Ya hay un Arduino conectado");
	}


	ProgressDialog dialog;
	private void loading_show_hide(int i){
		int show = i;
		if(dialog == null){
			//dialog = new ProgressDialog(this,R.style.AppTheme);
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
			dialog.setMessage("Conectando..");
	    	dialog.setCanceledOnTouchOutside(false);
		}
		if(!(dialog == null)){
			if(show==1){
				if(!dialog.isShowing())
					dialog.show();				
			}else
				if(dialog.isShowing())
					dialog.dismiss();
		}
		return;
    }	
}