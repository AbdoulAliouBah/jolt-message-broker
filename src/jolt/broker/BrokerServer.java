package jolt.broker;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class BrokerServer {
 public  static void main (String[] args){
 MessageStore messageStore = new MessageStore(); 
 HashMap<String ,Queue<String>>messageQueues= new HashMap<>(); 
 File folder = new File("."); 
 File[] files = folder.listFiles(); 
 
 for (File file : files) {
	 if (file.getName().endsWith(".txt")) {
		 String topic = file.getName().replace(".txt", ""); 
		 messageQueues.put(topic, messageStore.loadMessages(topic));
		 System.out.println("Loaded topic from disk: " + topic);
	 }
 }
 
 ServerSocket server;
 HashMap<String, ArrayList<Socket>>subscriptions=new HashMap<>();

	
 try {
		server = new ServerSocket(1602);
		new Thread(new BrokerDashboard(subscriptions, messageQueues)).start();
		while (true) {
		Socket connection = server.accept(); 
		ClientHandler handler = new ClientHandler(connection , subscriptions,messageStore ,messageQueues ); 
		new  Thread(handler).start(); 
		}
		
 }	
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	 
		
	}
 }