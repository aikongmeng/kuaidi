package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;

public class LoadDialog extends Dialog {

	public LoadDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context context;
		private ImageView iv;
		private LoadDialog dialog;
		private AnimationDrawable animation;
		private boolean flag;
		private long mExitTime;

		public Builder(Context context) {
			this.context = context;
		}

		public void change() {
			AnimationDrawable animationing = (AnimationDrawable) context
					.getResources().getDrawable(R.anim.fram_jiazaiing);
			iv.setImageDrawable(animationing);
			animationing.start();
		}

		public void close() {
			if (flag) {
				int size = (int) ((System.currentTimeMillis() - mExitTime) / 100);
				//Log.i("iii", size + "");
				animation.stop();
				AnimationDrawable anim = new AnimationDrawable();

				for (int i = size + 1; i < 35; i++) {
					try {
						int id = R.drawable.class
								.getDeclaredField("jiazai" + i).getInt(null);
						Drawable mBitAnimation = context.getResources()
								.getDrawable(id);
						anim.addFrame(mBitAnimation, 300 / (34 - size));
						//Log.i("iii", "add" + i);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				iv.setImageDrawable(anim);
				anim.start();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}, 300);
			} else {
				dialog.dismiss();
			}
		}

		public LoadDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dialog = new LoadDialog(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_loading, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			iv = (ImageView) layout.findViewById(R.id.iv_dialog_loading);
			animation = (AnimationDrawable) context.getResources().getDrawable(
					R.anim.fram_jiazai);
			iv.setImageDrawable(animation);
			animation.start();
			mExitTime = System.currentTimeMillis();
			int duration = 0;
			for (int i = 0; i < animation.getNumberOfFrames(); i++) {
				duration += animation.getDuration(i);
			}
			flag = true;
			new Handler().postDelayed(new Runnable() {
				public void run() {
					if (flag) {
						flag = false;
						animation.stop();
						change();
					}
				}
			}, duration);
			return dialog;
		}
	}
}
