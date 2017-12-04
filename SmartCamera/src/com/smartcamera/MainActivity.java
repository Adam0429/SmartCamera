package com.smartcamera;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.smartcemera.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.analyze;
import api.detect;

public class MainActivity extends Activity {


	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void upload(View view){
	      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
	      intent.setType("image/*");//��������
          intent.addCategory(Intent.CATEGORY_OPENABLE);  
          startActivityForResult(intent,1);  
	}
	
	public void camera(View view){
		Intent intent = new Intent(this, TakePhoto.class);
		intent.putExtra("para", 0);
		startActivity(intent);
		
//		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
//
//		Camera c = Camera.open();
//		CameraView cameraView = new CameraView(this,c);
//
//		cameraView.surfaceCreated(surfaceView.getHolder());
//		Toast.makeText(this, "��ȡͼƬerror,���ͼ����ѡ��Ȼ�ᱨ��", Toast.LENGTH_SHORT).show();   	

	}
	
	public void camera2(View view){
		Intent intent = new Intent(this, TakePhoto.class);
		intent.putExtra("para", 1);
		startActivity(intent);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //��Activity�еõ��´�Activity�رպ󷵻ص�����

        super.onActivityResult(requestCode, resultCode, data);  
        TextView textView = (TextView)findViewById(R.id.textView1);
        TextView textView2 = (TextView)findViewById(R.id.textView2);
		ImageView mIV = (ImageView) findViewById(R.id.imageView1);
		
		  // TODO Auto-generated method stub  
		  
	    if(requestCode == 1) { //requestcode�������������Ǵ��ĸ�acivity���,����1���ϴ�,2������
	    	Bitmap bm = null;  
	    	ContentResolver resolver = getContentResolver();  
	    
	    	if(data != null){
	   
	    		try {  
	        	
	    			Uri originalUri = data.getData(); // ���ͼƬ��uri,ѡ��ͼƬʱ�����ͼ����ѡ��Ȼ�ᱨ��  
	    			bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //�õ�bitmapͼƬ
	    			mIV.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 500, 500)); 
//	    			textView2.setText(new detect(bitmapToBase64(bm)).run());
	    			String detectresult = new detect(bitmapToBase64(bm)).run();
	    			if(detectresult.contains("face_token")){
	    				String token = detectresult.split("face_token\": \"")[1].split("\"")[0];
	    				textView.setText(new analyze(token).run());
	    			}
	    			else if(detectresult.contains("INVALID_IMAGE_SIZE")||detectresult.contains("IMAGE_FILE_TOO_LARGE"))
	    				textView.setText("ͼƬ��С������,�������2M����");
	    			else{
	    				textView.setText("");
	    				textView2.setText("���󱨸�:");
	    				textView2.append(detectresult);
	    			}
	    			mIV.setImageBitmap(bm); 
	      			String[] proj = { MediaStore.Images.Media.DATA }; 
	    			Cursor cursor = managedQuery(originalUri, proj, null, null, null);  
	    			// ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ  
	    			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	    			// �����������ͷ ���������Ҫ����С�ĺ���������Խ��  
	    			cursor.moveToFirst();  
	    			// ����������ֵ��ȡͼƬ·��  
	    			String path = cursor.getString(column_index);  
	    			//Toast.makeText(this, new detect(base64).run(), Toast.LENGTH_SHORT).show(); 
	    			//Toast.makeText(this, , Toast.LENGTH_SHORT).show();
        
	    		} catch (IOException e) {  
	    			Toast.makeText(this, "��ȡͼƬerror,���ͼ����ѡ��Ȼ�ᱨ��", Toast.LENGTH_SHORT).show();   	
	    		}  
	    	}       
	    }

	    if(requestCode == 2){
	    	 if(data != null){  
	                Bundle extras = data.getExtras();  
	                if(extras != null){  
	                    Bitmap imageBitmap = (Bitmap) extras.get("data");  
	                    mIV.setImageBitmap(imageBitmap);  
	                }else{  
//	                    Log.d(tag,"no Bitmap return");  
	                }  
	            }else{  
//	                Log.d(tag,"data is null");  
	            }  
	          
	    }
	        
	}  
	
	public String bitmapToBase64(Bitmap bitmap) {  
        
		TextView textView2 = (TextView) findViewById(R.id.textView2);
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
	        else 
	        	textView2.append("�յ�bitmap");
	    } catch (Exception e) {  
        	textView2.append("����");
	    }
//    	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();  
//	    textView2.setText(result);
	    return result;  
	}  
}
