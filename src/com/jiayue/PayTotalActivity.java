package com.jiayue;

import android.os.Bundle;
import android.view.Window;

public class PayTotalActivity extends BaseActivity /*implements OnClickListener*/ {
//	private Button mBtn_Edit, mBtn_Pay, mBtn_Cancel;
//	private ListView mListView;
//	private PayTotalAdapter mAdapter;
//	private final String TAG = getClass().getSimpleName();
//	private LinearLayout mLayout_Pay, mLayout_Cancel;
//	private boolean isCancelMode = false;
//	private List<ShopListBean> list;
	public static final String PAY_LIST = "PAY_LIST";
//	private TaskManager tm;
//	private BookController bookcontroller;
//	private DisplayImageOptions options;
//	private Context context;
//
//	private final int CMD_REFRESH = 0x01;
//	private Handler mHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//				case CMD_REFRESH:
//					mAdapter.setmList(list);
//					mAdapter.notifyDataSetChanged();
//					break;
//
//				default:
//					break;
//			}
//		};
//	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shopping);

//		initView();
//		initListener();
	}

//	private void initView() {
//		// TODO Auto-generated method stub
//		context = this;
//		tm = getApplicationContext().getTaskManager();
//		bookcontroller = IoC.getInstance(BookController.class);
//		// 默认一张图片
//		int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
//		options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
//		// 设置图片下载期间显示的图片
//				.showImageForEmptyUri(cover_normal)
//				// 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(cover_normal)
//				// 设置图片加载或解码过程中发生错误显示的图片
//				.cacheInMemory(true)
//				// 设置下载的图片是否缓存在内存中
//				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
//
//		mBtn_Edit = (Button) findViewById(R.id.btn_edit);
//		mBtn_Edit.setOnClickListener(this);
//		mBtn_Pay = (Button) findViewById(R.id.btn_pay);
//		mBtn_Pay.setOnClickListener(this);
//		mListView = (ListView) findViewById(R.id.listView1);
//		list = new ArrayList<ShopListBean>();
//		mAdapter = new PayTotalAdapter(this, list);
//		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		mListView.setAdapter(mAdapter);
//		mLayout_Cancel = (LinearLayout) findViewById(R.id.layout_cancel);
//		mLayout_Pay = (LinearLayout) findViewById(R.id.layout_pay);
//		mBtn_Cancel = (Button) findViewById(R.id.button2);
//		mBtn_Cancel.setOnClickListener(this);
//		
//		getShopList();
//	}
//	//获取购物车列表
//	private void getShopList() {
//		// TODO Auto-generated method stub
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("userId", UserUtil.getInstance(PayTotalActivity.this).getUserId());
//		
//		tm.createNewTask(new TaskListener() {
//			
//			@Override
//			public void onProgressUpdate(BaseTask task, Object param) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPreExecute(BaseTask task) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPostExecute(BaseTask task, String errorMsg) {
//				// TODO Auto-generated method stub
//				if (errorMsg != null) {
//					ActivityUtils.showToastForFail(context, "操作失败!," + errorMsg);
//				} else {
//					BookJson bookJson = bookcontroller.getModel();
//					if (null == bookJson) {
//						ActivityUtils.showToastForFail(context, "操作失败," + "返回空数据");
//						return;
//					} else {
//						if (!TextUtils.isEmpty(bookJson.getCode()) && bookJson.getCode().equals(MyPreferences.SUCCESS)) {
//							if (bookJson.getShoplist() != null) {
//								list.clear();
//								list = bookJson.getShoplist();
//								mHandler.sendEmptyMessage(CMD_REFRESH);
//							}
//							return;
//						} else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
//							ActivityUtils.showToastForFail(context, "操作失败," + bookJson.getCodeInfo());
//							return;
//						}
//					}
//				}
//			}
//			
//			@Override
//			public String onDoInBackground(BaseTask task, MultiValueMap<String, String> param) {
//				// TODO Auto-generated method stub
//				try {
//					String str = PayTotalActivity.this.bookcontroller.ShopList(param);
//					return str;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//			
//			@Override
//			public void onCancelled(BaseTask task) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public String getName() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		}).execute(params);
//	}
//
//	private void initListener() {
//		// TODO Auto-generated method stub
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				// if(mAdapter.getIsSelected().get(position)){
//				// mAdapter.getIsSelected().put(position, false);
//				// }else {
//				// mAdapter.getIsSelected().put(position, true);
//				// }
//				// mAdapter.notifyDataSetChanged();
//			}
//		});
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//			case R.id.btn_pay:
//				// for (Map.Entry me : mAdapter.getIsSelected().entrySet()) {
//				// Toast.makeText(PayTotalActivity.this, "size====" +
//				// mAdapter.getIsSelected().size() + "----key===" + me.getKey()
//				// + "-------value====" + me.getValue(), 1).show();
//				// }
//				Intent intent = new Intent(PayTotalActivity.this, PaySubmitActivity.class);
//				intent.putExtra(PAY_LIST, (Serializable) list);
//				startActivity(intent);
//				break;
//			case R.id.btn_edit:
//				if (isCancelMode) {
//					mBtn_Edit.setText(R.string.edit);
//					isCancelMode = false;
//				} else {
//					isCancelMode = true;
//					mBtn_Edit.setText(R.string.done);
//				}
//				showLayout(isCancelMode);
//				break;
//			case R.id.button2:
//				DialogUtils.showMyDialog(PayTotalActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, getString(R.string.querenshanchu), getString(R.string.shifouqueding), new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						for (Map.Entry<Integer, Boolean> me : mAdapter.getCancle().entrySet()) {
//							// Toast.makeText(PayTotalActivity.this, "size===="
//							// + mAdapter.getCancle().size() + "----key===" +
//							// me.getKey() + "-------value====" + me.getValue(),
//							// 1).show();
//							int index = -1;
//							Iterator<ShopListBean> sListIterator = list.iterator();
//							while (sListIterator.hasNext()) {
//								sListIterator.next();
//								index++;
//								if (index == (int) me.getKey()) {
//									sListIterator.remove();
//									break;
//								}
//							}
//						}
//
//						mAdapter.setmList(list);
//						mAdapter.clearTreeMap();
//						mAdapter.notifyDataSetChanged();
//						DialogUtils.dismissMyDialog();
//					}
//				});
//
//				break;
//			default:
//				break;
//		}
//	}
//
//	private void showLayout(boolean b) {
//		if (b) {
//			mLayout_Cancel.setVisibility(View.VISIBLE);
//			mLayout_Pay.setVisibility(View.GONE);
//			mAdapter.setCancelMode(b);
//		} else {
//			mLayout_Pay.setVisibility(View.VISIBLE);
//			mLayout_Cancel.setVisibility(View.GONE);
//			mAdapter.setCancelMode(b);
//		}
//		mAdapter.notifyDataSetChanged();
//	}
}
