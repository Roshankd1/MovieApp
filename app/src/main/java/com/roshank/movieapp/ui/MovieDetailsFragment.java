package com.roshank.movieapp.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roshank.movieapp.R;
import com.roshank.movieapp.databaseSQlite.MovieContract;
import com.roshank.movieapp.model.Movie;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsFragment extends Fragment {



        public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

        public static final String ARG_MOVIE = "ARG_MOVIE";

        private Movie mMovie;


        @BindView(R.id.movie_title)
        TextView mMovieTitleView;
        @BindView(R.id.movie_overview)
        TextView mMovieOverviewView;

        @BindView(R.id.movie_poster)
        ImageView mMoviePosterView;



        public MovieDetailsFragment() {

        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            assert getArguments() != null;
            if (getArguments().containsKey(ARG_MOVIE)) {
                mMovie = getArguments().getParcelable(ARG_MOVIE);
            }
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Activity activity = getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);

            if (appBarLayout != null && activity instanceof MovieDetailsActivity) {
                appBarLayout.setTitle(mMovie.getOriginalTitle());
            }

            ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
            if (movieBackdrop != null) {
                Picasso.get()
                        .load(mMovie.getBackdropPath())
                        .config(Bitmap.Config.RGB_565)
                        .into(movieBackdrop);
            }
        }
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
            ButterKnife.bind(this, rootView);

            mMovieTitleView.setText(mMovie.getOriginalTitle());
            mMovieOverviewView.setText(mMovie.getOverview());
            Picasso.get()
                    .load(mMovie.getPosterPath())
                    .config(Bitmap.Config.RGB_565)
                    .into(mMoviePosterView);

            Log.d(LOG_TAG, MovieContract.MovieEntry.CONTENT_URI.toString());
            /*IF savedInstanceState == null*/




            Log.d(LOG_TAG, "Current selected movie id is: " + String.valueOf(mMovie.getId()));

            return rootView;
        }
        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);

        }

        @Override
        public void onResume() {
            super.onResume();
        }










       /* @SuppressLint("StaticFieldLeak")
        public void mark_as_favorite() {
            Log.d(LOG_TAG, "Calling check for favorite method");


            new  AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    if (!check_for_favorite()) {
                   *//*
                    //Store movie object to the database
                    Log.d(LOG_TAG, "So creating new movieEntry object and storing it!");
                    final Date date = new Date();
                    MovieEntry movieEntry = new MovieEntry(
                            mMovie.getId(),
                            mMovie.getVoteAverage(),
                            mMovie.getOriginalTitle(),
                            mMovie.getBackdropPath(),
                            mMovie.getOverview(),
                            mMovie.getReleaseDate(),
                            mMovie.getPosterPath(),
                            date);
                    mDb.movieDao().insertMovie(movieEntry);
                    Log.d(LOG_TAG, "Was not marked as Favorite, so Marked it!");
                  *//*
                        ContentValues movie_data = new ContentValues();
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                                mMovie.getId());
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME,mMovie.getName());
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,mMovie.getOriginalTitle());
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, mMovie.getBackdropPath());
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
                        movie_data.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
                        Objects.requireNonNull(getContext()).getContentResolver().insert(
                                MovieContract.MovieEntry.CONTENT_URI,
                                movie_data
                        );
                        Log.d(LOG_TAG, "Was not marked as Favorite, so Marked it!");

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    Log.d(LOG_TAG, "Calling uupdate Favorites, inside markasfavorite");

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
*/








    }

