package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;

import cn.dcx.privatebox.R;
import cn.dcx.privatebox.utils.BitmapUtils;
import cn.dcx.privatebox.utils.UtilTools;

public class SingleImgViewActivity extends Activity {
	private View.OnClickListener mClickListener = null;
	private ImageView mImageView = null;
	private boolean m_bIsDoubleClick = false;
	private Matrix matrix = new Matrix();
	private Button btn_back = null;
	private String imgPath = null;
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_singleimg_show);
		initActivity();
	}

	private void initActivity() {
		Intent localIntent = getIntent();
		this.imgPath = localIntent.getStringExtra("imgPath");
		this.mClickListener = new MOnClickListener();
		initWidget();
		showImg(0);

	}

	private void initWidget() {
		this.btn_back = ((Button) findViewById(R.id.btn_back));
		this.btn_back.setOnClickListener(this.mClickListener);
		this.mImageView = ((ImageView) findViewById(R.id.img_show));
		this.mImageView.setOnTouchListener(new MTouchListener());
	}

	private void showImg(int paramInt) {
		if (new File(imgPath).exists()) {
			Display localDisplay = getWindowManager().getDefaultDisplay();
			try {
				Bitmap localBitmap = BitmapUtils.getBitmapByPath(imgPath, localDisplay.getWidth(), localDisplay.getHeight());
				if (localBitmap != null) {
					this.mImageView.setImageBitmap(localBitmap);
					DisplayMetrics metric = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metric);
					int mWidth = metric.widthPixels; // 屏幕宽度（像素）
					int mHeight = metric.heightPixels; // 屏幕高度（像素）
					matrix = new Matrix();
					matrix.postTranslate((mWidth - localBitmap.getWidth()) / 2, (mHeight - localBitmap.getHeight()) / 2);
					this.mImageView.setImageMatrix(matrix);
				}
			} catch (FileNotFoundException localFileNotFoundException) {
				localFileNotFoundException.printStackTrace();
			}
		} else {
			UtilTools.MsgBox(this, "图片已删除或者不存在");
		}

	}
	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {

			case R.id.btn_back:
				SingleImgViewActivity.this.finish();
				break;
			default:
				return;
			}
		}

	}

	public class MTouchListener implements OnTouchListener {
		static final int DRAG = 1;
		static final int NONE = 0;
		static final int ZOOM = 2;		
		PointF mid = new PointF();
		int mode = 0;
		float oldDist = 1.0F;
		Matrix savedMatrix = new Matrix();
		PointF start = new PointF();

		// 计算移动距离
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float)Math.sqrt(x * x + y * y);
		}

		// 计算中点位置
		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}

		public boolean onTouch(View paramView, MotionEvent event) {
			ImageView view = (ImageView) paramView;

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 设置拖拉模式
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			// 设置多点触摸模式
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			// 若为DRAG模式，则点击移动图片
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					// 设置位移
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				}
				// 若为ZOOM模式，则多点触摸缩放
				else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						// 设置缩放比例和图片中点位置
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			// Perform the transformation
			view.setImageMatrix(matrix);
			return true;

		}
	}
}