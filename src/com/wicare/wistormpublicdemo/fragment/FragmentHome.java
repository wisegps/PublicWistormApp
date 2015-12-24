package com.wicare.wistormpublicdemo.fragment;

import com.wicare.wistormpublicdemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Wu
 *
 */
public class FragmentHome extends Fragment{
	
	static final String TAG = "FragmentHome";
	
	
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.home, container, false);  
        return view;  
    }  
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "---home-->onResume()");
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "---home-->onDestroy()");
	}
	

}
