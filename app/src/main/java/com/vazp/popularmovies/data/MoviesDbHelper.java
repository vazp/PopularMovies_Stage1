package com.vazp.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Miguel on 21/07/2015.
 */
public class MoviesDbHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "movies.db";

	public MoviesDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		final String SQL_CREATE_MOVIE_TABLE =
				"CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
				MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
	            MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
	            MoviesContract.MovieEntry.COLUMN_VOTEAVERAGE + " REAL NOT NULL, " +
	            MoviesContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
	            MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
	            MoviesContract.MovieEntry.COLUMN_POSTER + ");";

		sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
	{
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
