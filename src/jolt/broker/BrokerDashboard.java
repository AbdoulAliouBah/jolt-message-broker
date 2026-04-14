package jolt.broker;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class BrokerDashboard implements Runnable{
	HashMap<String ,Queue<String>>messageQueues ; 
	 HashMap<String, ArrayList<Socket>>subscriptions ;
	 
	public BrokerDashboard (HashMap<String, ArrayList<Socket>>subscriptions,HashMap<String ,Queue<String>>messageQueues) {
	this.subscriptions = subscriptions;
	 this.messageQueues = messageQueues;

	} 
	public void run (){
		while (true){
		// print the dashboard 

		System.out.println("=== JOLT BROKER ===");
		for (String topic : subscriptions.keySet()) { System.out.println(" - " + topic + " : " + subscriptions.get(topic).size() + " subscribers"); }
		System.out.println("===================");

		try {
		Thread.sleep(5000); }
		catch (InterruptedException e) {
		e.printStackTrace();
		}
		}

}
	
}
