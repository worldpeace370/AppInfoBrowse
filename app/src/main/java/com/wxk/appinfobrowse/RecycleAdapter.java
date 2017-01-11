package com.wxk.appinfobrowse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bjwuxiangkun on 2016/12/15.
 * Contact by bjwuxiangkun@corp.netease.com
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> implements View.OnClickListener{
  private Context context;
  private List<AppInfoModel> list;
  private OnChildClickListener listener;
  private RecyclerView recyclerView;
  public RecycleAdapter(Context context, List<AppInfoModel> list){
    this.context = context;
    this.list = list;
  }

  /**
   * 创建ViewHOlder对象
   * @param parent
   * @param viewType
   * @return
   */
  @Override
  public RecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_recycleview, parent, false);
    view.setOnClickListener(this);
    return new MyViewHolder(view);
  }
  /**
   * 将数据放入到ViewHolder里面的控件中
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.setData(position);
  }

  /**
   * Called by RecyclerView when it starts observing this Adapter
   * @param recyclerView
   */
  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView;
  }


  @Override
  public int getItemCount() {
    return list.size();
  }

  /**
   * 实现了View的OnClickListener的方法
   * @param view
   */
  @Override
  public void onClick(View view) {
    if (listener != null){
      int position = recyclerView.getChildAdapterPosition(view);
      listener.onChildClick(view, position, list.get(position));
    }
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView ivAppIcon;
    TextView tvAppName;
    TextView tvPackageName;
    MyViewHolder(View itemRootView) {
      super(itemRootView);
      ivAppIcon = ((ImageView) itemRootView.findViewById(R.id.iv_app_icon));
      tvAppName = (TextView)itemRootView.findViewById(R.id.tv_app_name);
      tvPackageName = ((TextView) itemRootView.findViewById(R.id.tv_package_name));
    }

    void setData(int position){
      ivAppIcon.setImageDrawable(list.get(position).getAppIcon());
      tvAppName.setText(list.get(position).getAppName());
      tvPackageName.setText(list.get(position).getPackageName());
    }
  }

  interface OnChildClickListener{
    void onChildClick(View view, int position, AppInfoModel data);
  }

  /**
   * 内部定义的接口的set方法，用于接口回调
   * @param listener
   */
  public void setOnChildClickListener(OnChildClickListener listener) {
    this.listener = listener;
  }

  /**
   * 将list中position位置的数据移除后，更新Ui
   * @param position
   */
  public void remove(int position){
    list.remove(position);
    notifyItemRemoved(position);
  }

  /**
   * 在position位置插入数据data，然后更新UI
   * @param position
   * @param data
   */
  public void add(int position, AppInfoModel data){
    list.add(position, data);
    notifyItemInserted(position);
  }
}
