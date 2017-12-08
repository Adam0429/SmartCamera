package helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.smartcamera.TakePhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;
import api.detect;

public class CameraInterface implements PreviewCallback{
	private static final String TAG = "yanzi";
	public Camera mCamera;
	private Camera.Parameters mParams;
	public boolean isPreviewing = false;
	private static CameraInterface mCameraInterface;
	private float mPreviwRate = -1f;
	public interface CamOpenOverCallback{	//不知道这里回调有什么意义,猜测可能是如果有多个使用camera类的时候方便些吧
		public void cameraHasOpened();
	}

	public static synchronized CameraInterface getInstance(){//这个类时单例模式,所以用这个封装
		if(mCameraInterface == null){
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}
	/**打开Camera
	 * @param callback
	 */
	public void doOpenCamera(CamOpenOverCallback callback,int i){
		Log.i(TAG, "Camera open....");
		mCamera = Camera.open(i);
		Log.i(TAG, "Camera open over....");
		callback.cameraHasOpened();		//不知道这里回调有什么意义,猜测可能是如果有多个使用camera类的时候方便些吧
	}
	/**开启预览
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate){
		Log.i(TAG, "doStartPreview...");
		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
			CamParaUtil.getInstance().printSupportPictureSize(mParams);
			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
			//设置PreviewSize和PictureSize
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(),previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

			CamParaUtil.getInstance().printSupportFocusMode(mParams);
			List<String> focusModes = mParams.getSupportedFocusModes();
			if(focusModes.contains("continuous-video")){
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);	

			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();//开启预览
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); //重新get一次
			Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
					+ "Height = " + mParams.getPreviewSize().height);
			Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
					+ "Height = " + mParams.getPictureSize().height);
			Thread photoThread = new Thread(){
				public void run() {
					while(CameraInterface.getInstance().isPreviewing){
						Log.i("sddsd", "sdsdsdd");
						mCamera.setOneShotPreviewCallback(CameraInterface.this);
						//使用此方法注册预览回调接口时，会将下一帧数据回调给onPreviewFrame()方法，调用完成后这个回调接口将被销毁。也就是只会回调一次预览帧数据。 
						try{	
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
//		                    Thread.currentThread().interrupt();  

						}
					}
				}
			};
			photoThread.start();
		}
	}
	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera(){
		if(null != mCamera)
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview(); 
			isPreviewing = false; 
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;     
		}
	}
	/**
	 * 拍照
	 * @return 
	 */
	public void doTakePicture(){//这个方法比回调方法更先执行，所以picture不能用他返回
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
			Log.i("照片","拍照!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}
		

	}

	/*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
	ShutterCallback mShutterCallback = new ShutterCallback() 
	//快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(TAG, "myShutterCallback:onShutter...");
			
		}
	};
	PictureCallback mRawCallback = new PictureCallback() 
	// 拍摄的未压缩原数据的回调,可以为null
	{

		public void onPictureTaken(byte[] data, Camera camera) {

			// TODO Auto-generated method stub
			Log.i(TAG, "myRawCallback:onPictureTaken...");
	
		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback() 
	//对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(data != null){

				b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;

			}
			else
				Log.i("存照片","照片为空");
			
			//保存图片到sdcard
			if(b != null)
			{	
				//设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
				//图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 180.0f);
				FileUtil.saveBitmap(b);
				Log.i("存照片","成功");

			}
			//再次进入预览
			mCamera.startPreview();
			isPreviewing = true;

		}
	};
	
	public void onPreviewFrame(byte[] data, Camera arg1) {//这个函数里的data就是实时预览帧视频。一旦程序调用PreviewCallback接口，就会自动调用onPreviewFrame这个函数。
		//如果Activity继承了PreviewCallback这个接口，只需继承Camera.setOneShotPreviewCallback(this);就可以了。程序会自动调用主类Activity里的onPreviewFrame函数
		//处理数据写在这里,拍照的动作也写这里	     
		if(data != null){
			 Size size = mCamera.getParameters().getPreviewSize();          
			    try{  
			        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);  
			        if(image!=null){  
			            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);  
			            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());  
			            stream.close(); 
	            		Bitmap bm2 = rotateBitmap(bmp, -90);	//得转成正脸才能被api识别
			            String base64= bitmapToBase64(bm2);
	       				String result = new detect(base64).run(); 
	       				Log.i("detect", result);
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
