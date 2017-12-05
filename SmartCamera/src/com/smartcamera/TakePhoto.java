package com.smartcamera;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.Toast;
import helper.CameraInterface;
import helper.CameraSurfaceView;
import helper.DisplayUtil;
import helper.CameraInterface.CamOpenOverCallback;

public class TakePhoto extends Activity implements CamOpenOverCallback {
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	float previewRate = -1f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread openThread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CameraInterface.getInstance().doOpenCamera(TakePhoto.this,getIntent().getIntExtra("para",1));
			}
		};
		openThread.start();
		setContentView(com.smartcemera.R.layout.activity_take_photo);
		initUI();
		initViewParams();
		
	}



	private void initUI(){
		surfaceView = (CameraSurfaceView)findViewById(com.smartcemera.R.id.camera_surfaceview);
		shutterBtn = (ImageButton)findViewById(com.smartcemera.R.id.btn_shutter);
	}
	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		//手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayUtil.dip2px(this, 80);
		p2.height = DisplayUtil.dip2px(this, 80);;		
		shutterBtn.setLayoutParams(p2);	

	}

	@Override
	public void cameraHasOpened() {
		// TODO Auto-generated method stub
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	
	public void shuttle(View v){
		byte[] picture = CameraInterface.getInstance().returnpicture();
		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra("picture", picture);
		if(picture != null)
			Toast.makeText(this, "byte不为空", Toast.LENGTH_SHORT).show();   	
		else 
			Toast.makeText(this, "byte为空", Toast.LENGTH_SHORT).show();   	

//		startActivity(intent);
	}
	
	
	

}
