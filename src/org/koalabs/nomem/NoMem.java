package org.koalabs.nomem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NoMem extends ListActivity {
	// Menu codes
	protected static final int MENU_NEW_NOTE = 0;
	protected static final int MENU_QUIT = 1;

	// Sub-Activity request codes
	protected static final int EDIT_NOTE_REQUEST_CODE = 100;
	protected static final int NEW_NOTE_REQUEST_CODE = 200;
	
	// Local use
	private ArrayList<Note> notes = null;
	private ArrayAdapter<Note> noteAdapter = null;
	private Runnable viewNotes;
	private ProgressDialog progressDialog = null;
	private Note currentNote;
	
	// Configuration stuff
	private String host = null;
	private String apiKey = null;
	
	// thread handler
	final Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		host = getString(R.string.host);
		apiKey = getString(R.string.api_key);
		notes = new ArrayList<Note>();
		noteAdapter = new ArrayAdapter<Note>(this, R.layout.notes, notes);
		setListAdapter(noteAdapter);
		
		viewNotes = new Runnable() {
			@Override
			public void run() {
				getNotes();
			}
		};
		Thread thread = new Thread(null, viewNotes, "MagentoBackground");
		progressDialog = ProgressDialog.show(NoMem.this, "Please wait...", "Retrieving notes", true);
		thread.start();
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				currentNote = ((Note) parent.getAdapter().getItem(position));
				// setting data for sub activity
				Bundle b = new Bundle();
				b.putString("title", ((Note) parent.getAdapter().getItem(
						position)).getTitle());
				b.putString("body", ((Note) parent.getAdapter().getItem(
						position)).getBody());

				Intent i = new Intent(view.getContext(), NoteActivity.class);
				i.putExtras(b);

				startActivityForResult(i, EDIT_NOTE_REQUEST_CODE);
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private Runnable mUpdateNotes = new Runnable() {
		@Override
		public void run() {
			progressDialog.dismiss();
			if(noteAdapter.getCount() < notes.size()) {
				Log.d("DEBUG", "Adding element to adapter: "+noteAdapter.getCount()+" : notes count:"+notes.size());
				noteAdapter.add(notes.get(notes.size()-1));
			}
			if(noteAdapter.getCount() > notes.size()) {
				Log.d("DEBUG", "Removing element from adapter: "+noteAdapter.getCount() + " : notes count: "+notes.size());
				noteAdapter.remove(currentNote);
			}
			noteAdapter.notifyDataSetChanged();
		}
	};

	private void updateNote(Note note) {
		Integer user_id = 1;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI(host+"/notes/"
					+ note.getId());

			// setting up POST HTTP parameters
			List<NameValuePair> putParams = new ArrayList<NameValuePair>(4);
			putParams
					.add(new BasicNameValuePair("note[user_id]", user_id.toString()));
			putParams.add(new BasicNameValuePair("note[title]", note.getTitle()));
			putParams.add(new BasicNameValuePair("note[body]", note.getBody()));

			HttpPut request = new HttpPut(uri);
			request.addHeader("API-Key", apiKey);
			request.addHeader("Accept", "application/json");
			request.setEntity(new UrlEncodedFormEntity(putParams));

			HttpResponse response;
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				Log.d("DEBUG", "PUT SUCCESS! \\o/");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteNote(Note note) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI(host+"/notes/"
					+ note.getId());
			
			HttpDelete request = new HttpDelete(uri);
			request.addHeader("API-Key", apiKey);
			request.addHeader("Accept", "application/json");

			HttpResponse response;
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				Log.d("DEBUG", "DELETE SUCCESS! \\o/");
				notes.remove(currentNote);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createNote(Note note) {
		Integer user_id = 1;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI(host+"/notes");

			// setting up POST HTTP parameters
			List<NameValuePair> postParams = new ArrayList<NameValuePair>(4);
			postParams
					.add(new BasicNameValuePair("note[user_id]", user_id.toString()));
			postParams.add(new BasicNameValuePair("note[title]", note.getTitle()));
			postParams.add(new BasicNameValuePair("note[body]", note.getBody()));

			HttpPost request = new HttpPost(uri);
			request.addHeader("API-Key", apiKey);
			request.addHeader("Accept", "application/json");
			request.setEntity(new UrlEncodedFormEntity(postParams));

			HttpResponse response;
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_CREATED) {
				Log.d("DEBUG", "POST SUCCESS! \\o/");
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
				String result = ostream.toString();
				
				Log.d("DEBUG", result);
				
				JSONObject json = new JSONObject(result);
				if(json.length() == 1) {
					JSONObject noteObject = json.getJSONObject("note");
					JSONArray nameArray = noteObject.names();
					JSONArray valArray = noteObject.toJSONArray(nameArray);
					for (int i = 0; i < nameArray.length(); i++) {
						Log.d("DEBUG", nameArray.getString(i) + "/" +  valArray.getString(i));
						if(nameArray.getString(i).startsWith("id")) {
							Integer id = Integer.parseInt(valArray.getString(i));
							Log.d("DEBUG", "Created Note ID: " + id);
							note.setId(id);
							notes.add(note);
							Log.d("DEBUG", "Notes size: "+notes.size());
							break;
						}
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getNotes() {
		int user_id = 1;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI(host+"/notes?user_id="
					+ user_id);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Accept", "application/json");
			request.addHeader("User-Agent", "NoMem/Android");
			request.addHeader("API-Key", apiKey);
			HttpResponse response;

			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {

				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
				String result = ostream.toString();
				
				Log.d("DEBUG", result);
				
				JSONObject json = new JSONObject(result);
				
				Log.d("DEBUG", "<jsonobject>\n" + json.toString()
						+ "\n</jsonobject>");
				
				JSONArray nameArray = json.names();
				if(json.length() > 1) {
					JSONArray valArray = json.toJSONArray(nameArray);
					for (int i = 0; i < valArray.length(); i++) {
						Log.d("DEBUG", "<jsonname" + i + ">\n"
								+ nameArray.getString(i) + "\n</jsonname" + i
								+ ">\n" + "<jsonvalue" + i + ">\n"
								+ valArray.getJSONArray(i) + "</jsonvalue" + i
								+ ">\n");
	
						String title = valArray.getJSONArray(i).getString(0);
						String body = valArray.getJSONArray(i).getString(1);
						Integer id = Integer.parseInt(nameArray.getString(i));
						Log.d("DEBUG", "Note/" + id + " : " + title + "/" + body);
						notes.add(new Note(id, title, body));
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runOnUiThread(mUpdateNotes);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == NoteActivity.SUCCESS_RETURN_CODE) {
			Bundle b = data.getExtras();
			final String title = b.getString("title");
			final String body = b.getString("body");

			// finding the right note (maybe there is a better way to do this)
			switch(requestCode) {
			case EDIT_NOTE_REQUEST_CODE:
				currentNote.setTitle(title);
				currentNote.setBody(body);
				Runnable updateNoteRunnable = new Runnable() {
					public void run() {
						updateNote(currentNote);
						mHandler.post(mUpdateNotes);
					}
				};
				progressDialog = ProgressDialog.show(NoMem.this, "Please wait...", "Updating note...", true);
				new Thread(updateNoteRunnable).start();
				break;
			case NEW_NOTE_REQUEST_CODE:
				final Note createdNote = new Note(null, title, body);
				Runnable createNoteRunnable = new Runnable() {
					public void run() {
						createNote(createdNote);
						mHandler.post(mUpdateNotes);
					}
				};
				progressDialog = ProgressDialog.show(NoMem.this, "Please wait...", "Creating note...", true);
				new Thread(createNoteRunnable).start();
				break;
			}
		} else {
			if(resultCode == NoteActivity.DELETE_RETURN_CODE && requestCode == EDIT_NOTE_REQUEST_CODE) {
				Runnable updateNoteRunnable = new Runnable() {
					public void run() {
						deleteNote(currentNote);
						mHandler.post(mUpdateNotes);
					}
				};
				progressDialog = ProgressDialog.show(NoMem.this, "Please wait...", "Deleting note...", true);
				new Thread(updateNoteRunnable).start();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_NOTE, 0, "New Note").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_QUIT, 0, "Quit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case MENU_NEW_NOTE:
			Intent i = new Intent(NoMem.this, NoteActivity.class);
			startActivityForResult(i, NEW_NOTE_REQUEST_CODE);
			return true;
		case MENU_QUIT:
			finish();
			return true;
		}
		return false;
	}
}