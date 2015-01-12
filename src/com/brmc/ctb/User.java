package com.brmc.ctb;

import android.graphics.Bitmap;

public class User {
	String name;
	int id = 0;
	Bitmap picture;

	public User(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		// TODO Auto-generated method stub
		if (this.name.equals("")) {return "Schwänz";}
		return this.name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setPic(Bitmap b) {
		if (b.getWidth() > b.getHeight()) {
			this.picture = Bitmap.createBitmap(b, (b.getWidth()-b.getHeight())/2, 0, b.getHeight(), b.getHeight());
		} else {
			this.picture = Bitmap.createBitmap(b, 0, (b.getHeight()-b.getWidth())/2, b.getWidth(), b.getWidth());
		}
	}

}
