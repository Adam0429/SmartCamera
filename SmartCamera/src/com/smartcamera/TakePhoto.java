package com.smartcamera;

import javax.net.ssl.SSLException;

import org.apache.http.entity.SerializableEntity;

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

public class TakePhoto extends Activity implements CamOpenOverCallback,PreviewCallback{
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
		Thread photoThread = new Thread(){
			public void run() {
				while(true){
					CameraInterface.getInstance().doTakePicture();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		photoThread.start();
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
//		cameraInterface.doTakePicture();
//		byte[] picture = cameraInterface.getpicture();
//		Intent intent = new Intent(this,MainActivity.class);
//		if(picture != null)
//			Toast.makeText(this, "byte不为空", Toast.LENGTH_SHORT).show();   	
//		else 
//			Toast.makeText(this, "byte为空", Toast.LENGTH_SHORT).show();   	
//		intent.putExtra("picture", picture);

		CameraInterface.getInstance().doTakePicture();
		int path = FileUtil.DirNumeber(FileUtil.initPath());	
		Intent intent = new Intent(this,MainActivity.class);			
		intent.putExtra("path", Integer.toString(path));	
		intent.putExtra("camera", Integer.toString(ca));	
		try {
			Thread.sleep(1100);//之前立即就startactivity,会报错,猜测是因为存图还需要时间,所以那里一直不能获得,所以让他睡一会儿,300ms不够还可以再加
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//延迟3s，时间自己定
//		else 
//		Toast.makeText(this, Integer.toString(path), Toast.LENGTH_SHORT).show();   
		startActivity(intent);			
		}



	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
//		CameraInterface.getInstance().mCamera.setOneShotPreviewCallback(TakePhoto.this);
	}



	
	
	
	

}
