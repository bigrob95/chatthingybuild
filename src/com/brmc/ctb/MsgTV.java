package com.brmc.ctb;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgTV extends LinearLayout implements OnLongClickListener {
	private static final String tag = "ctb";
	String msg;
	boolean fromMe;
	long date;

	public MsgTV(Context context, String msg, boolean fromMe, long date) {
		super(context);
		this.msg = msg;
		this.fromMe = fromMe;
		this.date = date;
		create(context);

	}

	public MsgTV(Context context, AttributeSet attrs, String msg, boolean fromMe, long date) {
		super(context, attrs);
		this.msg = msg;
		this.fromMe = fromMe;
		this.date = date;
		create(context);

	}

	TextView tvMsg, tvDate;
	Typeface font;

	private void create(Context context) {
		// TODO Auto-generated method stub
		Log.i(tag, msg + "/" + date);

		this.setOrientation(LinearLayout.VERTICAL);
		font = Typeface.createFromAsset(context.getAssets(), "fonts/AppleColorEmoji.ttf");
		TextView tvMsg = new TextView(context), tvDate = new TextView(context);
		LinearLayout llBox = new LinearLayout(context);
		llBox.setOrientation(LinearLayout.VERTICAL);
		LayoutParams thisLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams llBoxLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams tvLP = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams tvDateLP = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		llBoxLP.gravity = (!fromMe) ? Gravity.LEFT : Gravity.RIGHT;
		tvDateLP.gravity = Gravity.RIGHT;

		
		tvLP.setMargins(20, 10, 20, 10);
		tvDateLP.setMargins(0, 0, 20, 0);
		
		
		llBox.setLayoutParams(llBoxLP);
		tvMsg.setLayoutParams(tvLP);
		tvDate.setLayoutParams(tvDateLP);
		this.setLayoutParams(thisLP);
		// Freund: Nachricht wird linksbündig, "Ich": rechtsbündig angezeigt.

		tvMsg.setBackgroundColor(Color.TRANSPARENT);
		tvDate.setBackgroundColor(Color.TRANSPARENT);
		llBox.setBackgroundColor(Color.rgb(255, 187, 55));
		tvMsg.setTextColor(Color.BLACK);
		tvDate.setTextColor(Color.DKGRAY);
		tvMsg.setTextSize(15);
		tvDate.setTextSize(10);

		// Smileys vergrößern

		tvMsg.setTypeface(font);
		tvDate.setTypeface(font);
		tvMsg.setText(msg);
		tvDate.setText(getDateFormat(date));
		
		
		llBox.addView(tvMsg);
		llBox.addView(tvDate);
		addView(llBox);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	private CharSequence getDateFormat(long date) {
		if (DateFormat.format("dd.MM.yyyy", new Date()).equals(DateFormat.format("dd.MM.yyyy", date))) {
			// selber Tag
			return DateFormat.format("HH:mm", date);
		} else {
			return DateFormat.format("dd.MM.yyyy HH:mm", date);
		}
	}

}
