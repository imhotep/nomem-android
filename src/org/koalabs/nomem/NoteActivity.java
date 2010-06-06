package org.koalabs.nomem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteActivity extends Activity {
	
	public static final int SUCCESS_RETURN_CODE = 1;
	public static final int DELETE_RETURN_CODE = 2;
	public static final int CANCEL_RETURN_CODE = 3;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note);
		
		// getting data from main activity
		Intent app_intent = getIntent();
		final Bundle data = app_intent.getExtras();
		final EditText titleView = (EditText)findViewById(R.id.titleId);
		final EditText bodyView = (EditText)findViewById(R.id.bodyId);
		
		// depending on the intent: new/edit
		if(data != null) {
			String title = data.getString("title");
			String body = data.getString("body");
			
			// setting title
			titleView.setText(title);
			
			// setting body
			bodyView.setText(body);
		}
		Button okButton = (Button)findViewById(R.id.ok_btn);
		Button deleteButton = (Button)findViewById(R.id.delete_btn);
		Button cancelButton = (Button)findViewById(R.id.cancel_btn);

		okButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putString("title", titleView.getText().toString());
				b.putString("body", bodyView.getText().toString());
				intent.putExtras(b);
				setResult(SUCCESS_RETURN_CODE, intent);
				finish();				
			}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent();
				setResult(DELETE_RETURN_CODE, intent);
				finish();				
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent();
				setResult(CANCEL_RETURN_CODE, intent);
				finish();				
			}
		});
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

}
