package org.gradle;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class AfreecaClientTest {
	
	@Test
	public void getRecommendCntTest() throws IOException {
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		try {
			client = HttpClients.createDefault();
			URIBuilder uriBuilder = new URIBuilder();
			URI uri = uriBuilder.setScheme("http")
				.setHost("live.afreeca.com")
				.setPort(8079)
				.setPath("/app/index.cgi")
				.setParameter("szBjId", "siteking").build();
			HttpGet httpGet = new HttpGet();
			httpGet.setHeader("Accept", "application/xhtml+xml");
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
			httpGet.setURI(uri);
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String entityString = EntityUtils.toString(entity, Charset.forName("utf-8"));
			String substringAfter = StringUtils.substringAfter(entityString, "추천: <span>");
			String substringBefore = StringUtils.substringBefore(substringAfter, "(");
			System.out.println(substringBefore);
			EntityUtils.consume(entity);
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

}
