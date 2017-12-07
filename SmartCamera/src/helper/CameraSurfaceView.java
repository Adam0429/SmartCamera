package helper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,PreviewCallback{//这个类管理显示camera画面的功能
	private static final String TAG = "yanzi";
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;
	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mCameraInterface = new CameraInterface();
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceCreated...");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceChanged...");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceDestroyed...");
		CameraInterface.getInstance().doStopCamera();
	}
	public SurfaceHolder getSurfaceHolder(){
		return mSurfaceHolder;
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {//这个函数里的data就是实时预览帧视频。一旦程序调用PreviewCallback接口，就会自动调用onPreviewFrame这个函数。
		//如果Activity继承了PreviewCallback这个接口，只需继承Camera.setOneShotPreviewCallback(this);就可以了。程序会自动调用主类Activity里的onPreviewFrame函数
//		  if(null != mFaceTask){
//	            switch(mFaceTask.getStatus()){
//	            case RUNNING:
//	                return;
//	            case PENDING:
//	                mFaceTask.cancel(false);
//	                break;
//	            }
//	        }
//	        mFaceTask = new PalmTask(data);
//	        mFaceTask.execute((Void)null);
	
	}
	
}
