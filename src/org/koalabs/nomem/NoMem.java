package org.koalabs.nomem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NoMem extends ListActivity {

	private ArrayList<Note> notes;
	private String apiKey;
	protected static final int EDIT_NOTE_REQUEST_CODE = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		apiKey = getString(R.string.api_key); // for some reason this won't work outside of this method.
		notes = getNotes(3);
		
		ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this, R.layout.notes, notes);
		setListAdapter(adapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("DEBUG", "Note ID: "+((Note)parent.getAdapter().getItem(position)).getId());
				
				// setting data for sub activity
				Bundle b = new Bundle();
				b.putString("title", ((Note)parent.getAdapter().getItem(position)).getTitle());
				b.putString("body", ((Note)parent.getAdapter().getItem(position)).getBody());
				b.putInt("id", ((Note)parent.getAdapter().getItem(position)).getId());
				
				Intent i = new Intent(view.getContext(), NoteActivity.class);
				i.putExtras(b);
				
				startActivityForResult(i, EDIT_NOTE_REQUEST_CODE);
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void addNote(Note note, Integer user_id) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI("http://192.168.32.110:3000/notes/"+note.getId());
			
			// setting up POST HTTP parameters
			BasicHttpParams put_params = new BasicHttpParams();
			put_params.setParameter("user_id", user_id);
			put_params.setParameter("title", note.getTitle());
			put_params.setParameter("body", note.getBody());
			
			HttpPut request = new HttpPut(uri);
			request.addHeader("API-Key", apiKey);
			request.addHeader("Accept", "application/json");
			request.setParams(put_params);
			
			Log.d("DEBUG", put_params.toString());
			
			HttpResponse response;
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if(status == HttpStatus.SC_OK) {
				Log.d("DEBUG", "SUCCESS! \\o/");
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

	private ArrayList<Note> getNotes(int user_id) {
		ArrayList<Note> notes = new ArrayList<Note>();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI("http://192.168.32.110:3000/notes?user_id="+user_id);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Accept", "application/json");
			request.addHeader("User-Agent", "NoMem/Android");
			request.addHeader("API-Key", apiKey);
			Log.d("DEBUG", uri.toString());
			HttpResponse response;
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();

			if(status == HttpStatus.SC_OK) {

					ByteArrayOutputStream ostream = new ByteArrayOutputStream();
					response.getEntity().writeTo(ostream);
					String result = ostream.toString();
					Log.d("DEBUG", result);

					JSONObject json = new JSONObject(result);
					Log.d("DEBUG", "<jsonobject>\n"+json.toString()+"\n</jsonobject>");
					JSONArray nameArray = json.names();
					JSONArray valArray = json.toJSONArray(nameArray);
					for(int i = 0 ; i < valArray.length() ; i++) {
						Log.d("DEBUG", "<jsonname"+i+">\n"+nameArray.getString(i)+"\n</jsonname"+i+">\n"
								+"<jsonvalue"+i+">\n"+valArray.getJSONArray(i)+"</jsonvalue"+i+">\n");
						
						String title = valArray.getJSONArray(i).getString(0);
						String body = valArray.getJSONArray(i).getString(1);
						Integer id = Integer.parseInt(nameArray.getString(i));
						Log.d("DEBUG", "Note/"+id+" : "+title+"/"+body);
						notes.add(new Note(id, title, body));
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
		return notes;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("DEBUG", "Request code: "+requestCode+"/"+resultCode+"/"+data);
		if(requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == NoteActivity.SUCCESS_RETURN_CODE) {
			Bundle b = data.getExtras();
			String title = b.getString("title");
			String body = b.getString("body");
			int id = b.getInt("id");
			
			Log.d("DEBUG", "Note/"+id+" : "+title+"/"+body);
			
			// finding the right note (maybe there is a better way to do this)
			Iterator<Note> it = notes.iterator();
			Note n = null;
			
			while(it.hasNext()) {
				n = it.next();
				if(n.getId() == id) {
					n.setTitle(title);
					n.setBody(body);
					break;
				}
			}
			
			addNote(n, 3);
		}
	}
}