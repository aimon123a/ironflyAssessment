package com.company;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Scanner;

public class Main {
	public static int port = 443;
	public static String datafeedServer;

	public static void subscribe(String[] stocks,DataOutputStream dout,SSLSocket socket){
		try{
			String subscribeStocks = "";

			for (int i = 0; i < stocks.length;i++){
				subscribeStocks = stocks[i] + ";";
			}
			System.out.println(subscribeStocks);
			// send the subscribe info to python datafeed provider server
			// format would be something like
			// VOD LN;TELSTRA LN; etc
			dout.writeUTF("s" + subscribeStocks);
			dout.flush();
			dout.close();
			socket.close();
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void unsubscribe(String[] stocks,DataOutputStream dout,SSLSocket socket){
		try{

			String unsubscribeStocks = "";

			for (int i = 0; i < stocks.length;i++){
				unsubscribeStocks = stocks[i] + ";";
			}
			System.out.println(unsubscribeStocks);
			// send the unsubscribe info to python datafeed provider server
			// format would be something like
			// VOD LN;TELSTRA LN; etc
			dout.writeUTF("u" + unsubscribeStocks);
			dout.flush();
			dout.close();
			socket.close();
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		boolean exit = false;
		String input;

		// get address of datafeed server
		System.out.println("Enter IP address of datafeed Server or 'localhost' if running on the same machine: ");
		Scanner scan = new Scanner(System.in);
		datafeedServer = scan.nextLine();
		System.out.println("datafeed server address: " + datafeedServer);

		try{
			SSLSocketFactory ssf = DataFeedServer.sslContext.getSocketFactory();
			SSLSocket socket = (SSLSocket) ssf.createSocket(Main.datafeedServer, 2004);
			socket.startHandshake();

			SSLSession session = socket.getSession();
			Certificate[] certs = session.getPeerCertificates();
			//System.out.println(certs[0]);

			DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
			DataInputStream din=new DataInputStream(socket.getInputStream());

			// get ticker feed
			String feed = din.readUTF();//in.readLine();
			// need to implement function to manage feed
			// public state void updateStockPrice(feed)
			// ideally using a hashmap to store function, or mySQL database for enterprise level of implementation

			//sub and unsub
			String[] toSub = {"VOD LN", "TELSTRA LN"};
			String [] toUnSub = {"TELSTRA LN"};
			System.out.println("feed "+ feed);
			subscribe(toSub, dout, socket);
			unsubscribe(toUnSub, dout, socket);

		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}
