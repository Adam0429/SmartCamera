package helper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.renderscript.Script.KernelID;
import android.util.Log;

public class FileUtil {
	private static final  String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static String PicturePath = "";
	private static final String DST_FOLDER_NAME = "PlayCamera2";

	/**��ʼ������·��
	 * @return
	 */

	
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

	/**����Bitmap��sdcard
	 * @param b
	 */
	public static void saveBitmap(Bitmap b){

		String path = initPath();
		
		String jpegName = path + "/" + DirNumeber(path) +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap�ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:ʧ��");
			e.printStackTrace();
		}
	}
	
	public static int DirNumeber(String dir){
		File file = new File(dir);
		File[] files = file.listFiles();
		int i = files.length;
		return i+1;
	}
}
	
