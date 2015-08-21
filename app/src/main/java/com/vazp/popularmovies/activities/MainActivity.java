package com.vazp.popularmovies.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.vazp.popularmovies.adapters.MoviesAdapter;
import com.vazp.popularmovies.fragments.MainDiscoveryFragment;
import com.vazp.popularmovies.R;

public class MainActivity extends AppCompatActivity implements MainDiscoveryFragment.Callback
{
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	@Override
	public void onItemSelected(MoviesAdapter.MoviesAdapterViewHolder viewHolder)
	{
		Intent detailsIntent = new Intent(this, DetailsActivity.class);
		detailsIntent.putExtra(getResources().getString(R.string.id_movie_key), viewHolder.getIdMovie());
		detailsIntent.putExtra(getResources().getString(R.string.title_key), viewHolder.getTitle());
		startActivity(detailsIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
