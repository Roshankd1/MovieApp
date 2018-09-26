package com.roshank.movieapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.roshank.movieapp.BuildConfig;
import com.roshank.movieapp.R;
import com.roshank.movieapp.adapter.MovieAdapter;
import com.roshank.movieapp.databaseSQlite.MovieContract;
import com.roshank.movieapp.model.Movie;
import com.roshank.movieapp.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity implements MainActivityView, MovieAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();


    String SEARCH_KEY = "";

    String myApiKey = BuildConfig.API_KEY;
    @BindView(R.id.recycled_movie_grid)
    RecyclerView movie_grid_recyclerView;

    @BindView(R.id.back_btn)
    ImageView mBackBtn;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.search_text)
    EditText mSearchText;
    @BindView(R.id.search_btn)
    ImageView mSearchBtn;
    @BindView(R.id.close_btn)
    ImageView mCloseBtn;
    @BindView(R.id.indeterminateBar)
    ProgressBar mProgressBar;


    ArrayList<Movie> mNowPlayingList;
    ArrayList<Movie> mSearchMoviesList;

    private MovieAdapter mAdapter;

    private final static String NOW_PLAYING = "now_playing";
    private final static String SEARCH = "search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE); //Hiding Progressbar by Default

//        setSupportActionBar(mToolbar);
//        setTitle(null);
        mToolbarTitle.setText(getString(R.string.app_name));
        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

        movie_grid_recyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.number_of_grid_columns)));

        mAdapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        movie_grid_recyclerView.setAdapter(mAdapter);


        if (NetworkUtils.networkStatus(MainActivity.this)) {
            new FetchMovies().execute();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(getString(R.string.title_network_alert));
            dialog.setMessage(getString(R.string.message_network_alert));
            dialog.setCancelable(false);
            dialog.show();
        }

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchPressed();
            }
        });
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelPressed();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackBtnPressed();
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (TextUtils.isEmpty(charSequence)) {
                    initView();
                    refreshList(NOW_PLAYING);
                } else {
                    SEARCH_KEY = charSequence.toString();
                    executeMovie();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    refreshList(NOW_PLAYING);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();

    }


    @Override
    public void send_details(Movie movie, int position) {

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsFragment.ARG_MOVIE, movie);
        startActivity(intent);

    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.indeterminateBar).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mAdapter.add(cursor);
        findViewById(R.id.indeterminateBar).setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void executeMovie() {
        if (NetworkUtils.networkStatus(MainActivity.this)) {
            new FetchMovies().execute();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(getString(R.string.title_network_alert));
            dialog.setMessage(getString(R.string.message_network_alert));
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchMovies extends AsyncTask<Void, Void, Void> {


        private String nowPlayingMoviesURL;
        private String searchMoviesURL;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {

            nowPlayingMoviesURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + myApiKey + "&language=en-US";
            searchMoviesURL = "https://api.themoviedb.org/3/search/movie?api_key=" + myApiKey + "&language=en-US" + "&query=" + SEARCH_KEY;


            mNowPlayingList = new ArrayList<>();
            mSearchMoviesList = new ArrayList<>();
            try {
                if (NetworkUtils.networkStatus(MainActivity.this)) {
                    mNowPlayingList = NetworkUtils.fetchData(nowPlayingMoviesURL); //Get now playing movies
                    mSearchMoviesList = NetworkUtils.fetchData(searchMoviesURL); //Get search movies

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(getString(R.string.title_network_alert));
                    dialog.setMessage(getString(R.string.message_network_alert));
                    dialog.setCancelable(false);
                    dialog.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.INVISIBLE);
            //Load popular movies by default
            mAdapter = new MovieAdapter(MainActivity.this, new ArrayList<Movie>(), MainActivity.this);
            mAdapter.add(mNowPlayingList);
            movie_grid_recyclerView.setAdapter(mAdapter);

            if (!mSearchText.getText().toString().isEmpty()) {
                refreshList(SEARCH);
            }
        }
    }

    @Override
    public void initView() {
        mBackBtn.setVisibility(View.GONE);
        mSearchText.setVisibility(View.GONE);
        mCloseBtn.setVisibility(View.GONE);
        mSearchBtn.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSearchPressed() {
        mSearchBtn.setVisibility(View.GONE);
        mBackBtn.setVisibility(View.VISIBLE);
        mCloseBtn.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.GONE);
        mSearchText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelPressed() {
        initView();
        refreshList(NOW_PLAYING);
        mSearchText.setText(null);
    }

    @Override
    public void onBackBtnPressed() {
        initView();
        mSearchText.setText(null);
        refreshList(NOW_PLAYING);
    }

    private void refreshList(String sortBy) {
        switch (sortBy) {
            case NOW_PLAYING:
                mAdapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
                mAdapter.add(mNowPlayingList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case SEARCH:
                mAdapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
                mAdapter.add(mSearchMoviesList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
        }
    }

}

