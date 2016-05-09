package com.dongba.afreeca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AfreecaRatingServer {
	
	private ServerSocket ss;
	
	private long EXPIRES = 201604;
	
	public AfreecaRatingServer(int port) throws IOException {
		ss = new ServerSocket(port);
	}
	
	@SuppressWarnings("resource")
	private void runServer() throws IOException, ClassNotFoundException {
		if (isOverDue()) {
			System.out.println("license expired.");
			System.exit(1);
		}
		
		System.out.println("Waiting client connection...");
		Socket socket = ss.accept();
		System.out.println("Connected Client : " + socket.getInetAddress().getHostName());
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			while (true) {
				String entityString = "";
				String substringAfter = "";
				String substringBefore = "";
				CloseableHttpResponse response = null;
				CloseableHttpClient client = null;
				try {
					client = HttpClients.createDefault();
					URIBuilder uriBuilder = new URIBuilder();
					URI uri = uriBuilder.setScheme("http")
							.setHost("live.afreeca.com")
							.setPort(8079)
							.setPath("/app/index.cgi")
							.setParameter("szBjId", "cjf2906").build();
					HttpGet httpGet = new HttpGet();
					httpGet.setHeader("Accept", "application/xhtml+xml");
					httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
					httpGet.setURI(uri);
					response = client.execute(httpGet);
					HttpEntity entity = response.getEntity();
					entityString = EntityUtils.toString(entity, Charset.forName("utf-8"));
					substringAfter = StringUtils.substringAfter(entityString, "ÃßÃµ: <span>");
					substringBefore = StringUtils.substringBefore(substringAfter, "(");
					String recvMsg = in.readLine();
		            if (recvMsg.indexOf("hey") >= 0) {
		            	writer.print(Integer.parseInt(StringUtils.strip(StringUtils.replace(substringBefore, ",", ""))));
		            	writer.flush();
		            	EntityUtils.consume(entity);
		            }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} finally {
					if (response != null) {
						response.close();
					}
					if (client != null) {
						client.close();
					}
				}
			}
		} finally {
			writer.close();
		}
	}

	private boolean isOverDue() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String thisMonth = sdf.format(System.currentTimeMillis());
		return EXPIRES < Long.parseLong(thisMonth);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		AfreecaRatingServer ratingServer = new AfreecaRatingServer(50002);
		ratingServer.runServer();
	}
}
