package com.smartcamera;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import api.analyze;
import api.detect;
import helper.CameraInterface;
import helper.CameraInterface.CamOpenOverCallback;
import helper.CameraSurfaceView;
import helper.DataHelper;
import helper.DisplayHelper;
import helper.FileHelper;
import helper.ImageHelper;

public class TakePhoto extends Activity implements CamOpenOverCallback,PreviewCallback{//�̳���callback�ӿ�,˵�������ᱻ����callback�ӿ���ķ���//use callback interface
	private static final String TAG = "takephoto";
	CameraSurfaceView surfaceView = null;
	CameraInterface cameraInterface;
	ImageButton shutterBtn;
	float previewRate = -1f;
	String mode;
	int cameramode;
	boolean check = true;		//���������̵߳�,�ҷ������߳�interrupt���ܴﵽ�ҵ�Ŀ��//use to control thread
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mode = getIntent().getStringExtra("mode");
		Log.i("mode", mode);
		cameramode = getIntent().getIntExtra("para",1);
		Thread openThread = new Thread(){//�������ԣ�����ִ��Camera.open������仰һ����Ҫ140ms���ҡ�����������߳���������һ���˷�
		public void run() {
			CameraInterface.getInstance().doOpenCamera(TakePhoto.this,cameramode);
			}
		};
		openThread.start();

		setContentView(com.smartcemera.R.layout.activity_take_photo);
		initUI();
		initViewParams();
		
		Thread photoThread = new Thread(){
			public void run() {
				while(check){		//normal���Լ���
					if(CameraInterface.getInstance().isPreviewing){					
						CameraInterface.getInstance().mCamera.setOneShotPreviewCallback(TakePhoto.this);
					}
					else 
						Log.i("cam", "not previewing");
//					//ʹ�ô˷���ע��Ԥ���ص��ӿ�ʱ���Ὣ��һ֡���ݻص���onPreviewFrame()������������ɺ�����ص��ӿڽ������١�Ҳ����ֻ��ص�һ��Ԥ��֡���ݡ� 
					try{	
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}}
//				}
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
		Point p = DisplayHelper.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = DisplayHelper.getScreenRate(this); //Ĭ��ȫ���ı���Ԥ��
		surfaceView.setLayoutParams(params);

		//�ֶ���������ImageButton�Ĵ�СΪ120dip��120dip,ԭͼƬ��С��64��64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayHelper.dip2px(this, 80);
		p2.height = DisplayHelper.dip2px(this, 80);;		
		shutterBtn.setLayoutParams(p2);	

	}

	public void cameraHasOpened() {
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	
	public void shuttle(View v){
		check = false;		//ֹͣԤ���߳�
		CameraInterface.getInstance().doTakePicture();
		int path = FileHelper.DirNumeber(FileHelper.initPath());	
		Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		intent.putExtra("path", Integer.toString(path));	
		intent.putExtra("camera", Integer.toString(cameramode));	
		try {
			Thread.sleep(300);//֮ǰ������startactivity,�ᱨ��,�²�����Ϊ��ͼ����Ҫʱ��,��������һֱ���ܻ��,��������˯һ���,300ms�����������ټ�
		} catch (InterruptedException e) {
			e.printStackTrace();
		}//�ӳ�3s��ʱ���Լ���
		startActivity(intent);			
		}



	public void onPreviewFrame(byte[] data, Camera arg1) {//����������data����ʵʱԤ��֡��Ƶ��һ���������PreviewCallback�ӿڣ��ͻ��Զ�����onPreviewFrame���������
		//���Activity�̳���PreviewCallback����ӿڣ�ֻ��̳�Camera.setOneShotPreviewCallback(this);�Ϳ����ˡ�������Զ���������Activity���onPreviewFrame����
		//��������д������,���յĶ���Ҳд����	     
		//this callback method is used to get preview frame
		if(data != null){
			 Size size = CameraInterface.getInstance().mCamera.getParameters().getPreviewSize();          
			    try{  
			        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);  
			        if(image!=null){  
			            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);  
			            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());  
			            stream.close(); 
			            Bitmap bm2;
			            if(getIntent().getIntExtra("para",1) == 1)
			            	bm2 = ImageHelper.RotateBitmap(bmp, -90);	//��ת���������ܱ�apiʶ��//api just can use Andy 
			            else 
			            	bm2 = ImageHelper.RotateBitmap(bmp, 90);
			            String base64= ImageHelper.bitmapToBase64(bm2);
	       				String detectresult = new detect(base64).run(); 
//	       				Log.i("detect", detectresult);
	       				TextView textView = (TextView) findViewById(com.smartcemera.R.id.textView1);
	       				textView.setText(detectresult);
	       				String analyzeresult = "";
	       				if(detectresult.contains("face_token")){
	       					String token = detectresult.split("face_token\": \"")[1].split("\"")[0];
       						analyzeresult = new analyze(token).run();
       						textView.setText(analyzeresult);
       						Toast.makeText(this, DataHelper.emotion(analyzeresult),Toast.LENGTH_SHORT).show();;

	       				}
	       				
	       				Log.i("mode!!!!!!", mode);

	       				if(mode.equals("smile")){
	       					if(DataHelper.SplitResult(analyzeresult, "smile").equals("smiling")){
	       						shuttle(surfaceView);
	       					}
	       				}

	       				if(mode.equals("pre")){

	       					String emotion = getIntent().getStringExtra("Emotion");
	       					String gender = getIntent().getStringExtra("Gender");
	       					String beau = getIntent().getStringExtra("beau");
	       					String nowemotion = DataHelper.emotion(analyzeresult);
	       					String nowgender = DataHelper.SplitResult(analyzeresult, "gender");
	       					Log.i("emotion", emotion);
	       					Log.i("noewemotion", nowemotion);
	       					Log.i("gender", gender);
	       					Log.i("nowgender",nowgender);
       						float beauscore = Float.parseFloat(DataHelper.SplitResult(analyzeresult,"beau"));
//	       					if(beau.equals("1")){
//	       						if(DataHelper.emotion(analyzeresult).equals(emotion) && gender.equals(DataHelper.emotion("gender"))&&beauscore>70){
//	       							shuttle(surfaceView);//��֪�������view�в��У���Ϊshuttle�б�����view	
//	       						}
//	       					}
//	       					else {
	       						if(gender.equals(nowgender)&&emotion.equals(nowemotion)){
	       							shuttle(surfaceView);
	       						}
//	       					}
	       				}
			        }  }catch (Exception e) {
				}
		}
	}

	
	
	

}
