package com.frameworkexample.android.common.sharebind;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

/**
 * Android中，静默（不调用客户端）通过新浪微博分享。
 * @author Yin Yong
 * @version 1.0
 */
class ShareByWeiboUtility {

	private static final String TAG = ShareByWeiboUtility.class.getSimpleName();

	private static final String PARAMS_SOURCE = "source";
	private static final String PARAMS_STATUS = "status";
	private static final String PARAMS_PIC = "pic";
	
	// HTTP method
	public static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";
	public static final String HTTPMETHOD_DELETE = "DELETE";
	
	final String CONST_HMAC_SHA1 = "HmacSHA1";
	final String CONST_SIGNATURE_METHOD = "HMAC-SHA1";
	final String CONST_OAUTH_VERSION = "1.0";

	final String BOUNDARY = "7cd4a6d158c";
	final String MP_BOUNDARY = "--" + BOUNDARY;
	final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static final ShareByWeiboUtility newInstance() {
		return new ShareByWeiboUtility();
	}

	private Bitmap mBitmap;
	private String mAccessToken;
	private final Map<String, String> mParams = new HashMap<String, String>();

	public ShareByWeiboUtility setAccessToken(String accessToken) {
		mAccessToken = accessToken;
		return this;
	}
	
	/**
	 * 设置APP_KEY
	 */
	public ShareByWeiboUtility setSource(String source) {
		mParams.put(PARAMS_SOURCE, source);
		return this;
	}

	/**
	 * 设置消息内容
	 */
	public ShareByWeiboUtility setMessage(String status) {
		mParams.put(PARAMS_STATUS, status);
		return this;
	}

	public ShareByWeiboUtility setPic(String picFile) {
		mParams.put(PARAMS_PIC, picFile);
		return this;
	}
	
	public ShareByWeiboUtility setPic(Bitmap bm) {
		mBitmap = bm;
		return this;
	}

