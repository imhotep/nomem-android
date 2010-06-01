package org.koalabs.nomem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteActivity extends Activity {
	
	public static final int SUCCESS_RETURN_CODE = 1;
	public static final int FAILURE_RETURN_CODE = 2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// getting data from main activity
		Intent app_intent = getIntent();
		Bundle data = app_intent.getExtras();
		String title = data.getString("title");
		String body = data.getString("body");
		final int id = data.getInt("id");
		
		setContentView(R.layout.note);
		
		// setting title
		final EditText titleView = (EditText)findViewById(R.id.titleId);
		titleView.setText(title);
		
		// setting body
		final EditText bodyView = (EditText)findViewById(R.id.bodyId);
		bodyView.setText(body);
		
		Button okButton = (Button)findViewById(R.id.ok_btn);
		Button cancelButton = (Button)findViewById(R.id.cancel_btn);

		okButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putString("title", titleView.getText().toString());
				b.putString("body", bodyView.getText().toString());
				b.putInt("id", id);
				intent.putExtras(b);
				setResult(SUCCESS_RETURN_CODE, intent);
				finish();				
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent();
				setResult(FAILURE_RETURN_CODE, intent);
				finish();				
			}
		});
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

}
