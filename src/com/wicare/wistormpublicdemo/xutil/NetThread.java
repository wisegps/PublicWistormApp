package com.wicare.wistormpublicdemo.xutil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 多线程操作类
 * @author honesty
 */
public class NetThread {
	static String TAG = "NetThread";
	
	/**
	 * @author Wu
	 * 
	 * put
	 *
	 */
	public static class putDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		
		List<NameValuePair> parms;
		public putDataThread(Handler handler,String url,List<NameValuePair> parms,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
			this.parms = parms;
		}

		@Override
		public void run() {
			super.run();
			try {
				BasicHttpParams httpParams = new BasicHttpParams();  
			    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);  
			    HttpConnectionParams.setSoTimeout(httpParams, 10000); 
				HttpClient client = new DefaultHttpClient(httpParams);
		        HttpPut httpPut = new HttpPut(url);	
		        if(parms != null){
		        	httpPut.setEntity(new UrlEncodedFormEntity(parms,HTTP.UTF_8));
		        }
		        HttpResponse response = client.execute(httpPut); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}

	/**
	 * @author Wu
	 *
	 * Post
	 */
	public static class postDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> params;
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what,int index){
		    this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what){
			this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		@Override
		public void run() {
			super.run();
			HttpPost httpPost = new HttpPost(url);
			try {
				 httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				 HttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
				 HttpResponse httpResponse = client.execute(httpPost);
				 Log.d(TAG, "状态" +httpResponse.getStatusLine().getStatusCode());
				 if(httpResponse.getStatusLine().getStatusCode() == 200){
					 String strResult = EntityUtils.toString(httpResponse.getEntity());
					 Message message = new Message();
					 message.what = what;
					 message.obj = strResult;
					 handler.sendMessage(message);	
				 }else{					 
					 Message message = new Message();
					 message.what = what;
					 message.obj = "";
					 handler.sendMessage(message);
				 }
			} catch (Exception e) {
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);			
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * @author Wu
	 *
	 */
	public static class DeleteThread extends Thread{
		Handler handler;
		String url;
		int what;		
		public DeleteThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			try {
				HttpClient client = new DefaultHttpClient();
				HttpDelete httpDelete = new HttpDelete(url);
		        HttpResponse response = client.execute(httpDelete); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
}