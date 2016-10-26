package com.recycleviewwithdeleteoption;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;

/**
 * Created by aalishan on 2/5/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> adapterList;
    private MainActivity mainActivity;
    private Context ctx;
    private List<Contact> mModels;

    public ContactAdapter(Context ctx, ArrayList<Contact> adapterList) {
        this.adapterList = adapterList;
        this.mModels = adapterList;
        this.ctx = ctx;
        mainActivity = (MainActivity) ctx;

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view, mainActivity);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        holder.img.setImageResource(adapterList.get(position).getImg_id());
        holder.tvName.setText(adapterList.get(position).getName());
        holder.tvEmail.setText(adapterList.get(position).getEmail());
        if (!mainActivity.is_in_action_mode) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public void dismissItem(int adapterPosition) {
        adapterList.remove(adapterPosition);
        this.notifyItemRemoved(adapterPosition);


    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img;
        private TextView tvName;
        TextView tvEmail;
        CheckBox checkBox;
        MainActivity mainActivity;
        CardView cardView;

        public ContactViewHolder(View itemView, MainActivity mainActivity) {
            super(itemView);
            this.mainActivity = mainActivity;
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnLongClickListener(mainActivity);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mainActivity.prepareSelection(v, getAdapterPosition());

        }
    }

    public void updateAdapter(ArrayList<Contact> list) {
        for (Contact cont : list) {
            adapterList.remove(cont);

        }
        notifyDataSetChanged();
    }

    public void animateTo(List<Contact> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Contact> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final Contact model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Contact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Contact model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Contact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Contact model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Contact removeItem(int position) {
        final Contact model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Contact model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Contact model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
