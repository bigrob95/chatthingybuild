package com.brmc.ctb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Messenger extends Activity {

	// Bestandteile des Fensters:
	Button bSend;
	LinearLayout msgs;
	EditText etMsg;
	TextView userName, userId;
	ImageButton userPic;
	ScrollView sv;
	Typeface font;
	int myId, adId;
	int selMsg = -1;
	ArrayList<Message> history = new ArrayList<Message>();
	OnClickListener oclSend = new OnClickListener() {
		// für bSend
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
		setContentView(R.layout.messenger);
		etMsg = (EditText) findViewById(R.id.etMsg);
		bSend = (Button) findViewById(R.id.bSend);
		msgs = (LinearLayout) findViewById(R.id.lyMsges);
		bSend.setOnClickListener(oclSend);
		userPic = (ImageButton) findViewById(R.id.ibProfile);
		userName = (TextView) findViewById(R.id.tvName);
		userId = (TextView) findViewById(R.id.tvNumber);
		sv = (ScrollView) findViewById(R.id.sv);
		// Lade das Bild, den Namen und den Nachrichtenverkehr zum angegebenen
		// User
		setUser(getIntent().getIntExtra("id", 0));
		sv.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sv.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		sv.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				// TODO Auto-generated method stub
				sv.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/AppleColorEmoji.ttf");

		etMsg.setTypeface(font);
		(findViewById(R.id.llEt)).setBackgroundColor(Color.argb(128, 255, 255, 255));
		
		if (getIntent().hasExtra("Forward")) etMsg.setText(getIntent().getStringExtra("Forward"));
	}

	protected void addMessage(Message msg) {
		// Fügt dem Fenster eine Nachricht hinzu. Wird von setUser() und
		// bSend.OnClickListener.OnClick() aufgerufen
		boolean putSpace = (history.size() > 0) ? msg.sentById != history.get(history.size() - 1).sentById : false;
		MsgTV msgtv = new MsgTV(getApplicationContext(), msg.msg, msg.sentById == myId, msg.date, putSpace);
		registerForContextMenu(msgtv);
		msgs.addView(msgtv);

		Log.i(tag, "MsgTV.getWidth = " + msgtv.getWidth());
		Log.i(tag, msgs.getWidth() + "");
		etMsg.setText("");
		history.add(msg);
	}

	void setUser(int id) {
		Log.i(tag, "User" + id);
		msgs.removeAllViews();
		User ad = Menu.getUser(id);
		adId = id;
		myId = (adId == 1) ? 2 : 1;

		// Fenster für Freund einrichten
		userName.setText(ad.getName());
		userId.setText("" + ad.id);
		userPic.setImageBitmap(ad.picture);

		// History wird abgerufen
		FileInputStream fis;
		String h = "";

		try {
			fis = openFileInput("history_" + ((myId > adId) ? adId : myId) + "_" + ((myId > adId) ? myId : adId));
			Log.i(tag, fis.available() + " Bytes zu lesen.");
			byte[] input = new byte[fis.available()];
			String rawData = "";
			while (fis.read(input) != -1) {
				h += new String(input);

			}

			fis.close();
			Log.i(tag, getApplicationContext().fileList()[0] + " fertig eingelesen. So schauts aus:");
			Log.i(tag, h);

			int numberOfMsgs = Integer.parseInt(h.substring(0, h.indexOf("<")));
			int p = h.indexOf("<author:"); // position
			for (int i = 0; i <= numberOfMsgs - 1; i++) {

				int author = Integer.parseInt(h.substring(p + 8, h.indexOf(">", p)));
				long date = Long.parseLong(h.substring(h.indexOf("<date:", p) + 6, h.indexOf(">", h.indexOf("<date:", p))));
				String msg = h.substring(h.indexOf("<msg:", p) + 5, h.indexOf(">/", p));

				Log.i(tag, "In Bytes: " + rawData);

				Message m = new Message(msg, date, author, (author == myId) ? adId : myId);
				Log.i(tag, "Gespeicherte Nachricht '" + m.msg + "' von " + m.sentById + " wird hinzugefügt.");
				addMessage(m);
				p = h.indexOf("<author:", p + 1);
			}

		} catch (FileNotFoundException e) {

			Toast.makeText(getApplicationContext(), "Keine Daten vorhanden.", Toast.LENGTH_LONG).show();

			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "I/O-Fehler.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		catch (IndexOutOfBoundsException e) {
			Toast.makeText(getApplicationContext(), "IndexOutOfBoundsException.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		try {
			FileOutputStream fos = getApplicationContext().openFileOutput(
					"history_" + ((myId > adId) ? adId : myId) + "_" + ((myId > adId) ? myId : adId), Context.MODE_PRIVATE);
			String outputHistory = history.size() + ""; // + "/";
			for (Message m : history) {
				outputHistory += "<author:" + m.sentById + "><date:" + m.date + "><msg:" + m.msg + ">/";

			}
			Log.i(tag, outputHistory);
			Log.i(tag, "history_" + myId + "_" + adId + "gespeichert.");
			fos.write(outputHistory.getBytes());
			for (int i = 0; i <= outputHistory.getBytes().length - 1; i++)
				fos.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		selMsg = msgs.indexOfChild(v);
		inflater.inflate(R.menu.msgtvcm, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("msg", ((MsgTV) msgs.getChildAt(selMsg)).msg);
		switch (item.getItemId()) {
		case R.id.itemCopy : 
			clipboard.setPrimaryClip(clip);
			selMsg = -1;
			Toast.makeText(getApplicationContext(), "In Zwischenablage kopiert.", Toast.LENGTH_LONG).show();
			break;
		case R.id.itemDelete : 
			
			history.remove(selMsg);
			msgs.removeViewAt(selMsg);
			selMsg = -1;
			break;
		case R.id.itemFw:
			Intent intent = new Intent("com.brmc.ctb.MENU");
			intent.putExtra("Forward", ((MsgTV) msgs.getChildAt(selMsg)).msg);
			selMsg = -1;
			
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}
}
