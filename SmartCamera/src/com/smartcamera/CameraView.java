package com.smartcamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {  
	  private Camera mCamera;  
	    /** SurfaceHolder **/  
	    private SurfaceHolder mHolder;  
	      
	    /** CamreaPreview���캯��   **/  
	    @SuppressWarnings("deprecation")  
	    public CameraView(Context mContext,Camera mCamera)   
	    {  
	        super(mContext);  
	        this.mCamera=mCamera;  
	        // ��װһ��SurfaceHolder.Callback��  
	        // �������������ٵײ�surfaceʱ�ܹ����֪ͨ��  
	        mHolder = getHolder();   
	        mHolder.addCallback(this);   
	        // �ѹ��ڵ����ã����汾����3.0��Android����Ҫ  
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   
	    }  
	  
	    @Override  
	    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)   
	    {  
	          
	    }  
	  
	    @Override  
	    public void surfaceCreated(SurfaceHolder holder)   
	    {  
	       try {  
	        mCamera.setPreviewDisplay(holder);  
	        mCamera.startPreview();  
	        mCamera.setDisplayOrientation(90);  
	       } catch (Exception e) {  
//	        Log.d("TAG", "Error is "+e.getMessage());  
	       }      
	    }  
	  
	    @Override  
	    public void surfaceDestroyed(SurfaceHolder arg0)   
	    {  
	        // ���Ԥ���޷����Ļ���ת��ע��˴����¼�  
	        // ȷ�������Ż�����ʱֹͣԤ��  
	        if (mHolder.getSurface() == null){   
	          // Ԥ��surface������  
	        
	          return;   
	        }   
	        // ����ʱֹͣԤ��   
	        try {   
	            mCamera.stopPreview();   
	        } catch (Exception e){   
	          // ���ԣ���ͼֹͣ�����ڵ�Ԥ��  
	        }   
	        // �ڴ˽������š���ת��������֯��ʽ  
	        // ���µ���������Ԥ  
	        try {   
	            mCamera.setPreviewDisplay(mHolder);   
	            mCamera.setDisplayOrientation(90);   
	            mCamera.startPreview();   
	        } catch (Exception e){   
//	            Log.d("TAG", "Error is " + e.getMessage());   
	        }   
	          
	    }
	    
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			 
			
		}  
}