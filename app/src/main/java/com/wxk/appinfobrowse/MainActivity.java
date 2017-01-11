package com.wxk.appinfobrowse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
  private int currentCategory = -999;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ChoiceFragment choiceFragment = new ChoiceFragment();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.add(R.id.content_container, choiceFragment, "choice").commit();
    choiceFragment.setListener(new ChoiceFragment.OnCategoryChangedListener() {
      @Override
      public void categoryChanged(int appCategory) {

        switch (appCategory) {
          case ChoiceFragment.FILTER_ALL_APP:
            currentCategory = ChoiceFragment.FILTER_ALL_APP;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container,
                AppCategoryFragment.newInstance(ChoiceFragment.FILTER_ALL_APP)).addToBackStack(null).commit();
            break;
          case ChoiceFragment.FILTER_SYSTEM_APP:
            currentCategory = ChoiceFragment.FILTER_SYSTEM_APP;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container,
                AppCategoryFragment.newInstance(ChoiceFragment.FILTER_SYSTEM_APP)).addToBackStack(null).commit();
            break;
          case ChoiceFragment.FILTER_THIRD_APP:
            currentCategory = ChoiceFragment.FILTER_THIRD_APP;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container,
                AppCategoryFragment.newInstance(ChoiceFragment.FILTER_THIRD_APP)).addToBackStack(null).commit();
            break;
          case ChoiceFragment.FILTER_SDCARD_APP:
            currentCategory = ChoiceFragment.FILTER_SDCARD_APP;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container,
                AppCategoryFragment.newInstance(ChoiceFragment.FILTER_SDCARD_APP)).addToBackStack(null).commit();
            break;
          default:
            break;
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  //可以简化代码，和上面的switch case合并，懒得弄了
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_show) {
      String filePath = "";
      // 确定文件路径
      switch (currentCategory) {
        case ChoiceFragment.FILTER_ALL_APP:
          filePath = "/storage/emulated/0/Download/all_app.txt";
          break;
        case ChoiceFragment.FILTER_SYSTEM_APP:
          filePath = "/storage/emulated/0/Download/system_app.txt";
          break;
        case ChoiceFragment.FILTER_THIRD_APP:
          filePath = "/storage/emulated/0/Download/third_party_app.txt";
          break;
        case ChoiceFragment.FILTER_SDCARD_APP:
          filePath = "/storage/emulated/0/Download/sd_card_app.txt";
          break;
        default:
          break;
      }
      if (filePath.equals("")) {
        Toast.makeText(this, "还没有生成app信息文件!", Toast.LENGTH_SHORT).show();
      } else {
        //打开文件,intent跳转，会跳转到QQ如果安装了
        openFile(filePath);
      }
    }
    return super.onOptionsItemSelected(item);
  }

  private void openFile(String filePath) {
    File file = new File(filePath);
    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //设置intent的Action属性
    intent.setAction(Intent.ACTION_VIEW);
    //获取文件file的MIME类型
    String type = "text/plain";
    //设置intent的data和Type属性。
    intent.setDataAndType(Uri.fromFile(file), type);
    //跳转
    startActivity(intent);
  }
}