	/**
	 * @param method {@link ShareByWeiboUtility#HTTPMETHOD_GET} or
	 * {@link ShareByWeiboUtility#HTTPMETHOD_POST} or 
	 * {@link ShareByWeiboUtility#HTTPMETHOD_DELETE}
	 * @return
	 * @throws Exception
	 */
	public String request(Context context, String url, String method)
			throws Exception {
		String rlt = "";
		String file = "";
		try {
			file = mParams.remove(PARAMS_PIC);
			if (null == file || file.length() == 0) {
				rlt = openUrl(context, url, method, mBitmap);
			} else {
				rlt = openUrl(context, url, method, getBitmapFromnFile(file));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			mBitmap = null;
		}
		return rlt;
	}
	
	private String openUrl(Context context, String url, String method,
			Bitmap imageBm) throws Exception {
		String result = "";
		try {
			HttpClient client = getNewHttpClient(context);
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			if (HTTPMETHOD_GET.equalsIgnoreCase(method)) {
				url = url + "?" + encodeUrl();
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (HTTPMETHOD_POST.equalsIgnoreCase(method)) {
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				bos = new ByteArrayOutputStream(1024 * 50);
				if (null != imageBm) {
					paramToUpload(bos);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
					imageContentToUpload(bos, imageBm);
				} else {
					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
					String postParam = encodeParameters();
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				// UrlEncodedFormEntity entity = getPostParamters(params);
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				request = post;
			} else if (HTTPMETHOD_DELETE.equalsIgnoreCase(method)) {
				request = new HttpDelete(url);
			}
			prepareHeader(context, method, request, url);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				result = read(response);
				if (result.startsWith("{") && result.contains("\"error\":")
						&& result.contains("\"error_code\":")) {
					try {
						JSONObject jo = new JSONObject(result);
						throw new Exception("error: " + jo.getString("error")
								+ ", error_code: " + jo.getInt("error_code"));
					} catch (Exception e) {
						Log.e(TAG, "openUrl Exception: " + e);
						throw e;
					}
				} else {
					throw new Exception("error: "
							+ String.format(status.toString())
							+ ", error_code: " + statusCode);
				}
			}
			// parse content stream from response
			result = read(response);
			return result;
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	// 解决javax.net.ssl.SSLPeerUnverifiedException no peer certificate
	private final static int TIMEOUT = 35 * 1000;

	private static HttpClient getNewHttpClient(Context context) {
		HttpClient httpClient = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "gbk");
			HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, TIMEOUT);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			httpClient = new DefaultHttpClient(ccm, params);
			httpClient = setNetWork(context, httpClient);
		} catch (Exception e) {
			Log.e(TAG, "getNewHttpClient Exception " + e.getMessage());
		}
		return httpClient;
	}

	private static HttpClient setNetWork(Context context, HttpClient client) {
		String netType = getNetworkType(context);
		if (!"WIFI".equalsIgnoreCase(netType)) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			if (null != proxyHost && proxyHost.length() > 0) {
				if (android.os.Build.VERSION.SDK_INT <= 7) {
					HttpHost proxy = new HttpHost(proxyHost, proxyPort);
					client.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
			} else {
				// cmnet set proxy
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
						null);
			}
		} else if ("WIFI".equalsIgnoreCase(netType)) {
			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
		return client;
	}

	private static String getNetworkType(Context context) {
		String networkType = "UNKNOWN";
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null) {
				switch (networkInfo.getType()) {
				case ConnectivityManager.TYPE_WIFI: {
					networkType = "WIFI";
					break;
				}
				default: {
					if (!TextUtils.isEmpty(networkInfo.getExtraInfo())) {
						networkType = networkInfo.getExtraInfo().toUpperCase();
					} else {
						Log.w(TAG, "networkInfo.getExtraInfo() is empty!");
						networkType = networkInfo.getTypeName();
					}
					break;
				}
				}
			}
			networkType = TextUtils.isEmpty(networkType) ? "UNKNOWN"
					: networkType;
			Log.w(TAG, "getNetworkType = " + networkType);
		} catch (Exception e) {
			networkType = "";
			e.printStackTrace();
		}
		return networkType;
	}

	// for get method
	private String encodeUrl() {
		if (null == mParams || mParams.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : mParams.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key)).append("=")
					.append(URLEncoder.encode(mParams.get(key)));
		}
		return sb.toString();
	}

	/**
	 * Upload image into output stream .
	 * 
	 * @param out
	 *            : output stream for uploading weibo
	 * @param imgpath
	 *            : bitmap for uploading
	 * @return void
	 */
	private void imageContentToUpload(OutputStream out, Bitmap imgpath)
			throws Exception {
		StringBuilder temp = new StringBuilder();

		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"")
				.append("news_image").append("\"\r\n");
		String filetype = "image/png";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		try {
			out.write(res);
			imgpath.compress(CompressFormat.PNG, 75, out);
			out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
		}
	}

	/**
	 * Upload weibo contents into output stream .
	 * 
	 * @param baos
	 *            : output stream for uploading weibo
	 * @param params
	 *            : post parameters for uploading
	 * @return void
	 */
	private void paramToUpload(OutputStream baos) throws Exception {
		for (String key : mParams.keySet()) {
			StringBuilder temp = new StringBuilder();
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key)
					.append("\"\r\n\r\n");
			temp.append(mParams.get(key)).append("\r\n");
			byte[] res = temp.toString().getBytes();
			try {
				baos.write(res);
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
	}

	private String encodeParameters() {
		if (null == mParams || mParams.isEmpty()) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (String key : mParams.keySet()) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(mParams.get(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();
	}

	// 设置http头,如果authParam不为空，则表示当前有token认证信息需要加入到头中
	private void prepareHeader(Context context, String httpMethod,
			HttpUriRequest request, String url) throws Exception {
		request.setHeader("Accept-Encoding", "gzip");
		if (null != mParams && !mParams.isEmpty()) {
			String authHeader = generateAuthHeader(context, httpMethod, url);
			if (authHeader != null) {
				request.setHeader("Authorization", authHeader);
			}
		}
		request.setHeader("User-Agent",
				System.getProperties().getProperty("http.agent")
						+ " WeiboAndroidSDK");
	}

	private String generateAuthHeader(Context context, String method, String url)
			throws Exception {
//		// step 1: generate timestamp and nonce
//		final long timestamp = System.currentTimeMillis() / 1000;
//		final long nonce = timestamp + (new Random()).nextInt();
//		// step 2: authParams有两个用处：1.加密串一部分 2.生成最后Authorization头域
//		mParams.put("oauth_consumer_key", SinaSNSAPI.getSinaAppKey(context));
//		mParams.put("oauth_nonce", String.valueOf(nonce));
//		mParams.put("oauth_signature_method", CONST_SIGNATURE_METHOD);
//		mParams.put("oauth_timestamp", String.valueOf(timestamp));
//		mParams.put("oauth_version", CONST_OAUTH_VERSION);
//		String token = SinaSNSAPI.getSinaToken(context);
//		if (null != token && token.length() > 0) {
//			mParams.put("oauth_token", token);
//		}
//		Map<String, String> signatureBaseParams = parseUrlParameters(url);
//		// step 3: 生成用于签名的base String
//		StringBuffer base = new StringBuffer(method).append("&")
//				.append(encode(constructRequestURL(url))).append("&");
//		base.append(encode(encodeParameters(signatureBaseParams, "&", false)));
//		String oauthBaseString = base.toString();
//		// step 4: 生成oauth_signature
//		String signature = "";// generateSignature(oauthBaseString, token);
//		mParams.put("oauth_signature", signature);
//		// step 5: for additional parameters
//		this.addAdditionalParams(authParams, params);
//		return "OAuth " + encodeParameters(mParams, ",", true);
		return "OAuth2 " + mAccessToken;
	}

	// 解析url中参数对,存储到signatureBaseParams
	@SuppressWarnings("unused")
	private Map<String, String> parseUrlParameters(String url) throws Exception {
		int queryStart = url.indexOf("?");
		if (-1 != queryStart) {
			String[] queryStrs = url.substring(queryStart + 1).split("&");
			try {
				Map<String, String> map = new HashMap<String, String>();
				for (String query : queryStrs) {
					String[] split = query.split("=");
					if (split.length == 2) {
						map.put(URLDecoder.decode(split[0], "UTF-8"),
								URLDecoder.decode(split[1], "UTF-8"));
					} else {
						map.put(URLDecoder.decode(split[0], "UTF-8"), "");
					}
				}
				return map;
			} catch (UnsupportedEncodingException e) {
				throw new Exception(e);
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static String constructRequestURL(String url) {
		int index = url.indexOf("?");
		if (-1 != index) {
			url = url.substring(0, index);
		}
		int slashIndex = url.indexOf("/", 8);
		String baseURL = url.substring(0, slashIndex).toLowerCase();
		int colonIndex = baseURL.indexOf(":", 8);
		if (-1 != colonIndex) {
			// url contains port number
			if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
				// http default port 80 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			} else if (baseURL.startsWith("https://")
					&& baseURL.endsWith(":443")) {
				// http default port 443 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			}
		}
		url = baseURL + url.substring(slashIndex);
		return url;
	}

	@SuppressWarnings("unused")
	private static String encodeParameters(Map<String, String> postParams,
			String splitter, boolean quot) {
		if (null == postParams || postParams.isEmpty()) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		for (String key : postParams.keySet()) {
			if (buf.length() != 0) {
				if (quot) {
					buf.append("\"");
				}
				buf.append(splitter);
			}
			buf.append(encode(key)).append("=");
			if (quot) {
				buf.append("\"");
			}
			buf.append(encode(postParams.get(key)));
		}
		if (buf.length() != 0) {
			if (quot) {
				buf.append("\"");
			}
		}
		return buf.toString();
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	private String read(HttpResponse response) throws Exception {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		ByteArrayOutputStream content = null;
		try {
			inputStream = entity.getContent();
			content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}
			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			if (null != content) {
				try {
					content.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public static Bitmap getBitmapFromnFile(String file) {
		Bitmap bitmap = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (Exception e) {
			bitmap = null;
			Log.e(TAG, "getBitmapFromnFile Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return bitmap;
	}

	/**
	 * @param value
	 *            string to be encoded
	 * @return encoded parameters string
	 */
	private static String encode(String value) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && (i + 1) < encoded.length()
					&& encoded.charAt(i + 1) == '7'
					&& encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}
}

class MySSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = null;

	public MySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		sslContext = SSLContext.getInstance("TLS");
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
//		Log.w("MySSLSocketFactory", "socket--->" + socket);
//		Log.w("MySSLSocketFactory", "host--->" + host);
//		Log.w("MySSLSocketFactory", "port--->" + port);
//		Log.w("MySSLSocketFactory", "autoClose--->" + autoClose);
		if (port == -1) {
			port = 443;
		}
		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
}
