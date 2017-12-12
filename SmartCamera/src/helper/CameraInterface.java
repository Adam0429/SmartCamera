package helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.smartcamera.TakePhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
//some reference
public class CameraInterface{
	private static final String TAG = "CameraInterface";
	public Camera mCamera;
	private Camera.Parameters mParams;
	public boolean isPreviewing = false;
	private static CameraInterface mCameraInterface;
	private float mPreviwRate = -1f;
	public interface CamOpenOverCallback{	//不知道这里回调有什么意义,猜测可能是如果有多个使用camera类的时候方便些吧
		public void cameraHasOpened();
	}

	public static synchronized CameraInterface getInstance(){//这个类时单例模式,所以用这个封装 //only need one object
		if(mCameraInterface == null){
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}

	public void doOpenCamera(CamOpenOverCallback callback,int i){
		Log.i(TAG, "Camera open....");
		mCamera = Camera.open(i);
		Log.i(TAG, "Camera open over....");
		callback.cameraHasOpened();		//不知道这里回调有什么意义,猜测可能是如果有多个使用camera类的时候方便些吧
	}

	public void doStartPreview(SurfaceHolder holder, float previewRate){
		Log.i(TAG, "doStartPreview...");
		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
			CameraParaHelper.getInstance().printSupportPictureSize(mParams);
			CameraParaHelper.getInstance().printSupportPreviewSize(mParams);
			//设置PreviewSize和PictureSize
			Size pictureSize = CameraParaHelper.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(),previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CameraParaHelper.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

			CameraParaHelper.getInstance().printSupportFocusMode(mParams);
			List<String> focusModes = mParams.getSupportedFocusModes();
			if(focusModes.contains("continuous-video")){
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);	

			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();//开启预览
			} catch (IOException e) {
				e.printStackTrace();
			}

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); //重新get一次
			Log.i(TAG, "set:PreviewSiz===With = " + mParams.getPreviewSize().width
					+ "Height = " + mParams.getPreviewSize().height);
			Log.i(TAG, "set:PictureSize===With = " + mParams.getPictureSize().width
					+ "Height = " + mParams.getPictureSize().height);

		}
	}

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
	
	public void doTakePicture(){//这个方法比回调方法更先执行，所以picture不能用他返回
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
			Log.i("照片","拍照!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}
		

	}

	//实现拍照的快门声音及拍照保存照片
	ShutterCallback mShutterCallback = new ShutterCallback() 
	//快门按下声的回调，默认的就是咔嚓。
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

			Log.i(TAG, "myRawCallback:onPictureTaken...");
	
		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback() 
	//对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(data != null){

				b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;

			}
			else
				Log.i("存照片","照片为空");
			
			if(b != null)
			{	
				//设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
				//图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = ImageHelper.RotateBitmap(b, 180.0f);
				FileHelper.saveBitmap(b);
				Log.i("存照片","成功");

			}
			//再次进入预览
//			mCamera.startPreview();
//			isPreviewing = true;

		}
	};
	

}
