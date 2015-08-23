package com.vazp.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vazp.popularmovies.NetworkUtility;
import com.vazp.popularmovies.R;
import com.vazp.popularmovies.data.MoviesContract.MovieEntry;

/**
 * Created by Miguel on 25/07/2015.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>
{
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context mContext;
    private MoviesAdapterOnClickHandler mClickHandler;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView posterImageView;
        private int mIdMovie;
        private String mTitle, mPoster;

        public MoviesAdapterViewHolder(View view)
        {
            super(view);

            posterImageView = (ImageView) view.findViewById(R.id.list_item_movie_imageview);

            posterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            mClickHandler.onClick(this);
        }

        public void setIdmovie(int idMovie)
        {
            mIdMovie = idMovie;
        }

        public int getIdMovie()
        {
            return mIdMovie;
        }

        public void setTitle(String title)
        {
            mTitle = title;
        }

        public String getTitle()
        {
            return mTitle;
        }

        public void setPoster(String poster)
        {
            mPoster = poster;
        }

        public String getPoster()
        {
            return mPoster;
        }

    }

    public interface MoviesAdapterOnClickHandler
    {
        void onClick(MoviesAdapterViewHolder viewHolder);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler)
    {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (parent instanceof RecyclerView)
        {
            int layoutId = R.layout.list_item_movie_entry;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);

            return new MoviesAdapterViewHolder(view);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position)
    {
        mCursor.moveToPosition(position);

        holder.setIdmovie(mCursor.getInt(mCursor.getColumnIndex(MovieEntry._ID)));
        holder.setTitle(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_TITLE)));
        holder.setPoster(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));

        if (holder.getPoster().equals("null") || !NetworkUtility.checkConnection(mContext))
        {
            holder.posterImageView.setImageResource(R.drawable.image_not_available);
        }
        else
        {
            Picasso.with(mContext).load(mContext.getResources().getString(R.string.base_movie_image_url) +
                                        mContext.getResources().getString(R.string.image_size) +
                                        holder.getPoster()).into(holder.posterImageView);
        }
    }

    @Override
    public int getItemCount()
    {
        if (mCursor == null)
        {
            return 0;
        }
        else
        {
            return mCursor.getCount();
        }
    }

    public void swapCursor(Cursor newCursor)
    {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor()
    {
        return mCursor;
    }
}
