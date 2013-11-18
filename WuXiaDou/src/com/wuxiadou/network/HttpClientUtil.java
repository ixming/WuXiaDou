package com.wuxiadou.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.wuxiadou.network.utils.NetWorkUtils;

public class HttpClientUtil
{
    private final  static String TAG = "HttpClientUtil";
    private final static int TIMEOUT = 35 * 1000;
    private final static int USER_BINDING_TIMEOUT = 121 * 1000;
    
    
    private static HttpClient httpClient = null;
    private static HttpClient userBindingClient = null;


    
    // 解决javax.net.ssl.SSLPeerUnverifiedException no peer certificate
    public static HttpClient getNewHttpClient(Context context)
    {
        try
        {
        	if(httpClient ==null)
        	{
        		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                 trustStore.load(null, null);
                 SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                 sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                 HttpParams params = new BasicHttpParams();
                 HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                 HttpProtocolParams.setContentCharset(params, "UTF-8");
                 HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
                 HttpConnectionParams.setSoTimeout(params, TIMEOUT);
                 SchemeRegistry registry = new SchemeRegistry();
                 registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                 registry.register(new Scheme("https", sf, 443));
                 ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                 httpClient = new DefaultHttpClient(ccm,params);
        	}
        	  httpClient = setNetWork(context, httpClient);
        } catch (Exception e)
        {
            Log.e(TAG, "HttpClientUtil   getNewHttpClient Exception " + e.getMessage());
        }
        return httpClient;
    }

