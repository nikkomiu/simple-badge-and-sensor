package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

public class EditInformationActivity extends Activity {
	
	ImageView badge;
	private EditText name;
	private EditText extra;
	private EditText postal;
	private Uri pictureUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_information);
		
		badge = (ImageView) findViewById(R.id.contactBadge);
		name = (EditText) findViewById(R.id.nameField);
		postal = (EditText) findViewById(R.id.postalField);
		extra = (EditText) findViewById(R.id.extraInfo);
		
		try {
			String fileString = "";
			Scanner file = new Scanner(openFileInput("info.json"));
			
			while(file.hasNextLine())
				fileString += file.nextLine();
			file.close();
			
			JSONObject obj = new JSONObject(fileString);
			
			name.setText(obj.getString("name"));
			postal.setText(obj.getString("postal"));
			extra.setText(obj.getString("extra"));
			
			pictureUri = Uri.fromFile(new File(obj.getString("image_uri")));
			
			setImageBitmap();
		} catch (Exception e) {
			// Do nothing (file probably doesn't exist)...
		}
		
		ScrollView scrollView = (ScrollView) findViewById(R.id.editScrollView);
		scrollView.smoothScrollBy(0, 100);
		
		setupBadgeOnClick();
	}

	private void setupBadgeOnClick() {
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
							
							StringBuilder path = new StringBuilder()
						    .append(Environment.getExternalStorageDirectory())
						    .append("/Pictures/")
						    .append("user_img")
						    .append(".jpg");
						    
							File file = new File(path.toString());
						    pictureUri = Uri.fromFile(file);
						    i.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
							
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
	}
	
	private void setImageBitmap() {
		badge.setImageBitmap(BitmapFactory.decodeFile(pictureUri.getPath()));
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
				try {
					// Create the JSONObject
					JSONObject obj = new JSONObject();
					obj.put("name", name.getText().toString());
					obj.put("postal", postal.getText().toString());
					obj.put("extra", extra.getText().toString());
					obj.put("image_uri", pictureUri.getPath());
					
					// Write to file
					PrintWriter writer = new PrintWriter(openFileOutput("info.json", MODE_PRIVATE));
					writer.write(obj.toString());
					writer.close();
					
					// Display saved successfully message
					Toast.makeText(EditInformationActivity.this, R.string.saved_changes, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finish();
				return false;
			}
		});
		MenuItem deleteLocation = menu.findItem(R.id.editDeleteLocation);
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

		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == 4712) {
					// Got code block from:
					// http://stackoverflow.com/questions/2507898/how-to-pick-an-image-from-gallery-sd-card-for-my-app-in-android
					
					// Begin Code Block
					Uri selectedImage = data.getData();
		            String[] filePathColumn = {MediaStore.Images.Media.DATA};

		            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();

		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            String filePath = cursor.getString(columnIndex);
		            cursor.close();
		            // End Code Block
					
		            pictureUri = Uri.parse(filePath);
				}
				if (requestCode == 4711 || requestCode == 4712)
					setImageBitmap();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
