package com.smartcemera;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.util.Base64;
import android.util.Log;
import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import api.detect;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void upload(View view){
	      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
	      intent.setType("image/*");//设置类型
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //在Activity中得到新打开Activity关闭后返回的数据
        super.onActivityResult(requestCode, resultCode, data);  

		ImageView mIV = (ImageView) findViewById(R.id.imageView1);
		
		  // TODO Auto-generated method stub  
		  
	      
	    Bitmap bm = null;  
	  
	    // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口  
	  
	    ContentResolver resolver = getContentResolver();  
	  
	   
	        try {  
	  
	            Uri originalUri = data.getData(); // 获得图片的uri  
	  
	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);  
	  
	            mIV.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 500, 500));  //使用系统的一个工具类，参数列表为 Bitmap Width,Height  这里使用压缩后显示，否则在华为手机上ImageView 没有显示  
	            // 显得到bitmap图片  
	            // imageView.setImageBitmap(bm);  
	  
	            String[] proj = { MediaStore.Images.Media.DATA };  
	  
	            // 好像是android多媒体数据库的封装接口，具体的看Android文档  
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null);  
	  
	            // 按我个人理解 这个是获得用户选择的图片的索引值  
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	            // 将光标移至开头 ，这个很重要，不小心很容易引起越界  
	            cursor.moveToFirst();  
	            // 最后根据索引值获取图片路径  
	            String path = cursor.getString(column_index);  
	            String base64 = encode(path);
            	Toast.makeText(this, new detect(base64).run(), Toast.LENGTH_SHORT).show(); 
            	
            	
	        } catch (IOException e) {  
//	            Log.e("TAG-->Error", e.toString());  
	  
	            }  
	  
	            finally {  
	                return;  
	            }  
	        
	  
//		
//		try {
//			URL url = new URL("http://www.baidu.com/img/baidu_sylogo1.gif");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setConnectTimeout(5000);
//			conn.setRequestMethod("get");
//			//获得请求的响应码
//			int code = conn.getResponseCode();
//			if(code == HttpStatus.SC_OK){				// String string = new detect("/data/IMG_20170117_165227.jpg").run();
//
//				InputStream inputStream = conn.getInputStream();
//				mIV.setImageBitmap(BitmapFactory.decodeStream(inputStream));
//				Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			
//	            Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();  
//	            File file = new File(uri.getpath().toString());
//	            String string = new detect("/data/IMG_20170117_165227.jpg").run();
//	            File file = new File("/data/IMG_20170117_165227.jpg");
//	            if(file.exists())
//	            	Toast.makeText(this, "存在", Toast.LENGTH_SHORT).show();  
	        
	        
	}  
	
	private String encode(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);				//path错了
//        Log.d(TAG, "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        String encodeString = new String(encode);
    	Toast.makeText(this, "sds", Toast.LENGTH_SHORT).show(); 

        return encodeString;
}
}
