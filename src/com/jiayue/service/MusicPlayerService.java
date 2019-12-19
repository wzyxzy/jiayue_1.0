package com.jiayue.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.core.app.NotificationCompat;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.jiayue.BookSynActivity;
import com.jiayue.MediaPlayerActivity;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.download.TestService;
import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.FileUtil;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipException;

/**
 * 播放歌曲
 *
 * @author Administrator
 */
@SuppressLint("NewApi")
public class MusicPlayerService extends Service {
    private StringBuffer sbf;
    private String name;
    /**
     * 音频准备好了，可以获取演唱者和歌曲名称了
     */
    public static final String PREPARED_MESSAGE = "com.jiayue.PREPARED_MESSAGE";
    public static final String PREPARED_CLOSE = "com.jiayue.CLOSE";
    public static final String PREPARED_STATUSA = "com.jiayue.STATUSA";

    private IMusicPlayerService.Stub iBinder = new IMusicPlayerService.Stub() {

        MusicPlayerService playerService = MusicPlayerService.this;

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            playerService.setPlayMode(playmode);

        }

        @Override
        public void seeKTo(int position) throws RemoteException {
            playerService.seeKTo(position);

        }

        @Override
        public void pre() throws RemoteException {
            playerService.pre();

        }

        @Override
        public void play() throws RemoteException {
            playerService.play();

        }

        @Override
        public void pause() throws RemoteException {
            playerService.pause();

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            playerService.openAudio(position);

        }

        @Override
        public void next() throws RemoteException {
            playerService.next();

        }

        @Override
        public int getPlayMode() throws RemoteException {
            return playerService.getPlayMode();
        }

        @Override
        public int getDuration() throws RemoteException {
            return playerService.getDuration();
        }

        @Override
        public int getCurrentPositon() throws RemoteException {
            return playerService.getCurrentPositon();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return playerService.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return playerService.getArtistName();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return playerService.isPlaying();
        }

        @Override
        public void notifyChange(String notify) throws RemoteException {
            playerService.notifyChange(notify);

        }

        @Override
        public String getAudioPath() throws RemoteException {
            return playerService.getAudioPath();
        }

        @Override
        public void getAllAudio(String path) throws RemoteException {
            playerService.getAllAudio(path);

        }

        @Override
        public void setAudioList(List<AudioItem> audioLists) throws RemoteException {
            // TODO Auto-generated method stub
            playerService.setAudioLists(audioLists);
        }

        @Override
        public List<AudioItem> getAudioList() throws RemoteException {
            // TODO Auto-generated method stub
            return playerService.getAudioLists();
        }

        @Override
        public AudioItem getAudioItem() throws RemoteException {
            // TODO Auto-generated method stub
            return playerService.getAudioLists().get(currentPosition);
        }

        @Override
        public int getAudioPosition() throws RemoteException {
            // TODO Auto-generated method stub
            return playerService.getCurrentPosition();
        }
    };
    /**
     * 音频列表
     */
    private List<AudioItem> audioLists;
    /**
     * 当前音频列表中的某个对象
     */
    private AudioItem currentAudio;
    /**
     * 当前播放列表中的位置
     */
    private int currentPosition;
    private MediaPlayer mediaPlayer;
    /**
     * 顺序播放
     */
    public static final int REPEATE_NORMAL = 1;

    /**
     * 单曲播放
     */
    public static final int REPEATE_CURRENT = 2;

