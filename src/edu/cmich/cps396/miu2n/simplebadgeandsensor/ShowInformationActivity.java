package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.File;
import java.util.Scanner;

import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowInformationActivity extends Activity {
	
	private String location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_information);
		
		setupActivity();
		
	    Intent intent = new Intent(this, LocationService.class);
		intent.putExtra("location", location);
		
		startService(intent);
		
		ScrollView scrollView = (ScrollView) findViewById(R.id.viewScrollView);
		scrollView.smoothScrollBy(0, 100);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setupActivity();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_information, menu);
		
		// Sets the Menu Option for Editing Information
		MenuItem editInfo = menu.findItem(R.id.edit_information_menu_item);
		editInfo.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem clickedItem) {
                Intent i = new Intent(ShowInformationActivity.this, EditInformationActivity.class);
                startActivity(i);
                return false;
            }
		});
		
		MenuItem deleteLocation = menu.findItem(R.id.showDeleteLocation);
		deleteLocation.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				File f = new File(getFilesDir(), "location.json");
				f.delete();
				Toast.makeText(getApplicationContext(), "Deleted Location Information", Toast.LENGTH_LONG).show();
				return false;
			}
		});
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		ImageView badge = (ImageView) findViewById(R.id.contactBadge);
		
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == 4711) {
					Bitmap map = (Bitmap) data.getExtras().get("data");
					badge.setImageBitmap(map);
				}
				if (requestCode == 4712) {
					badge.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getJSONData(Context context) {
		try {
			String fileString = "";
			Scanner file = new Scanner(context.openFileInput("info.json"));
			
			while(file.hasNextLine())
				fileString += file.nextLine();
			file.close();
			
			JSONObject obj = new JSONObject(fileString);
			
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
	
	private void setupActivity() {
		try {
			ImageView badge = (ImageView) findViewById(R.id.showContactBadge);
			TextView name = (TextView) findViewById(R.id.showNameField);
			TextView postal = (TextView) findViewById(R.id.showPostalField);
			TextView extra = (TextView) findViewById(R.id.showExtraInfo);
			
			JSONObject obj = getJSONData(this);
			
			location = obj.getString("postal");
			
			name.setText(obj.getString("name"));
			postal.setText(obj.getString("postal"));
			extra.setText(obj.getString("extra"));
			
			badge.setImageBitmap(BitmapFactory.decodeFile(Uri.fromFile(new File(obj.getString("image_uri"))).getPath()));
		} catch (Exception e) {
			Intent i = new Intent(this, EditInformationActivity.class);
			startActivity(i);
		}
	}
}
