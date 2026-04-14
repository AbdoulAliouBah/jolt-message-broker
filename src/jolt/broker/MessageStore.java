package jolt.broker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class MessageStore {
	// saveMessage method
	public void saveMessage(String topic, String message) {
	    // write message to topic's file
		FileWriter fw;
		try {
			fw = new FileWriter(topic + ".txt", true);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write(message);
			writer.newLine();
			writer.close();
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

}
	public Queue<String> loadMessages(String topic) {
	    Queue<String> queue = new LinkedList<>();
	    File file = new File(topic + ".txt");
	    if (!file.exists()) return queue;
	    // read lines and add to queue
	    
	    	
	    	try {
	    	    BufferedReader reader = new BufferedReader(new FileReader(file));
	    	    String line;
	    	    while ((line = reader.readLine()) != null) {
	    	        queue.add(line);
	    	    }
	    	    reader.close();
	    	} catch (IOException e) {
	    	    e.printStackTrace();
	    	}	
			
			
			
	    
	    return queue;
	}
	}
