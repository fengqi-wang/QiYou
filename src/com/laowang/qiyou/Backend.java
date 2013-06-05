package com.laowang.qiyou;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


public final class Backend extends HandlerThread {
	
	static final int OAUTH = 6100000;
	static final int RELOAD = 6100001;
	static final int PREV = 6100002;
	static final int NEXT = 6100003;
	static final int LOADING = 6100004;
	static final int RELOADED = 6100005;
	
	static final int AUTHRIZED = 6180000;
	static final int UPDATE_UI = 6180001;
	static final int NO_MORE = 6180002;
	

	public Backend(Handler aHandler) {
		// TODO Auto-generated constructor stub
		super("QiYou_Backend");
		mainHandler = aHandler;
		start();
		
		mWorker = new Handler(this.getLooper()) {
			@Override
			 public void handleMessage(Message msg) {
        	    Log.i("Backend",msg.toString());
        	    
           	     switch(msg.what){
           	     case RELOAD:
           	    	 acquireNews();
           	    	 return;
        	     case RELOADED:
        	    	 mIndex = 0; 
        	    	 break;
        	     case PREV:
        	    	 if(--mIndex < 0) { mIndex =0; mainHandler.sendEmptyMessage(NO_MORE);}
         	     	 break;
        	     case NEXT:
        	    	 if(++mIndex > (mJsonArray.length()-1)) { mIndex = (mJsonArray.length()-1);  mainHandler.sendEmptyMessage(NO_MORE);}
        	    	 
        	    	 break;
        	     default:
        	    	 break;
        	     } 
          	     
           	   	 JSONObject jsonObject = (JSONObject)mJsonArray.opt(mIndex);
                 text = jsonObject.optString("text");
                 picture = null;
 	    	     mainHandler.sendEmptyMessage(UPDATE_UI);
 	    	     updatePic();
 	    	     mainHandler.sendEmptyMessage(UPDATE_UI);
           }
		};
	}
	
	public boolean reload(){
		mWorker.sendEmptyMessage(RELOAD);
		return true;
	}
	
	public boolean next(){
		mWorker.sendEmptyMessage(NEXT);
		return true;
	}
	
	public boolean prev(){
		mWorker.sendEmptyMessage(PREV);
		return true;
	}
	
	private boolean acquireNews()
	{
		RequestListener listener = new RequestListener(){
         	public void onComplete(String response){
         		Log.i("Backend", response);

         		try
         		{
            		JSONObject jsonData = new JSONObject(response);
         	    	mJsonArray = jsonData.getJSONArray("statuses"); 
         		}
         		
         		catch(JSONException e)
         		{
         		    Log.i("Backend", e.toString());         		                           
         		}	
         		
    	    	mWorker.sendEmptyMessage(RELOADED);                  
         	}

         	public void onIOException(IOException e){Log.i("Backend", "IO problem!!");}

         	public void onError(WeiboException e){Log.i("Backend", e.toString());}
         };
         StatusesAPI api = new StatusesAPI(accessToken);
         //api.update("Test QiTu","","",listener);
         api.userTimeline("��������", (long)0, (long)0, 50, 1, false, WeiboAPI.FEATURE.ALL, false, listener);
	
		return true;
	}
	
    private Bitmap returnPic(String url) {   
    	if(url == "") return null;
		URL myFileUrl = null;   
		Bitmap bitmap = null;   
		
		try {   
		myFileUrl = new URL(url);   
		} catch (Exception e) {   
		e.printStackTrace();   
		}   
		if(myFileUrl == null) return null;
		try {   
		HttpURLConnection conn = (HttpURLConnection) 
		myFileUrl.openConnection();   
		conn.setDoInput(true);   
		conn.connect();   
		InputStream is = conn.getInputStream();   
		bitmap = BitmapFactory.decodeStream(is);   
		is.close();   
		} catch (IOException e) {   
			return null;  //	e.printStackTrace();   
		}   
		return bitmap;   
	}
    
	private void updatePic(){
		JSONObject jsonObject = (JSONObject)mJsonArray.opt(mIndex);            
        String picUrl = jsonObject.optString("original_pic");
        Log.i("Backend",picUrl);     

        if(picUrl.isEmpty()) picUrl = "http://tp2.sinaimg.cn/1730866981/180/1289531929/1";
		picture = returnPic(picUrl);
	}
	
	private Handler mainHandler;
	private Handler mWorker;
	
	public static Oauth2AccessToken accessToken;
	
	private JSONArray mJsonArray;
	private int mIndex;
	
	public static Bitmap picture;
	public static String text;
}
