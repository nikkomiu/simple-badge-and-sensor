package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class GeneralFunctions {
	public static JSONObject getJSONData(Context context, String filename) throws FileNotFoundException, JSONException {
		String fileString = "";
		Scanner file = new Scanner(context.openFileInput(filename));
		
		while(file.hasNextLine())
			fileString += file.nextLine();
		file.close();
		
		JSONObject obj = new JSONObject(fileString);
		
		return obj;
	}
	
	public static void setImage(Context context, ImageView view, String path) {
		Bitmap bmp = decodeImage(new File(path), 300, 500);
		view.setImageBitmap(bmp);
	}
	
	public static Bitmap decodeImage(File f, int width, int height) {
	    try {
	        System.gc();

	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

	        final int requiredWidth = width;
	        final int requiredHeight = height;

	        int sampleScaleSize = 1;

	        while (o.outWidth / sampleScaleSize / 2 >= requiredWidth && o.outHeight / sampleScaleSize / 2 >= requiredHeight)
	            sampleScaleSize *= 2;

	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = sampleScaleSize;

	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (Exception e) {
	    	return null;
	    }
	}
}
