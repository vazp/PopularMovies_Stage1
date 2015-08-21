package com.vazp.popularmovies.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vazp.popularmovies.R;
import com.vazp.popularmovies.fragments.DetailsFragment;

/**
 * Created by Miguel on 8/15/2015.
 */
public class DetailsActivity extends AppCompatActivity
{
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int idMovie = getIntent().getIntExtra(getResources().getString(R.string.id_movie_key), 0);
        String title = getIntent().getStringExtra(getResources().getString(R.string.title_key));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.details_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);

        Bundle arguments = new Bundle();
        arguments.putInt(getResources().getString(R.string.id_movie_key), idMovie);

        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_content_frame, detailsFragment)
                .commit();
    }
}
