package com.brmc.ctb;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends Activity {
	static ArrayList<User> userDb = new ArrayList<User>();
	static int myId = 2;
	int[] connUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		connUsers = new int[2];
		connUsers[0] = 1;
		connUsers[1] = 2;
		userDb.add(new User("Max Christian Biegert", 1));
		getUser(1).setPic(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.tw));
		userDb.add(new User("Robin Maisch", 2));
		getUser(2).setPic(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.nc));
		LinearLayout ll‹ber = new LinearLayout(getApplicationContext());
		ScrollView sv = new ScrollView(getApplicationContext());
		ll‹ber.addView(sv);
		Button bClear = new Button(getApplicationContext());
		bClear.setText("History lˆschen");
		bClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int i = 0;
				for (String s : getApplicationContext().fileList()) {
					if (getApplicationContext().deleteFile(s))
						i++;
				}
				Toast.makeText(getApplicationContext(), i + " erfolgreich", Toast.LENGTH_LONG).show();

			}
		});

		setContentView(ll‹ber);
		LinearLayout ll = new LinearLayout(getApplicationContext());
		ll.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		sv.addView(ll);
		ll‹ber.addView(bClear);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll‹ber.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i <= 1; i++) {
			UserPreview up = new UserPreview(getApplicationContext());
			up.setId(connUsers[i]);
			ll.addView(up);
		}

	}

	public static User getUser(int id) {
		for (User u : userDb) {
			if (u.id == id)
				return u;
		}
		return null;
	}

	public class UserPreview extends LinearLayout implements OnClickListener {
		int id;
		TextView tv;
		ImageView iv;

		public UserPreview(Context context) {
			super(context);
			this.setOnClickListener(this);
			setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
			iv = new ImageView(context);
			iv.setLayoutParams(new LayoutParams(180, 180));
			tv = new TextView(context);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			lp.setMargins(20, 50, 0, 0);
			tv.setLayoutParams(lp);
			this.addView(iv);
			this.addView(tv);

			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			v.setBackgroundColor(Color.BLUE);
			
			Intent intent = new Intent("com.brmc.ctb.MESSENGER");
			if (getIntent().hasExtra("Forward")) {intent.putExtra("Forward", getIntent().getStringExtra("Forward"));}
			intent.putExtra("id", id);
			startActivity(intent);
			v.setBackgroundColor(Color.TRANSPARENT);
		}

		public void setId(int id) {
			iv.setImageBitmap(Menu.getUser(id).picture);
			tv.setText(Menu.getUser(id).name);
			this.id = id;
		}

	}
}
