package com.wxk.appinfobrowse;

import android.graphics.drawable.Drawable;

/**用来读取app名字、包名、图标的model
 * Created by bjwuxiangkun on 2016/12/15.
 * Contact by bjwuxiangkun@corp.netease.com
 */

public class AppInfoModel {
  private String appName;
  private String packageName;
  private Drawable appIcon ;  //应用程序图像

  public AppInfoModel() {
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public Drawable getAppIcon() {
    return appIcon;
  }

  public void setAppIcon(Drawable appIcon) {
    this.appIcon = appIcon;
  }

}
