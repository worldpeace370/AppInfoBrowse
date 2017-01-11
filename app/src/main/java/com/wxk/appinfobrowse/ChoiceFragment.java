package com.wxk.appinfobrowse;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoiceFragment extends Fragment implements View.OnClickListener{
  public static final int FILTER_ALL_APP = 0; // 所有应用程序
  public static final int FILTER_SYSTEM_APP = 1; // 系统程序
  public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
  public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序

  private Button allApp;
  private Button systemApp;
  private Button thirdPartyApp;
  private Button sdCardApp;

  private OnCategoryChangedListener listener;

  public ChoiceFragment() {
  }

  public void setListener(OnCategoryChangedListener listener) {
    this.listener = listener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_choice, container, false);
    initView(rootView);
    intListener();
    return rootView;
  }

  private void initView(View rootView) {
    allApp = ((Button) rootView.findViewById(R.id.all_app));
    systemApp = ((Button) rootView.findViewById(R.id.system_app));
    thirdPartyApp = ((Button) rootView.findViewById(R.id.third_party_app));
    sdCardApp = ((Button) rootView.findViewById(R.id.sd_card_app));
  }

  private void intListener() {
    allApp.setOnClickListener(this);
    systemApp.setOnClickListener(this);
    thirdPartyApp.setOnClickListener(this);
    sdCardApp.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.all_app:
        if (listener != null) {
          listener.categoryChanged(FILTER_ALL_APP);
        }
        break;
      case R.id.system_app:
        if (listener != null) {
          listener.categoryChanged(FILTER_SYSTEM_APP);
        }
        break;
      case R.id.third_party_app:
        if (listener != null) {
          listener.categoryChanged(FILTER_THIRD_APP);
        }
        break;
      case R.id.sd_card_app:
        if (listener != null) {
          listener.categoryChanged(FILTER_SDCARD_APP);
        }
        break;
      default:
        break;
    }
  }

  interface OnCategoryChangedListener{
    void categoryChanged(int appCategory);
  }
}
