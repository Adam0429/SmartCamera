package helper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,PreviewCallback{//����������ʾcamera����Ĺ���
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
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent��͸�� transparent͸��
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
	public void onPreviewFrame(byte[] arg0, Camera arg1) {//����������data����ʵʱԤ��֡��Ƶ��һ���������PreviewCallback�ӿڣ��ͻ��Զ�����onPreviewFrame���������
		//���Activity�̳���PreviewCallback����ӿڣ�ֻ��̳�Camera.setOneShotPreviewCallback(this);�Ϳ����ˡ�������Զ���������Activity���onPreviewFrame����
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
