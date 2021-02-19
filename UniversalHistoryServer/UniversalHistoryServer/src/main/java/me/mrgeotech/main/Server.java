package me.mrgeotech.main;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	
	public static String user;
	public static String password;
	private static ServerConnector sc;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			user = args[0];
			password = args[1];
		} else {
			System.err.println("Inproper arguments!");
			System.exit(1);
		}
		sc = new ServerConnector();
		Thread server = new Thread(sc);
		server.start();
		System.out.println("IO chanel started!");
		Scanner s = new Scanner(System.in);
		String input;
		while (true) {
			if (s.hasNext()) {
				input = s.nextLine();
				if (input.equalsIgnoreCase("stop") || input.equalsIgnoreCase("close")) {
					sc.terminate();
					try {
						Socket close = new Socket("localhost", 50000);
						String[] string = new String[1];
						string[0] = "close";
						new ObjectOutputStream(close.getOutputStream()).writeObject(string);
						close.close();
						server.join();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					s.close();
					break;
				}
			}
		}
	}
	
}
