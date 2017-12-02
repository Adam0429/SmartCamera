package com.smartcemera;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import android.util.Base64;
import android.util.Log;
import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.analyze;
import api.detect;

public class MainActivity extends Activity {

	@Override
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
		  
	      
	    Bitmap bm = null;  
	  
	  
	    ContentResolver resolver = getContentResolver();  
	  
	   
	        try {  
	  
	            Uri originalUri = data.getData(); // ���ͼƬ��uri,ѡ��ͼƬʱ�����ͼ����ѡ��Ȼ�ᱨ��  
	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //�õ�bitmapͼƬ
	            mIV.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 500, 500)); 
	            textView2.setText(new detect(bitmapToBase64(bm)).run());
	            if(new detect(bitmapToBase64(bm)).run().contains("face_token")){
	            	String token = new detect(bitmapToBase64(bm)).run().split("face_token\": \"")[1].split("\"")[0];
	            	textView.setText(new analyze(token).run());
	            }
	            else
	            	textView.setText("ͼƬ��С������,�������2M����");
            	mIV.setImageBitmap(bm); 
	            // �Եõ�bitmapͼƬ  
	            // imageView.setImageBitmap(bm);  
	  
	            String[] proj = { MediaStore.Images.Media.DATA };  
	  
	            // ������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�  
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null);  
	  
	            // ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ  
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	            // �����������ͷ ���������Ҫ����С�ĺ���������Խ��  
	            cursor.moveToFirst();  
	            // ����������ֵ��ȡͼƬ·��  
	            String path = cursor.getString(column_index);  
            	//Toast.makeText(this, new detect(base64).run(), Toast.LENGTH_SHORT).show(); 
//            	Toast.makeText(this, , Toast.LENGTH_SHORT).show();//
            	
	        } catch (IOException e) {  
            	Toast.makeText(this, "��ȡͼƬerror,���ͼ����ѡ��Ȼ�ᱨ��", Toast.LENGTH_SHORT).show(); 
            	
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
	    } catch (IOException e) {  
        	textView2.append("����");
	    }
//    	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();  
//	    textView2.setText(result);
	    return result;  
	}  
}
