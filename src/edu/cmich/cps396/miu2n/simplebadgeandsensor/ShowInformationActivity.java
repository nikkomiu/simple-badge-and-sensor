package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ShowInformationActivity extends Activity {
	
	private SharedPreferences user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_information);
		
		setupActivity();
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
		MenuItem item = menu.findItem(R.id.edit_information_menu_item);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem clickedItem) {
                Intent i = new Intent(ShowInformationActivity.this, EditInformationActivity.class);
                startActivity(i);
                return true;
            }
		});
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		QuickContactBadge badge = (QuickContactBadge) findViewById(R.id.contactBadge);
		
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
	
	protected void setupActivity() {
		if ((user = getSharedPreferences("user", 0)) == null || user.contains("name") == false || user.contains("extra") == false) {
			Intent i = new Intent(this, EditInformationActivity.class);
			startActivity(i);
		}

		TextView name = (TextView) findViewById(R.id.showNameField);
		name.setText(user.getString("name", ""));
		
		TextView postal = (TextView) findViewById(R.id.showPostalField);
		postal.setText(user.getString("postal", ""));
		
		TextView extra = (TextView) findViewById(R.id.showExtraInfo);
		extra.setText(user.getString("extra", ""));
		
		QuickContactBadge badge = (QuickContactBadge) findViewById(R.id.showContactBadge);
		try {
			if (user.getBoolean("external", false)) {
				badge.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(user.getString("image_uri", ""))));
			} else {
				badge.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().toString() + "/" + getString(R.string.image_filename)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		badge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}
}
