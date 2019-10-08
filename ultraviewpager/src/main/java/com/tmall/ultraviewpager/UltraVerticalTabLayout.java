package com.tmall.ultraviewpager;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.tmall.ultraviewpager.verticaltablayout.VerticalTabLayout;
import com.tmall.ultraviewpager.verticaltablayout.adapter.TabAdapter;
import com.tmall.ultraviewpager.verticaltablayout.widget.ITabView;
import com.tmall.ultraviewpager.verticaltablayout.widget.QTabView;

/**
 * 垂直方向的标签导航栏
 */
public class UltraVerticalTabLayout extends VerticalTabLayout {

    public UltraVerticalTabLayout(Context context) {
        super(context);
    }

    public UltraVerticalTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UltraVerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        notifyDataSetChanged();
    }

    /**
     * 页面数据发送变换，调用该方法重新更新标签导航栏
     */
    public void notifyDataSetChanged() {
        final int tabCount = mViewPager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            QTabView tabView = new QTabView(getContext());
            this.addTab(tabView);
        }
        this.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return tabCount;
            }

            @Override
            public ITabView.TabBadge getBadge(int position) {
                return null;
            }

            @Override
            public ITabView.TabIcon getIcon(int position) {
                return null;
            }

            @Override
            public ITabView.TabTitle getTitle(int position) {
                return new ITabView.TabTitle.Builder()
                        .setContent(mViewPager.getAdapter().getPageTitle(position).toString())
                        .setTextColor(Color.BLUE, Color.WHITE)
                        .build();
            }

            @Override
            public int getBackground(int position) {
                return 0;
            }
        });
    }
}
