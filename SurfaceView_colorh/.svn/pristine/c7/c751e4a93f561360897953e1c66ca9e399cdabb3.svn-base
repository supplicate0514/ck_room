package com.viplab.baseoncameraidentitfy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 *需求描述：
 *利用SurfaceView預覽,且拍照.
 *將拍攝後的圖片保存至相冊
 *
 *注意
 *1 權限
 *  <uses-permission android:name="android.permission.CAMERA"/>
 *2 屏幕旋轉後SurfaceView中預覽的圖片有90度的旋轉
 *  解决方式:
 *  2.1將屏幕設置為豎屏
 *  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
 *  2.2設置相機的Orientation
 *  mCamera.setDisplayOrientation(90);
 *  注意:
 *  該問題雖然解决了,但是會帶來以下的一個問題.
 *3 拍照保存的圖片可能被旋轉90度.
 *  解決方式:
 *  http://blog.csdn.net/walker02/article/details/8211628
 *  http://www.cnblogs.com/andgoo/archive/2012/08/29/2661896.html
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,OnClickListener,Camera.PictureCallback{
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    //private DisplayMetrics dm;
    private Time time = new Time();
    //private Bitmap bitmap;
    private ProgressDialog pd;
    //private boolean iswaitPicture=true;
    private String PicturePath;
    //private Context context = getApplication();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//設置豎屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		//dm = new DisplayMetrics();
        //this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		init();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//iswaitPicture=true;
	}
	
	private void init(){
		mSurfaceView=(SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setFocusable(true);
		mSurfaceView.setFocusableInTouchMode(true);
		mSurfaceView.setClickable(true);
		mSurfaceView.setOnClickListener(this);
		
		mSurfaceHolder=mSurfaceView.getHolder();//得到持有人
		//設置該SurfaceView是一個"推送"類型的SurfaceView
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//相機指定
		mSurfaceHolder.addCallback(this);
	}


	public void surfaceCreated(SurfaceHolder holder) {
		mCamera=Camera.open();
		//Camera.Parameters parameters=mCamera.getParameters();
		// 當屏幕變化時,旋轉角度.否則不對
		mCamera.setDisplayOrientation(90);
		//操作結束
		try {
			//將攝像頭的預覽顯示設置为mSurfaceHolder
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			mCamera.release();
		}
		//設置輸出格式
		//parameters.setPictureFormat(PixelFormat.JPEG);
		//Log.v("TAG",parameters.getPictureFormat()+"");
		//設置攝像頭的参數.否則前面的設置無效
		//mCamera.setParameters(parameters);
		//攝像頭開始預覽
		mCamera.startPreview();
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mCamera.startPreview();
		
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
	}

	//處理SurfaceView的點擊事件
	public void onClick(View v) {
	   Log.i("Tag","Don't onClick");
	   pd = ProgressDialog.show(this, "Waiting", "Saving the picture, please wait...");
	   //拍照後重新開始預覽
	   //camera.startPreview();
		mCamera.takePicture(null, null, this);
	}
		
	public void onPictureTaken(byte[] data, Camera camera) {
	   //將圖片保存至相冊
	   Log.i("Tag","Don't Save");
	   //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	   mCamera.stopPreview();
	   savePicture(data,"test");
	   Log.i("Tag","SaveOK");
	   //iswaitPicture=false;
	   /**跳轉頁面**/
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("PicturePath", PicturePath);//路徑參數
		intent.setClass( MainActivity.this, ImgStatisticHSV.class );
		intent.putExtras( bundle);
		startActivity( intent );
		pd.dismiss();
	}
	
	private void savePicture(byte[] data, String imgName)
	 {  
		
	  String path = "/sdcard/CameraIdent/";
		time.setToNow();
		File resultFile = new File(path);
		if (!resultFile.exists()) {
			resultFile.mkdirs();
			Log.d("Tag", "pathFile OK");
		}
		imgName = path + imgName + time.format3339(true) + "_"
				+ time.hour + "_" + time.minute + "_" + time.second + ".jpg";
	   
		try {
			File myCaptureFile = new File(imgName);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			/* 採用壓縮轉檔方法 */
			 for(int i = 0; i < data.length; i++)
			{bos.write(data[i]);}
			
			//bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			Log.d("Tag", "save img OK");
			
			/* 呼叫flush()方法，更新BufferStream */
			bos.flush();
			PicturePath=imgName;
			/* 結束OutputStream */
			bos.close();
			
			
		} catch (Exception e) {
			Log.e("TAG", e.getMessage());
		}
	 }
	
}