    public static HttpRes proxyHttpGet(Context context, String url,String json,String sign) throws Exception
    {
        HttpGet httpGet = null;
        HttpRes res = null;
        try
        {
        	json=URLEncoder.encode(json,"UTF-8");
        	url+="?" + "p" + "=" + json
        	+ "&" + "sign" + "=" + sign;
        	//Log.w(TAG,"httpGet -->"+url);
        	httpGet = new HttpGet(url);
            httpClient = getNewHttpClient(context);
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null)
            {
                StatusLine line = response.getStatusLine();
                if (line != null)
                {
                    int resCode = line.getStatusCode();
                    if (resCode == HttpStatus.SC_OK)
                    {
                    	res = new HttpRes();
                    	res.setEntity(response.getEntity());
                    	res.setHttpGet(httpGet);
                    }
                }
            }
        } catch (Exception e)
        {
            if(httpGet != null)
            {
                httpGet.abort();
            }
            Log.e(TAG, "get() Exception -- " + e.toString());
            e.printStackTrace();
            res=null;
        } finally
        {
 
            if( httpClient!=null)
            {
                httpClient.getConnectionManager().closeExpiredConnections();
            }
        }
        return res;
    }
    public static HttpRes proxyHttpDelete(Context context,String url ,String json,String sign)
    {
    	HttpDelete httpDelete=null;
    	HttpResponse response=null;
    	HttpRes res=null;
    	try{
        	json=URLEncoder.encode(json,"UTF-8");
        	url+="?" + "p" + "=" + json
        	+ "&" + "sign" + "=" + sign;
    		httpDelete=new HttpDelete(url);
    		httpClient=getNewHttpClient(context);
            response = httpClient.execute(httpDelete);
            if (response != null)
            {
                StatusLine line = response.getStatusLine();
                if (line != null)
                {
                    int resCode = line.getStatusCode();
                    Log.w(TAG," httpClientUtil resCode"+resCode);
                    if (resCode == HttpStatus.SC_OK)
                    {
                    	res = new HttpRes();
                    	res.setEntity(response.getEntity());
                    }
                }
            }
    	}catch(Exception e)
    	{
            e.printStackTrace();
            res =null;
            if(httpDelete != null)
            {
            	httpDelete.abort();
            }
    	}finally
        {
            if( httpClient!=null)
            {
                httpClient.getConnectionManager().closeExpiredConnections();
            }
        }
		return res;
    }

    public static HttpRes proxyHttpPost(Context context, String url, String json,String sign)
    {
    	String logHeader = "url : "+ url +" json : "+ json ;
        HttpPost httpPost = null;
        HttpResponse response = null;
        httpClient = getNewHttpClient(context);
        HttpRes res = null;
        try
        {
            httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json, */*; q=0.01");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            Object obj = httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
            boolean proxyFlag = (obj != null);
            if(proxyFlag)
            {
            	//2013-03-05 解决使用代理服务器时发生服务器未响应错误
                //原因: Expect:100-Continue 默认设置，导致客户端与服务器交互时先验证
                //参见: http://www.cnblogs.com/cxd4321/archive/2012/01/30/2331621.html
                //HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
               /*httpPost.setHeader("Connection", "Close");*/
            }else
            {
            	//httpPost.setHeader("Connection", "Keep-Alive");
            }
            //httpPost.setHeader("Connection", "close");
            /***/
            /***/
            httpPost = addParams(httpPost, json,sign);
            response = httpClient.execute(httpPost);
            if (response != null)
            {
                StatusLine line = response.getStatusLine();
                Log.i(TAG, logHeader+ " proxyHttpPost StatusLine------->" + line);
                if (line != null)
                {
                    int resCode = line.getStatusCode();
                    Log.i(TAG, logHeader + "proxyHttpPost resCode -- >" + resCode );
                    if (resCode >199&&resCode<300)
                    {
                    	res = new HttpRes();
                    	res.setEntity(response.getEntity());
                    	res.setHttpPost(httpPost);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, logHeader + " proxyHttpPost connect : " + url + " Exception  " + e.getMessage());
            res =null;
            if(httpPost != null)
            {
            	httpPost.abort();
            }
        }finally
        {
        	if(res == null && httpPost != null)
        	{
        		httpPost.abort();
        	}
        	if( httpClient!=null)
        	{
        		httpClient.getConnectionManager().closeExpiredConnections();
        	}
        }
        return res;
    }
    
    
    /**
     * 使用中...
     */
    public static HttpRes proxyHttpPostFile(Context context, String url, String json,String sign,
    		Bundle reqExtras)
    {
        if (null == reqExtras || reqExtras.isEmpty()) {
        	return null;
        }
        
    	String logHeader = "proxyHttpPostFile url : "+ url +" json : "+ json ;
        HttpPost httpPost = null;
        HttpResponse response = null;
        httpClient = getNewHttpClient(context);
        HttpRes res = null;
        try
        {
            httpPost = new HttpPost(url);
            final Charset cs = Charset.forName("UTF-8");
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart(new FormBodyPart("p", new StringBody(json, cs)));
            entity.addPart(new FormBodyPart("sign", new StringBody(sign)));
            //entity.addPart("p", new StringBody(json, Charset.forName("UTF-8")));
            //entity.addPart("sign", new StringBody(sign));
            for (String key : reqExtras.keySet()) {
            	ByteArrayOutputStream out = null;
            	FileInputStream in = null;
            	try {
            		File file = new File(reqExtras.getString(key));
                	if (file.exists()) {
                		out = new ByteArrayOutputStream();
                		in = new FileInputStream(file);
                		int len = -1;
                		byte buf[] = new byte[512];
                		while (-1 != (len = in.read(buf))) {
                			out.write(buf, 0 , len);
                		}
                		out.flush();
                		ByteArrayBody picBody = new ByteArrayBody(out.toByteArray(),
                				MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"), file.getName());
            	        entity.addPart(new FormBodyPart(key, picBody));
                	}
				} catch (Exception e) {
					Log.e(TAG, "proxyHttpPostFile Exception: " + e.getMessage());
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (Exception e_in) { }
					}
					if (null != out) {
						try {
							out.close();
						} catch (Exception e_out) { }
					}
				}
			}
            httpPost.setEntity(entity);
            
            httpPost.addHeader("Accept", "application/json, image/*, */*; q=0.01");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            Object obj = httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
            boolean proxyFlag = (obj != null);
            if(proxyFlag)
            {
            	//2013-03-05 解决使用代理服务器时发生服务器未响应错误
                //原因: Expect:100-Continue 默认设置，导致客户端与服务器交互时先验证
                //参见: http://www.cnblogs.com/cxd4321/archive/2012/01/30/2331621.html
                //HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
               /*httpPost.setHeader("Connection", "Close");*/
            }else
            {
            	//httpPost.setHeader("Connection", "Keep-Alive");
            }
            //httpPost.setHeader("Connection", "close");
            /***/
            /***/
            //httpPost = addParams(httpPost, json,sign);
            response = httpClient.execute(httpPost);
            if (response != null)
            {
                StatusLine line = response.getStatusLine();
                Log.i(TAG, logHeader+ " proxyHttpPostFile StatusLine------->" + line);
                if (line != null)
                {
                    int resCode = line.getStatusCode();
                    Log.i(TAG, logHeader + "proxyHttpPostFile resCode -- >" + resCode );
                    if (resCode >= HttpStatus.SC_OK && resCode < 300)
                    {
                    	res = new HttpRes();
                    	res.setEntity(response.getEntity());
                    	res.setHttpPost(httpPost);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, logHeader + " proxyHttpPostFile connect : " + url + " Exception  " + e.getMessage());
            res =null;
            if(httpPost != null)
            {
            	httpPost.abort();
            }
        }finally
        {
        	if(res == null && httpPost != null)
        	{
        		httpPost.abort();
        	}
        	if( httpClient!=null)
        	{
        		httpClient.getConnectionManager().closeExpiredConnections();
        	}
        }
        return res;
    }
    
    /**
     * 使用中...
     */
    public static HttpRes proxyHttpPutFile(Context context, String url, String json,String sign,
    		Bundle reqExtras)
    {
        if (null == reqExtras || reqExtras.isEmpty()) {
        	return null;
        }
        
    	String logHeader = "proxyHttpPostFile url : "+ url +" json : "+ json ;
        HttpPut httpPut = null;
        HttpResponse response = null;
        httpClient = getNewHttpClient(context);
        HttpRes res = null;
        try
        {
        	httpPut = new HttpPut(url);
            final Charset cs = Charset.forName("UTF-8");
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart(new FormBodyPart("p", new StringBody(json, cs)));
            entity.addPart(new FormBodyPart("sign", new StringBody(sign)));
            //entity.addPart("p", new StringBody(json, Charset.forName("UTF-8")));
            //entity.addPart("sign", new StringBody(sign));
            for (String key : reqExtras.keySet()) {
            	ByteArrayOutputStream out = null;
            	FileInputStream in = null;
            	try {
            		File file = new File(reqExtras.getString(key));
                	if (file.exists()) {
                		out = new ByteArrayOutputStream();
                		in = new FileInputStream(file);
                		int len = -1;
                		byte buf[] = new byte[512];
                		while (-1 != (len = in.read(buf))) {
                			out.write(buf, 0 , len);
                		}
                		out.flush();
                		ByteArrayBody picBody = new ByteArrayBody(out.toByteArray(),
                				MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"), file.getName());
            	        entity.addPart(new FormBodyPart(key, picBody));
                	}
				} catch (Exception e) {
					Log.e(TAG, "proxyHttpPostFile Exception: " + e.getMessage());
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (Exception e_in) { }
					}
					if (null != out) {
						try {
							out.close();
						} catch (Exception e_out) { }
					}
				}
			}
            httpPut.setEntity(entity);
            
            httpPut.addHeader("Accept", "application/json, image/*, */*; q=0.01");
            httpPut.addHeader("Accept-Encoding", "gzip,deflate");
            Object obj = httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
            boolean proxyFlag = (obj != null);
            if(proxyFlag)
            {
            	//2013-03-05 解决使用代理服务器时发生服务器未响应错误
                //原因: Expect:100-Continue 默认设置，导致客户端与服务器交互时先验证
                //参见: http://www.cnblogs.com/cxd4321/archive/2012/01/30/2331621.html
                //HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
               /*httpPost.setHeader("Connection", "Close");*/
            }else
            {
            	//httpPost.setHeader("Connection", "Keep-Alive");
            }
            //httpPost.setHeader("Connection", "close");
            /***/
            /***/
            //httpPost = addParams(httpPost, json,sign);
            response = httpClient.execute(httpPut);
            if (response != null)
            {
                StatusLine line = response.getStatusLine();
                Log.i(TAG, logHeader+ " proxyHttpPostFile StatusLine------->" + line);
                if (line != null)
                {
                    int resCode = line.getStatusCode();
                    Log.i(TAG, logHeader + "proxyHttpPostFile resCode -- >" + resCode );
                    if (resCode >= HttpStatus.SC_OK && resCode < 300)
                    {
                    	res = new HttpRes();
                    	res.setEntity(response.getEntity());
                    	res.setHttpPut(httpPut);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, logHeader + " proxyHttpPostFile connect : " + url + " Exception  " + e.getMessage());
            res =null;
            if(httpPut != null)
            {
            	httpPut.abort();
            }
        }finally
        {
        	if(res == null && httpPut != null)
        	{
        		httpPut.abort();
        	}
        	if( httpClient!=null)
        	{
        		httpClient.getConnectionManager().closeExpiredConnections();
        	}
        }
        return res;
    }

    private static HttpClient setNetWork(Context context, HttpClient client)
    {
        String netType = NetworkManager.getNetWorkType((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (!NetWorkUtils.WIFI_STATE.equalsIgnoreCase(netType))
        {
            if (!NetWorkUtils.isOPhone())
            {
                Map<String, Object> map = NetworkManager.getProxy();
                if (map != null && !map.isEmpty())
                {
                	if(android.os.Build.VERSION.SDK_INT <=7)
                	{
                		 String proxyHost = (String) map.get(NetworkManager.PROXY_HOST);
                         int proxyPort = (Integer) map.get(NetworkManager.PROXY_PORT);
                         HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                         client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                	}
                }
                else
                {
                    // cmnet set proxy
                    client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
                }
            }
            	
        }
        else if (NetWorkUtils.WIFI_STATE.equalsIgnoreCase(netType))
        {
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
        }
        return client;

    }

    private static HttpPost addParams(HttpPost httpPost, String json,String sign)
    {
        UrlEncodedFormEntity urlEncode = null;
        List<NameValuePair> params = null;
        try
        {
        	Log.i(TAG, "addParams httpPost------>" + httpPost);
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("p", json));
            params.add(new BasicNameValuePair("sign",sign));
            Log.i(TAG, "params size " + params.size());
            urlEncode = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(urlEncode);

        } catch (Exception e)
        {
            Log.e(TAG, "addParams Exception " + e.getMessage());
        } finally
        {
              if (params != null) 
              { 
            	  params.clear(); 
            	  params = null; 
               } 
              if (urlEncode != null) {
              urlEncode = null; 
              }
             
        }
        return httpPost;
    }
    private static HttpDelete addParams(HttpDelete httpDelete, String json,String sign)
    {
        UrlEncodedFormEntity urlEncode = null;
        List<NameValuePair> params = null;
        try
        {
        	Log.i(TAG, "addParams httpDelete------>" + httpDelete);
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("p", json));
            params.add(new BasicNameValuePair("sign",sign));
            Log.i(TAG, "params size " + params.size());
            urlEncode = new UrlEncodedFormEntity(params, "utf-8");
           // HttpParams params=new 
           //httpDelete.setParams(params);
          //httpDelete.setEntity(urlEncode);

        } catch (Exception e)
        {
            Log.e(TAG, "httpDelete  addParams Exception " + e.getMessage());
        } finally
        {
              if (params != null) 
              { 
            	  params.clear(); 
            	  params = null; 
               } 
              if (urlEncode != null) {
              urlEncode = null; 
              }
             
        }
        return httpDelete;
    }

//    public static boolean downloadZipFile(Context context, String url, String zipPath, String json) throws Exception
//    {
//    	HttpPost httpPost = null;
//    	HttpResponse httpResponse = null;
//    	HttpEntity entity = null;
//    	InputStream in = null;
//    	FileOutputStream out = null;
//    	File file = null;
//    	try {
//    			file = new File(zipPath);
//    		    httpClient = getNewHttpClient(context);
//    		    httpPost = new HttpPost(url);
//    		    httpResponse = httpClient.execute(addParams(httpPost, json));
//    	        int responseCode = httpResponse.getStatusLine().getStatusCode();
//    	        if (responseCode == HttpStatus.SC_OK)
//    	        {
//    	            // 获取响应实体
//    	        	entity = httpResponse.getEntity();
//
//		            long contentlength = entity.getContentLength();
//		            Log.d(TAG, "Zip contentlength：" + contentlength);
//		            if (!(contentlength > 0))
//		            {
//			             Log.d(TAG, "Zip File is not exist");
//			             return false;
//		            }
//
//    	            in = entity.getContent();           
//
//    	            out = new FileOutputStream(file);
//
//    	            byte[] bytes = new byte[4096];
//    	            int c;
//
//    	            Log.v(TAG, "server msb file:begin: \n");
//    	            while ((c = in.read(bytes)) != -1)
//    	            {
//    	                String content = new String(bytes, 0, c);
//    	                Log.v(TAG, content);
//    	                out.write(bytes, 0, c);
//    	            }
//    	            out.flush();
//    	            if(contentlength == file.length()){
//    	            	return true;
//    	            }else{
//    	            	if(null != file && file.exists()){
//    	            		file.delete();
//    	            	}
//    	            	
//    	            }
//    	        }
//    	        
//    	        return true;
//			
//		} catch (Exception e) {
//			Log.e(TAG, " downloadZipFile exception : " + e.getMessage());
//			e.printStackTrace();
//			if(null != file && file.exists()){
//         		file.delete();
//         	}
//			if (httpPost != null) {
//				httpPost.abort();
//			}
//			
//		} finally{
//			 if(null != out){
//				 out.close();
//			 }
//			 if(null != in){
//				 in.close();
//			 }
//			 if (entity != null) {
//					entity.consumeContent();
//			 }
//			 if (httpPost != null) {
//					httpPost.abort();
//				}
//			 if (httpClient != null)
//             {
//                 httpClient.getConnectionManager().closeExpiredConnections();
//             }
//		}
//        return false;
//    }


    @SuppressWarnings("finally")
    public static File downloadImageFile(Context context, String url, File file)
    {
        HttpEntity entity = null;
        InputStream conIn = null;
        DataInputStream in = null;
        OutputStream out = null;
        httpClient = getNewHttpClient(context);
        HttpGet httpGet = null;
        long totalSize = 0;
        try
        {
        	long startTime = System.currentTimeMillis();
        	Log.i("downImage", url+ " downImage start-----"+startTime);
        	httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            Log.i("downImage", url + " downloadImageFile httpResponse --->" + httpResponse);
            if (httpResponse != null)
            {
            	long endTime = System.currentTimeMillis();
            	Log.i("downImage", url + " downImage end-----"+endTime +" use time :"+((endTime-startTime)/1000));
                StatusLine line = httpResponse.getStatusLine();
                Log.i("downImage", url+ " downloadImageFile line --->" + line);
                if (line != null)
                {
                    int responseCode = line.getStatusCode();
                    if (responseCode == HttpStatus.SC_OK)
                    {
                        entity = httpResponse.getEntity();
                        if (entity != null)
                        {
                            conIn = entity.getContent();
                            totalSize = entity.getContentLength();
                            in = new DataInputStream(conIn);
                            out = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int byteread = 0;
                            while ((byteread = in.read(buffer)) != -1)
                            {
                                out.write(buffer, 0, byteread);
                            }
                        }
                        else
                        {
                            if (file != null)
                            {
                                file.delete();
                                file = null;
                            }
                        }

                    }
                    else
                    {
                    	Log.i("downImage", url + " downLoadImage Server return error, response code = " + responseCode);
                        if (file != null)
                        {
                            file.delete();
                            file = null;
                        }
                    }
                }
                else
                {
                    if (file != null)
                    {
                        file.delete();
                        file = null;
                    }
                    Log.i("downImage", url + " Server return error, StatusLine  " + line);
                }

            }
            else
            {
                if (file != null)
                {
                    file.delete();
                    file = null;
                }
                Log.i("downImage", url + " Server return error, httpResponse  " + httpResponse);
            }

        } catch (Exception e)
        {
        	Log.e("downImage", url + " downImage Exception -----"+e.getMessage());
            if (file != null)
            {
                file.delete();
                file = null;
            }
            if(httpGet != null)
            {
            	httpGet.abort();
            }
        } finally
        {
        	if(file != null)
        	{
        		if(file.length() != totalSize)
        		{
        			file.delete();
        		}
        	}
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
                if (conIn != null)
                {
                    conIn.close();
                }
                if (entity != null)
                {
                    entity.consumeContent();
                }
                if(httpGet != null)
                {
                	httpGet.abort();
                	httpGet = null;
                }
                if(httpClient != null)
                {
                	httpClient.getConnectionManager().closeExpiredConnections();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return file;
        }
    }

//    @SuppressWarnings("finally")
//    public static File downloadFile(Context context, String url, String json, File file)
//    {
//        HttpEntity entity = null;
//        InputStream conIn = null;
//        DataInputStream in = null;
//        OutputStream out = null;
//        httpClient = getNewHttpClient(context);
//        HttpPost httpPost = null;
//        try
//        {
//            
//            httpPost = new HttpPost(url);
//            HttpResponse httpResponse = httpClient.execute(addParams(httpPost, json));
//            int responseCode = httpResponse.getStatusLine().getStatusCode();
//            if (responseCode == HttpStatus.SC_OK)
//            {
//                entity = httpResponse.getEntity();
//                conIn = entity.getContent();
//                
//                in = new DataInputStream(conIn);
//                out = new FileOutputStream(file);
//                byte[] buffer = new byte[1024];
//                int byteread = 0;
//                while ((byteread = in.read(buffer)) != -1)
//                {
//                    out.write(buffer, 0, byteread);
//                }
//            }
//            else
//            {
//            	Log.i(TAG, "Server return error, response code = " + responseCode);
//                if (file != null)
//                {
//                    file.delete();
//                    file = null;
//                }
//            }
//        } catch (Exception e)
//        {
//            Log.e(TAG, e.getMessage()+"");
//            if (file != null)
//            {
//                file.delete();
//                file = null;
//            }
//            if(httpPost != null)
//            {
//            	httpPost.abort();
//            }
//        } finally
//        {
//            try
//            {
//                if (out != null)
//                {
//                    out.close();
//                }
//                if (in != null)
//                {
//                    in.close();
//                }
//                if (conIn != null)
//                {
//                    conIn.close();
//                }
//                if (entity != null)
//                {
//                    entity.consumeContent();
//                }
//                if(httpPost != null)
//                {
//                	httpPost.abort();
//                }
//                if(httpClient != null)
//                {
//                	httpClient.getConnectionManager().closeExpiredConnections();
//                }
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            return file;
//        }
//    }


}
