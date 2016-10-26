package com.recycleviewwithdeleteoption;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, SearchView.OnQueryTextListener {

    private Toolbar mToolbar;
    private TextView mTvCounter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ContactAdapter mContactAdapter;
    protected boolean is_in_action_mode = false;
    private ArrayList<Contact> selection_list_itmes;
    private List<Contact> mContactModels;
    private ArrayList<Contact> mContactArrayList;
    private int counter = 0;
    private int[] images = {R.drawable.image, R.drawable.image, R.drawable.image,
            R.drawable.image, R.drawable.image, R.drawable.image, R.drawable.image,
            R.drawable.image, R.drawable.image,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        String[] name;
        String[] email;
        selection_list_itmes = new ArrayList<>();
        mContactModels = new ArrayList<>();
        mContactArrayList = new ArrayList<>();
        name = getResources().getStringArray(R.array.names);
        email = getResources().getStringArray(R.array.email);
        int i = 0;
        for (String Name : name) {
            Contact contact = new Contact(images[i], Name, email[i]);
            mContactModels.add(contact);
            mContactArrayList.add(contact);
            i++;
        }
        mContactAdapter = new ContactAdapter( MainActivity.this,mContactArrayList);
        recyclerView.setAdapter(mContactAdapter);
        ItemTouchHelper.Callback callback = new SwipeToDelete(mContactAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTvCounter = (TextView) findViewById(R.id.tv_counter);
        mTvCounter.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.item_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onLongClick(View v) {
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.menu_action_mode);
        mTvCounter.setVisibility(View.VISIBLE);
        is_in_action_mode = true;
        mContactAdapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public void prepareSelection(View view, int position) {
        if (((CheckBox) view).isChecked()) {
            selection_list_itmes.add(mContactArrayList.get(position));
            counter = counter + 1;
            updateCounter(counter);
        } else {
            selection_list_itmes.remove(mContactArrayList.get(position));
            counter = counter - 1;
            updateCounter(counter);
        }


    }

    public void updateCounter(int counter) {
        if (counter == 0) {
            mTvCounter.setText("0 item selected");
        } else if (counter == 1) {
            mTvCounter.setText(counter + " item selected");
        } else {
            mTvCounter.setText(counter + " items selected");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            if (counter == 0) {
                Toast.makeText(this, "Select at least one item", Toast.LENGTH_SHORT).show();

            } else {


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if (counter == 1) {
                    builder.setMessage("Do you want to delete selected item ?");
                } else {
                    builder.setMessage("Do you want to delete selected items ?");
                }


                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mContactAdapter.updateAdapter(selection_list_itmes);
                        clearActionMode();

                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearActionMode();
                                mContactAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Alert Dialog");
                alert.show();
            }


        } else if (item.getItemId() == android.R.id.home)

        {
            clearActionMode();
            mContactAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.item_search) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(this);
        }

        return true;
    }

    public void clearActionMode() {
        is_in_action_mode = false;
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.menu_activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mTvCounter.setVisibility(View.GONE);
        mTvCounter.setText("0 item selected");
        counter = 0;
        selection_list_itmes.clear();
    }

    @Override
    public void onBackPressed() {
        if (is_in_action_mode) {
            clearActionMode();
            mContactAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Contact> filteredModelList = filter(mContactModels, query);
        mContactAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Contact> filter(List<Contact> models, String query) {
        query = query.toLowerCase();

        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}

