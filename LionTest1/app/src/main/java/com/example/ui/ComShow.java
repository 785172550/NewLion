package com.example.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.baoyz.actionsheet.ActionSheet;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.example.com.example.base.BaseActivity;
import com.example.entity.Company;
import com.example.sean.liontest1.R;
import com.example.util.NetworkImageHolderView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/3/18.
 *
 */
public class ComShow extends BaseActivity {

	private ConvenientBanner convenientBanner;//顶部广告栏控件
	private List<String> networkImages = new ArrayList<String>();

    private Company company;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_comshow);
		setStatusBar();
		initTopBarForLeft("商家信息");
		init_IMG();
		initview();
	}



	private void initview() {

		TextView com_name = (TextView) findViewById(R.id.com_name);
		TextView com_info = (TextView) findViewById(R.id.com_profile);
		TextView com_adr = (TextView) findViewById(R.id.com_info);

		com_name.setText(company.getCompany_name());
		com_info.setText(company.getIntroduction());
		// com_adr.setText(adr);

		TextView lianxi = (TextView) findViewById(R.id.lianxi_tv);
		lianxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionSheet.createBuilder(ComShow.this,getSupportFragmentManager())
						.setCancelButtonTitle("取消")
						.setOtherButtonTitles("打电话")
						.setCancelableOnTouchOutside(true).
						setListener(new ActionSheet.ActionSheetListener() {
							@Override
							public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

							}

							@Override
							public void onOtherButtonClick(ActionSheet actionSheet, int index) {
								if (index == 0) {
									//打电话
                                    Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + company.getContact()));
                                    ComShow.this.startActivity(intent);
								}
							}
						}).show();

			}
		});
	}

	private void init_IMG() {
        company = (Company)getIntent().getSerializableExtra("company");

		ShowLog("deeplee 公司的展示图片" + company.getShow_photo());
        // 获取照片数组
        String [] photoArray = company.getShow_photo().split(";");


		convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
		//图片加载器
		initImageLoader();
		//动画
		init_anim();

//		convenientBanner.setPages(
//				new CBViewHolderCreator<LocalImageHolderView>() {
//					@Override
//					public LocalImageHolderView createHolder() {
//						return new LocalImageHolderView();
//					}
//				}, localImages)
//				//设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//				.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
//						//设置指示器的方向
////                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
////                .setOnPageChangeListener(this)//监听翻页事件
//				//.setOnItemClickListener(this);

        networkImages = Arrays.asList(photoArray);

        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },networkImages);


	}

	private void init_anim() {
		Class cls = null;
		try {
			cls = Class.forName("com.ToxicBakery.viewpager.transforms." + CubeOutTransformer.class.getSimpleName());
			ABaseTransformer transforemer= (ABaseTransformer)cls.newInstance();
			convenientBanner.getViewPager().setPageTransformer(true, transforemer);
			convenientBanner.setScrollDuration(2200);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	private  void initImageLoader(){

		//网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
				showImageForEmptyUri(R.drawable.ic_gf_default_photo)
				.cacheInMemory(true).cacheOnDisk(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
	 *
	 * @param variableName
	 * @param c
	 * @return
	 */
	public static int getResId(String variableName, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(variableName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	// 开始自动翻页
	@Override
	protected void onResume() {
		super.onResume();
		//开始自动翻页
		convenientBanner.startTurning(4000);
	}
	// 停止自动翻页
	@Override
	protected void onPause() {
		super.onPause();
		//停止翻页
		convenientBanner.stopTurning();
	}

}
