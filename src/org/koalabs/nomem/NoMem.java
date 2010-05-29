package org.koalabs.nomem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
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

	static final ArrayList<String> NOTES = getNotes(3);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.notes, NOTES));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("DEBUG", "Element "+id+"/Position "+position);
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static ArrayList<String> getNotes(int user_id) {
		ArrayList<String> notes = new ArrayList<String>();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = new URI("http://192.168.10.138:3000/notes?user_id="+user_id);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Accept", "application/json");
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
					for(int i=0;i<valArray.length();i++) {
						Log.d("DEBUG", "<jsonname"+i+">\n"+nameArray.getString(i)+"\n</jsonname"+i+">\n"
								+"<jsonvalue"+i+">\n"+valArray.getString(i)+"</jsonvalue"+i+">\n");
						notes.add(valArray.getString(i));
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
}