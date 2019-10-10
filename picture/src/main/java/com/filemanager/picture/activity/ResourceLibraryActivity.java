package com.filemanager.picture.activity;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.filemanager.picture.R;
import com.filemanager.picture.adapter.ResourceDeviceAdapter;
import com.filemanager.picture.adapter.ResourceViewPagerAdapter;
import com.filemanager.picture.bean.FileRelay;
import com.filemanager.picture.config.StatusConfig;
import com.filemanager.picture.file.FileDataManager;
import com.filemanager.picture.file.FileUtils;
import com.filemanager.picture.file.ResourceHistory;
import com.filemanager.picture.model.FileMode;
import com.filemanager.picture.util.ScreenUtil;
import com.tmall.ultraviewpager.UltraVerticalTabLayout;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.tablayout.SlidingTabLayout;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.filemanager.picture.file.FileUtils.getVolumePaths;
import static com.filemanager.picture.file.FileUtils.isQualified;

/**
 * 资源库
 */
public class ResourceLibraryActivity extends AppCompatActivity {

    private FileMode fileMode;
    private ResourceHistory resourceHistory;
    private ExecutorService mExecutorService;

    private RecyclerView listRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UltraViewPager resourceViewPager;
    private SlidingTabLayout mHorizontalTabLayout;
    private LinearLayout pathLayout;
    private ImageButton backImageBtn;
    private RadioButton searchRadioBtn;
    private EditText searchEditText;
    private TextView pathText;
    private TextView toastText;
    private ImageButton selectPathImageBtn;

    private ResourceDeviceAdapter deviceAdapter;
    private ResourceViewPagerAdapter viewPagerAdapter;

    private List<String> mDevicePaths;//存储设备路径
    private List<String> mDeviceNames;//存储设备名称
    private List<File> mFiles = new ArrayList<>();
    private List<File> mImageFolder = new ArrayList<>();
    private List<File> mMediaFiles = new ArrayList<>();

    private int resource_type = 0;//当前显示的类别
    private String currentPaths;//当前目录路径
    private String currentDevicePath;//当前设备路径
    private boolean isFileNull = false;//文件是否为空
    private boolean isSearch = false;//搜索是否开启
    private int viewDisplayHeight = 0;//当前资源库可见高度

    private ResourceDeviceAdapter.OnDevicesItemClickListener mDevicesListener = new ResourceDeviceAdapter.OnDevicesItemClickListener() {
        @Override
        public void onItemClick(int position) {
            checkDeviceType(position);
            deviceAdapter.setSelectText(position);
            currentDevicePath = mDevicePaths.get(position);
            viewPagerAdapter.clear();
            reLoadListData(currentDevicePath);
            resourceHistory.setCurrentPicturePath("");
            resourceHistory.setCurrentMediaPath("");
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                checkDevicePicturePath(currentDevicePath);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_library_layout);

        resource_type = getIntent().getIntExtra("resource_type", 0);
        mExecutorService = Executors.newSingleThreadExecutor();
        resourceHistory = new ResourceHistory();
        registerReceivers();
        setFinishOnTouchOutside(true);

        addListViews();
        initFilePaths();
        addGridViews();
        addListener();
        reLoadListData(currentDevicePath);
    }

