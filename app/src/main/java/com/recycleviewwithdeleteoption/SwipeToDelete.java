package com.recycleviewwithdeleteoption;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by aalishan on 5/5/16.
 */
public class SwipeToDelete extends ItemTouchHelper.SimpleCallback {
    ContactAdapter contactAdapter;

    public SwipeToDelete(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeToDelete(ContactAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        this.contactAdapter = adapter;

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        contactAdapter.dismissItem(viewHolder.getAdapterPosition());

    }
}
