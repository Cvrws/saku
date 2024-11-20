package cc.unknown.util.client.irc;

import java.util.ArrayList;

import cc.unknown.Sakura;

public class IrcProcesser {
	
	public IRC sender;
	
	public ArrayList<String> logs = new ArrayList<String>();
	public String latestMessage = "", lastMessageSentByMe = "";
	
	public IrcProcesser(IRC bot) {
		this.sender = bot;
	}
	
	public void messageReceived(String message) {
		if((!(message.equals(lastMessageSentByMe)))) {
			latestMessage = message;
			logs.add(message);
		}
	}
	
	public void sendMessage(String message) {
		sender.sendMessage(message, Sakura.NAME);
	}
	
	public void initBot() {
		sender.init();
	}

}
