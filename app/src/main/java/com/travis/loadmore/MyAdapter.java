package com.travis.loadmore;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 亲爱的,谢谢你!
 * 终于录制了第一个视频,准备了很长时间,但是讲起来还是有点结巴<br/>
 * 不过总算是一个良好的开端,我会继续加油的!<br/>
 */
public class MyAdapter<T> extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_EMPTY = 3;
    private static final int VIEW_TYPE_NO_MORE = 4;

    public interface DynamicLoadListener {
        void onLoadMore();
    }

    private Context mContext;
    private ArrayList<T> datas;
    private DynamicLoadListener mDynamicLoadListener;
    private LayoutInflater mInflater;

    private boolean mIsLoadMoreEnabled = true;
    private boolean mIsLoadingMore = false;

    private Handler mHandler = new Handler();

    public MyAdapter(Context context, ArrayList<T> datas, DynamicLoadListener listener) {
        this.mContext = context;
        this.datas = datas;
        this.mDynamicLoadListener = listener;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 根据viewType加载不同的ViewHolder
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                View loading = mInflater.inflate(R.layout.layout_loading, parent, false);
                return new ViewHolder(loading);
            case VIEW_TYPE_EMPTY:
                View empty = mInflater.inflate(R.layout.layout_empty, parent, false);
                return new ViewHolder(empty);
            case VIEW_TYPE_NO_MORE:
                View noMore = mInflater.inflate(R.layout.layout_no_more, parent, false);
                return new ViewHolder(noMore);
            case VIEW_TYPE_ITEM:
                View item = mInflater.inflate(R.layout.item, parent, false);
                return new ItemViewHolder(item);
        }
        return null;
    }

    /**
     * 根据不同的viewType或者ViewHolder执行绑定数据的操作
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (VIEW_TYPE_ITEM == getItemViewType(position)) {
            ItemViewHolder vh = ((ItemViewHolder) holder);
            vh.bind(position);
        }
    }

    /**
     * 我们考虑实际情况有以下种:<br/>
     * 1.刚进入页面的时候,没有数据项   ItemCount:1(loading)<br/>
     * 2.加载数据完成,但是数据列表是空的,即没有数据  ItemCount:1(empty)<br/>
     * 3.有数据项,且需要load more  ItemCount: datas.size()+1(loading)<br/>
     * 4.有数据项,且全部加载完成    ItemCount: datas.size()+1(no more)<br/>
     * @return
     */
    @Override
    public int getItemCount() {
        return datas == null ? 1 : datas.size() + 1;
    }

    /**
     * ★★★这是上拉加载更多的核心代码★★★<br/>
     * 1.大的方面来说一共有两种情况:最后一条和数据项<br/>
     * 2.最后一条又有以下三种情况:<br/>
     * &nbsp;&nbsp;1)加载更多是开启的,最后一条一定是loading<br/>
     * &nbsp;&nbsp;2)加载更多是关闭的,且没有数据项,最后一条一定是empty<br/>
     * &nbsp;&nbsp;3)加载更多是关闭的,且有数据项,最后一条一定是no more<br/>
     * 3.除了最后一条,其他的都是数据项<br/>
     * 4.加载更多是开启的,并且当前没有处于加载更多的状态,触发加载更多的操作★<br/>
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {//最后一条的情况
            if (mIsLoadMoreEnabled) {//加载更多是开启的,最后一条一定是loading
                if (!mIsLoadingMore) {//加载更多是开启的,并且当前没有处于加载更多的状态,触发加载更多的操作
                    loadMore();
                }
                return VIEW_TYPE_LOADING;
            } else if (getItemCount() == 1) {//加载更多是关闭的,且没有数据项,最后一条一定是empty
                return VIEW_TYPE_EMPTY;
            } else {//加载更多是关闭的,且有数据项,最后一条一定是no more
                return VIEW_TYPE_NO_MORE;
            }
        } else {//除了最后一条,其他的都是数据项
            return VIEW_TYPE_ITEM;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView content;

        public ItemViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
        }

        public void bind(int position) {
            content.setText("Content:" + position);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 需要加载更多时被触发<br/>
     * 标记当前处于loading状态,避免被重复调用
     */
    private void loadMore() {
        mIsLoadingMore = true;
        mDynamicLoadListener.onLoadMore();
    }

    /**
     * 完成一次loadmore的回调方法<br/>
     * 尽量放在UI线程中,否则有可能出现冲突
     * @param enabled
     */
    public void updateState(final boolean enabled) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsLoadingMore = false;
                mIsLoadMoreEnabled = enabled;
                notifyDataSetChanged();
            }
        });
    }
}