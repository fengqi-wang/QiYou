package com.laowang.qiyou;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QiYouActivity extends Activity implements OnGestureListener, OnTouchListener {
	
	class AuthDialogListener implements WeiboAuthListener {
	    @Override
	    public void onComplete(Bundle values) {
	        String token = values.getString("access_token");
	        String expires_in = values.getString("expires_in");
	        Backend.accessToken = new Oauth2AccessToken(token, expires_in);
	        if (Backend.accessToken.isSessionValid()) {
	            AccessTokenKeeper.keepAccessToken(QiYouActivity.this,
	                   Backend.accessToken);
	            mHandler.sendEmptyMessage(Backend.AUTHRIZED);
	        }
	    }

	    @Override
	    public void onError(WeiboDialogError e) {
	    
	    }

	    @Override
	    public void onCancel() {
	       
	    }

	    @Override
	    public void onWeiboException(WeiboException e) {
	        
	    }
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getLayoutInflater();
		View fm = inflater.inflate(R.layout.activity_qi_you, null);
		setContentView(fm);
		
		//View mv = findViewById(R.id.mainView);
		fm.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this,this);
        				
		mHandler = new Handler(){
			 public void handleMessage(Message msg) {
				 Log.i("Main", msg.toString());
				 switch(msg.what){
				 case Backend.AUTHRIZED:
					 Toast.makeText(QiYouActivity.this, "��֤�ɹ�", Toast.LENGTH_SHORT)
	                 .show();
					 mBackend.reload();
					 break;
				 case Backend.UPDATE_UI:
					 update();
					 break;
				 case Backend.NO_MORE:
					 Toast.makeText(QiYouActivity.this, "���û����", Toast.LENGTH_SHORT)
	                 .show();
					 break;
				 default:
					 Toast.makeText(QiYouActivity.this, msg.toString(), Toast.LENGTH_SHORT)
	                 .show();						 
				 }
				 
	           }
		};
		
		mBackend = new Backend(mHandler);
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		mWeibo.authorize(QiYouActivity.this, new AuthDialogListener());
		
		Backend.picture = BitmapFactory.decodeResource(getResources(), R.drawable.sinaweibo);
		Backend.text = "������������Ƶ���ٷ�΢����רҵ�ڸ������������±���";
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_qi_you, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_refresh)
			mBackend.reload();
		return true;
	}
	
	@Override 
	public boolean onTouch(View v, MotionEvent event) { 	
	// OnGestureListener will analyzes the given motion event
	//�����mGestureDetector�Ǹ�Activity��һ������.�ڹ��췽����ʵ��������oncreate()������ʵ����.
	Log.i("Main",event.toString());	
	return mGestureDetector.onTouchEvent(event); 
	} 
	
	@Override
	public void onShowPress(MotionEvent e) {
	} 
	@Override
	public boolean onDown(MotionEvent e) {
	// TODO Auto-generated method stub
	//	Toast.makeText(this, "onDown", Toast.LENGTH_SHORT).show();
	return false;
	} 	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	//	Toast.makeText(this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
	return false;
	} 
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		// TODO Auto-generated method stub
		 if(e1==null || e2==null) return false;
		 
		 final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200; 
		 if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
		 // Fling left 
		 Log.i("MyGesture", "Fling left"); 
		 mBackend.next(); 
		 } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
		 // Fling right 
		 Log.i("MyGesture", "Fling right"); 
		 mBackend.prev(); 
		 } else if(e2.getY()-e1.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
		 // Fling down 
		 Log.i("MyGesture", "Fling down"); 
		 //Toast.makeText(this, "Fling down", Toast.LENGTH_SHORT).show();
		 } else if(e1.getY()-e2.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
		 // Fling up 
		 Log.i("MyGesture", "Fling up"); 
		 //Toast.makeText(this, "Fling up", Toast.LENGTH_SHORT).show();
		 } 
		 return true; 
    } 
	@Override
	public void onLongPress(MotionEvent e) {
	// TODO Auto-generated method stub
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	// TODO Auto-generated method stub
	//	Toast.makeText(this, "onScroll", Toast.LENGTH_SHORT).show();
	return false;
	} 
	 
	
	private void update()
	{
		Bitmap bmp = Backend.picture;
		if(bmp == null)	{
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
		}		
        int bmpwidth = bmp.getWidth();
		int bmpheight = bmp.getHeight();
	    int screenWidth;//��Ļ���� 
	    //int screenHeight;//��Ļ�߶� 
	    WindowManager windowManager = getWindowManager(); 
	    Display display = windowManager.getDefaultDisplay(); 
	    screenWidth = display.getWidth(); 
	    //screenHeight = display.getHeight(); 
		
		float scaleWidth = (float) (screenWidth) / bmpwidth;
		//float scaleHeight = (float) screenHeight / bmpheight;
        
        Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth,scaleWidth);
		Bitmap bm = Bitmap.createBitmap(bmp,0,0,bmpwidth,bmpheight ,matrix,true);
		
		ImageView iv = (ImageView)findViewById(R.id.imageView);
		iv.setImageBitmap(bm);
		
		TextView tv = (TextView)findViewById(R.id.text);
		tv.setText(Backend.text);
	}

	private GestureDetector mGestureDetector;
	
	private Backend mBackend;
	Handler mHandler;

	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "3756648256"; 
	private static final String REDIRECT_URL = "http://www.sina.com.cn";
}
