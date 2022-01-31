package com.communi5.c5.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class HeadNumReader {

	public static void main(String... args) {
		Option option = new Option();
		JCommander cmd = JCommander.newBuilder().addObject(option).build();

		try {
			cmd.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			cmd.usage();
			System.exit(-1);
		}

		try {
			readData(option);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	private static void readData(Option option) throws URISyntaxException, IOException, InterruptedException {
		String uri = option.getUri() + "SIPManagement/rest/v1/headNum?limit=" + option.getLimit();

		if (option.getNum() != null) {
			uri += "&num=" + URLEncoder.encode(option.getNum(), StandardCharsets.UTF_8);
		}

		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.header("X-C5-Application", option.getApp())
				.header("Authorization", "Basic " + getAuthorization(option))
				.header("Accept", "application/json")
				.timeout(Duration.ofSeconds(30))
				.GET();

		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(30))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();

		int total = 0;
		int offset = 0;

		List<HeadNum> numbers;

		do {
			numbers = readOnePage(client, requestBuilder);
			System.err.println("got: " + numbers.size() + " entries");
			total += numbers.size();

			if (!numbers.isEmpty()) {
				handleData(numbers);
			}

			if (numbers.size() == option.getLimit()) {
				// we increment offset for next page and change the URI in the builder
				offset += option.getLimit();
				requestBuilder.uri(new URI(uri + "&offset=" + offset));
			}
		} while (numbers.size() == option.getLimit()); // check if we should try with next page

		System.err.println("Total numbers: " + total);
	}

	private static String getAuthorization(Option option) {
		return new String(Base64.getEncoder().encode((option.getUser() + ':' + option.getPassword()).getBytes()));
	}

	private static List<HeadNum> readOnePage(HttpClient client, HttpRequest.Builder requestBuilder) throws InterruptedException, IOException {
		HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
		System.err.println("rc=" + response.statusCode());

		if (response.statusCode() != 200) {
			System.err.println(response.body());
			return Collections.emptyList();
		} else {
			return getListFromJson(response.body(), HeadNum.class);
		}
	}

	private static <T> List<T> getListFromJson(String data, Class<T> className) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, className));
	}

	private static void handleData(List<HeadNum> numbers) {
		// just write the number and the ported status to console
		numbers.forEach(n -> System.out.printf("%s,%s%n", n.getNum(), n.getPortedStatus()));
	}

	@SuppressWarnings("all")
	static class Option {
		@Parameter(names = "-uri", description = "C5 Provisioning base URI (path not included)")
		private String uri = "http://localhost:8080/";
		@Parameter(names = "-u", description = "Admin user (login name)")
		private String user = "admin@defaultdomain";
		@Parameter(names = "-p", description = "Admin password", required = true, password = true)
		private String password;
		@Parameter(names = "-app", description = "C5 application ID to be set in request header (X-C5-Application)", required = true)
		private String app;
		@Parameter(names = "-l", description = "Limit of maximal entries the server should return (page size)")
		private int limit = 5000;
		@Parameter(names = "-num", description = "Phone number to search (with possible '%' and/or '_' as wildcard)")
		private String num;

		public String getUri() {
			return uri;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

		public String getApp() {
			return app;
		}

		public int getLimit() {
			return limit;
		}

		public String getNum() {
			return num;
		}
	}

	@SuppressWarnings("all")
	static class HeadNum {
		private String num;
		private String portedStatus;
		private long id;
		private long enterpriseId;
		private long domainId;
		private long serviceProviderId;
		private Long numRangeId;
		private Long btUserId;
		private Long screenedNumId;

		public String getNum() {
			return num;
		}

		public void setNum(String num) {
			this.num = num;
		}

		public String getPortedStatus() {
			return portedStatus;
		}

		public void setPortedStatus(String portedStatus) {
			this.portedStatus = portedStatus;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getEnterpriseId() {
			return enterpriseId;
		}

		public void setEnterpriseId(long enterpriseId) {
			this.enterpriseId = enterpriseId;
		}

		public long getDomainId() {
			return domainId;
		}

		public void setDomainId(long domainId) {
			this.domainId = domainId;
		}

		public long getServiceProviderId() {
			return serviceProviderId;
		}

		public void setServiceProviderId(long serviceProviderId) {
			this.serviceProviderId = serviceProviderId;
		}

		public Long getNumRangeId() {
			return numRangeId;
		}

		public void setNumRangeId(Long numRangeId) {
			this.numRangeId = numRangeId;
		}

		public Long getBtUserId() {
			return btUserId;
		}

		public void setBtUserId(Long btUserId) {
			this.btUserId = btUserId;
		}

		public Long getScreenedNumId() {
			return screenedNumId;
		}

		public void setScreenedNumId(Long screenedNumId) {
			this.screenedNumId = screenedNumId;
		}
	}
}
