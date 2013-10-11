package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.File;

import org.json.JSONObject;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
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
			Log.e("onActivityResult()", e.getMessage());
		}
	}
	
	private void setupActivity() {
		try {
			ImageView badge = (ImageView) findViewById(R.id.showContactBadge);
			TextView name = (TextView) findViewById(R.id.showNameField);
			TextView postal = (TextView) findViewById(R.id.showPostalField);
			TextView extra = (TextView) findViewById(R.id.showExtraInfo);
			
			JSONObject obj = GeneralFunctions.getJSONData(this, "info.json");
			
			location = obj.getString("postal");
			
			name.setText(obj.getString("name"));
			postal.setText(obj.getString("postal"));
			extra.setText(obj.getString("extra"));
			
			GeneralFunctions.setImage(this, badge, obj.getString("image_uri"));
		} catch (Exception e) {
			Intent i = new Intent(this, EditInformationActivity.class);
			startActivity(i);
		}
	}
}
