package com.example.plantainteligente;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.util.Log;

public class TabListener extends Activity implements ActionBar.TabListener {

	private Fragment fragment;
	public TabListener(Fragment fg,Context context)
	{
		this.fragment = fg;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.i("ActionBar", tab.getText() + " reseleccionada.");
	}
	static MainActivity mn = new MainActivity();
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.i("ActionBar", tab.getText() + " seleccionada.");
		Log.i("--->", tab.getPosition() + " reseleccionada.");
		MainActivity.tab_active=tab.getPosition()+1;
		ft.replace(R.id.contenedor, fragment);
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.i("ActionBar", tab.getText() + " deseleccionada.");
		ft.remove(fragment);
	}
}
