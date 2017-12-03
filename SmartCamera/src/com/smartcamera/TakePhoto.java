package com.smartcamera;

import com.smartcemera.R;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class TakePhoto extends Activity{
	CameraView cameraView;
	SurfaceHolder surfaceHolder;
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView2);
		surfaceHolder = surfaceView.getHolder();
		Camera c = Camera.open(1);
		cameraView = new CameraView(this,c);
		cameraView.surfaceCreated(surfaceHolder);
		Toast.makeText(this, "成功调用相机", Toast.LENGTH_SHORT).show();   	
		
	}
	
	
	public void camera(View v){

	}
}
