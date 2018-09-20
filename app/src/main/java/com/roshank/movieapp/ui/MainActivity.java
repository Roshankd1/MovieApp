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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;


import com.roshank.movieapp.BuildConfig;
import com.roshank.movieapp.R;
import com.roshank.movieapp.adapter.MovieAdapter;
import com.roshank.movieapp.databaseSQlite.MovieContract;
import com.roshank.movieapp.model.Movie;
import com.roshank.movieapp.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final String SEARCH_KEY = "search_key";

    String myApiKey = BuildConfig.API_KEY;
    private static final int NOW_PLAYING_MOVIES_LOADER = 0;
    @BindView(R.id.recycled_movie_grid)
    RecyclerView movie_grid_recyclerView;

    @BindView(R.id.indeterminateBar)
    ProgressBar mProgressBar;

    String nowPlayingMoviesURL;
    String searchMoviesURL;

    ArrayList<Movie> mNowPlayingList;
    ArrayList<Movie> mSearchMoviesList;


    private MovieAdapter mAdapter;
    private String mSearch = FetchMovies.SEARCH;

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SEARCH_MOVIES = "EXTRA_SEARCH_MOVIES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE); //Hide Progressbar by Default
        //Dealing with View Model

        //Define recyclerView Layout
        movie_grid_recyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.number_of_grid_columns)));
        mAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        movie_grid_recyclerView.setAdapter(mAdapter);


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(movies);
                findViewById(R.id.indeterminateBar).setVisibility(View.GONE);

                // For listening content updates for tow pane mode
                if (mSearch.equals(FetchMovies.NOW_PLAYING)) {
                    getSupportLoaderManager().initLoader(NOW_PLAYING_MOVIES_LOADER, null, this);
                }
            }
            update_empty_state();
        } else {
            // Fetch Movies only if savedInstanceState == null
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
    }

    @Override
    public void onBackPressed() {
        if (mSearch.equals(FetchMovies.NOW_PLAYING)) {
            getSupportLoaderManager().destroyLoader(NOW_PLAYING_MOVIES_LOADER);
        }
        mSearch = FetchMovies.SEARCH;
        refreshList(mSearch);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putString(EXTRA_SEARCH_MOVIES, mSearch);

        if (!mSearch.equals(FetchMovies.SEARCH)) {
            getSupportLoaderManager().destroyLoader(NOW_PLAYING_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity,menu);

        switch (mSearch) {
            case FetchMovies.NOW_PLAYING:

                break;
            case FetchMovies.SEARCH:

                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn:
                if (mSearch.equals(FetchMovies.NOW_PLAYING)) {
                    getSupportLoaderManager().destroyLoader(NOW_PLAYING_MOVIES_LOADER);
                }
                mSearch = FetchMovies.NOW_PLAYING;
                refreshList(mSearch);
                break;
            case R.id.cancel_btn:
                if (mSearch.equals(FetchMovies.SEARCH)) {
                    getSupportLoaderManager().destroyLoader(NOW_PLAYING_MOVIES_LOADER);
                }
                mSearch = FetchMovies.SEARCH;
                refreshList(mSearch);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList(String sort_by) {

        switch (sort_by) {
            case FetchMovies.NOW_PLAYING:
                mAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
                mAdapter.add(mNowPlayingList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case FetchMovies.SEARCH:
                mAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
                mAdapter.add(mSearchMoviesList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
        }


    }

    @Override
    public void send_details(Movie movie, int position) {

        Intent intent = new Intent(this, MainActivity.class);
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
        update_empty_state();
        findViewById(R.id.indeterminateBar).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    //AsyncTask
    @SuppressLint("StaticFieldLeak")
    public class FetchMovies extends AsyncTask<Void, Void, Void> {

        public final static String NOW_PLAYING = "popular";
        public final static String SEARCH = "search";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {


            nowPlayingMoviesURL = "https://api.themoviedb.org/3/movie/popular?api_key=" + myApiKey + "&language=en-US&page="+ 1 + "&region="+"IT";
            searchMoviesURL = "https://api.themoviedb.org/3/search/movie?api_key=" + myApiKey + "&language=en-US" + "&query=" + SEARCH_KEY;


            mNowPlayingList = new ArrayList<>();
            mSearchMoviesList = new ArrayList<>();
            try {
                if (NetworkUtils.networkStatus(MainActivity.this)) {
                    mNowPlayingList = NetworkUtils.fetchData(nowPlayingMoviesURL); //Get popular movies
                    mSearchMoviesList = NetworkUtils.fetchData(searchMoviesURL); //Get top rated movies

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
            mAdapter = new MovieAdapter(new ArrayList<Movie>(), MainActivity.this);
            mAdapter.add(mNowPlayingList);
            movie_grid_recyclerView.setAdapter(mAdapter);
        }
    }

    private void update_empty_state() {
        if (mAdapter.getItemCount() == 0) {
            findViewById(R.id.empty_state).setVisibility(View.GONE);
        }
    }
}

