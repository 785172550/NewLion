package com.example.view;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import com.bigkoo.convenientbanner.holder.Holder;
import com.example.sean.liontest1.CustomApplcation;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/18.
 *
 */
public class LocalImageHolderView implements Holder<Integer> {
	private ImageView imageView;

	@Override
	public View createView(Context context) {
		imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		return imageView;
	}

	@Override
	public void UpdateUI(Context context, int position, Integer data) {
		//imageView.setImageResource(data);
		imageView.setImageBitmap(initbit(data,context));
	}

	//加载大图片 （最省内存的方法） 返回bitmap
	public Bitmap initbit(int rec,Context context)
	{
		//获取图片资源
		InputStream is = context.getResources().openRawResource(rec);

		BitmapFactory.Options options = new  BitmapFactory.Options();

		options.inJustDecodeBounds =  false;
		//文件大小缩小8倍
		options.inSampleSize = 2;
		/**
		 * decodeStream最大的秘密在于其直接调用JNI>>nativeDecodeAsset()来完成decode，
		 * 无需再使用java层的createBitmap，从而节省了java层的空间
		 */
		Bitmap btp =  BitmapFactory.decodeStream(is, null,  options);
		return btp;
	}

}
