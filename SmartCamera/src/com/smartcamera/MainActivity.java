package com.smartcamera;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.smartcemera.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import api.analyze;
import api.detect;
import helper.FileUtil;

public class MainActivity extends Activity {//加一个后台上传的代码
	

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView mIV = (ImageView) findViewById(R.id.imageView1);
		TextView textView = (TextView)findViewById(R.id.textView1);
	    TextView textView2 = (TextView)findViewById(R.id.textView2);
		Intent intent =getIntent();
		if(getIntent().getStringExtra("path") != null){	    
	    	 File file = new File(FileUtil.initPath()+"/"+getIntent().getStringExtra("path")+".jpg");
             if (file.exists()) {//如果太快进入的话,图还没存好，就要显示，会出错
            	File file1 = new File(FileUtil.initPath());
         		File[] files = file.listFiles();

//            	int i = Integer.parseInt(getIntent().getStringExtra("path"));
        		Toast.makeText(this,FileUtil.initPath()+"/"+getIntent().getStringExtra("path")+".jpg", Toast.LENGTH_SHORT).show();   
            	Bitmap bm1 = BitmapFactory.decodeFile(FileUtil.initPath()+"/"+getIntent().getStringExtra("path")+".jpg");
        		Bitmap bm2;
            	if(getIntent().getStringExtra("camera").equals("1"))
            		bm2 = rotateBitmap(bm1, -90);
            	else
            		bm2 = rotateBitmap(bm1, 90);

 	    		mIV.setImageBitmap(bm2); 
            
    			String detectresult = new detect(bitmapToBase64(bm2)).run();
    			if(detectresult.contains("face_token")){
    				String token = detectresult.split("face_token\": \"")[1].split("\"")[0];
    				String analyzeresult = new analyze(token).run();
    				textView.setText(emotion(analyzeresult));
    			}
    			else if(detectresult.contains("INVALID_IMAGE_SIZE")||detectresult.contains("IMAGE_FILE_TOO_LARGE"))
    				textView.setText("图片大小不合适,请控制在2M以内");
    			else{
    				textView.setText("");
    				textView2.setText("错误报告:");
    				textView2.append(detectresult);
    			}
  
             }
		}
	        	
		isNetworkConnected(this);
	
	}

	public static void isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				Toast.makeText(context, "有网",Toast.LENGTH_SHORT).show();
			}
			else 
				Toast.makeText(context, "无网",Toast.LENGTH_SHORT).show();;
		}
	}
	
	public void upload(View view){
	      Intent intent = new Intent(Intent.ACTION_GET_CONTENT).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//销毁前一个intent防止过多的界面  
	      intent.setType("image/*");//设置类型
          intent.addCategory(Intent.CATEGORY_OPENABLE);  
          startActivityForResult(intent,1);  
	}
	
	public void camera(View view){
		Intent intent = new Intent(this, TakePhoto.class);
		intent.putExtra("para", 0);
//		startActivity(intent);

	}
	
	public void camera2(View view){
		
		final Intent intent = new Intent(this, TakePhoto.class);
		intent.putExtra("para", 1);
//		startActivity(intent);
		
		LayoutInflater inflater = getLayoutInflater();//将xml转换成一个View对象，用于动态的创建布局
		View layout = inflater.inflate(R.layout.insert_dialog,null);

		new AlertDialog.Builder(this).setTitle("Choose Preference").setView(layout)
		.setNegativeButton("Cancel", null).show();

		final CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkBox1);
		Button button = (Button) layout.findViewById(R.id.buttonpre);
		Button button2 = (Button) layout.findViewById(R.id.buttonsmile);
		final Spinner Gender = (Spinner) layout.findViewById(R.id.Spinner01);
		final Spinner EmotionSel = (Spinner) layout.findViewById(R.id.spinner2); 
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(checkBox.isChecked()){
					intent.putExtra("beau", "1");
					Log.i("beau", "1");
				}
				if(Gender.getSelectedItem().toString() == "")
					intent.putExtra("Gender", "Male");
				else 
					intent.putExtra("Gender", Gender.getSelectedItem().toString());
				
				Log.i("gender", Gender.getSelectedItem().toString());

				intent.putExtra("Emotion", EmotionSel.getSelectedItem().toString());

				startActivity(intent);
				
			}
		});
		
		button2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				intent.putExtra("smile", "1");
				Log.i("smile","sm");
				startActivity(intent);
	
			}
		});
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
        TextView textView = (TextView)findViewById(R.id.textView1);
        TextView textView2 = (TextView)findViewById(R.id.textView2);
		ImageView mIV = (ImageView) findViewById(R.id.imageView1);
		
		  // TODO Auto-generated method stub  
		  
	    if(requestCode == 1) { //requestcode用来区分数据是从哪个acivity获得,这里1是上传,2是拍照
	    	Bitmap bm = null;  
	    	ContentResolver resolver = getContentResolver();  
	    
	    	if(data != null){
	   
	    		try {  
	        	
	    			Uri originalUri = data.getData(); // 获得图片的uri,选择图片时必须从图库中选择不然会报错  
	    			Toast.makeText(this, originalUri.toString(), Toast.LENGTH_SHORT).show();   	
					Log.i("读照片",originalUri.toString());

	    			textView.append(originalUri.toString());
	    			bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //得到bitmap图片
	    			mIV.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 500, 500)); 

	    			String detectresult = new detect(bitmapToBase64(bm)).run();
	    			if(detectresult.contains("face_token")){
	    				String token = detectresult.split("face_token\": \"")[1].split("\"")[0];
	    				textView.setText(new analyze(token).run());
	    			}
	    			else if(detectresult.contains("INVALID_IMAGE_SIZE")||detectresult.contains("IMAGE_FILE_TOO_LARGE"))
	    				textView.setText("图片大小不合适,请控制在2M以内");
	    			else{
	    				textView.setText("");
	    				textView2.setText("错误报告:");
	    				textView2.append(detectresult);
	    			}
	    			mIV.setImageBitmap(bm); 
	      			String[] proj = { MediaStore.Images.Media.DATA }; 
	    			Cursor cursor = managedQuery(originalUri, proj, null, null, null);  
	    			// 按我个人理解 这个是获得用户选择的图片的索引值  
	    			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	    			// 将光标移至开头 ，这个很重要，不小心很容易引起越界  
	    			cursor.moveToFirst();  
	    			// 最后根据索引值获取图片路径  
	    			String path = cursor.getString(column_index);  
	    			//Toast.makeText(this, new detect(base64).run(), Toast.LENGTH_SHORT).show(); 
	    			//Toast.makeText(this, , Toast.LENGTH_SHORT).show();
        
	    		} catch (IOException e) {  
	    			Toast.makeText(this, "读取图片error,请从图库中选择不然会报错", Toast.LENGTH_SHORT).show();   	
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
                bitmap.compress(CompressFormat.JPEG, 50, baos);//Bitmap.compress方法确实可以压缩图片，但压缩的是存储大小，即你放到disk上的大小.bitmap大小不变

	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes,Base64.NO_WRAP);  //
	           
	        }  
	        else 
	        	textView2.append("空的bitmap");
	    } catch (Exception e) {  
        	textView2.append("错误");
	    }
