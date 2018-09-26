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


import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsFragment extends Fragment {


    public static final String TAG = MovieDetailsFragment.class.getSimpleName();

    public static final String ARG_MOVIE = "ARG_MOVIE";

    private Movie mMovie;


    @BindView(R.id.movie_title)
    TextView mMovieTitleView;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewView;


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
        CollapsingToolbarLayout appBarLayout = Objects.requireNonNull(activity).findViewById(R.id.toolbar_layout);

        if (appBarLayout != null && activity instanceof MovieDetailsActivity) {
            appBarLayout.setTitle(mMovie.getOriginalTitle());
        }

        ImageView movieBackdrop = (activity.findViewById(R.id.movie_backdrop));
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

        Log.d(TAG, MovieContract.MovieEntry.CONTENT_URI.toString());
        /*if savedInstanceState == null*/


        Log.d(TAG, "Current selected movie id is: " + String.valueOf(mMovie.getId()));

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

}

