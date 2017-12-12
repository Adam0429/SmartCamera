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

public class TakePhoto extends Activity implements CamOpenOverCallback,PreviewCallback{//继承了callback接口,说明这个类会被调用callback接口里的方法//use callback interface
	private static final String TAG = "takephoto";
	CameraSurfaceView surfaceView = null;
	CameraInterface cameraInterface;
	ImageButton shutterBtn;
	float previewRate = -1f;
	String mode;
	int cameramode;
	boolean check = true;		//用来控制线程的,我发现让线程interrupt不能达到我的目的//use to control thread
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mode = getIntent().getStringExtra("mode");
		Log.i("mode", mode);
		cameramode = getIntent().getIntExtra("para",1);
		Thread openThread = new Thread(){//经过测试，但就执行Camera.open（）这句话一般需要140ms左右。如果放在主线程里无疑是一种浪费
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
				while(check){		//normal是自己拍
					if(CameraInterface.getInstance().isPreviewing){					
						CameraInterface.getInstance().mCamera.setOneShotPreviewCallback(TakePhoto.this);
					}
					else 
						Log.i("cam", "not previewing");
//					//使用此方法注册预览回调接口时，会将下一帧数据回调给onPreviewFrame()方法，调用完成后这个回调接口将被销毁。也就是只会回调一次预览帧数据。 
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
		previewRate = DisplayHelper.getScreenRate(this); //默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		//手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
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
		check = false;		//停止预览线程
		CameraInterface.getInstance().doTakePicture();
		int path = FileHelper.DirNumeber(FileHelper.initPath());	
		Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		intent.putExtra("path", Integer.toString(path));	
		intent.putExtra("camera", Integer.toString(cameramode));	
		try {
			Thread.sleep(300);//之前立即就startactivity,会报错,猜测是因为存图还需要时间,所以那里一直不能获得,所以让他睡一会儿,300ms不够还可以再加
		} catch (InterruptedException e) {
			e.printStackTrace();
		}//延迟3s，时间自己定
		startActivity(intent);			
		}



	public void onPreviewFrame(byte[] data, Camera arg1) {//这个函数里的data就是实时预览帧视频。一旦程序调用PreviewCallback接口，就会自动调用onPreviewFrame这个函数。
		//如果Activity继承了PreviewCallback这个接口，只需继承Camera.setOneShotPreviewCallback(this);就可以了。程序会自动调用主类Activity里的onPreviewFrame函数
		//处理数据写在这里,拍照的动作也写这里	     
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
			            	bm2 = ImageHelper.RotateBitmap(bmp, -90);	//得转成正脸才能被api识别//api just can use Andy 
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
//	       							shuttle(surfaceView);//不知道传这个view行不行，因为shuttle中必须有view	
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