//    /**
//     * 全部循环
//     */
//    public static final int REPEATE_ALL = 3;

    /**
     * 随机播放
     */
    public static final int RANDOM_ALL = 3;

    private int playMode = REPEATE_NORMAL;

    public static final int TYPE_Customer = 0x01;

    public List<AudioItem> getAudioLists() {
        return audioLists;
    }

    public void setAudioLists(List<AudioItem> audioLists) {
        this.audioLists = audioLists;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    // 接口和服务进行关联
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    protected String getAudioPath() {
        if (currentAudio != null) {
            return currentAudio.getData();
        }
        return "";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        try {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (intent != null) {
                if (intent.getIntExtra(COMMAND, 0) == CommandPlay) {
                    if (isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                    notifyChange(PREPARED_STATUSA);
                } else if (intent.getIntExtra(COMMAND, 0) == CommandClose) {
                    stop();
                    notifyChange(PREPARED_CLOSE);
                    manager.cancel(TYPE_Customer);
                    deleteAll();
                    stopSelf();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    /**
     * 判断当前是否真正播放音乐
     *
     * @return
     */
    protected boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;

    }

    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();


    }


    /**
     * 初始化数据
     */
    private void initData() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        playMode = sp.getInt("playmode", REPEATE_NORMAL);


    }


    /**
     *
     */

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        SPUtility.putSPString(getApplicationContext(), "isPlay", "false");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            stop();
//            deleteAll();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void deleteAll() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (AudioItem item : audioLists) {
                    ActivityUtils.deleteBookFormSD(item.getData());
                }
            }
        }).start();
    }

    /**
     * 得到手机里面的视频 Android系统：在开启完成后或者sdcard插入可用的时候 媒体扫描器
     */
    public void getAllAudio(String path) {

    }

    /**
     * 发消息
     *
     * @param nofity
     */
    private void notifyChange(String nofity) {
        Intent intent = new Intent();
        intent.setAction(nofity);
        sendBroadcast(intent);// 发广播
    }

    // /**
    // * 播放音乐
    // */
    // private void play() {
    // if (mediaPlayer != null) {
    // mediaPlayer.start();
    // SPUtility.putSPString(getApplicationContext(), "isPlay", "true");
    // }
    // }

    /**
     * 暂停音乐
     */
    private void pause() {
        SPUtility.putSPString(getApplicationContext(), "isPlay", "false");
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        sendCustomerNotification();
    }

    /**
     * 根据位置打开一个音频
     *
     * @param position
     */
    private void openAudio(int position) {
        // path = SPUtility.getSPString(this, "mPath");
        // bookName = SPUtility.getSPString(this, "mName");
        setCurrentPosition(position);
        currentAudio = new AudioItem();
        currentAudio.setData(audioLists.get(currentPosition).getData());
        Log.d("audioaudioaudio", audioLists.get(currentPosition).getData());
        currentAudio.setTitle(audioLists.get(currentPosition).getTitle());
        if (!FileUtil.fileIsExists(audioLists.get(currentPosition).getData())) {
            downloadAndUnlock();
            pause();
            return;
        }
        Log.d("audioaudioaudio", "audioLists.get(currentPosition).getData()==" + audioLists.get(currentPosition).getData() + "-------audioLists.get(currentPosition).getTitle()=" + audioLists
                .get(currentPosition).getTitle());
        // 释放资源
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }
        try {
            if (currentAudio.getData() == null)
                return;
            mediaPlayer.setDataSource(currentAudio.getData());
            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void openAudio() {
        // path = SPUtility.getSPString(this, "mPath");
        // bookName = SPUtility.getSPString(this, "mName");
        currentAudio = new AudioItem();
        currentAudio.setData(audioLists.get(currentPosition).getData());
        currentAudio.setTitle(audioLists.get(currentPosition).getTitle());
        Log.d("audioaudioaudio", "audioLists.get(currentPosition).getData()==" + audioLists.get(currentPosition).getData() + "-------audioLists.get(currentPosition).getTitle()=" + audioLists
                .get(currentPosition).getTitle());
        // 释放资源
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }
        try {
            if (currentAudio.getData() == null)
                return;
            mediaPlayer.setDataSource(currentAudio.getData());
            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void downloadAndUnlock() {
        downLoadFile(audioLists.get(currentPosition).getOldData(), audioLists.get(currentPosition).getArtist(), audioLists.get(currentPosition).getTitle(), audioLists.get(currentPosition).getBookId());
    }

    /**
     * 下载附件
     *
     * @param url      附件的服务器地址
     * @param saveName 附件要保存的名字
     * @param fileName 附件要现实的名字
     */
    public void downLoadFile(String url, String saveName, String fileName, String bookId) {

        if (TextUtils.isEmpty(url)) {
            ActivityUtils.showToast(MusicPlayerService.this, "扫码的音乐不能在这里下载，请扫码下载！");
            next();
            return;
        }
        ActivityUtils.showToast(MusicPlayerService.this, "正在下载音乐，请稍等！");
//        DialogUtils.showMyDialog(this.getApplicationContext(), MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在下载音乐，请稍等...", null);
        String path = Preferences.FILE_DOWNLOAD_URL + url + "&userId=" + UserUtil.getInstance(this).getUserId() + "&bookId=" + bookId;
        Log.i("BookSynActivity", "url=" + path);
        Log.i("BookSynActivity", "saveName=" + saveName);
        Log.i("BookSynActivity", "fileName=" + fileName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setName(saveName);
            d.setBookId(bookId);
            d.setBookName(fileName);
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
        } else {
            ActivityUtils.showToastForFail(MusicPlayerService.this, "未检测到SD卡,或未赋予应用存储权限");
        }
    }

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            play();
            notifyChange(PREPARED_MESSAGE);
        }
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            // SPUtility.putSPString(getApplicationContext(), "isPlay",
            // "false");
            next();
            sendCustomerNotification();

        }
    };
    private OnErrorListener mOnErrorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d("onError", what + "--------------" + extra);
            return false;
        }
    };

    // private String path;

    /**
     * 拖动视频
     *
     * @param position
     */
    private void seeKTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }

    }

    /**
     * 得到音频的名称
     *
     * @return
     */
    private String getAudioName() {
        if (currentAudio != null) {
            return currentAudio.getTitle();
        }
        return "";
    }

    /**
     * 得到艺术家名字
     *
     * @return
     */
    private String getArtistName() {
        if (currentAudio != null) {
            return currentAudio.getArtist();
        }
        return "";
    }

    /**
     * 得到总时长
     *
     * @return
     */
    private int getDuration() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 得到当前播放进度
     *
     * @return
     */
    private int getCurrentPositon() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 设置模式
     *
     * @param playmode
     */
    private void setPlayMode(int playmode) {
        if (playmode > RANDOM_ALL)
            playmode = REPEATE_NORMAL;
        this.playMode = playmode;
        Editor editor = sp.edit();
        editor.putInt("playmode", playMode);
        editor.commit();
    }

    /**
     * 得到当前播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return playMode;
    }

    /**************************************************************************************************/
    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            SPUtility.putSPString(getApplicationContext(), "isPlay", "true");
        }
        // // 当播放音乐的时候，在状态栏显示播放音乐
        // int icon = R.drawable.notification_music_playing;
        // bookName = SPUtility.getSPString(this, "mName");
        // CharSequence tickerText = "正在播放:" + bookName;
        // long when = System.currentTimeMillis();
        // NotificationManager nm = (NotificationManager)
        // getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification notification = new Notification(icon, tickerText, when);
        // // 设置通知栏的属性-点击后还在，并没有消掉
        // notification.flags = Notification.FLAG_ONGOING_EVENT;
        // CharSequence contentText = "正在播放:" + bookName;
        // Intent intent = new Intent(MusicPlayerService.this,
        // MediaPlayerActivity.class);
        // intent.putExtra("from_notification", true);
        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        // intent, 0);
        // notification.setLatestEventInfo(this, "外研视听", contentText,
        // contentIntent);
        // startForeground(1, notification);
        sendCustomerNotification();
    }

    public static final int CommandPlay = 111;
    public static final int CommandClose = 112;
    public static final String COMMAND = "command";
    NotificationManager manager;

    // command是自定义用来区分各种点击事件的
    private void sendCustomerNotification(/* int command */) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("bookName");
        builder.setContentText("正在播放");
        builder.setSmallIcon(R.drawable.ic_launcher);
        // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.push));
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_template_customer);
        remoteViews.setTextViewText(R.id.title, getAudioName());
        remoteViews.setTextViewText(R.id.text, "正在播放");
        // if(command==CommandNext){
        // remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_pause_white);
        // }else
        // if(command==CommandPlay){
        if (SPUtility.getSPString(getApplicationContext(), "isPlay").equals("true")) {
            int pause = getResources().getIdentifier("music_pause", "drawable", getPackageName());
            remoteViews.setImageViewResource(R.id.btn1, pause);
            remoteViews.setTextViewText(R.id.text, "正在播放");
        } else {
            int play = getResources().getIdentifier("music_play", "drawable", getPackageName());
            remoteViews.setImageViewResource(R.id.btn1, play);
            remoteViews.setTextViewText(R.id.text, "停止播放");
        }
        // }
        Intent Intent1 = new Intent(this, MusicPlayerService.class);
        Intent1.putExtra(COMMAND, CommandPlay);
        // getService(Context context, int requestCode, @NonNull Intent intent,
        // @Flags int flags)
        // 不同控件的requestCode需要区分开 getActivity broadcoast同理
        PendingIntent PIntent1 = PendingIntent.getService(this, 5, Intent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn1, PIntent1);

        // Intent Intent2 = new Intent(this,MusicPlayerService.class);
        // Intent2.putExtra("command",CommandNext);
        // PendingIntent PIntent2 = PendingIntent.getService(this,6,Intent2,0);
        // remoteViews.setOnClickPendingIntent(R.id.btn2,PIntent2);

        Intent Intent3 = new Intent(this, MusicPlayerService.class);
        Intent3.putExtra(COMMAND, CommandClose);
        PendingIntent PIntent3 = PendingIntent.getService(this, 7, Intent3, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn3, PIntent3);

        Intent Intent4 = new Intent(this, MediaPlayerActivity.class);
        Intent3.putExtra("from_notification", true);
        PendingIntent PIntent4 = PendingIntent.getActivity(this, 8, Intent4, 0);
        remoteViews.setOnClickPendingIntent(R.id.status_bar_latest_event_content, PIntent4);

        builder.setContent(remoteViews);
        Notification notification = builder.build();
        manager.notify(TYPE_Customer, notification);
    }

    // public void getAllAudio(String path) {
    // try {
    // currentAudio = new AudioItem();
    // currentAudio.setData(path);
    //
    // MP3File mp3File = (MP3File) AudioFileIO.read(new File(path));
    //
    // org.jaudiotagger.tag.Tag tag = mp3File.getTag();
    // sbf = new StringBuffer();
    // // sbf.append("歌手："+tag.getFirst(FieldKey.ARTIST) + "\n");
    // // currentAudio.setArtist(tag.getFirst(FieldKey.ARTIST));
    // // sbf.append("专辑名："+tag.getFirst(FieldKey.ALBUM) + "\n");
    // // sbf.append("歌名："+tag.getFirst(FieldKey.TITLE) + "\n");
    // currentAudio.setTitle(tag.getFirst(FieldKey.TITLE));
    // // sbf.append("年份："+tag.getFirst(FieldKey.YEAR));
    //
    // MP3AudioHeader header = mp3File.getMP3AudioHeader();
    //
    // sbf.append("长度: " + header.getTrackLength() + "\n");
    // sbf.append("精确的长度: " + header.getPreciseTrackLength() + "\n");
    // // sbf.append("比特率: " + header.getBitRate() + "\n");
    // // sbf.append("编码器: " + header.getEncoder()+"\n");
    // // sbf.append("格式: " + header.getFormat() + "\n");
    // // sbf.append("声道: " + header.getChannels() + "\n");
    // // sbf.append("采样率: " +header.getSampleRate() + "\n");
    // // sbf.append("MPEG: " + header.getMpegLayer() + "\n");
    // // sbf.append("MP3起始字节: "+header.getMp3StartByte() + "\n");
    //
    // currentAudio.setDuration(header.getTrackLengthAsString());
    // currentAudio.setSize(header.getPreciseTrackLength() + "");
    //
    // // sbf.append("帧数："+header.getNumberOfFrames()+ "\n");
    // // sbf.append("编码类型："+header.getEncodingType()+ "\n");
    // // sbf.append("MPEG版本:"+header.getMpegVersion()+ "\n");
    // Log.i("Mp3info", sbf.toString());
    // Log.e("audio", currentAudio.toString());
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // // 此处存在一个问题。至于是什么呢？
    // new Thread() {
    // public void run() {
    // // audioLists = new ArrayList<AudioItem>();
    // // 加载视频
    // ContentResolver resolver = getContentResolver();
    // Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    // String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME,
    // MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE,
    // MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST };
    // Cursor cursor = resolver.query(uri, projection, null, null,
    // MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    //
    // while (cursor.moveToNext()) {
    // String data = cursor.getString(3);
    // if
    // (data.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsoluteFile()
    // + File.separator + "wyst" + File.separator + "夏洛特烦恼.mp3")) {
    // String size = cursor.getString(2);
    // String title = cursor.getString(0);
    // String duration = cursor.getString(1);
    // String artist = cursor.getString(4);
    // currentAudio.setData(data);
    // currentAudio.setTitle(title);
    // currentAudio.setDuration(duration);
    // currentAudio.setSize(size);
    // currentAudio.setArtist(artist);
    // }
    //
    // // audioLists.add(audioItem);
    //
    // }
    // cursor.close();
    // // handler.sendEmptyMessage(0);
    //
    // };
    // }.start();
    //
    // };
    //

    /**
     * 播放下一个音频
     */
    private void next() {
        setNextPosition();
        setNextOpen();

    }

    /**
     * 更加位置，打开对应的音频并且播放
     */
    private void setNextOpen() {

        if (playMode == MusicPlayerService.REPEATE_NORMAL) {
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
//                if (currentPosition != audioLists.size()) {
//                    if (isNormalEnd) {
//                        pause();
//                        notifyChange(PREPARED_STATUSA);
//                    } else
//                        openAudio(currentPosition);
//                }
                openAudio(currentPosition);
            }
        } else if (playMode == MusicPlayerService.REPEATE_CURRENT) {
            // 单曲播放
            openAudio(currentPosition);
        }
