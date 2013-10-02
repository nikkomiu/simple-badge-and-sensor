package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.QuickContactBadge;

public class ShowInformationActivity extends Activity {
	
	private SharedPreferences user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_information);
		
		if ((user = getSharedPreferences("user", 0)) != null)
			if (user.contains("name") || user.contains("extra"))
				return;
		
		Intent i = new Intent(this, EditInformationActivity.class);
		startActivity(i);
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
}
