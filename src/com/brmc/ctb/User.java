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

}
