package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.FileOutputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.QuickContactBadge;

public class EditInformationActivity extends Activity {
	
	private SharedPreferences user;
	private EditText name;
	private EditText extra;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_information);
		
		user = getSharedPreferences("user", 0);
		name = (EditText) findViewById(R.id.nameField);
		extra = (EditText) findViewById(R.id.extraInfo);
		
		QuickContactBadge badge = (QuickContactBadge) findViewById(R.id.contactBadge);
		if (user.getBoolean("external", false)) {
			try {
				badge.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(user.getString("image_uri", ""))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
			
		name.setText(user.getString("name", ""));
		extra.setText(user.getString("extra", ""));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_information, menu);
		return true;
	}

	public void buttonOnClick(View v) {
		Intent i;
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		
		switch (v.getId()) {
			case R.id.choosePhoto:
				i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("image/*");
				startActivityForResult(i, 4712);
			    break;
			
			case R.id.takePhoto:
				i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(i, 4711);
				break;
			
			case R.id.editSubmitButton:
				SharedPreferences.Editor editor = user.edit();
				editor.putString("name", name.getText().toString());
				editor.putString("extra", extra.getText().toString());
				editor.commit();
				break;
				
			case R.id.testButton:
				String list = "";
				for (String file : fileList())
					list += file + '\n';
				d.setTitle("Information")
				 .setMessage("Name: "+user.getString("name", "")+"\nExtra: "+user.getString("extra", "")+user.getString("image_uri", "")+"\nDirectory: "+list)
				 .show();
				break;
				
			default:
				d.setTitle("Error")
				 .setMessage("That Button Does Not Exist!")
				 .show();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		QuickContactBadge badge = (QuickContactBadge) findViewById(R.id.contactBadge);
		SharedPreferences.Editor editor = user.edit();
		
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == 4711) {
					Bitmap img = (Bitmap) data.getExtras().get("data");
					FileOutputStream stream = openFileOutput("user_img.jpg", 0);
					img.compress(Bitmap.CompressFormat.JPEG, 90, stream);
					stream.close();
					badge.setImageBitmap(img);
					editor.putBoolean("external", false);
				}
				if (requestCode == 4712) {
					editor.putString("image_uri", data.getData().toString());
					editor.putBoolean("external", true);
					badge.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
				}
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
