package com.brmc.ctb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Messenger extends Activity {
	
	//Bestandteile des Fensters:
	Button bSend;
	LinearLayout msgs;
	EditText etMsg;
	TextView userName, userId;
	ImageButton userPic;
	
	
	int myId, adId;
	ArrayList<Message> history = new ArrayList<Message>();
	OnClickListener oclSend = new OnClickListener() {
		//für bSend
		@Override
		public void onClick(View v) {
			if (etMsg.getText().toString().length() > 0) {
				Message msg = new Message(etMsg.getText().toString(), new Date().getTime(), myId, adId);
				
				addMessage(msg);

			}
		}

	};
	final static String tag = "ctb";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.messenger);
		etMsg = (EditText) findViewById(R.id.etMsg);
		bSend = (Button) findViewById(R.id.bSend);
		msgs = (LinearLayout) findViewById(R.id.lyMsges);
		bSend.setOnClickListener(oclSend);
		userPic = (ImageButton) findViewById(R.id.ibProfile);
		userName = (TextView) findViewById(R.id.tvName);
		userId = (TextView) findViewById(R.id.tvNumber);
		
		//Lade das Bild, den Namen und den Nachrichtenverkehr zum angegebenen User
		setUser(getIntent().getIntExtra("id", 0));

	}

	protected void addMessage(Message msg) {
		//Fügt dem Fenster eine Nachricht hinzu. Wird von setUser() und bSend.OnClickListener.OnClick() aufgerufen 
		TextView tvMsg = new TextView(getApplicationContext());
		LayoutParams tvLP = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tvLP.setMargins(20, 10, 20, 0);
		tvMsg.setLayoutParams(tvLP);
		
		//Freund: Nachricht wird linksbündig, "Ich": rechtsbündig angezeigt.
		tvMsg.setGravity((msg.sentById != myId) ? Gravity.LEFT : Gravity.RIGHT);
		tvMsg.getLayoutParams();
		Log.i(tag, "" + msg.sentById);
		tvMsg.setText(msg.msg + "\n" + DateFormat.format("dd.MM.yyyy, HH:mm", msg.date));
		etMsg.setText("");
		tvMsg.setTextColor(Color.BLACK);
		msgs.addView(tvMsg);
		
		//history wird beim Beenden gespeichert
		history.add(msg);
	}

	void setUser(int id) {
		Log.i(tag, "User" + id);
		msgs.removeAllViews();
		User ad = Menu.getUser(id);
		adId = id;
		myId = (adId == 1) ? 2 : 1;
		
		//Fenster für Freund einrichten
		userName.setText(ad.getName());
		userId.setText("" + ad.id);
		userPic.setImageBitmap(ad.picture);
		
		//History wird abgerufen
		FileInputStream fis;
		String h = "";
		try {
			fis = openFileInput("history_" + ((myId > adId) ? adId : myId) + "_" + ((myId > adId) ? myId : adId));
			Log.i(tag, fis.available() + " Bytes zu lesen.");
			byte[] input = new byte[fis.available()];
			
			while (fis.read(input) != -1) {
				
				h += new String(input);
			}
			fis.close();
			Log.i(tag, getApplicationContext().fileList()[0] + " fertig eingelesen. So schauts aus:");
			Log.i(tag, h);
			int numberOfMsgs = Integer.parseInt(h.substring(0, h.indexOf("<")));
			int p = h.indexOf("<author:"); // position
			for (int i = 0; i <= numberOfMsgs-1; i++) {

				int author = Integer.parseInt(h.substring(p + 8, h.indexOf(">", p)));
				long date = Long.parseLong(h.substring(h.indexOf("<date:", p) + 6, h.indexOf(">", h.indexOf("<date:", p))));
				String msg = h.substring(h.indexOf("<msg:", p) + 5, h.indexOf(">/", p));

				Message m = new Message(msg, date, author, (author == myId) ? adId : myId);
				Log.i(tag, "Gespeicherte Nachricht '" + m.msg + "' von " + m.sentById + " wird hinzugefügt.");
				addMessage(m);
				p = h.indexOf("<author:", p+1);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Daten nicht gefunden.", Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "I/O-Fehler.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			Toast.makeText(getApplicationContext(), "Fehlerhafte Daten.", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		try {
			FileOutputStream fos = getApplicationContext().openFileOutput("history_" + ((myId > adId) ? adId : myId) + "_" + ((myId > adId) ? myId : adId), Context.MODE_PRIVATE);
			String outputHistory = history.size() + ""; // + "/";
			for (Message m : history) {
				outputHistory += "<author:" + m.sentById + "><date:" + m.date + "><msg:" + m.msg + ">/";
				
			}
			Log.i(tag, outputHistory);
			Log.i(tag, "history_" + myId + "_" + adId + "gespeichert.");
			fos.write(outputHistory.getBytes());
			for (int i = 0; i <= outputHistory.getBytes().length-1; i++) 
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
