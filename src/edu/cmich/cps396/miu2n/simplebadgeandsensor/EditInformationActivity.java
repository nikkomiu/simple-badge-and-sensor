package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.FileOutputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

public class EditInformationActivity extends Activity {
	
	private SharedPreferences user;
	private EditText name;
	private EditText extra;
	private EditText postal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_information);
		
		user = getSharedPreferences("user", 0);
		name = (EditText) findViewById(R.id.nameField);
		postal = (EditText) findViewById(R.id.postalField);
		extra = (EditText) findViewById(R.id.extraInfo);
		
		QuickContactBadge badge = (QuickContactBadge) findViewById(R.id.contactBadge);
		try {
			if (user.getBoolean("external", false)) {
				badge.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(user.getString("image_uri", ""))));
			} else {
				badge.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().toString() + "/" + getString(R.string.image_filename)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Create Badge onClick Listener (Gets a little intense)
		badge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(EditInformationActivity.this);
				dialog.setTitle(R.string.select_photo_dialog_title)
				.setCancelable(false)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setItems(R.array.select_photo_location, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(i, 4711);
						}
						if (which == 1) {
							Intent i = new Intent(Intent.ACTION_GET_CONTENT);
							i.setType("image/*");
							startActivityForResult(i, 4712);
						}
					}
				})
				.show();
			}
		});
			
		name.setText(user.getString("name", ""));
		postal.setText(user.getString("postal", ""));
		extra.setText(user.getString("extra", ""));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_information, menu);
		
		menu.findItem(R.id.menuEditCancelButton).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return false;
			}
		});
		menu.findItem(R.id.menuEditSubmitButton).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				SharedPreferences.Editor editor = user.edit();
				editor.putString("name", name.getText().toString());
				editor.putString("postal", postal.getText().toString());
				editor.putString("extra", extra.getText().toString());
				editor.commit();
				Toast.makeText(EditInformationActivity.this, R.string.saved_changes, Toast.LENGTH_SHORT).show();
				finish();
				return false;
			}
		});
		
		return true;
	}

	public void buttonOnClick(View v) {
		// Intent i; // Removing Old Buttons (no need for intent)
		
		switch (v.getId()) {
			/* Removing Old Buttons
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
				editor.putString("postal", postal.getText().toString());
				editor.putString("extra", extra.getText().toString());
				editor.commit();
				Toast.makeText(this, R.string.saved_changes, Toast.LENGTH_SHORT).show();
				finish();
				break;
			*/
		
			default:
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Congratulations!")
				.setMessage("You have managed to hit a button that doesn't exist!\nYou Win the Nigerian Lottery!!!")
				.setNegativeButton("Go Die!", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				})
				.setPositiveButton("Collect Prize", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});
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
					FileOutputStream stream = openFileOutput(getString(R.string.image_filename), 0);
					img.compress(Bitmap.CompressFormat.JPEG, 90, stream);
					stream.close();
					Bitmap map = BitmapFactory.decodeFile(getFilesDir().toString() + "/" + getString(R.string.image_filename));
					badge.setImageBitmap(map);
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
