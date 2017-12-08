package helper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{//这个类管理显示camera画面的功能,仅仅重写了一个类功能
	//SurfaceHolder.Callback是用来预览摄像头视频
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

	
}
