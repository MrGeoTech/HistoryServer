package me.mrgeotech.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerConnector implements Runnable {
	
	private final int PORT = 50000;
	private boolean running = false;
	private static Map<String,Thread> threads;
	
	public ServerConnector() {
		threads = new HashMap<String,Thread>();
	}
	
	public void terminate() {
		this.running = false;
	}
	
	@Override
	public void run() {
		running = true;
		try {
			ServerSocket ss = new ServerSocket(PORT);
			while (running) {
				Socket s = ss.accept();
				Thread t = new Thread(new ClientHandler(s));
				threads.put(s.getInetAddress().toString(), t);
			}
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeThread(String ip) {
		threads.remove(ip);
	}
}