//    	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();  
//	    textView2.setText(result);
	    return result;  
	}  
	
	public Bitmap byteTobitmap(byte[] data){
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public Bitmap rotateBitmap(Bitmap origin, float alpha) {
		if (origin == null) {
	            return null;
	    }
	     int width = origin.getWidth();
	     int height = origin.getHeight();
	        Matrix matrix = new Matrix();
	        matrix.setRotate(alpha);
	        // 围绕原地进行旋转
	        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
	        if (newBM.equals(origin)) {
	            return newBM;
	        }
	        origin.recycle();
	        return newBM;
	    }
	
	public String SplitResult(String result,String attribute){
		switch (attribute) {
		case "sadness":
			return "sadness" + result.split("sadness\":")[1].split(",")[0];
		case "nautral":
			return "neutral" + result.split("neutral\":")[1].split(",")[0];		
		case "disgust":
			return "disgust" + result.split("disgust\":")[1].split(",")[0];
		case "anger":
			return "anger" + result.split("anger\":")[1].split(",")[0];
		case "surprise":
			return "surprise" + result.split("surprise\":")[1].split(",")[0];
		case "fear":
			return "fear" + result.split("fear\":")[1].split(",")[0];
		case "happiness":
			String s = result.split("happiness\":")[1].split(",")[0]; 
			s = s.substring(0,s.length()-1);
			return "happiness" + s;
		default:
			break;
		}
		return "";
	}
	
	public static String emotion(String result){//演示的时候记得说我在算法上下了功夫
		String emotion = "";
		float max = 0;
		int index = -1;
		float[] a = new float[7];
		a[0] = Float.parseFloat(result.split("sadness\":")[1].split(",")[0]);
		a[1] = Float.parseFloat(result.split("neutral\":")[1].split(",")[0]);
		a[2] = Float.parseFloat(result.split("disgust\":")[1].split(",")[0]);
		a[3] = Float.parseFloat(result.split("anger\":")[1].split(",")[0]);
		a[4] = Float.parseFloat(result.split("surprise\":")[1].split(",")[0]);
		String s = result.split("happiness\":")[1].split(",")[0]; 
		s = s.substring(0,s.length()-1);
		a[5] = Float.parseFloat(s);
		a[6] = Float.parseFloat(result.split("fear\":")[1].split(",")[0]);
		for(int i = 0 ; i <= 6 ; i++){
			if(a[i]>max){
				index = i;
				max = a[i];
			}
		}
		switch (index) {
		case 0:
			return "sadness";
		case 1:
			return "neutral";
		case 2:
			return "disgust";
		case 3:
			return "anger";
		case 4:
			return "surprise";
		case 5:
			return "happiness";
		case 6:
			return "fear";
		default:
			break;
		}
		return "";
		
	}
}
