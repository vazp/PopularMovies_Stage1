package com.vazp.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vazp.popularmovies.NetworkUtility;
import com.vazp.popularmovies.R;
import com.vazp.popularmovies.data.MoviesContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Miguel on 15/07/2015.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private TextView mReleaseDateTextView, mRatingTextView, mOverviewTextView;
    private ImageView mPoster;

    private int mIdMovie;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTEAVERAGE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_POSTER
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(getResources().getString(R.string.id_movie_key)))
        {
            mIdMovie = arguments.getInt(getResources().getString(R.string.id_movie_key));
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.details_release_date_textview);
        mRatingTextView = (TextView) rootView.findViewById(R.id.details_rating_textview);
        mOverviewTextView = (TextView) rootView.findViewById(R.id.details_overview_textview);
        mPoster = (ImageView) rootView.findViewById(R.id.details_poster_imageview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(mIdMovie);

        return new CursorLoader(
                getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if (cursor.moveToFirst())
        {
            String releaseDate = cursor.getString(
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
            double rating = cursor.getDouble(
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTEAVERAGE));
            String overview = cursor.getString(
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
            String poster = cursor.getString(
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try
            {
                date = simpleDateFormat.parse(releaseDate);
                simpleDateFormat = new SimpleDateFormat("MMMM W, yyyy");
                mReleaseDateTextView.setText(simpleDateFormat.format(date));
            }
            catch (ParseException e)
            {
                Log.e(LOG_TAG, e.getMessage());
            }

            mRatingTextView.setText(String.format("%.1f / 10", rating));
            mOverviewTextView.setText(overview);

            if (poster.equals("null") || !NetworkUtility.checkConnection(getActivity()))
            {
                mPoster.setImageResource(R.drawable.image_not_available);
            }
            else
            {
                Picasso.with(getActivity()).load(
                        getActivity().getResources().getString(R.string.base_movie_image_url) +
                        getActivity().getResources().getString(R.string.image_size) +
                        poster).into(mPoster);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        //Do nothing
    }
}
