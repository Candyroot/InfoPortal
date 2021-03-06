package com.iBeiKe.InfoPortal.news;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.iBeiKe.InfoPortal.news.RSSHandler;

import android.util.Log;

/**
 * 意见建议和信息通知数据获取的辅助类，
 * 提供对服务器数据的获取，使用阻塞队列来传递数据与控制信息。
 *
 */
public class NewsHandler {
	private BlockingQueue<Map<String, String>> queue;
	private BlockingQueue<Integer> msg;
	
	public NewsHandler(BlockingQueue<Map<String, String>> queue,
			BlockingQueue<Integer> msg) {
		this.queue = queue;
		this.msg = msg;
	}
	
	public void getData(String urlString) throws Exception {
		URL url = new URL(urlString);
		Log.d("getData", urlString);
		HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
		httpconn.connect();
		Log.d("getData", httpconn.getResponseCode() + httpconn.getResponseMessage());
    	if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(new RSSHandler(queue, msg));
    		InputStream input = httpconn.getInputStream();
    		xr.parse(new InputSource(input));
    		Log.d("getData", input.toString());
    		input.close();
    	} else {
        	String httpResponse = httpconn.getResponseMessage();
    		Log.e("News:", "Connect Error: " + httpResponse);
    	}
    }
}
