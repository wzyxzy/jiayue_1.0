package com.jiayue.service;

import com.jiayue.dto.base.AudioItem;

/**
 * 播放歌曲
 * @author Administrator
 *
 */
interface IMusicPlayerService {

	String getAudioPath();

	void notifyChange(String notify);

	boolean isPlaying();
	
	void setAudioList(in List<AudioItem> audioLists);
	
	List<AudioItem> getAudioList();
	
	AudioItem getAudioItem();
	
	int getAudioPosition();
	
	/**
	 * 播放音乐
	 */
	 void play();
	/**
	 * 暂停音乐
	 */
	 void pause();
	/**
	 * 根据位置打开一个音频
	 * @param position
	 */
	 void openAudio(int position);
	/**
	 * 拖动视频
	 * @param position
	 */
	 void seeKTo(int position);
	/**
	 * 得到音频的名称
	 * @return
	 */
	 String getAudioName();
	 

	void getAllAudio(String path);
	/**
	 * 得到艺术家名字
	 * @return
	 */
	 String getArtistName();
	
	/**
	 * 得到总时长
	 * @return
	 */
	 int getDuration();
	
	/**
	 * 得到当前播放进度
	 * @return
	 */
	 int getCurrentPositon();
	
	/**
	 * 设置模式
	 * @param playmode
	 */
	 void setPlayMode(int playmode);
	/**
	 * 得到当前播放模式
	 * @return
	 */
	 int getPlayMode();
	
	/**
	 * 播放下一个音频
	 */
	 void next();
	/**
	 * 播放上一个音频
	 */
	void pre();
	
	
	
	
	
	

}
