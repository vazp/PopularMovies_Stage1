package com.vazp.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.vazp.popularmovies.R;
import com.vazp.popularmovies.data.MoviesContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Miguel on 24/07/2015.
 */
public class GetMoviesTask extends AsyncTask<String, Void, Void>
{
	private static final String LOG_TAG = GetMoviesTask.class.getSimpleName();
	private final Context mContext;

	public GetMoviesTask (Context context)
	{
		mContext = context;
	}

	@Override
	protected Void doInBackground(String... params)
	{
		if (params.length == 0)
		{
			return null;
		}

		String sortBy = params[0];
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		String moviesJsonString = null;

		try
		{
			final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
			final String SORT_PARAM = "sort_by";
			final String API_KEY_PARAM = "api_key";

			Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
					.appendQueryParameter(SORT_PARAM, sortBy)
					.appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
					.build();

			URL url = new URL(buildUri.toString());
//			Log.v(LOG_TAG, "Builr URI " + url.toString());

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();

			if (inputStream == null)
			{
				return null;
			}

			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while((line = reader.readLine()) != null)
			{
				buffer.append(line + "\n ");
			}

			if (buffer.length() == 0)
			{
				return null;
			}

			moviesJsonString = buffer.toString();
//			Log.v(LOG_TAG, "JSON String " + moviesJsonString);
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "Error " + e);
			return null;
		}
		finally
		{
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ex)
				{
					Log.e(LOG_TAG, "Error closing stream " + ex);
				}
			}
		}

		final String RESULTS = "results";
		final String RELEASE_DATE = "release_date";
		final String TITLE = "original_title";
		final String VOTE_AVERAGE = "vote_average";
		final String POPULARITY = "popularity";
		final String OVERVIEW = "overview";
		final String POSTER = "poster_path";

		try
		{
			JSONObject moviesJson = new JSONObject(moviesJsonString);
			JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

			Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(moviesArray.length());

			for (int i = 0; i < moviesArray.length(); i++)
			{
				String releaseDate;
				String title;
				double voteAverage;
				double popularity;
				String overview;
				String poster;

				JSONObject movieEntry = moviesArray.getJSONObject(i);

				releaseDate = movieEntry.getString(RELEASE_DATE);
				title = movieEntry.getString(TITLE);
				voteAverage = movieEntry.getDouble(VOTE_AVERAGE);
				popularity = movieEntry.getDouble(POPULARITY);
				overview = movieEntry.getString(OVERVIEW);
				poster = movieEntry.getString(POSTER);

//				Log.v(LOG_TAG, "Title > " + title + "\n" +
//				     "Release date > " + releaseDate + "\n" +
//				     "Rating > " + voteAverage + "\n" +
//				     "Popularity > " + popularity + "\n" +
//				     "Overview > " + overview + "\n" +
//				     "Poster > " + poster);

				ContentValues moviesValues = new ContentValues();

				moviesValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
				moviesValues.put(MovieEntry.COLUMN_TITLE, title);
				moviesValues.put(MovieEntry.COLUMN_VOTEAVERAGE, voteAverage);
				moviesValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
				moviesValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
				moviesValues.put(MovieEntry.COLUMN_POSTER, poster);

				contentValuesVector.add(moviesValues);
			}

			if (contentValuesVector.size() > 0)
			{
				mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
				ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
				contentValuesVector.toArray(contentValuesArray);
				mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, contentValuesArray);
			}
//			Log.d(LOG_TAG, "Inserted > " + contentValuesVector.size());
		}
		catch (JSONException e)
		{
			Log.e(LOG_TAG, e.getMessage(), e);
			e.printStackTrace();
		}

		return null;
	}
}
