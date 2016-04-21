package com.example.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.example.adapter.ChoosePhotoListAdapter;
import com.example.com.example.base.BaseActivity;
import com.example.listener.UILPauseOnScrollListener;
import com.example.model.ImagePath;
import com.example.sean.liontest1.R;
import com.example.util.UILImageLoader;
import com.example.view.HeaderLayout;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.HorizontalListView;

/**
 * Created by Administrator on 2016/3/16.
 *
 */
public class ImageChoose extends BaseActivity{

	private List<PhotoInfo> mPhotoList;
	private ChoosePhotoListAdapter mChoosePhotoListAdapter;
	private Button mOpenGallery;
	HorizontalListView mLvPhoto;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_image_choose);

		initTopBarForBothWithText("选择图片", "保存",
				new HeaderLayout.onRightImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						ArrayList<String> photoArray = new ArrayList<String>();
						for (PhotoInfo photoInfo:mPhotoList)
						{
							photoArray.add(photoInfo.getPhotoPath());
						}
						ShowLog("在图片选择界面：" + photoArray.size());

						setResult(RESULT_OK, getIntent().putStringArrayListExtra("photos", photoArray));

						ImageChoose.this.finish();
					}
				},
				R.drawable.base_action_bar_back_bg_selector,
				new HeaderLayout.onLeftImageButtonClickListener() {
					@Override
					public void onClick() {
						ImagePath.deleteAll(ImagePath.class);
						finish();
					}
				});

		initview();

	}

	private void initview() {

		initImageLoader(this);
		// 返回显示的图片列表
		mLvPhoto = (HorizontalListView) findViewById(R.id.lv_photo);
		mPhotoList = new ArrayList<>();

		try{
				ShowLog("-------------");
				List<ImagePath> ml = ImagePath.listAll(ImagePath.class);
				for (ImagePath path1 : ml) {
					PhotoInfo p = new PhotoInfo();
					p.setPhotoPath(path1.getPath());
					ShowLog("-----------------------------"+path1.getPath());
					mPhotoList.add(p);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		mChoosePhotoListAdapter = new ChoosePhotoListAdapter(this, mPhotoList);
		mLvPhoto.setAdapter(mChoosePhotoListAdapter);
		mLvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//删除照片
				List<ImagePath> imagePath = ImagePath.find(ImagePath.class, "path = ?",
						mPhotoList.get(position).getPhotoPath());
				imagePath.get(0).delete();
				mPhotoList.remove(position);
				mChoosePhotoListAdapter.notifyDataSetChanged();
			}
		});
		//选择图片按钮
		mOpenGallery = (Button) findViewById(R.id.open_gall);

		mOpenGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//设置主题  蓝绿色主题
				//ThemeConfig themeConfig = ThemeConfig.CYAN;
				//自定义主题 已经和应用一样
				ThemeConfig themeConfig = new ThemeConfig.Builder()
						.setTitleBarBgColor(Color.rgb(12, 159, 227))
						.setFabNornalColor(Color.rgb(12, 159, 227))
						.setFabPressedColor(Color.rgb(0x01, 0x83, 0x93))
						.setCheckSelectedColor(Color.rgb(12, 159, 227))
						.setCropControlColor(Color.rgb(0x00, 0xac, 0xc1)).build();
				//功能设置

				final FunctionConfig functionConfig = new FunctionConfig.Builder()
						.setSelected(mPhotoList)
						.setMutiSelectMaxSize(10)//配置多选数量
						.setEnablePreview(true)//预览功能
						.build();

				cn.finalteam.galleryfinal.ImageLoader imageLoader = new UILImageLoader();
				PauseOnScrollListener pauseOnScrollListener = null;
				pauseOnScrollListener = new UILPauseOnScrollListener(false, true);

				//核心设置
				CoreConfig coreConfig =
						new CoreConfig.Builder(ImageChoose.this, imageLoader, themeConfig)
								.setFunctionConfig(functionConfig)
								.setPauseOnScrollListener(pauseOnScrollListener)
								.setNoAnimcation(true)
								.build();
				GalleryFinal.init(coreConfig);
				//打开多选图片
				ActionSheet.createBuilder(ImageChoose.this, getSupportFragmentManager())
						.setCancelButtonTitle("Cancel")
						.setOtherButtonTitles("打开相册")
						.setCancelableOnTouchOutside(true)
						.setListener(new ActionSheet.ActionSheetListener() {
							@Override
							public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

							}

							@Override
							public void onOtherButtonClick(ActionSheet actionSheet, int index) {
								switch (index) {
									case 0:
										GalleryFinal.openGalleryMuti(1, functionConfig, mOnHanlderResultCallback);
										break;
								}
							}
						}).show();

			}
		});
	}

	private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback =
			new GalleryFinal.OnHanlderResultCallback() {
		@Override
		public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
			if (resultList != null) {
				mPhotoList.addAll(resultList);
				for (PhotoInfo photoInfo : mPhotoList) {
					//本地缓存
					ImagePath path = new ImagePath(photoInfo.getPhotoPath());
					ShowLog("-----------------"+path.getId());
					path.save();
				}
				mChoosePhotoListAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onHanlderFailure(int requestCode, String errorMsg) {
			Toast.makeText(ImageChoose.this, errorMsg, Toast.LENGTH_SHORT).show();
		}
	};

	//初始化imageloader
	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

}
