	package me.mrgeotech.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ClientHandler extends Thread {
	
	private Socket client;
	private final String SQL_IP = "jdbc:mysql://localhost/universalhistorydb?"
			   + "user=" + Server.user
			   + "&password=" + Server.password;
	
	
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	
	public ClientHandler(Socket client) {
		this.client = client;
		this.run();
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Client " + client.getInetAddress().toString() + " Connected!");
			outStream = new ObjectOutputStream(client.getOutputStream());
			inStream = new ObjectInputStream(client.getInputStream());
			String[] inputs = (String[]) inStream.readObject();
			System.out.println("Inputs read!");
			System.out.println(inputs[0]);
			System.out.println(inputs[1]);
			if (inputs[0].equals("close")) {
				return;
			}
			
			// Handles the SQL handling in the other class because I have some sense or organization
			this.handleClient(inputs);
			
			System.out.println("Client " + client.getInetAddress().toString() + " has been handled! Disconecting...");
			ServerConnector.removeThread(client.getInetAddress().toString());
			client.close();
			System.out.println("Client disconnected! Stopping thread...");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.gc();
	}
	
	private void handleClient(String[] inputs) {
			System.out.println(inputs.length);
			if (!(inputs.length == 2 || inputs.length == 9)) {
				System.out.println("Returning");
				return;
			}
			String command = inputs[0];
			// If they are getting a player's history via uuid
			System.out.println(command + ":" + command.equalsIgnoreCase("get"));
			if (command.equalsIgnoreCase("get")) {
				System.out.println("Getting...");
				try {
					Connection mySQL = DriverManager.getConnection(SQL_IP);
					Statement st = mySQL.createStatement();
					ResultSet rs = st.executeQuery("SELECT PlayerUUID, PlayerName, StaffUUID, StaffName, ServerIP, Date, Type, Reason FROM Historys WHERE PlayerUUID='" + inputs[1] + "';");
					ArrayList<String> output = new ArrayList<String>();
					while (rs.next()) {
						output.add(rs.getString("PlayerUUID"));
						output.add(rs.getString("PlayerName"));
						output.add(rs.getString("StaffUUID"));
						output.add(rs.getString("StaffName"));
						output.add(rs.getString("ServerIP"));
						output.add(rs.getString("Date"));
						output.add(rs.getString("Type"));
						output.add(rs.getString("Reason"));
					}
					outStream.writeObject(output);
					System.out.println("[" + java.time.LocalDateTime.now() + "]: " + "SELECT PlayerUUID, PlayerName, StaffUUID, StaffName, ServerIP, Date, Type, Reason FROM Historys WHERE PlayerUUID='" + inputs[1] + "';");
					return;
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (command.equalsIgnoreCase("add")) {
				try {
					Connection mySQL = DriverManager.getConnection(SQL_IP);
					Statement st = mySQL.createStatement();
					st.execute("INSERT INTO Historys (PlayerUUID, PlayerName, StaffUUID, StaffName, ServerIP, Date, Type, Reason) VALUES (" + inputs[1] + ", " + inputs[2] + ", " + inputs[3] + ", " + inputs[4] + ", " + inputs[5] + ", " + inputs[6] + ", " + inputs[7] + ", " + inputs[8] + ");");
					System.out.println("[" + java.time.LocalDateTime.now() + "]: " + "INSERT INTO Historys (PlayerUUID, PlayerName, StaffUUID, StaffName, ServerIP, Date, Type, Reason) VALUES (" + inputs[1] + ", " + inputs[2] + ", " + inputs[3] + ", " + inputs[4] + ", " + inputs[5] + ", " + inputs[6] + ", " + inputs[7] + ", " + inputs[8] + ");");
					return;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}

}
