package com.wxk.appinfobrowse;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wxk.appinfobrowse.ChoiceFragment.FILTER_ALL_APP;
import static com.wxk.appinfobrowse.ChoiceFragment.FILTER_SDCARD_APP;
import static com.wxk.appinfobrowse.ChoiceFragment.FILTER_SYSTEM_APP;
import static com.wxk.appinfobrowse.ChoiceFragment.FILTER_THIRD_APP;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppCategoryFragment extends Fragment {
  public int CURRENT_FILTER = -999;
  private PackageManager mPackageManager;
  private List<AppInfoModel> mModelList;
  private List<AppInfo2Save> mSaveList = new ArrayList<>();
  private RecycleAdapter adapter;
  private RecyclerView mRecycleView;
  private FrameLayout frameLayout;
  private MyHandler handler;
  private TextView tvNotice;

  private static class MyHandler extends Handler{
      WeakReference<AppCategoryFragment> weakReference;
      MyHandler(AppCategoryFragment fragment){
          weakReference = new WeakReference<>(fragment);
      }
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          AppCategoryFragment fragment = weakReference.get();
          if (fragment != null){
              switch (msg.what){
                case 0x01:
                  fragment.frameLayout.setVisibility(View.GONE);
                  fragment.adapter = new RecycleAdapter(fragment.getActivity(), fragment.mModelList);
                  fragment.mRecycleView.setAdapter(fragment.adapter);
                  break;
                case 0x02:
                  fragment.frameLayout.setVisibility(View.VISIBLE);
                  fragment.tvNotice.setText("不存在当前种类app!");
                  break;
                case 0x03:
                  Toast.makeText(fragment.getActivity(), "文件写入完成!", Toast.LENGTH_SHORT).show();
                default:
                  break;
              }
          }
      }
  }

  /**
   * 构造方法也能传值，但是不建议这么干，最好通过fragment.setArguments(bundle)
   */
  public AppCategoryFragment() {

  }

  public static AppCategoryFragment newInstance(int current) {
    AppCategoryFragment fragment = new AppCategoryFragment();
    Bundle bundle = new Bundle();
    bundle.putInt("app_category", current);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      CURRENT_FILTER = getArguments().getInt("app_category");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_app_category, container, false);
    mRecycleView = (RecyclerView) rootView.findViewById(R.id.recycleView);
    mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    frameLayout = ((FrameLayout) rootView.findViewById(R.id.layout_loading));
    frameLayout.setVisibility(View.VISIBLE);
    tvNotice = ((TextView) rootView.findViewById(R.id.tv_notice));
    handler = new MyHandler(this);
    startQueryAppInfo();
    return rootView;
  }


  private void startQueryAppInfo(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        mModelList = queryFilterAppInfo(CURRENT_FILTER);
        if (mModelList != null && mModelList.size() == 0) {
          handler.sendEmptyMessage(0x02);
        } else {
          Message msg = handler.obtainMessage();
          msg.what = 0x01;
          handler.sendMessage(msg);
        }
      }
    }).start();
  }

  /**
   * 根据查询条件，AppInfo
   * @param filter
   * @return
   */
  private List<AppInfoModel> queryFilterAppInfo(int filter) {
    mPackageManager = getActivity().getPackageManager();
    // 查询所有已经安装的应用程序
    List<ApplicationInfo> listApplications = mPackageManager
        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
    Collections.sort(listApplications,
        new ApplicationInfo.DisplayNameComparator(mPackageManager));// 排序
    List<AppInfoModel> appShowInfoList = new ArrayList<>(); // 保存过滤查到的AppInfo
    // 根据条件来过滤
    switch (filter) {
      case FILTER_ALL_APP: // 所有应用程序
        appShowInfoList.clear();
        mSaveList.clear();
        for (ApplicationInfo app : listApplications) {
          appShowInfoList.add(getAppShowInfo(app));
          mSaveList.add(getAppWriteInfo(app));
        }
        saveAppInfoInJson(mSaveList, "all_app.txt");
        return appShowInfoList;
      case FILTER_SYSTEM_APP: // 系统程序
        appShowInfoList.clear();
        mSaveList.clear();
        for (ApplicationInfo app : listApplications) {
          if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            appShowInfoList.add(getAppShowInfo(app));
            mSaveList.add(getAppWriteInfo(app));
          }
        }
        saveAppInfoInJson(mSaveList, "system_app.txt");
        return appShowInfoList;
      case FILTER_THIRD_APP: // 第三方应用程序
        appShowInfoList.clear();
        mSaveList.clear();
        for (ApplicationInfo app : listApplications) {
          //非系统程序
          if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
            appShowInfoList.add(getAppShowInfo(app));
            mSaveList.add(getAppWriteInfo(app));
          }
          //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
          else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            appShowInfoList.add(getAppShowInfo(app));
            mSaveList.add(getAppWriteInfo(app));
          }
        }
        saveAppInfoInJson(mSaveList, "third_party_app.txt");
        break;
      case FILTER_SDCARD_APP: // 安装在SDCard的应用程序
        appShowInfoList.clear();
        mSaveList.clear();
        for (ApplicationInfo app : listApplications) {
          if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
            appShowInfoList.add(getAppShowInfo(app));
            mSaveList.add(getAppWriteInfo(app));
          }
        }
        saveAppInfoInJson(mSaveList, "sd_card_app.txt");
        return appShowInfoList;
      default:
        return null;
    }
    return appShowInfoList;
  }

  /**
   * 构造一个AppInfo对象,并赋值
   * @param app
   * @return
   */
  private AppInfoModel getAppShowInfo(ApplicationInfo app) {
    AppInfoModel appInfo = new AppInfoModel();
    appInfo.setAppName((String) app.loadLabel(mPackageManager));
    appInfo.setPackageName(app.packageName);
    appInfo.setAppIcon(app.loadIcon(mPackageManager));
    return appInfo;
  }

  /**
   * 构造一个AppInfo对象,只保留能写入文件的字段信息
   * @param app
   * @return
   */
  private AppInfo2Save getAppWriteInfo(ApplicationInfo app) {
    AppInfo2Save appInfo = new AppInfo2Save();
    appInfo.setAppName((String) app.loadLabel(mPackageManager));
    appInfo.setPackageName(app.packageName);
    return appInfo;
  }

  /**
   * 将可写的app信息，转为json格式化，写入本地文件
   * @param list
   */
  private void saveAppInfoInJson(final List<AppInfo2Save> list, final String fileName) {
    final ObjectMapper mapper = new ObjectMapper();
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          writeTxtToFile(mapper.writeValueAsString(list), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
              .getAbsolutePath() + File.separator, fileName);
          handler.sendEmptyMessage(0x03);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  // 将字符串写入到文本文件中
  private void writeTxtToFile(String strContent, String filePath, String fileName) {
    String strFilePath = filePath + fileName;
    try {
      File file = new File(strFilePath);
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(strContent.getBytes(), 0, strContent.getBytes().length);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      Log.e("TestFile", "Error on write File:" + e);
    }
  }
}
