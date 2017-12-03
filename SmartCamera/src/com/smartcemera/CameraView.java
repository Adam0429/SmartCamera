package com.smartcemera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {  
    private SurfaceHolder holder;  
    private Camera camera;  
    private boolean af;  
   
    public CameraView(Context context) {//构造函数  
        super(context);  
   
        holder = getHolder();//生成Surface Holder  
        holder.addCallback(this);  
   
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//指定Push Buffer  
    }  
   
    public void surfaceCreated(SurfaceHolder holder) {//Surface生成事件的处理  
try {  
            camera = Camera.open();//摄像头的初始化  
            Camera.Parameters p =camera.getParameters();  
            p.setPictureSize(1500, 2000); //设置照片分辨率，太大上传不了  
            camera.setParameters(p);  
              
            camera.setPreviewDisplay(holder);  
            //camera.setDisplayOrientation(90);  
        } catch (Exception e) {  
        }  
    }

	@Override
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	} 
}