    @SuppressLint("WrongConstant")
    public void addListViews() {
        swipeRefreshLayout = findViewById(R.id.resource_swipe_refresh);

        listRecyclerView = findViewById(R.id.resource_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecyclerView.setLayoutManager(layoutManager);

        loadListView();
    }

    public void loadListView() {
        mDeviceNames = new ArrayList<>();
        mDevicePaths = getVolumePaths(this);

        if (resource_type == 0
                || resource_type == 1
                || resource_type == 4
                || resource_type == 5
                || resource_type == 6
                || resource_type == 7
                || resource_type == 8) {
            fileMode = FileMode.FILE;
//            mDevicePaths.add(0, myResourcePath);
            currentDevicePath = mDevicePaths.get(0);
        } else if (resource_type == 2 || resource_type == 3 || resource_type == 9) {
            if (resource_type == 2 || resource_type == 9) {
                fileMode = FileMode.PICTURE;
            } else if (resource_type == 3) {
                fileMode = FileMode.MEDIA;
            }
            currentDevicePath = mDevicePaths.get(0);
        }

        for (String file : mDevicePaths) {
            File f = new File(file);
            String name;
            if ("/storage/sdcard".equals(file) || "/storage/emulated/0".equals(file)) {
                name = getStringName(R.string.source_storage);
            } else if ("/storage/sdcard1".equals(file) || "/mnt/sdcard".equals(file)) {
                name = getStringName(R.string.source_extstorage);
            } else if ("/".equals(file)) {
                name = getStringName(R.string.source_rootdirectory);
            } else {
                name = FileUtils.conversionToDBC(f.getName());
            }
            if (f.isDirectory() || f.canExecute()) {
                mDeviceNames.add(name);
            }
        }

        deviceAdapter = new ResourceDeviceAdapter(this, mDeviceNames, resource_type);
        listRecyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setOnDevicesItemClickListener(mDevicesListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addGridViews() {
        pathLayout = findViewById(R.id.resource_path_layout);
        searchRadioBtn = findViewById(R.id.resource_search_btn);
        backImageBtn = findViewById(R.id.resource_back_ibt);
        searchEditText = findViewById(R.id.resource_search_edit);
        pathText = findViewById(R.id.resource_path_text);
        toastText = findViewById(R.id.resource_toast_text);
        selectPathImageBtn = findViewById(R.id.resource_select_path);

        resourceViewPager = findViewById(R.id.resource_viewpager);
        resourceViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);

        mHorizontalTabLayout = findViewById(R.id.resource_top_layout);
        mHorizontalTabLayout.setLayoutMode(UltraVerticalTabLayout.TAB_MODE_FIXED);

        List<String> titles = new ArrayList<>();
        titles.add(getStringName(R.string.source_directory));
        titles.add(getStringName(R.string.source_picture));
        titles.add(getStringName(R.string.source_video));

        if (resource_type == 0
                || resource_type == 1
                || resource_type == 4
                || resource_type == 5
                || resource_type == 6
                || resource_type == 7
                || resource_type == 8) {
            viewPagerAdapter = new ResourceViewPagerAdapter(this, resource_type, titles, mFiles);
        } else if (resource_type == 2 || resource_type == 9) {
            viewPagerAdapter = new ResourceViewPagerAdapter(this, resource_type, titles, mImageFolder);
        } else if (resource_type == 3) {
            viewPagerAdapter = new ResourceViewPagerAdapter(this, resource_type, titles, mMediaFiles);
        }

        resourceViewPager.setAdapter(viewPagerAdapter);
        mHorizontalTabLayout.setViewPager(resourceViewPager.getViewPager());

        //TODO:修改为0
        checkDeviceType(0);
    }

    public void addListener() {
        viewPagerAdapter.setOnPagerItemClickListener(new ResourceViewPagerAdapter.OnPagerItemClickListener() {
            @Override
            public void onResourceFinish() {
                finish();
            }

            @Override
            public void onPagerTextNull(boolean isFileNull) {
                showToast(isFileNull);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onPagerItemClick(String path, FileMode mode, boolean isFileNull) {
                if (TextUtils.isEmpty(path)) {
                    path = currentDevicePath;
                }
                hideSearchEdit();
                fileMode = mode;
                if (!path.equals(currentDevicePath) && mode == FileMode.PICTURE) {
                    fileMode = FileMode.PICTURE_FILE;
                }
                refreshCurrentPath(path);
                showToast(isFileNull);
                viewPagerAdapter.refreshPagerData(mode);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mExecutorService.isShutdown()) {
                    mExecutorService.shutdown();
                }
                if (mExecutorService.isTerminated()) {
                    mExecutorService = Executors.newSingleThreadExecutor();
                    FileDataManager.getInstance().clean();
                    initFilePaths();
                    reLoadListData(currentPaths);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        searchRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearch) {
                    isSearch = true;
                    pathLayout.setVisibility(View.GONE);
                    searchEditText.setText("");
                    searchEditText.setVisibility(View.VISIBLE);
                    editAnimation(searchEditText, 50, 480, isSearch);
                } else {
                    isSearch = false;
                    editAnimation(searchEditText, 480, 50, isSearch);
                }
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<File> mFile = new ArrayList<>();

                if (fileMode == FileMode.FILE && FileDataManager.getInstance().mFilePath.size() != 0) {
                    mFile.clear();
                    for (String file : FileDataManager.getInstance().mFilePath) {
                        if (file.contains(currentDevicePath)) {
                            File f = new File(file);
                            if (f.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                mFile.add(f);
                            }
                        }
                    }
                } else if ((fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) && mImageFolder.size() != 0) {
                    mFile.clear();
                    for (String picture : FileDataManager.getInstance().mImageFilePath) {
                        if (picture.contains(currentDevicePath)) {
                            if (picture.toLowerCase().contains(s.toString().toLowerCase())) {
                                mFile.add(new File(picture));
                            }
                        }
                    }
                } else if (fileMode == FileMode.MEDIA && mMediaFiles.size() != 0) {
                    mFile.clear();
                    for (String media : FileDataManager.getInstance().mMediaFilesPath) {
                        if (media.contains(currentDevicePath)) {
                            if (media.toLowerCase().contains(s.toString().toLowerCase())) {
                                mFile.add(new File(media));
                            }
                        }
                    }
                }
                viewPagerAdapter.refreshData(mFile, fileMode);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    reLoadListData(currentPaths);
                }
            }
        });
        backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        resourceViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onPageSelected(int position) {
                hideSearchEdit();
                if (position == 0) {
                    fileMode = FileMode.FILE;
                    String filePath = resourceHistory.getCurrentFilePath();
                    if (!TextUtils.isEmpty(filePath) && filePath.indexOf(currentDevicePath) != -1) {
                        reLoadListData(filePath);
                    } else {
                        viewPagerAdapter.refreshPagerData(mFiles, fileMode);
                    }
                } else if (position == 1) {
                    String picturePath = resourceHistory.getCurrentPicturePath();
                    if (!TextUtils.isEmpty(picturePath) && picturePath.indexOf(currentDevicePath) != -1) {
                        fileMode = FileMode.PICTURE_FILE;
                    } else {
                        fileMode = FileMode.PICTURE;
                    }
                    reLoadListData(picturePath);
                } else if (position == 2) {
                    fileMode = FileMode.MEDIA;
                    String mediaPath = resourceHistory.getCurrentMediaPath();
                    reLoadListData(mediaPath);
                }
            }

            public void onPageScrollStateChanged(int state) {

            }
        });
        selectPathImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resource_type == 1) {
//                    LocalBroadcastManager.getInstance(ResourceLibraryActivity.this).sendBroadcast(
//                            new Intent(StatusConfig.RESOURCE_FILE_PATH_ACTION)
//                                    .putExtra(StatusConfig.RESOURCE_FOLDER_PATH_KEY, currentPaths));
                    FileRelay.setFilePath(currentPaths);
                    finish();
                } else if (resource_type == 9) {
                    Map<Integer, List<String>> picturePath = viewPagerAdapter.pictureAdapter.getPicturePath();
                    if (picturePath.size() == 5) {
//                        FileDataManager.getInstance().setPicturePaths(picturePath);
//                        LocalBroadcastManager.getInstance(ResourceLibraryActivity.this).sendBroadcast(
//                                new Intent(StatusConfig.RESOURCE_PICTURE_FOLDER_ACTION)
//                        );
                        finish();
                    } else {
                        Toast.makeText(ResourceLibraryActivity.this, "请选择5个图片文件夹！", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    /**
     * 获取当前路径下指定查询格式的文件
     *
     * @param path
     */
    public void reLoadListData(String path) {
        swipeRefreshLayout.setRefreshing(true);
        final List<File> mImages = new ArrayList<>();
        if (fileMode == FileMode.FILE || fileMode == FileMode.PICTURE_FILE) {
            mFiles.clear();
            FileUtils.getFiles(fileMode, resource_type, path, new FileUtils.GetModeCallBack() {
                @Override
                public void onFileNull(boolean isNull) {
                    isFileNull = isNull;
                }
            }, new FileUtils.OnFileFound() {
                @Override
                public void onFileFound(File file) {
                    if (fileMode == FileMode.FILE) {
                        mFiles.add(file);
                    } else if (fileMode == FileMode.PICTURE_FILE) {
                        mImages.add(file);
                    }
                }
            });
        } else if (fileMode == FileMode.PICTURE || fileMode == FileMode.MEDIA) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
        if (viewPagerAdapter != null) {
            if (fileMode == FileMode.FILE) {
                viewPagerAdapter.refreshPagerData(mFiles, fileMode);
            } else if (fileMode == FileMode.PICTURE_FILE) {
                viewPagerAdapter.refreshPagerData(mImages, fileMode);
            }
        }
        refreshCurrentPath(path);
        showToast(isFileNull);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 检索设备下所有文件
     */
    public void initFilePaths() {
        if (FileDataManager.getInstance().mFilePath.size() == 0) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (String file : mDevicePaths) {
                        listAllFiles(file);
                    }
                    if (index < 2000) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            });
        }
    }

    private int index = 0;

    /**
     * 当前设备的文件分类
     *
     * @param file
     */
    public void listAllFiles(String file) {
        File f = new File(file);
        if (f.exists()) {
            File[] fs = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    index++;
                    if (index == 2000) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        index = 0;
                    }
                    return isQualified(pathname);
                }
            });
            if (fs == null || fs.length < 1) {
                isFileNull = true;
                return;
            } else {
                isFileNull = false;
            }
            for (File f1 : fs) {
                if (f1.isDirectory()) {
                    listAllFiles(f1.getPath());
                    FileDataManager.getInstance().addPath(f1.getPath(), resource_type);
                } else {
                    FileDataManager.getInstance().addPath(f1.getPath(), resource_type);
                    FileDataManager.getInstance().addPicturePath(f1.getPath());
                    FileDataManager.getInstance().addMediaPath(f1.getPath());
                }
            }
        }
    }

    /**
     * 刷新当前路径显示
     *
     * @param filePath
     */
    public void refreshCurrentPath(String filePath) {
        currentPaths = filePath;
        pathText.setText(filePath);
        if (fileMode == FileMode.FILE) {
            resourceHistory.setCurrentFilePath(filePath);
        } else if (fileMode == FileMode.PICTURE_FILE) {
            resourceHistory.setCurrentPicturePath(filePath);
        } else if (fileMode == FileMode.PICTURE) {
            resourceHistory.setCurrentPicturePath("");
        } else if (fileMode == FileMode.MEDIA) {
            resourceHistory.setCurrentMediaPath("");
        }
    }

    /**
     * 当前界面是否有文件
     *
     * @param isNull
     */
    public void showToast(boolean isNull) {
        if (isNull) {
            isFileNull = true;
            toastText.setVisibility(View.VISIBLE);
            toastText.setText(getStringName(R.string.no_file));
        } else {
            isFileNull = false;
            toastText.setVisibility(View.GONE);
        }
    }

    /**
     * 将当前设备下图片和音视频分类
     *
     * @param devicesPath
     */
    public void checkDevicePicturePath(String devicesPath) {
        mImageFolder.clear();
        mMediaFiles.clear();
        for (String p : FileDataManager.getInstance().mImageFolderPath) {
            if (p.indexOf(devicesPath) != -1) {
                mImageFolder.add(new File(p));
            }
        }
        for (String p : FileDataManager.getInstance().mMediaFilesPath) {
            if (p.indexOf(devicesPath) != -1) {
                mMediaFiles.add(new File(p));
            }
        }
        if (viewPagerAdapter != null) {
            if (fileMode == FileMode.PICTURE) {
                viewPagerAdapter.refreshPagerData(mImageFolder, fileMode);
            } else if (fileMode == FileMode.MEDIA) {
                viewPagerAdapter.refreshPagerData(mMediaFiles, fileMode);
            }
        }
    }

    public String getStringName(int stringId) {
        return this.getResources().getString(stringId);
    }

    /**
     * 搜索显示隐藏动画
     */
    int currentValue = 0;

    public void editAnimation(final View view, final int start, final int end, final boolean isSearch) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(100, 1);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            IntEvaluator mEvaluator = new IntEvaluator();

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (Integer) animation.getAnimatedValue();

                float fraction = animation.getAnimatedFraction();
                view.getLayoutParams().width = mEvaluator.evaluate(fraction, start, end);
                view.requestLayout();
                if (isSearch) {
                    if (currentValue == 100) {
                        searchRadioBtn.setCompoundDrawables(null, null, null, null);
                        searchRadioBtn.setBackground(getResources().getDrawable(R.mipmap.ic_resource_close));
                        searchRadioBtn.setText("");
                        searchEditText.setFocusable(true);
                        searchEditText.setFocusableInTouchMode(true);
                        searchEditText.requestFocus();
                        imm.showSoftInput(searchEditText, 0);
                        viewDisplayHeight = ScreenUtil.getViewVisibleHeight(ResourceLibraryActivity.this);
                    }
                } else {
                    if (currentValue == 1) {
                        hideSearchEdit();
                        int currentDisplayHeight = ScreenUtil.getViewVisibleHeight(ResourceLibraryActivity.this);
                        if (viewDisplayHeight - currentDisplayHeight != 0) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });
        valueAnimator.setDuration(400).start();
    }

    /**
     * 隐藏搜索框
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void hideSearchEdit() {
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchRadioBtn.setText(getStringName(R.string.search));
            searchRadioBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.ic_resource_search), null);
            searchRadioBtn.setBackground(getResources().getDrawable(R.mipmap.ic_search_bg));
            searchEditText.setVisibility(View.GONE);
            pathLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据设备类型资源库显示不同分类的资源
     * resource_type = 0  全部显示
     * resource_type = 1 文件目录分类
     * resource_type = 2 图片单选分类
     * resource_type = 3 音视频分类
     * reource_type = 4,5,6,7, 8 pdf,excel,word,ppt,wps
     * resource_type=9 图片多选分类
     *
     * @param position
     */
    public void checkDeviceType(int position) {
        switch (resource_type) {
            case 0:
                resourceViewPager.setOnTouchPager(false);
                mHorizontalTabLayout.setCurrentTab(resource_type);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.VISIBLE);
                break;
            case 1:
                selectPathImageBtn.setVisibility(View.VISIBLE);
                resourceViewPager.setOnTouchPager(true);
                mHorizontalTabLayout.setCurrentTab(0);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.GONE);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                resourceViewPager.setOnTouchPager(true);
                mHorizontalTabLayout.setCurrentTab(0);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.GONE);
                break;
            case 2:
                resourceViewPager.setOnTouchPager(true);
                mHorizontalTabLayout.setCurrentTab(resource_type - 1);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.GONE);
                break;
            case 3:
                resourceViewPager.setOnTouchPager(true);
                mHorizontalTabLayout.setCurrentTab(resource_type - 1);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.VISIBLE);
                break;
            case 9:
                selectPathImageBtn.setVisibility(View.VISIBLE);
                resourceViewPager.setOnTouchPager(true);
                mHorizontalTabLayout.setCurrentTab(1);
                mHorizontalTabLayout.getTitleView(0).setVisibility(View.GONE);
                mHorizontalTabLayout.getTitleView(1).setVisibility(View.VISIBLE);
                mHorizontalTabLayout.getTitleView(2).setVisibility(View.GONE);
                break;
        }
    }

    private void registerReceivers() {
        IntentFilter mountFilter = new IntentFilter();
        mountFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mountFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mountFilter.addDataScheme(ContentResolver.SCHEME_FILE);
        registerReceiver(receiver, mountFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mDeviceNames.clear();
                            mDevicePaths.clear();
                            loadListView();
                            checkDeviceType(1);
                            if (!mExecutorService.isShutdown()) {
                                mExecutorService.shutdown();
                            }
                            if (mExecutorService.isTerminated()) {
                                FileDataManager.getInstance().clean();
                                initFilePaths();
                                reLoadListData(currentPaths);
                            }
                        }
                    }, 500);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (fileMode == FileMode.PICTURE_FILE) {
            fileMode = FileMode.PICTURE;
            reLoadListData(currentDevicePath);
        } else if (fileMode == FileMode.FILE) {
            int lastIndex = currentPaths.lastIndexOf("/");
            String path = currentPaths.substring(0, lastIndex);
            reLoadListData(path);
        } else if (fileMode == FileMode.PICTURE) {
            if (currentPaths.equals(currentDevicePath)) {
                //如果是根路径下的图片 显示路径和设备路径相同
                reLoadListData(currentPaths);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceNames.clear();
        mDevicePaths.clear();
        mFiles.clear();
        mImageFolder.clear();
        mMediaFiles.clear();
        FileDataManager.getInstance().clean();
        unregisterReceiver(receiver);
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
            mExecutorService.shutdownNow();
        }
    }
}
