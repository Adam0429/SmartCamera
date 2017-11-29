package com.smartcemera;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	    if (resultCode == Activity.RESULT_OK) {  
	        if (requestCode == 1) {  
	            Uri uri = data.getData();  
//	            Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();  
	            String string = new detect(uri.getPath().toString()).run();
	            Toast.makeText(this, "文件路径："+string, Toast.LENGTH_SHORT).show();  

	        }  
	    }  
	}  
}
