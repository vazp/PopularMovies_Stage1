package com.vazp.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Miguel on 16/07/2015.
 */
public class MoviesContract
{
	public static final String CONTENT_AUTHORITY = "com.vazp.popularmovies";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	public static final String PATH_MOVIE = "movie";

	public static final class MovieEntry implements BaseColumns
	{
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

		public static final String TABLE_NAME = "movie";

		/**
		 * TEXT
		 */
		public static final String COLUMN_RELEASE_DATE = "releasedate";
		/**
		 * TEXT
		 */
		public static final String COLUMN_TITLE = "title";
		/**
		 * REAL
		 */
		public static final String COLUMN_VOTEAVERAGE = "vote_average";
		/**
		 * REAL
		 */
		public static final String COLUMN_POPULARITY = "popularity";
		/**
		 * TEXT
		 */
		public static final String COLUMN_OVERVIEW = "overview";
		/**
		 * TEXT
		 */
		public static final String COLUMN_POSTER = "poster";

		public static Uri buildMovieUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
