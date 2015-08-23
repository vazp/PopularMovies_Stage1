package com.vazp.popularmovies.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vazp.popularmovies.NetworkUtility;
import com.vazp.popularmovies.adapters.MoviesAdapter;
import com.vazp.popularmovies.data.MoviesContract;
import com.vazp.popularmovies.R;
import com.vazp.popularmovies.tasks.GetMoviesTask;

/**
 * Created by Miguel on 15/07/2015.
 */
public class MainDiscoveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = MainDiscoveryFragment.class.getSimpleName();
    private static final String POSITION_KEY = "position";
    private static final int MOVIES_LOADER = 0;

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private int mPosition = RecyclerView.NO_POSITION;

    public interface Callback
    {
        void onItemSelected(MoviesAdapter.MoviesAdapterViewHolder viewHolder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main_discovery, container, false);

        mCoordinatorLayout =
                (CoordinatorLayout) rootView.findViewById(R.id.main_discovery_coordinatorlayout);
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) rootView.findViewById(R.id.main_discovery_swiperefresh);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_discovery_recyclerview);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                fetchMovies();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            layoutManager = new GridLayoutManager(getActivity(),
                                                  2,
                                                  LinearLayoutManager.VERTICAL,
                                                  false);
        }
        else
        {
            layoutManager = new GridLayoutManager(getActivity(),
                                                  2,
                                                  LinearLayoutManager.HORIZONTAL,
                                                  false);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(getActivity(), new MoviesAdapter.MoviesAdapterOnClickHandler()
        {
            @Override
            public void onClick(MoviesAdapter.MoviesAdapterViewHolder viewHolder)
            {
                ((Callback) getActivity()).onItemSelected(viewHolder);
            }
        });
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey(POSITION_KEY))
            {
                mPosition = savedInstanceState.getInt(POSITION_KEY);
                Log.v(LOG_TAG, "Position " + mPosition);
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if (savedInstanceState == null)
        {
            fetchMovies();
        }

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri moviesUri = MoviesContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                moviesUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mMoviesAdapter.swapCursor(null);
    }

    public void fetchMovies()
    {
        if (NetworkUtility.checkConnection(getActivity()))
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            GetMoviesTask getMoviesTask = new GetMoviesTask(getActivity());
            getMoviesTask.execute(
                    preferences.getString(
                            getString(R.string.settings_sort_key),
                            getString(R.string.settings_sort_popularity)));
        }
        else
        {
            Snackbar.make(mCoordinatorLayout,
                          getString(R.string.no_connection),
                          Snackbar.LENGTH_LONG)
                    .setAction(
                            getString(R.string.retry),
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    fetchMovies();
                                }
                            }).show();
        }
    }

}
