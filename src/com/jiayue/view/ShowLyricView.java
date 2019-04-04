package com.jiayue.view;

import java.util.ArrayList;

import com.jiayue.dto.base.Lyric;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;



public class ShowLyricView extends TextView {

	
	private Paint currentPaint;
	private Paint nocurrentPaint;
	
	/**
	 * 歌词列表
	 */
	private ArrayList<Lyric> lyrics;

	/**
	 * 设置歌词
	 * @param lyrics
	 */
	public void setLyrics(ArrayList<Lyric> lyrics) {
		this.lyrics = lyrics;
	}
	/**
	 * 歌词列表的某一句
	 */
	private int index;


	//在功能清单文件使用这个控件，将会使用带有两个参数的构造方法实例化
	public ShowLyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	
	private void initView() {
		currentPaint = new Paint();
		//设置颜色
		currentPaint.setColor(Color.GRAY);
		//设置抗锯齿
		currentPaint.setAntiAlias(true);
		//设置文字大小
		currentPaint.setTextSize(50);
		//设置对其
		currentPaint.setTextAlign(Paint.Align.CENTER);
		
		
		nocurrentPaint = new Paint();
		//设置颜色
		nocurrentPaint.setColor(Color.WHITE);
		//设置抗锯齿
		nocurrentPaint.setAntiAlias(true);
		//设置文字大小
		nocurrentPaint.setTextSize(50);
		//设置对其
		nocurrentPaint.setTextAlign(Paint.Align.CENTER);
		
		//假设歌词-直接用
		lyrics = new ArrayList<Lyric>();
//		for(int i = 0; i < 500; i++){
//			Lyric lyric= new Lyric();
//			lyric.setContent(i+"-aaaaaaaaaaaa-"+i);
//			lyric.setTimePoint(i*1000);
//			lyric.setSleepTime(5000+i);
//			
//			lyrics.add(lyric);
//			
//		}
		
//		LyricUtils lyricUtils = new LyricUtils();
//		File file = new File("/mnt/sdcard/video/beijingbeijing.txt");
//		try {
//			lyricUtils.readLyricFile(file);
//			lyrics = lyricUtils.getLyrics();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
	/**
	 * 控件的宽
	 */
	private int width;
	/**
	 * 当前控件的高
	 */
	private int height;

	/**
	 * 每行歌词的高度
	 */
	private float textHeight = 50;
	/**
	 * 音频播放当前进度
	 */
	private float currentTime;
	/**
	 * 要高亮显示的时间
	 */
	private float sleepTime;
	/**
	 * 歌词显示的时间点
	 */
	private float timePoint;
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}
	/**
	 * 当前音频播放的进度
	 * @param currentTime
	 */
	
	public void setShowNextLyric(int currentTime){
		this.currentTime = currentTime;
		//得到当前该高亮显示哪一句
		if(lyrics == null)
			return;
		
		for(int i=1;i<lyrics.size();i++){
			
			if(currentTime < lyrics.get(i).getTimePoint()){
				//当前句
				int tempIndex  = i -1;
				
				//要高亮显示的这一句
				if(currentTime >= lyrics.get(tempIndex).getTimePoint()){
					//当前句的下标
					index = tempIndex;
					//高亮显示的时间
					sleepTime = lyrics.get(tempIndex).getSleepTime();
					//这句歌词什么时间点高亮
					timePoint = lyrics.get(tempIndex).getTimePoint();
					
				}
				
			}
		}
		
		invalidate();//导致onDraw()方法执行
		
	}


	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		
		float plus = 0;
		if(sleepTime ==0){
			plus = 0;
		}else{
			//往上移动
			//移动一行的百分之多少 =     速率*整行的高度
			plus = textHeight + ((currentTime - timePoint)/sleepTime)*textHeight;
		}
		
		//平移
		canvas.translate(0, -plus);
		
		if(lyrics != null && lyrics.size() > 0){
			//1.画当前句
			String currentContent = lyrics.get(index).getContent();
			canvas.drawText(currentContent, width/2, height/2, currentPaint);
			
			//2.画前一句和前前一句
			float tempY = height/2;//居中的
			for(int i = index-1;i>=0 ; i--){
				tempY = tempY - textHeight ;
				String preContent = lyrics.get(i).getContent();
				canvas.drawText(preContent, width/2, tempY, nocurrentPaint);
			}
			
			
			
			//3.画后一句和后后一句
			tempY = height/2;//居中
			for(int i = index + 1; i < lyrics.size();i++){
				String nextContent = lyrics.get(i).getContent();
				tempY = tempY + textHeight ;
				canvas.drawText(nextContent, width/2, tempY, nocurrentPaint);
			}
			
		}else{
			sleepTime = 0;
			canvas.drawText("没有找到相对应文档", width/2, height/2, currentPaint);
		}
		
		
		
		
	}

}
