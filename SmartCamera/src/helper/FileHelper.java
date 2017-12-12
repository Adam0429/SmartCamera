package helper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.renderscript.Script.KernelID;
import android.util.Log;

public class FileHelper {
	private static final  String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static String PicturePath = "";
	private static final String DST_FOLDER_NAME = "SmartCamera";

	
	public static String initPath(){
		if(storagePath.equals("")){
			storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}

	public static void saveBitmap(Bitmap b){

		String path = initPath();
		
		String jpegName = path + "/" + DirNumeber(path) +".jpg";		//use index to be name
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
//			Bitmap b2 = ImageUtil.RotateBitmap(b, -90);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap³É¹¦");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:Ê§°Ü");
			e.printStackTrace();
		}
	}
	
	public static int DirNumeber(String dir){		//use to save 
		File file = new File(dir);
		File[] files = file.listFiles();
		int i = files.length;
		return i+1;
	}
}
	
