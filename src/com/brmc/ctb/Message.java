package com.brmc.ctb;



public class Message {
String msg;
long date;
int sentById, sentToId;

public Message(String msg, long date, int sentById, int sentToId) {
	this.msg = msg;
	this.date = date;
	this.sentById = sentById;
	this.sentToId = sentToId;
}

}
