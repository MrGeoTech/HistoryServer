package me.mrgeotech.main;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientChecker {
	
	public static boolean canTrust(Socket client) {
		try {
			File ipFile = new File(Paths.get("").toAbsolutePath().toString() + "/ips.txt");
			Scanner s = new Scanner(ipFile);
			ArrayList<String> ips = new ArrayList<String>();
			while (s.hasNext()) {
				ips.add(s.nextLine());
			}
			s.close();
			if (ips.contains(client.getInetAddress().getHostAddress())) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
