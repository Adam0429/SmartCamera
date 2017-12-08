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
	public interface CamOpenOverCallback{	//��֪������ص���ʲô����,�²����������ж��ʹ��camera���ʱ�򷽱�Щ��
		public void cameraHasOpened();
	}

	public static synchronized CameraInterface getInstance(){//�����ʱ����ģʽ,�����������װ
		if(mCameraInterface == null){
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}
	/**��Camera
	 * @param callback
	 */
	public void doOpenCamera(CamOpenOverCallback callback,int i){
		Log.i(TAG, "Camera open....");
		mCamera = Camera.open(i);
		Log.i(TAG, "Camera open over....");
		callback.cameraHasOpened();		//��֪������ص���ʲô����,�²����������ж��ʹ��camera���ʱ�򷽱�Щ��
	}
	/**����Ԥ��
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
			mParams.setPictureFormat(PixelFormat.JPEG);//�������պ�洢��ͼƬ��ʽ
			CamParaUtil.getInstance().printSupportPictureSize(mParams);
			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
			//����PreviewSize��PictureSize
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
				mCamera.startPreview();//����Ԥ��
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); //����getһ��
			Log.i(TAG, "��������:PreviewSize--With = " + mParams.getPreviewSize().width
					+ "Height = " + mParams.getPreviewSize().height);
			Log.i(TAG, "��������:PictureSize--With = " + mParams.getPictureSize().width
					+ "Height = " + mParams.getPictureSize().height);
			Thread photoThread = new Thread(){
				public void run() {
					while(CameraInterface.getInstance().isPreviewing){
						Log.i("sddsd", "sdsdsdd");
						mCamera.setOneShotPreviewCallback(CameraInterface.this);
						//ʹ�ô˷���ע��Ԥ���ص��ӿ�ʱ���Ὣ��һ֡���ݻص���onPreviewFrame()������������ɺ�����ص��ӿڽ������١�Ҳ����ֻ��ص�һ��Ԥ��֡���ݡ� 
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
	 * ֹͣԤ�����ͷ�Camera
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
	 * ����
	 * @return 
	 */
	public void doTakePicture(){//��������Ȼص���������ִ�У�����picture������������
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
			Log.i("��Ƭ","����!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}
		

	}

	/*Ϊ��ʵ�����յĿ������������ձ�����Ƭ��Ҫ���������ص�����*/
	ShutterCallback mShutterCallback = new ShutterCallback() 
	//���Ű��µĻص������������ǿ����������Ʋ��š����ꡱ��֮��Ĳ�����Ĭ�ϵľ������ꡣ
	{
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(TAG, "myShutterCallback:onShutter...");
			
		}
	};
	PictureCallback mRawCallback = new PictureCallback() 
	// �����δѹ��ԭ���ݵĻص�,����Ϊnull
	{

		public void onPictureTaken(byte[] data, Camera camera) {

			// TODO Auto-generated method stub
			Log.i(TAG, "myRawCallback:onPictureTaken...");
	
		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback() 
	//��jpegͼ�����ݵĻص�,����Ҫ��һ���ص�
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(data != null){

				b = BitmapFactory.decodeByteArray(data, 0, data.length);//data���ֽ����ݣ����������λͼ
				mCamera.stopPreview();
				isPreviewing = false;

			}
			else
				Log.i("����Ƭ","��ƬΪ��");
			
			//����ͼƬ��sdcard
			if(b != null)
			{	
				//����FOCUS_MODE_CONTINUOUS_VIDEO)֮��myParam.set("rotation", 90)ʧЧ��
				//ͼƬ��Ȼ������ת�ˣ�������Ҫ��ת��
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 180.0f);
				FileUtil.saveBitmap(b);
				Log.i("����Ƭ","�ɹ�");

			}
			//�ٴν���Ԥ��
			mCamera.startPreview();
			isPreviewing = true;

		}
	};
	
	public void onPreviewFrame(byte[] data, Camera arg1) {//����������data����ʵʱԤ��֡��Ƶ��һ���������PreviewCallback�ӿڣ��ͻ��Զ�����onPreviewFrame���������
		//���Activity�̳���PreviewCallback����ӿڣ�ֻ��̳�Camera.setOneShotPreviewCallback(this);�Ϳ����ˡ�������Զ���������Activity���onPreviewFrame����
		//��������д������,���յĶ���Ҳд����	     
		if(data != null){
			 Size size = mCamera.getParameters().getPreviewSize();          
			    try{  
			        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);  
			        if(image!=null){  
			            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);  
			            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());  
			            stream.close(); 
	            		Bitmap bm2 = rotateBitmap(bmp, -90);	//��ת���������ܱ�apiʶ��
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
                bitmap.compress(CompressFormat.JPEG, 50, baos);//Bitmap.compress����ȷʵ����ѹ��ͼƬ����ѹ�����Ǵ洢��С������ŵ�disk�ϵĴ�С.bitmap��С����

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
	        // Χ��ԭ�ؽ�����ת
	        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
	        if (newBM.equals(origin)) {
	            return newBM;
	        }
	        origin.recycle();
	        return newBM;
	    }
}
