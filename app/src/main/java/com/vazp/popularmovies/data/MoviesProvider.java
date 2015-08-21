package com.vazp.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.vazp.popularmovies.data.MoviesContract.MovieEntry;

/**
 * Created by Miguel on 21/07/2015.
 */
public class MoviesProvider extends ContentProvider
{
	private static final int MOVIE = 100;
	private static final int MOVIE_WITH_ID = 101;

	private static final UriMatcher uriMatcher = buildUriMatcher();
	private MoviesDbHelper mOpenHelper;

	@Override
	public boolean onCreate()
	{
		mOpenHelper = new MoviesDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor retCursor;
		switch (uriMatcher.match(uri))
		{
			case MOVIE:
			{
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				break;
			}
			case MOVIE_WITH_ID:
			{
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieEntry.TABLE_NAME,
						projection,
						MovieEntry._ID + " = " + ContentUris.parseId(uri),
						null,
						null,
						null,
						sortOrder);
				break;
			}

			default:
			{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}

		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public String getType(Uri uri)
	{
		switch (uriMatcher.match(uri))
		{
			case MOVIE:
			{
				return MovieEntry.CONTENT_TYPE;
			}
			case MOVIE_WITH_ID:
			{
				return MovieEntry.CONTENT_ITEM_TYPE;
			}
			default:
			{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		Uri returnUri;

		switch (uriMatcher.match(uri))
		{
			case MOVIE:
			{
				long _id = database.insert(MovieEntry.TABLE_NAME, null, values);
				if (_id > 0)
				{
					returnUri = MovieEntry.buildMovieUri(_id);
				}
				else
				{
					throw new SQLiteException("Failed to insert row into " + uri);
				}
				break;
			}
			default:
				throw new SQLiteException("Failed to insert row into " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values)
	{
		final SQLiteDatabase database = mOpenHelper.getWritableDatabase();

		switch (uriMatcher.match(uri))
		{
			case MOVIE:
				database.beginTransaction();
				int rowsAffected = 0;
				try
				{
					for(ContentValues value : values)
					{
						long id = database.insert(MovieEntry.TABLE_NAME, null, value);
						if (id != -1)
						{
							rowsAffected++;
						}
					}
					database.setTransactionSuccessful();
				}
				finally
				{
					database.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return rowsAffected;
			default:
				return super.bulkInsert(uri, values);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		int rowsDeleted;

		switch (uriMatcher.match(uri))
		{
			case MOVIE:
			{
				rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
				break;
			}
			default:
			{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}

		if (selection == null || rowsDeleted != 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		int rowsUpdated;

		switch (uriMatcher.match(uri))
		{
			case MOVIE:
			{
				rowsUpdated = database.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			}
			default:
			{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}

		if (rowsUpdated != 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rowsUpdated;
	}

	private static UriMatcher buildUriMatcher()
	{
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MoviesContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIE);
		matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

		return matcher;
	}
}