//        else if (playMode == MusicPlayerService.REPEATE_ALL) {
//            // 全部循环
//            if (audioLists != null && audioLists.size() > 0) {
//                openAudio(currentPosition);
//            }
//        }
        else {
            if (audioLists != null && audioLists.size() > 0) {
                openAudio(currentPosition);
            }
        }
        isNormalEnd = false;
    }

    private boolean isNormalEnd = false;

    /**
     * 根据不同的模式，得到下一个播放的位置
     */
    private void setNextPosition() {
        if (playMode == MusicPlayerService.REPEATE_NORMAL) {
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                currentPosition++;
                // 屏蔽非法值
                if (currentPosition > audioLists.size() - 1) {
//                    currentPosition--;
//                    isNormalEnd = true;
                    currentPosition = 0;
                }
            }
        } else if (playMode == MusicPlayerService.REPEATE_CURRENT) {
            // 单曲播放
        }
//        else if (playMode == MusicPlayerService.REPEATE_ALL) {
//            // 全部循环
//            if (audioLists != null && audioLists.size() > 0) {
//                currentPosition++;
//                // 屏蔽非法值
//                if (currentPosition > audioLists.size() - 1) {
//                    currentPosition = 0;
//                }
//            }
//        }
        else if (playMode == MusicPlayerService.RANDOM_ALL) {
            // 随机播放
            if (audioLists != null && audioLists.size() > 0) {
                if (audioLists.size() == 1) {
                    currentPosition = 0;
                } else {
                    int position = 0;
                    do {
                        position = new Random().nextInt(audioLists.size());
                    } while (currentPosition == position);
                    currentPosition = position;
                }
                // 屏蔽非法值
                if (currentPosition > audioLists.size() - 1) {
                    currentPosition = 0;
                }
            }
        } else {
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                currentPosition++;
                // 屏蔽非法值
                if (currentPosition > audioLists.size() - 1) {
                    currentPosition = 0;
                }

            }
        }

    }

    /**
     * 播放上一个音频
     */
    private void pre() {
        setPrePosition();
        setPreOpen();
    }

    private void setPreOpen() {

        if (playMode == MusicPlayerService.REPEATE_NORMAL) {
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                if (currentPosition != audioLists.size()) {
                    openAudio(currentPosition);
                }
            }
        } else if (playMode == MusicPlayerService.REPEATE_CURRENT) {
            // 单曲播放
            openAudio(currentPosition);
        }
