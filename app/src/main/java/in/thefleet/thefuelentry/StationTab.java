package in.thefleet.thefuelentry;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class StationTab extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView recyclerView;
    StationAdapter adapter;
    private int LOADER_ID1 = 1;
    private int LOADER_ID2 = 2;
    public EditText search;
    private List<String> list = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.station_tab, container, false);
        search = (EditText) rootView.findViewById( R.id.search);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.station_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StationAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Bundle search_bundle = new Bundle();
                search_bundle.putCharSequence("search_string",s);
                getLoaderManager().restartLoader(LOADER_ID2, search_bundle, StationTab.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new CursorLoader(getContext(), StationDataSource.CONTENT_URI2, null, null, null, null);
        } else  {
            String fFilter = StationDbOpenHelper.STATION_NAME + " like " + "'" + "%" + args.get("search_string") + "%" + "'";
            return new CursorLoader(getContext(), StationDataSource.CONTENT_URI2, null, fFilter, null, null);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}