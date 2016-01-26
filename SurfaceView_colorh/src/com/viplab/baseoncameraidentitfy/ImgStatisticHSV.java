package com.viplab.baseoncameraidentitfy;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ImgStatisticHSV extends Activity {

	private double [] HsvHist=new double[8];
	private ListView Histogram;
	private String[] data=new String[8];
	private String[] Colorname={"黑","白","紅","橘","黃","綠","藍","棕"};
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imgstatistic);
		
		
		
		ImageView img=(ImageView)findViewById(R.id.imgshow);
		Histogram=(ListView)findViewById(R.id.Histogram);
		Bundle bundle = getIntent().getExtras();
		String PicturePath = bundle.getString("PicturePath");
		/** 處理照片旋轉---目前沒有效果*/
		File file = new File(PicturePath);
		int degree = this.readPictureDegree(file.getAbsolutePath()); 
		Bitmap bitmap = BitmapFactory.decodeFile(PicturePath);
		bitmap = this.rotaingImageView(degree, bitmap);
		
		img. setImageBitmap (bitmap);
		

		spendTimeThread(bitmap);

	}
	
	private void spendTimeThread(final Bitmap bitmap)
	{
		pd = ProgressDialog.show(this, "等待", "統計顏色中，請稍後……");
		   new Thread(new Runnable() {
				@Override
				public void run() {
					getHSVHtogram(bitmap);	
					writeHistogram(bitmap);
				handler.sendEmptyMessage(0);// 執行耗時的方法之後發送给handler
				}
		    }).start();
		
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
			ArrayAdapter<String> listAdapter = new ArrayAdapter(ImgStatisticHSV.this,android.R.layout.simple_list_item_1,data);
			Histogram.setAdapter(listAdapter);	
			pd.dismiss();		
			
		}
	};
	
	private void writeHistogram(Bitmap pic)
	{
		double totalPixel=0;
		totalPixel=pic.getHeight()*pic.getWidth();
		Log.i("Img","Histogram ok"+totalPixel);
		for(int i=0;i<8;i++)
		{   double temp=(HsvHist[i]/totalPixel)*100;
			BigDecimal delDh = new BigDecimal(temp);    //小數點後兩位 
	        delDh = delDh.setScale(2, BigDecimal.ROUND_HALF_UP);
			data[i]=Colorname[i]+": "+delDh+"%";
		}
		
	}
	
	private void getHSVHtogram(Bitmap Pic)//圖片大小目前為640*480
	{
		int mColor;
		int R,G,B;//三原色
		float HSV[]=new float[3];
		float H,S,V;
		
		for (int i=0;i< Pic.getHeight();i++)
		{
			for (int j=0;j< Pic.getWidth();j++)
			{
				mColor=Pic.getPixel(j, i);// 將Pixel三原色放入RGB產生HSV
				R=Color.red(mColor);
				G=Color.green(mColor);
				B=Color.blue(mColor);
				Color.RGBToHSV(R, G, B, HSV);
//			    Log.d(TAG, "H: " + HSV[0] + " S : " + HSV[1]+" V: " + HSV[2]);
			    H=HSV[0];
			    S=HSV[1];
			    V=HSV[2];

			    if (V<=0.2)	//黑			    	
			    {				 
			    HsvHist[0]=HsvHist[0]+1;			
			    }
			    else if(V>=0.8 && S<=0.2) // 白
			    {			
			    HsvHist[1]=HsvHist[1]+1;
			    }
			    else if(V<0.8 && S<=0.2 && V>0.2)//灰色地帶
			    { 
			    	if(V>0.4)//Value大於0.4 歸類為白色，若小於歸類成黑色
			    	{
			    	HsvHist[1]=HsvHist[1]+1;
			    	}else  
			    	{
			    	HsvHist[0]=HsvHist[0]+1;	
			    	}
			    } 
			    else if(V>0.2 && S>0.2)
			    {	
			    	
			    	if(H>=300||H<20)//  紅色
			    	{/**棕色 H要藉於300~70 0.5>V>0.2 S>0.2**/
			    		 if(V>0.5)
			    		 {
			    		  HsvHist[2]=HsvHist[2]+1;	 
			    		 }else// 棕色
			    		 {
			    		  HsvHist[7]=HsvHist[7]+1;  
			    		 }
			    		
			    	}
			    	else if(H>=20 && H<45) // 橘色
			    	{
			    		 if(V>0.5)
			    		 {
			    		  HsvHist[3]=HsvHist[3]+1; 
			    		 }else// 棕色
			    		 {
			    		  HsvHist[7]=HsvHist[7]+1;  
			    		 }
			    		
			    	}
			    	else if(H>=45 && H<70)  //黃色
			    	{
			    		 if(V>0.5)
			    		 {
			    		  HsvHist[4]=HsvHist[4]+1;
			    		 }else// 棕色
			    		 {
			    		  HsvHist[7]=HsvHist[7]+1;	  
			    		 }
			    		
			    	}
			    	else if(H>=70 && H<155)  //綠色
			    	{				    		
			    	 HsvHist[5]=HsvHist[5]+1;	 				    		
			    	}
			    	else if(H>=155 && H<300)  // 藍色
			    	{
			    	 HsvHist[6]=HsvHist[6]+1;	
			    	}
			
			    }
			    
			}
		}
		
	}

	/*@param path 圖片路徑
	 * @return degree 旋轉角度*/
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
	
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋轉圖片
		Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        //得到新圖
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.img_statistic_hsv, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
