package com.smartcamera;

import android.R;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.util.Log;
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
import helper.FileUtil;
import helper.CameraInterface.CamOpenOverCallback;

public class TakePhoto extends Activity implements CamOpenOverCallback,PreviewCallback {
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	CameraInterface cameraInterface;
	ImageButton shutterBtn;
	float previewRate = -1f;
	int ca;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ca = getIntent().getIntExtra("para",1);
		Thread openThread = new Thread(){
		public void run() {
			CameraInterface.getInstance().doOpenCamera(TakePhoto.this,ca);
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
		previewRate = DisplayUtil.getScreenRate(this); //Ĭ��ȫ���ı���Ԥ��
		surfaceView.setLayoutParams(params);

		//�ֶ���������ImageButton�Ĵ�СΪ120dip��120dip,ԭͼƬ��С��64��64
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
//		cameraInterface.doTakePicture();
//		byte[] picture = cameraInterface.getpicture();
//		Intent intent = new Intent(this,MainActivity.class);
//		if(picture != null)
//			Toast.makeText(this, "byte��Ϊ��", Toast.LENGTH_SHORT).show();   	
//		else 
//			Toast.makeText(this, "byteΪ��", Toast.LENGTH_SHORT).show();   	
//		intent.putExtra("picture", picture);

		CameraInterface.getInstance().doTakePicture();
		int path = FileUtil.DirNumeber(FileUtil.initPath());	
		Intent intent = new Intent(this,MainActivity.class);			
		intent.putExtra("path", Integer.toString(path));	
		intent.putExtra("camera", Integer.toString(ca));	
		try {
			Thread.sleep(1100);//֮ǰ������startactivity,�ᱨ��,�²�����Ϊ��ͼ����Ҫʱ��,��������һֱ���ܻ��,��������˯һ���,300ms�����������ټ�
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//�ӳ�3s��ʱ���Լ���
//		else 
//		Toast.makeText(this, Integer.toString(path), Toast.LENGTH_SHORT).show();   
		startActivity(intent);			
		}



	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
//		CameraInterface.getInstance().mCamera.setOneShotPreviewCallback(TakePhoto.this);
	}
	
	
	

}