//        else if (playMode == MusicPlayerService.REPEATE_ALL) {
//            // 全部循环
//            if (audioLists != null && audioLists.size() > 0) {
//                openAudio(currentPosition);
//            }
//        }
        else {
            // 顺序循环
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                openAudio(currentPosition);
            }
        }

    }

    private void setPrePosition() {

        if (playMode == MusicPlayerService.REPEATE_NORMAL) {
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                currentPosition--;
                // 屏蔽非法值
                if (currentPosition < 0) {
                    currentPosition = audioLists.size() - 1;
                }
            }
        } else if (playMode == MusicPlayerService.REPEATE_CURRENT) {
            // 单曲播放
        }
//        else if (playMode == MusicPlayerService.REPEATE_ALL) {
//            // 全部循环
//            if (audioLists != null && audioLists.size() > 0) {
//                currentPosition--;
//                // 屏蔽非法值
//                if (currentPosition < 0) {
//                    currentPosition = audioLists.size() - 1;
//                }
//            }
//        }
        else if (playMode == MusicPlayerService.RANDOM_ALL) {
            // 随机播放
            if (audioLists != null && audioLists.size() > 0) {
                if (audioLists.size() == 1) {
                    currentPosition = 0;
                } else {
                    int position = 0;
                    do {
                        position = new Random().nextInt(audioLists.size());
                    } while (currentPosition == position);
                    currentPosition = position;
                }
                // 屏蔽非法值
                if (currentPosition < 0) {
                    currentPosition = audioLists.size() - 1;
                }
            }
        } else {
            // 顺序循环
            // 顺序播放
            if (audioLists != null && audioLists.size() > 0) {
                currentPosition--;
                // 屏蔽非法值
                if (currentPosition < 0) {
                    currentPosition = audioLists.size() - 1;
                }
            }
        }
    }

}
