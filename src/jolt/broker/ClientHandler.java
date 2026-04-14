package jolt.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class ClientHandler implements Runnable{
 private Socket connection ; 
 private HashMap<String, ArrayList<Socket>>subscriptions;
 private MessageStore messageStore ; 
 private HashMap<String ,Queue<String>>messageQueues; 
 
	 public ClientHandler(Socket connection,HashMap<String, ArrayList<Socket>>subscriptions,MessageStore messageStore,HashMap<String ,Queue<String>>messageQueues ){
		 this.connection = connection ; 
		 this.subscriptions = subscriptions; 
		 this.messageStore = messageStore ; 
		 this.messageQueues = messageQueues; 
	 }
 
 public void run() {
	 System.out.println("Thread started") ; 
	 BufferedReader reader;
	try {reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	 String message = reader.readLine();
	 if (message == null) return;
	 String [] parts = message.split("\\|"); 
	if (parts[0].trim().equals("SUBSCRIBE")) {
		 
		 
		 
		 String topic = parts[1].trim(); 
		 System.out.println("Subscriber registered for: " + topic);
		 
		 if (subscriptions.containsKey(topic)) {
			 ArrayList<Socket>list= subscriptions.get(topic); 
			 list.add(connection); 
		 }
		 else {
			 ArrayList<Socket>list= new ArrayList<>(); 
			 subscriptions.put(topic, list); 
			 list.add(connection); 
		 }
		 Queue<String> pending = messageQueues.get(topic);
		 if (pending != null && !pending.isEmpty()) {
		     PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		     for (String msg : pending) {
		         writer.println(msg);
		         writer.flush();
		     }
		 }

		}
		 
	else  if  (parts[0].trim().equals("PUBLISH")){
		String topic = parts[1].trim();
		ArrayList<Socket> list = subscriptions.get(topic);
		if (list == null) return;
		String actualMessage = parts[2].trim(); 
		for (Socket SuscriberSocket : list ) {
			PrintWriter writer = new PrintWriter (new OutputStreamWriter(SuscriberSocket.getOutputStream())); 
		
			writer.println(actualMessage); 
			writer.flush(); 
			
		}
		messageStore.saveMessage(topic,actualMessage);
	}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 
}
}