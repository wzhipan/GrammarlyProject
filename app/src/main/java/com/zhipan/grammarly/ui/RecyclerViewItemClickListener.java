package com.zhipan.grammarly.ui;

import android.view.View;

public interface RecyclerViewItemClickListener<T> {
    void onItemClick(View view, T value);
}
