package com.hzp.hiapp.demo.item;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.hzp.hi.ui.item.HiDataItem;
import com.hzp.hiapp.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopTabDataItem extends HiDataItem<ItemData, RecyclerView.ViewHolder> {

    public TopTabDataItem(ItemData itemData) {
        super(itemData);
    }

    @Override
    public void onBindData(@NotNull RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = holder.itemView.findViewById(R.id.item_image);
        imageView.setImageResource(R.drawable.item_top_tab);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.layout_list_item_top_tab;
    }

}
