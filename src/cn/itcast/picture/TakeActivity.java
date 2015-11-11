package cn.itcast.picture;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibMediaException;
import org.videolan.media.Util;
import org.videolan.media.MEDIAApplication;
import org.videolan.media.WeakHandler;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TakeActivity extends Activity implements IVideoPlayer, Callback,
		OnClickListener {
	private SurfaceView surfaceView;
	private LibVLC mLibMEDIA = null;
	private SurfaceHolder surfaceHolder = null;
	private Button switchs = null;
    private Button list = null;
    private Button videFile = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Window window = getWindow();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		// window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.video_player);
		initView();

		try {
			// Start LibMEDIA
			if (mLibMEDIA == null) {
				mLibMEDIA = Util.getLibMediaInstance();
				mLibMEDIA.useIOMX();
				// rtsp://217.146.95.166:554/live/ch6bqvga.3gp
				// rtsp://217.146.95.166:554/live/ch14bqvga.3gp
				// rtsp://217.146.95.166:554/live/ch12bqvga.3gp
				// rtsp://217.146.95.166:554/live/ch11yqvga.3gp
				// rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp
				// rtsp://218.204.223.237:554/mobile/1/42E708E27C2D4A84/hygogn9q2g6nr3de.sdp
                //"http://v.wm927.cn/video/2014/5/6/MF201405061700.mp4";//
                //"http://113.247.251.11:5000/nn_live.m3u8?id=CCTV13";//
				String path = "file:///storage/emulated/0/flloer.mp4";//http://v.wm927.cn/video/2013/9/25/ENGLISH-VIP-1.mp4";// "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";//http://live.wm927.cn/live/wm927/playlist.m3u8";//"http://v.wm927.cn/video/2013/9/25/ENGLISH-VIP-1.mp4";
                //"rtsp://217.146.95.166:554/live/ch6bqvga.3gp"
                mLibMEDIA.readMedia(path, false);
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		} catch (LibMediaException e) {
			e.printStackTrace();
			finish();
			super.onCreate(savedInstanceState);
			return;
		}

		mLibMEDIA.playIndex(0);

	}

	private void initView() {
        list = (Button) findViewById(R.id.video_list);
		// ��ȡSurfaceView���
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		// ��ȡSurfaceView�󶨵�SurfaceHolderonc
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setFormat(PixelFormat.RGBX_8888);
		// ע��ص�
		surfaceHolder.addCallback(this);

		btnPlayPause = (ImageView) findViewById(R.id.video_player_playpause);
		btnSize = (ImageView) findViewById(R.id.video_player_size);
		mTextTime = (TextView) findViewById(R.id.video_player_time);
		mTextLength = (TextView) findViewById(R.id.video_player_length);
		mSeekBar = (SeekBar) findViewById(R.id.video_player_seekbar);
		mTextShowInfo = (TextView) findViewById(R.id.video_player_showinfo);

		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		btnPlayPause.setOnClickListener(this);
		btnSize.setOnClickListener(this);

		switchs = (Button) findViewById(R.id.switchs);
		switchs.setOnClickListener(this);
        list.setOnClickListener(this);

        videFile = (Button) findViewById(R.id.video_file);
        videFile.setOnClickListener(this);
	}

	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				if (mLibMEDIA != null) {
					if (!mLibMEDIA.isPlaying()) {
						mLibMEDIA.play();
					}
					mLibMEDIA.setTime(progress);
				}
			}
		}
	};

	private void showVideoTime(int t, int l) {
		mTextTime.setText(millisToString(t));
		mTextLength.setText(millisToString(l));
	}

	/**
	 * Convert time to a string
	 * 
	 * @param millis
	 *            e.g.time/length from file
	 * @return formated string (hh:)mm:ss
	 */
	public static String millisToString(long millis) {
		boolean negative = millis < 0;
		millis = java.lang.Math.abs(millis);

		millis /= 1000;
		int sec = (int) (millis % 60);
		millis /= 60;
		int min = (int) (millis % 60);
		millis /= 60;
		int hours = (int) millis;

		String time;
		DecimalFormat format = (DecimalFormat) NumberFormat
				.getInstance(Locale.US);
		format.applyPattern("00");
		if (millis > 0) {
			time = (negative ? "-" : "") + hours + ":" + format.format(min)
					+ ":" + format.format(sec);
		} else {
			time = (negative ? "-" : "") + min + ":" + format.format(sec);
		}
		return time;
	}

	@Override
	protected void onDestroy() {
		mLibMEDIA.destroy();
		super.onDestroy();
	}

	private TextView mTextTime;
	private TextView mTextLength;

	private ImageView btnPlayPause;
	private ImageView btnSize;
	private SeekBar mSeekBar;
	private TextView mTextShowInfo;

	private int mVideoHeight;
	private int mVideoWidth;
	private int mVideoVisibleHeight;
	private int mVideoVisibleWidth;
	private int mSarNum;
	private int mSarDen;
	private static final int SURFACE_UPDATE = 2;// size changes
	private static final int SURFACE_SIZE = 3;// size changes

	private static final int SURFACE_BEST_FIT = 0;// ����Ӧ
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private int mCurrentSize = SURFACE_BEST_FIT;

	@Override
	public void setSurfaceSize(int width, int height, int visible_width,
			int visible_height, int sar_num, int sar_den) {
		// store video size
		mVideoHeight = height;
		mVideoWidth = width;
		mVideoVisibleHeight = visible_height;
		mVideoVisibleWidth = visible_width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
		mHandler.sendMessage(msg);
	}

	public void setSurfaceSize(int width, int height, int sar_num, int sar_den) {
		// store video size
		mVideoHeight = height;
		mVideoWidth = width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
		mHandler.sendMessage(msg);
	}

	private final Handler mHandler = new VideoPlayerHandler(this);

	private static class VideoPlayerHandler extends WeakHandler<TakeActivity> {
		public VideoPlayerHandler(TakeActivity owner) {
			super(owner);
		}

		public void handleMessage(Message msg) {
			TakeActivity activity = getOwner();
			if (activity == null) // WeakReference could be GC'ed early
				return;

			switch (msg.what) {
			case SURFACE_SIZE:
				activity.changeSurfaceSize();
				break;
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			updateTime();
		}
	};

	private void updateTime() {
		int time = (int) mLibMEDIA.getTime();
		int length = (int) mLibMEDIA.getLength();
		// Log.d(TAG, "handleMessage length: " + length + "; time: " +
		// time);
		mSeekBar.setMax(length);
		mSeekBar.setProgress(time);
		showVideoTime(time, length);
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	private void changeSurfaceSize() {

		// Display display = getWindowManager().getDefaultDisplay();
		// int sreenHeight = display.getHeight();
		// int sreenWidth = display.getWidth();
		// get screen size
		int sreenWidth = getWindow().getDecorView().getWidth();
		int sreenHeight = getWindow().getDecorView().getHeight();

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;// ��ʾ����
		if (sreenWidth > sreenHeight && isPortrait || sreenWidth < sreenHeight
				&& !isPortrait) {
			int d = sreenWidth;
			sreenWidth = sreenHeight;
			sreenHeight = d;
		}
		if (sreenWidth * sreenHeight == 0)
			return;

		// ������Ƶ�������
		double videoRatio = (double) mVideoWidth / (double) mVideoHeight;
		// ������Ļ����
		double sreenRatio = (double) sreenWidth / (double) sreenHeight;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			if (sreenRatio < videoRatio) {
				// ��ʾ��Ƶ�Ŀ�ȳ�������Ļ��ȣ���ʱ����Ƶ���ȡ��Ļ��ȣ�Ȼ��߶ȸ��ݱ�������
				sreenHeight = (int) (sreenWidth / videoRatio);
			} else {
				sreenWidth = (int) (sreenHeight * videoRatio);
			}
			break;
		case SURFACE_FIT_HORIZONTAL:// ˮƽ����ʾ����
			sreenHeight = (int) (sreenWidth / videoRatio);
			break;
		case SURFACE_FIT_VERTICAL:// ��ֱ����ֱ ��ʾ����
			sreenWidth = (int) (sreenHeight * videoRatio);
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			videoRatio = 16.0 / 9.0;
			if (sreenRatio < videoRatio)
				sreenHeight = (int) (sreenWidth / videoRatio);
			else
				sreenWidth = (int) (sreenHeight * videoRatio);
			break;
		case SURFACE_4_3:
			videoRatio = 4.0 / 3.0;
			if (sreenRatio < videoRatio)
				sreenHeight = (int) (sreenWidth / videoRatio);
			else
				sreenWidth = (int) (sreenHeight * videoRatio);
			break;
		case SURFACE_ORIGINAL:
			sreenHeight = mVideoHeight;
			sreenWidth = mVideoWidth;
			break;
		}

		surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		LayoutParams lp = surfaceView.getLayoutParams();
		lp.width = sreenWidth;
		lp.height = sreenHeight;
		surfaceView.setLayoutParams(lp);
		surfaceView.invalidate();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		mLibMEDIA.attachSurface(arg0.getSurface(), TakeActivity.this, arg2, arg3);
		System.out.println("surfaceCreated");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		System.out.println("surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mLibMEDIA.detachSurface();
	}

	public void sreenSwitch() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth,
				mVideoVisibleHeight, mSarNum, mSarDen);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == btnPlayPause.getId()) {
			if (mLibMEDIA.isPlaying()) {
				mLibMEDIA.pause();
				btnPlayPause.setImageResource(R.drawable.ic_play_selector);
			} else {
				btnPlayPause.setImageResource(R.drawable.ic_pause_selector);
				mLibMEDIA.play();
			}
		}
		if (v.getId() == btnSize.getId()) {
			if (mCurrentSize < SURFACE_ORIGINAL) {
				mCurrentSize++;
			} else {
				mCurrentSize = 0;
			}
			changeSurfaceSize();
		}
		if (v.getId() == switchs.getId()) {
			sreenSwitch();
		}

        if (list.getId() == v.getId()){
            showList();
        }

        if (videFile.getId() == v.getId()){
//            showFileChooser();
            String pat = "/mnt/long1.mp4";
            mLibMEDIA.readMedia(pat, false);

        }
	}

    private void showList(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.list, null);
        final EditText edtInput=(EditText)textEntryView.findViewById(R.id.edtInput);
        final EditText edtInput1=(EditText)textEntryView.findViewById(R.id.edtInput1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("Title");
        builder.setView(textEntryView);
        builder.setPositiveButton("第一个",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setTitle(edtInput.getText());
                        mLibMEDIA.readMedia(edtInput.getText().toString(), false);
                        System.out.println("+++++++++++++++硬件解码=" + mLibMEDIA.useIOMX());
                    }
                });
        builder.setNegativeButton("第二个",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setTitle(edtInput1.getText());
                        mLibMEDIA.readMedia(edtInput1.getText().toString(), false);
                        System.out.println("+++++++++++++++硬件解码=" + mLibMEDIA.useIOMX());
                    }
                });
        builder.show();
    }


    private void enableIOMX(boolean enableIomx){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(MEDIAApplication.getAppContext());
        SharedPreferences.Editor e = p.edit();
        e.putBoolean("enable_iomx", enableIomx);
        LibVLC.restart(MEDIAApplication.getAppContext());
    }

}