package com.smartcamera;

import java.io.ByteArrayOutputStream;

import javax.net.ssl.SSLException;

import org.apache.http.entity.SerializableEntity;

import android.R;
import android.R.integer;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import api.detect;
import helper.CameraInterface;
import helper.CameraInterface.CamOpenOverCallback;
import helper.CameraSurfaceView;
import helper.DisplayUtil;
import helper.FileUtil;

public class TakePhoto extends Activity implements CamOpenOverCallback,PreviewCallback{//继承了callback接口,说明这个类会被调用callback接口里的方法
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	CameraInterface cameraInterface;
	ImageButton shutterBtn;
	Thread photoThread;
	float previewRate = -1f;
	int ca;
	boolean check = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ca = getIntent().getIntExtra("para",1);
		Thread openThread = new Thread(){//经过测试，但就执行Camera.open（）这句话一般需要140ms左右。如果放在主线程里无疑是一种浪费
		public void run() {
			CameraInterface.getInstance().doOpenCamera(TakePhoto.this,ca);
			}
		};
		openThread.start();

		setContentView(com.smartcemera.R.layout.activity_take_photo);
		initUI();
		initViewParams();

		photoThread = new Thread(){
			public void run() {
				while(check){
				if(CameraInterface.getInstance().isPreviewing){					
						CameraInterface.getInstance().mCamera.setOneShotPreviewCallback(TakePhoto.this);
					}
					
				else 
					Log.i("cam", "error");
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

	public void cameraHasOpened() {
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	
	public void shuttle(){

		CameraInterface.getInstance().doTakePicture();
		int path = FileUtil.DirNumeber(FileUtil.initPath());	
		Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		intent.putExtra("path", Integer.toString(path));	
		intent.putExtra("camera", Integer.toString(ca));	
		try {
			Thread.sleep(1100);//之前立即就startactivity,会报错,猜测是因为存图还需要时间,所以那里一直不能获得,所以让他睡一会儿,300ms不够还可以再加
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//延迟3s，时间自己定
		startActivity(intent);			
		}



	public void onPreviewFrame(byte[] data, Camera arg1) {//这个函数里的data就是实时预览帧视频。一旦程序调用PreviewCallback接口，就会自动调用onPreviewFrame这个函数。
		//如果Activity继承了PreviewCallback这个接口，只需继承Camera.setOneShotPreviewCallback(this);就可以了。程序会自动调用主类Activity里的onPreviewFrame函数
		//处理数据写在这里,拍照的动作也写这里	     
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
			            	bm2 = rotateBitmap(bmp, -90);	//得转成正脸才能被api识别
			            else 
			            	bm2 = rotateBitmap(bmp, 90);
			            String base64= bitmapToBase64(bm2);
	       				String result = new detect(base64).run(); 
	       				Log.i("detect", result);
	       				check = false;
	       				if(result.contains("face_token")){
	       				
	       					shuttle();
	       				}
			        }  }catch (Exception e) {
						// TODO: handle exception
				}
		}
	}

	public String bitmapToBase64(Bitmap bitmap) {  
	    String result = null;  
	    ByteArrayOutputStream baos = null;  
	    try {  
	        if (bitmap != null) {  
	            baos = new ByteArrayOutputStream();  
                bitmap.compress(CompressFormat.JPEG, 50, baos);//Bitmap.compress方法确实可以压缩图片，但压缩的是存储大小，即你放到disk上的大小.bitmap大小不变

	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes,Base64.NO_WRAP);  //
	           
	        }  
	      
	    } catch (Exception e) {  
        	
	    }
	    
	    return result;  
	}  
	
	public Bitmap byteTobitmap(byte[] data){
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public Bitmap rotateBitmap(Bitmap origin, float alpha) {
		if (origin == null) {
	            return null;
	    }
	     int width = origin.getWidth();
	     int height = origin.getHeight();
	        Matrix matrix = new Matrix();
	        matrix.setRotate(alpha);
	        // 围绕原地进行旋转
	        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
	        if (newBM.equals(origin)) {
	            return newBM;
	        }
	        origin.recycle();
	        return newBM;
	    }
	
	
	

}
