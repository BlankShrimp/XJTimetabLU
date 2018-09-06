package com.blankshrimp.xjtimetablu.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blankshrimp.xjtimetablu.OthersTableViewer;
import com.blankshrimp.xjtimetablu.R;
import com.blankshrimp.xjtimetablu.util.NewListDAO;
import com.blankshrimp.xjtimetablu.util.People;
import com.blankshrimp.xjtimetablu.util.PeopleAdapter;

import java.util.List;

/**
 * Created by Flabbergast13 on 18/2/26.
 */

public class ListFragment extends Fragment {

    private PeopleAdapter mPeopleAdapter;
    private ListView mListView;
    private View xxView;
    private List<People> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        xxView = view;

        //read data from list.db and assemble data into people object
        list = new NewListDAO(view.getContext()).queryListOfPeople();

        //set an Adapter and apply it to listview
        mPeopleAdapter = new PeopleAdapter(view.getContext(), R.layout.list_person, list);
        mListView = (ListView) view.findViewById(R.id.people_listlist);
        mListView.setAdapter(mPeopleAdapter);
        mListView.setEmptyView(view.findViewById(R.id.listGoneImg));
        mListView.setEmptyView(view.findViewById(R.id.listGoneText1));
        mListView.setEmptyView(view.findViewById(R.id.listGoneText2));
        registerForContextMenu(mListView);

        //start activity when click on list item
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                People people = list.get(i);
                Bundle bundle = new Bundle();
                bundle.putBoolean("table_selected", true);
                bundle.putString("name", people.getName());
                Intent intent = new Intent(view.getContext(), OthersTableViewer.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * refresh the fragment after adding timetables
     */
    @Override
    public void onResume() {
        super.onResume();
        flash();
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, 0, Menu.NONE,view.getContext().getString(R.string.setAsFavour));
        menu.add(0, 1, Menu.NONE,view.getContext().getString(R.string.setAsPrimary));
        menu.add(0, 2, Menu.NONE,view.getContext().getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 0) {
            //favourite
            int target = menuInfo.position;
            SharedPreferences sp = this.getActivity().getSharedPreferences("com.blankshrimp.xjtimetablu_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("favourite", new NewListDAO(xxView.getContext()).returnFingerprint(target));
            editor.apply();

        } else if (item.getItemId() == 1) {
            //primary
            int target = menuInfo.position;
            SharedPreferences sp = this.getActivity().getSharedPreferences("com.blankshrimp.xjtimetablu_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("primary", new NewListDAO(xxView.getContext()).returnFingerprint(target));
            editor.apply();

        } else if (item.getItemId() == 2) {
            //delete
            int target = menuInfo.position;
            NewListDAO newListDAO = new NewListDAO(xxView.getContext());
            newListDAO.deleteRecord(newListDAO.returnFingerprint(target));
        }
        return super.onContextItemSelected(item);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void flash () {
        //just reloading
        NewListDAO newListDAO = new NewListDAO(xxView.getContext());
        list = newListDAO.queryListOfPeople();

        mPeopleAdapter = new PeopleAdapter(xxView.getContext(), R.layout.list_person, list);
        mListView = (ListView) xxView.findViewById(R.id.people_listlist);
        mListView.setAdapter(mPeopleAdapter);
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneImg));
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneText1));
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneText2));
    }
}
