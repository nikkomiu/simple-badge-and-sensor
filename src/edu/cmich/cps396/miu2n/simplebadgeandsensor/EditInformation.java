package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class EditInformation extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_information);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_information, menu);
		return true;
	}
	
	public void buttonOnClick(View v) {
		if (v.getId() == R.id.contactBadge) {
			
		}
	}

}
