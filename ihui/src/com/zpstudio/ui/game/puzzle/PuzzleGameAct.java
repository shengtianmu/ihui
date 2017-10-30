package com.zpstudio.ui.game.puzzle;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class PuzzleGameAct extends Activity {
	static String TAG = "PuzzleGameAct";
	TableLayout tableLayout;
	/**
	 * 存放导航图
	 */
	ImageView ivSamll;
	/**
	 * 存放结束游戏时的图片
	 */
	ImageView ivFugai;
	/**
	 * 用户走过的步数
	 */
	static int step = 0;
	/**
	 * 存放切割后的图片
	 */
	Map<Point, MyImageView> map = new HashMap<Point, MyImageView>();

	/**
	 * 拼图的列数
	 */
	static int ROW;
	/**
	 * 拼图的行数
	 */
	static int COL;
	/**
	 * 存放用户走过的路径
	 */
	static List<Integer> listPath;
	/**
	 * 定义常量,向上为-1
	 */
	final int UP = -1;
	/**
	 * 定义常量,向下为1
	 */
	final int DOWN = 1;
	/**
	 * 定义常量,向坐为-2
	 */
	final int LEFT = -2;
	/**
	 * 定义常量,向坐为2
	 */
	final int RIGHT = 2;
	/**
	 * 用于记录back数组长度,需要原路返回几次
	 */
	static int pathlength = 0;
	/**
	 * 获取的黑色图片的位置x
	 */
	private int x;
	/**
	 * 获取的黑色图片的位置y
	 */
	private int y;

	Handler handLer;

	/**
	 * 从上一个Activity传过来的图片路径
	 */
	String  picPath;
	String  picPahtTitle;

	/**
	 * 最优路径
	 */
	static List listMost;

	/**
	 * 图片数组,用于存放切割后的图片
	 */
	MyImageView iv[];

	int[] back;// 用于存放图片点击路径
	/**
	 * 存放黑色框的坐标x
	 */
	static int backx = 0;
	/**
	 * 存放黑色框的坐标y
	 */
	static int backy = 0;
	/**
	 * 判断游戏是否正在自动运行
	 */
	static boolean isAuto = true;

	int key;


	private ImageLoader imageLoader;
	Button   btnTitleBack;
	TextView tvTitleText;
	ImageButton   btnMore;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.puzzlegame_act);
		/* title */
        btnTitleBack = (Button)findViewById(R.id.title_reback_btn);
    	btnTitleBack.setVisibility(View.VISIBLE);
    	btnTitleBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onGoBack();
            }  
        });
        
        tvTitleText  = (TextView)findViewById(R.id.title_text);
        tvTitleText.setText(getString(R.string.puzzlegame));
        tvTitleText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            }  
        });
        
        /* more */
        btnMore = (ImageButton)findViewById(R.id.right_btn);
        btnMore.setVisibility(View.GONE);
        btnMore.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }  
        });
		initImageLoader();
		ivFugai = (ImageView) findViewById(R.id.ivFugai);
		ivSamll = (ImageView) findViewById(R.id.ivSmall);
		Intent it = getIntent();
		picPath = it.getStringExtra("pic");// 获取上一个Activity传回来的图片路径
		picPahtTitle = it.getStringExtra("picTitle");// 获取上一个Activity传回来的图片路径

		key = it.getIntExtra("key", 0);
		tableLayout = (TableLayout) findViewById(R.id.myTableLayout);// 初始化tableLayout
		imageLoader.displayImage(picPahtTitle,ivSamll);
		ivFugai.setVisibility(View.INVISIBLE);// 设置游戏结束时弹出的图片不可见
		imageLoader.displayImage(picPath,ivFugai);
		performSimple();

	}

	/**
	 * 过滤,用来过滤用户走过的重复路径,减少自动返回的所走的次数
	 */
	public void guolv() {
		int sum = 0;
		listMost = new ArrayList();
		back = new int[listPath.size()];

		for (int i = 0; i < listPath.size(); i++) {
			back[i] = listPath.get(i); // 将用户所走的路径添加到back数组中
		}
		for (int i = 0; i < back.length; i++) {
			sum = 0;
			for (int j = 0; j < 2; j++) {
				if (i + j < back.length) {// 判断前一个路径与后一个路径是否重复
					sum += back[i + j];
				}
			}
			if (sum == 0) {
				System.out.println("sum==0");
				i = i + 1;
			} else {
				listMost.add(back[i]);// 如果不存在相同路径,则添加到最有路径的list里面
			}
		}

	}

	/**
	 * 线程,用来返回原来拼图
	 */
	Runnable update = new Runnable() {

		@Override
		public void run() {

			if (pathlength >= 0) {

				gotoback();// 调用gotoback方法,用于按原来返回

			} else if (pathlength <= 0) {
				pandun();// 当自动拼图还原后,添加一个判断,用于显示动画和toast
			}

		}
	};

	/**
	 * 自动原路返回,通过判断上下左右,根据原来的路径,往相反方向走
	 */
	public void gotoback() {
		switch (back[pathlength]) {
		case UP:
			changePic(map.get(new Point(x + 1, y)).getId());
			x = x + 1;
			break;

		case DOWN:
			changePic(map.get(new Point(x - 1, y)).getId());
			x = x - 1;

			break;
		case LEFT:

			changePic(map.get(new Point(x, y + 1)).getId());
			y = y + 1;

			break;
		case RIGHT:

			changePic(map.get(new Point(x, y - 1)).getId());
			y = y - 1;

			break;
		default:
			break;
		}
		pathlength--;
		step += 1;// 步数加一
		handLer.postDelayed(update, 200);// 等待200毫秒之后将线程重新添加到handler中
	}

	Bitmap myPic;

	public void addPic() {

		myPic = imageLoader.loadImageSync(picPath);
		//myPic = BitmapFactory.decodeResource(super.getResources(), picPath);// 获取图片资源
		Matrix m = new Matrix();// 获取工具类
		WindowManager wm = (WindowManager) this
				.getSystemService(WINDOW_SERVICE);
		int wid = wm.getDefaultDisplay().getWidth() - 20;// 获取屏幕的宽度
		int hei = wm.getDefaultDisplay().getHeight() / 3 * 2 - 40;// 获取屏幕的高度
		m.postScale((float) wid / myPic.getWidth(),
				(float) hei / myPic.getHeight());// 图片的宽高和屏幕的宽高比
		int x = 0;
		for (int i = 0; i < ROW; i++) {

			TableRow tr = new TableRow(this);

			for (int j = 0; j < COL; j++) {
				iv[x] = new MyImageView(this, i * 10 + j);// 实例化一个imageview

				Bitmap bitmap = Bitmap.createBitmap(myPic, myPic.getWidth()
						/ COL * j, myPic.getHeight() / ROW * i,
						myPic.getWidth() / COL, myPic.getHeight() / ROW, m,
						true);// 将图片剪切为一定长宽的小图片

				iv[x].setId(i * 10 + j);// 设置id

				iv[x].setImageBitmap(bitmap);// 设置图片

				iv[x].setPadding(1, 1, 1, 1);// 设置每张图片的边距

				iv[x].setBackgroundColor(Color.WHITE);// 设置图片背景色,用于显示白色边框

				map.put(new Point(i, j), iv[x]);// 添加到map中,用于之后交换图片,可以根据point获取imageview

				iv[x].setOnClickListener(new OnClickImage());

				tr.addView(iv[x]);

				if (i == ROW - 1 && j == COL - 1) {

					iv[x].setVisibility(View.INVISIBLE);// 预设最后一张图片为不可见

					iv[x].setId(100);// 预设最后一张图片id为100
				}
				x++;
			}

			tableLayout.addView(tr, new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

		}

	}

	/**
	 * 用于判断游戏是否结束,根据图片之前设置的index与id做比较
	 */
	public void pandun() {
		int k = 0;
		for (int i = 0; i < ROW * COL; i++) {

			if (iv[i].getIndex() == iv[i].getId()) {
				k += 1;
			}

		}

		if (k == ROW * COL - 1) {
			Toast.makeText(this, "挑战成功", Toast.LENGTH_SHORT).show();
			tableLayout.setVisibility(View.INVISIBLE);// 将整个tablelayout设置为不可见
			ivFugai.setVisibility(View.VISIBLE);// 结束游戏图片出现
			Animation anim;// 添加动画
			anim = AnimationUtils.loadAnimation(this, R.anim.puzzlegame_rotate);
			ivFugai.startAnimation(anim);// 开始动画
			victory();
			

		}
	}

	/**
	 * 点击图片事件,判断相邻两张图片是否能够移动
	 * 
	 * @author Administrator
	 * 
	 */
	class OnClickImage implements OnClickListener {

		@Override
		public void onClick(View v) {

			int i = v.getId();
			isAuto = false;// 将自动游戏设为false
			changePic(i);// 调用图片跳转方法
			pandun();// 判断游戏是否结束

		}

	}

	public void changePic(int id) {
		int x = id / 10;// 将传过来的i值转为x
		int y = id % 10;// 将传过来的i值转为y
		if (map.get(new Point(x - 1, y)) != null) {// 判断上面是否为空

			if (map.get(new Point(x - 1, y)).getId() == 100) {// 判断上面一张图片是否为黑框,是则交换位置
				iv[(x) * COL + y].setVisibility(View.INVISIBLE);// 将当前一张图片设为不可见

				iv[(x - 1) * COL + y].setImageDrawable(iv[(x) * COL + y]
						.getDrawable());// 将黑框图片设为与它交换位置的那张图

				iv[(x - 1) * COL + y].setVisibility(View.VISIBLE);// 黑框设为可见

				map.get(new Point(x - 1, y)).setIndex(
						map.get(new Point(x, y)).getIndex());// 黑框获取当前图片的index

				map.get(new Point(x - 1, y)).setId((x - 1) * 10 + y);// 黑框的id跟着改变

				map.get(new Point(x, y)).setId(100);// 设置当前图片id为100

				listPath.add(1);// 将路径添加到listpath中

				if (isAuto == false) {// 是否正在自动运行,是则不继续运行
					step += 1;
					backx = backx + 1;
				}

			}

		}

		if (map.get(new Point(x + 1, y)) != null) {// 判断下边是否为空
			if (map.get(new Point(x + 1, y)).getId() == 100) {// 判断下面一张图片是否为黑框,是则交换位置
				iv[(x) * COL + y].setVisibility(View.INVISIBLE);
				iv[(x + 1) * COL + y].setImageDrawable(iv[(x) * COL + y]
						.getDrawable());
				iv[(x + 1) * COL + y].setVisibility(View.VISIBLE);
				map.get(new Point(x + 1, y)).setIndex(
						map.get(new Point(x, y)).getIndex());
				map.get(new Point(x + 1, y)).setId((x + 1) * 10 + y);
				map.get(new Point(x, y)).setId(100);

				listPath.add(-1);

				if (isAuto == false) {
					step += 1;
					backx = backx - 1;
				}

			}

		}

		if (map.get(new Point(x, y + 1)) != null) {// 判断右边是否为空
			if (map.get(new Point(x, y + 1)).getId() == 100) {// 判断右边一张图片是否为黑框,是则交换位置
				iv[(x) * COL + y].setVisibility(View.INVISIBLE);
				iv[(x) * COL + y + 1].setImageDrawable(iv[(x) * COL + y]
						.getDrawable());

				iv[(x) * COL + y + 1].setVisibility(View.VISIBLE);

				map.get(new Point(x, y + 1)).setIndex(
						map.get(new Point(x, y)).getIndex());
				map.get(new Point(x, y + 1)).setId((x) * 10 + y + 1);

				map.get(new Point(x, y)).setId(100);

				listPath.add(-2);

				if (isAuto == false) {
					step += 1;
					backy = backy - 1;
				}

			}

		}

		if (map.get(new Point(x, y - 1)) != null) {// 判断左边是否为空
			if (map.get(new Point(x, y - 1)).getId() == 100) {// 判断左边一张图片是否为黑框,是则交换位置

				iv[(x) * COL + y].setVisibility(View.INVISIBLE);
				iv[(x) * COL + y - 1].setImageDrawable(iv[(x) * COL + y]
						.getDrawable());

				iv[(x) * COL + y - 1].setVisibility(View.VISIBLE);

				map.get(new Point(x, y - 1)).setIndex(
						map.get(new Point(x, y)).getIndex());

				map.get(new Point(x, y - 1)).setId((x) * 10 + y - 1);

				map.get(new Point(x, y)).setId(100);

				listPath.add(2);

				if (isAuto == false) {
					step += 1;
					backy = backy + 1;
				}

			}

		}

	}

	public void gogogo(int num) {
		listPath = new ArrayList();
		int[] arr = { 1, -1, 2, -2 };// 生成一个一维数组,存放上下左右四个方向
		int x = ROW - 1;// 定义x为右下角x坐标
		int y = COL - 1;// 定义y为右下角y坐标

		for (int i = 0; i < num; i++) {
			int index = (int) (Math.random() * arr.length);// 随机生成一个方向
			int rand = arr[index];
			switch (rand) {
			case UP:
				if (x + 1 > ROW - 1) {
					break;
				} else {
					changePic(map.get(new Point(x + 1, y)).getId());
					x = x + 1;

				}

				break;

			case DOWN:
				if (x - 1 < 0) {
					break;
				} else {
					changePic(map.get(new Point(x - 1, y)).getId());
					x = x - 1;

				}

				break;
			case LEFT:
				if (y + 1 > COL - 1) {
					break;
				} else {
					changePic(map.get(new Point(x, y + 1)).getId());
					y = y + 1;

				}

				break;
			case RIGHT:
				if (y - 1 < 0) {
					break;
				} else {

					changePic(map.get(new Point(x, y - 1)).getId());
					y = y - 1;

				}

				break;
			default:
				break;
			}
		}

		backx = x;// 将x坐标赋给黑框x坐标
		backy = y;// 将y坐标赋给黑框y坐标

	}

	/**
	 * 自定义类,添加一个构造函数,增加一个变脸index,用于判断游戏结束时用到
	 * 
	 * @author Administrator
	 * 
	 */
	class MyImageView extends ImageView {

		int index;// 定义的下标,用于最后判断拼图是否已经拼成

		public MyImageView(Context context, int index) {
			super(context);
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}

	/**
	 * 点击简单按钮事件
	 * 
	 * @author Administrator
	 * 
	 */

	void performSimple() {

			ROW = 2;// 定义长
			COL = 2;// 定义宽
			initView();// 初始化游戏界面
			gogogo(45);// 打乱图片顺序

	}


	/**
	 * 点击正常按钮事件
	 * 
	 * @author Administrator
	 * 
	 */

	void performNormal() {

		ROW = 4;
		COL = 4;
		initView();// 初始化图片
		gogogo(85);// 打乱图片顺序

	}

	/**
	 * 点击困难按钮事件
	 * 
	 * @author Administrator
	 * 
	 */
	 void performHard() {

			ROW = 6;
			COL = 6;
			initView();// 初始化图片
			gogogo(155);// 打乱图片顺序
		}

	/**
	 * 点击游戏开始时,初始化游戏界面
	 */
	public void initView() {
		tableLayout.removeAllViews();// 将布局里的控件全部删除
		tableLayout.setVisibility(View.VISIBLE);// 布局设为可见
		ivFugai.setVisibility(View.INVISIBLE);// 游戏结束后图片不可见
		step = 0;// 步数设为0
		backx = 0;// 黑框x坐标设为0
		backy = 0;// 黑框y坐标设为0
		isAuto = true;
		iv = new MyImageView[ROW * COL];// 设置图片数组的大小
		addPic();// 添加图片,并且切割
	}

	
	private void initImageLoader() {
		
		imageLoader = ImageLoader.getInstance();
	}
	
	private void victory() {
		// TODO Auto-generated method stub
		
		String sId = getIntent().getStringExtra(Config.EXTRA_PUZZLE_GAME_ID);
		/*
		Log.i(TAG , "victory " + sId);
		Intent intent = new Intent();
		intent.setAction(Config.INTENT_PUZZLE_GAME_WIN);
		intent.putExtra(Config.EXTRA_PUZZLE_GAME_ID, sId);
		sendBroadcast(intent);
		*/
		IhuiClientApi.getInstance(this).puzzlegameWinInd(sId);
	}

	@Override
	protected void onDestroy() {
		if (myPic != null) {
			myPic.recycle();
		}
		super.onDestroy();
	}
	
	void onGoBack()
	{
		finish();
	}
}
