package com.jiayue;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jiayue.adapter.AudioAdapter;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.lrcview.DefaultLrcParser;
import com.jiayue.lrcview.LrcRow;
import com.jiayue.lrcview.LrcView;
import com.jiayue.service.IMusicPlayerService;
import com.jiayue.service.MusicPlayerService;
import com.jiayue.util.DensityUtil;
import com.jiayue.util.LyricUtils;
import com.jiayue.util.MusicUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerActivity extends Activity {

	private String path = "";
	private String bookName;
	private TextView start_time;
	private SeekBar seekBar_audio;
	private TextView end_time;
	private LinearLayout btn_pre;
	private ImageButton btn_play;
	private LinearLayout btn_next;
	private LinearLayout btn_list;
	private TextView tv_header_title;
	private ImageButton btn_header_right;
	private int mediaplayer_pause;
	private int mediaplayer_play;
	protected static final int PROGRESS = 1;
	protected static final int LYRIC_SHOW = 2;

	private LyricUtils lyricUtils;

	private LrcView mLrcView;
	/**
	 * 服务代理对象
	 */
	private com.jiayue.service.IMusicPlayerService service;
	/**
	 * 在音频列表中播放那一个
	 */
	private int currentPosition;

	private MyBroadcastReceiver receiver;
	private boolean isStoped = false;
	/**
	 * true:表示进入这个Activity状态栏进来 flase:播放列表进来
	 */
	private boolean from_notification;
	private MusicUtils utils;
	private List<AudioItem> audioList = new ArrayList<AudioItem>();
	private int currentIndex;
	private Dialog dialog;
	private AudioAdapter adapter;
	private LinearLayout btn_status;
	private ImageButton iv_mode;
	int mode_one;
	int mode_all;
	int mode_random;
	int mode = MusicPlayerService.REPEATE_NORMAL;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case PROGRESS:
					// 得到当前播放进度
					try {
						int currentPosition = service.getCurrentPositon();
						mLrcView.seekTo(currentPosition, true, true);
						seekBar_audio.setProgress(currentPosition);
						seekBar_audio.setMax(service.getDuration());
						start_time.setText(utils.stringForTime(service.getCurrentPositon()));
						end_time.setText(utils.stringForTime(service.getDuration()));

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!isStoped) {
						handler.sendEmptyMessageDelayed(PROGRESS, 1000);
					}

					break;
				case LYRIC_SHOW:
					// int currentTime;
					// try {
					// currentTime = service.getCurrentPositon();
					// // mLrcView.setShowNextLyric(currentTime);
					// } catch (RemoteException e) {
					// e.printStackTrace();
					// }
					if (!isStoped) {
						handler.removeMessages(LYRIC_SHOW);
						// showLyric();
					}
					break;
				default:
					break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载解码器
		// if (!Vitamio.isInitialized(this))
		// return;

		setContentView(R.layout.activity_music_player);

//		if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
//			path = getIntent().getStringExtra("filePath");
//			SPUtility.putSPString(this, "mPath", path);
//		} else {
//			path = SPUtility.getSPString(this, "mPath");// 下拉栏进去
//		}

//		if (getIntent().getStringExtra("bookId") != null) {
//			bookId = getIntent().getStringExtra("bookId");
//			SPUtility.putSPString(this, "bookId", bookId);
//		} else {
//			bookId = SPUtility.getSPString(this, "bookId");
//		}
		initView();
		startService();
		getData();
		setOnlistener();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// int getmCurRow = mLrcView.getmCurRow();
			switch (v.getId()) {
				case R.id.btn_pre:
					// mLrcView.setmCurRow(getmCurRow-1);
					try {
						service.pre();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					break;
				case R.id.btn_next:

					// mLrcView.setmCurRow(getmCurRow+1);
					try {
						service.next();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					break;
				case R.id.btn_play:// 播放和暂停

					if (service != null) {
						try {
							if (service.isPlaying()) {
								service.pause();
							} else {
								service.play();
							}
							setPlayOrPauseStatus();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					break;
				case R.id.btn_list:
					showDialog();
					break;
				default:
					break;
			}

		}
	};

	public void showDialog() {
		dialog = new Dialog(this, R.style.my_dialog);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.list_bottom, null);
		ListView listView = (ListView) dialogView.findViewById(R.id.listview);
		try {
			adapter = new AudioAdapter(this, service.getAudioList(), service.getAudioPosition());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				try {
					service.openAudio(position);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.setIndex(position);
				adapter.notifyDataSetChanged();
			}
		});
		dialog.setContentView(dialogView);

		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 300));
		dialogWindow.setGravity(Gravity.BOTTOM);
		dialogWindow.setWindowAnimations(R.style.mystyle); // 添加动画
		dialogWindow.setAttributes(lp);

		dialog.show();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setPlayOrPauseStatus();
	}

	private void getData() {
		utils = new MusicUtils();
		lyricUtils = new LyricUtils();
		currentPosition = getIntent().getIntExtra("currentPosition", 0);
		from_notification = getIntent().getBooleanExtra("from_notification", false);
		// 注册广播
		IntentFilter filter = new IntentFilter();
		receiver = new MyBroadcastReceiver();
		filter.addAction(MusicPlayerService.PREPARED_MESSAGE);
		filter.addAction(MusicPlayerService.PREPARED_CLOSE);
		filter.addAction(MusicPlayerService.PREPARED_STATUSA);
		registerReceiver(receiver, filter);
	}

	/**
	 * 绑定服务
	 */

	private void startService() {
		Bundle extras = new Bundle();
		Intent intent = new Intent(this, MusicPlayerService.class);
		intent.putExtras(extras);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		startService(intent);
	}

	/**
	 * 设置按钮监听
	 */
	private void setOnlistener() {
		btn_play.setOnClickListener(mOnClickListener);
		btn_next.setOnClickListener(mOnClickListener);
		btn_pre.setOnClickListener(mOnClickListener);
		btn_list.setOnClickListener(mOnClickListener);
		seekBar_audio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					try {
						service.seeKTo(progress);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 设置播放和暂停按钮状态
	 */
	protected void setPlayOrPauseStatus() {
		if (service != null) {
			try {
				if (service.isPlaying()) {
					// 暂停
					btn_play.setBackgroundResource(mediaplayer_pause);
				} else {
					// 播放
					btn_play.setBackgroundResource(mediaplayer_play);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MusicPlayerService.PREPARED_MESSAGE)) {
				// 音频是播起来了
				setViewStatus();
			} else if (intent.getAction().equals(MusicPlayerService.PREPARED_CLOSE)) {
				finish();
			} else if (intent.getAction().equals(MusicPlayerService.PREPARED_STATUSA)) {
				setPlayOrPauseStatus();
			}
		}
	}

	private void initView() {
		
		audioList = getIntent().getParcelableArrayListExtra("list");
		if(audioList==null||audioList.size()==0){
			audioList = new ArrayList<AudioItem>();
		}
		currentIndex = getIntent().getIntExtra("index", 0);
		try {
			path = audioList.get(currentIndex).getData();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		start_time = (TextView) findViewById(R.id.start_time);
		seekBar_audio = (SeekBar) findViewById(R.id.seekBar_audio);
		end_time = (TextView) findViewById(R.id.end_time);
		btn_pre = (LinearLayout) findViewById(R.id.btn_pre);
		btn_play = (ImageButton) findViewById(R.id.btn_play);
		btn_list = (LinearLayout) findViewById(R.id.btn_list);
		mediaplayer_pause = getResources().getIdentifier("mediaplayer_pause", "drawable", getPackageName());
		mediaplayer_play = getResources().getIdentifier("mediaplayer_play", "drawable", getPackageName());
		btn_next = (LinearLayout) findViewById(R.id.btn_next);
		mLrcView = (LrcView) findViewById(R.id.show_lyric_view);
		mLrcView.setOnSeekToListener(new com.jiayue.lrcview.LrcView.OnSeekToListener() {

			@Override
			public void onSeekTo(int progress) {
				try {
					service.seeKTo(progress);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		});
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);
		btn_header_right.setVisibility(View.VISIBLE);

		btn_header_right.setBackgroundResource(getResources().getIdentifier("mediaplayer_hide", "drawable", getPackageName()));
		btn_header_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				moveTaskToBack(true);
			}
		});
		
		btn_status = (LinearLayout) findViewById(R.id.btn_status);
		iv_mode = (ImageButton) findViewById(R.id.play_status);
		mode_one = getResources().getIdentifier("mode_one", "drawable", getPackageName());
		mode_all = getResources().getIdentifier("mode_all", "drawable", getPackageName());
		mode_random = getResources().getIdentifier("mode_random", "drawable", getPackageName());
		btn_status.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					service.setPlayMode(service.getPlayMode()+1);
					showPlayMode();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void showPlayMode(){
		
		try {
			mode = service.getPlayMode();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (mode) {
			case MusicPlayerService.RANDOM_ALL:
				iv_mode.setBackgroundResource(mode_random);
				break;
			case MusicPlayerService.REPEATE_NORMAL:
				iv_mode.setBackgroundResource(mode_all);
				break;
			case MusicPlayerService.REPEATE_CURRENT:
				iv_mode.setBackgroundResource(mode_one);
				break;
			default:
				iv_mode.setBackgroundResource(mode_all);
				break;
		}
	}

	private void showLyric() {

		try {
			String lrcpath = service.getAudioPath();// mnt/sdcard/video/beijingbeijing.mp3
			Log.d("mediaplayer", "lrcpath==" + lrcpath);
			if (!TextUtils.isEmpty(lrcpath)) {
				lrcpath = lrcpath.substring(0, lrcpath.lastIndexOf("/")) + "/" + service.getAudioName() + ".lrc_copy.lrc";// mnt/sdcard/video/beijingbeijing
			}
			Log.d("mediaplayer", "lrcpath2==" + lrcpath);
			File file = new File(lrcpath);
			if (!file.exists()) {
				file = new File(lrcpath + ".txt");
			}
			// Log.d("mediaplayer","file=="+file.getPath());
			// lyricUtils.readLyricFile(file);
			// mLrcView.setLyrics(lyricUtils.getLyrics());
			mLrcView.setLrcRows((List<LrcRow>) getLrcRows(file));
			if (lyricUtils.isExistLyric()) {
				handler.sendEmptyMessage(LYRIC_SHOW);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取歌词List集合
	 * 
	 * @return
	 */
	private List<LrcRow> getLrcRows(File file) {
		List<LrcRow> rows = new ArrayList<LrcRow>();
		String line = "";
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), getCharset(file)));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			System.out.println(sb.toString());
			rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	/**
	 * 判断文件编码
	 * 
	 * @param file
	 *            文件
	 * @return 编码：GBK,UTF-8,UTF-16LE
	 */
	public String getCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				// int loc = 0;
				while ((read = bis.read()) != -1) {
					// loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return charset;
	}

	public void setViewStatus() {
		Log.d("setViewStatus", "setViewStatussetViewStatussetViewStatus");
		try {
			// tv_artist.setText(service.getArtistName());
			tv_header_title.setText(service.getAudioName());
			// 设置seeKBar的总长度
			seekBar_audio.setMax(service.getDuration());

			start_time.setText(utils.stringForTime(service.getCurrentPositon()));
			end_time.setText(utils.stringForTime(service.getDuration()));
			handler.sendEmptyMessage(PROGRESS);
			showLyric();

			if(dialog!=null&&dialog.isShowing()){
				adapter.setIndex(service.getAudioPosition());
				adapter.notifyDataSetChanged();
			}
			// setPlayModeButtonStatus();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private ServiceConnection conn = new ServiceConnection() {

		// 当取消绑定服务成功后，会回调这个方法
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;

		}

		// 当绑定服务成功后，会回调--
		@Override
		public void onServiceConnected(ComponentName name, IBinder ibinder) {
			// 代表服务
			service = IMusicPlayerService.Stub.asInterface(ibinder);
			try {
				// 下拉栏、歌曲相同
				if (!from_notification && !TextUtils.isEmpty(path)&& !service.getAudioPath().equals(path)) {
					Log.d("audioaudioaudio", "!from_notification!from_notification");
					service.setAudioList(audioList);
					service.openAudio(currentIndex);
				} else {
					service.notifyChange(MusicPlayerService.PREPARED_MESSAGE);
					setPlayOrPauseStatus();
				}
			showPlayMode();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
	};
	

	public void btnBack(View v) {
		// if (service != null) {
		// try {
		// service.pause();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// }
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// if (service != null) {
		// try {
		// service.pause();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public void onBackPressed() {
		// if (service != null) {
		// try {
		// service.pause();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// }
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {

		// if (service != null) {
		// try {
		// service.pause();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// }
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		unbindService(conn);
		
		// isStoped = true;
		// ActivityUtils.deleteBookFormSD(path);
		super.onDestroy();
	}
}
