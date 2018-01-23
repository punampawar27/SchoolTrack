package com.schooltrack.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ParserUtil {

	private static HttpClient client = null;
	private Context _act;

	public ParserUtil(Context _act2) {
		_act = _act2;
	}

	InputStream getInputStreamFromApi(String url) throws Exception {
		Log.i("Api Hit ->", "->" + url);
		InputStream is = null;

		SharedPreferences shared = _act.getSharedPreferences("appdata", 0);
		String timeout = shared.getString("timeOut", "60000");
		int time = Integer.parseInt(timeout);
		if (time < 1000)
			time = time * 1000;
		try {
			url = removeSpace(url);
			final URL xmlUrl = new URL(url);

			HttpURLConnection urlConnection = (HttpURLConnection) xmlUrl.openConnection();
			urlConnection.setConnectTimeout(time);
			urlConnection.setReadTimeout(time);
			urlConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConnection.connect();

			String encoding = urlConnection.getContentEncoding();

			// create appropriate stream wrapper based on encoding type
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				is = new GZIPInputStream(urlConnection.getInputStream());
			} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
				is = new InflaterInputStream(urlConnection.getInputStream(), new Inflater(true));
			} else {
				is = urlConnection.getInputStream();
			}
			// Log.i("Api Response ->", "->" + url);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return is;
	}

	public InputStream getUrlInputStream(String url, String requestMethod, List<NameValuePair> nameValuePairs) {
		try {

			url = removeSpace(url);
			HttpClient httpClient = getDefaultHttpClient();
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
			HttpResponse httpResponse = null;

			if ("GET".equals(requestMethod)) {
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			} else if ("POST".equals(requestMethod)) {
				HttpPost httpPost = new HttpPost(url);

				if (nameValuePairs != null)
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				httpResponse = httpClient.execute(httpPost);
			}

			InputStream is = httpResponse.getEntity().getContent();
			return is;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private HttpClient getDefaultHttpClient() {
		int time =60000;// Integer.parseInt(timeout);
		if (time < 1000)
			time = time * 1000;
		try {
			final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
			final String ENCODING_GZIP = "gzip";
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new AndroidSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, time);
			HttpConnectionParams.setSoTimeout(httpParams, time);
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams, registry);

			// check if httpCilent is already there
			if (client != null)
				return client;

			DefaultHttpClient client = new DefaultHttpClient(ccm, httpParams);

			client.addRequestInterceptor(new HttpRequestInterceptor() {
				public void process(HttpRequest request, HttpContext context) {
					if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
						request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
					}
				}
			});

			client.addResponseInterceptor(new HttpResponseInterceptor() {
				public void process(HttpResponse response, HttpContext context) {
					final HttpEntity entity = response.getEntity();
					final Header encoding = entity.getContentEncoding();
					if (encoding != null) {
						for (HeaderElement element : encoding.getElements()) {
							if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
								class InflatingEntity extends HttpEntityWrapper {
									public InflatingEntity(HttpEntity wrapped) {
										super(wrapped);
									}

									@Override
									public InputStream getContent() throws IOException {
										return new GZIPInputStream(wrappedEntity.getContent());
									}

									@Override
									public long getContentLength() {
										return -1;
									}
								}
								response.setEntity(new InflatingEntity(response.getEntity()));
								break;
							}
						}
					}
				}
			});

			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));

			return client;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// return new DefaultHttpClient();
		}
	}

	public String fetchJSONResponse(String url) throws Exception {

		Log.i("Api Hit, GET->", "->" + url);

		String json = null;
		InputStream is = null;
		url = removeSpace(url);
		try {
			is = getUrlInputStream(url, "GET", null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			json = sb.toString();
			if (json != null) {
				// Log.i("Api Response-GET->", "->" + url);
				return json;
			} else {
				Log.e("Api Failed-GET->", "->" + url);
			}
		} catch (Exception e) {
			if (is != null)
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			Log.e("Buffer Error", "Error converting result " + e.toString());
			throw e;
		}

		return json;

	}

	public String getJsonFromApiByPOST(String url, UrlEncodedFormEntity urlEncodedFormEntity) {

		SharedPreferences shared = _act.getSharedPreferences("appdata", 0);
		String timeout = shared.getString("timeOut", "60000");
		int time = Integer.parseInt(timeout);
		if (time < 1000)
			time = time * 1000;

		String json = null;
		InputStream is = null;
		Log.i("Api Hit, POST ->", "->" + url);
		for (int i = 0; i < 1; i++) {
			// Making HTTP request
			try {
				url = removeSpace(url);
				HttpPost httpPost = new HttpPost(url);
				final String androidVersion = android.os.Build.VERSION.RELEASE;
				httpPost.setHeader("User-Agent", "osVersion=" + androidVersion);
				if (urlEncodedFormEntity != null)
					httpPost.setEntity(urlEncodedFormEntity);

				HttpParams httpParams = new BasicHttpParams();
				int some_reasonable_timeout = time;
				HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
				HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
				HttpClient client = new DefaultHttpClient(httpParams);
				client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
				HttpResponse httpResponse = client.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				if (json != null) {
					// Log.i("Api Response-POST->", url);
					return json;
				} else {
					Log.e("Api Failed-POST->", url);
				}
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
		}

		return json;

	}

	private String removeSpace(String str) {
		try {
			str = str.trim();
			str = str.replaceAll(" ", "%20");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
