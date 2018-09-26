package com.roshank.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public static final String TAG = Movie.class.getSimpleName();


    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;

    private static String mSearchText;

    public static String getmSearchText() {
        return mSearchText;
    }

    public static void setmSearchText(String SearchText) {
        mSearchText = SearchText;
    }

    public Movie(){

    }

    public Movie(long id,
                 String title,
                 String originalTitle,
                 String backdropPath,
                 String overview,
                 String posterPath)
    {
        this.id = id;
        this.title=title;
        this.originalTitle = originalTitle;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.posterPath = posterPath;
    }

    protected Movie(Parcel in){
        id = in.readLong();
        title=in.readString();
        originalTitle = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
          return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(backdropPath);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
    }

    //Getter Methods
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackdropPath() {
        if (backdropPath != null && !backdropPath.isEmpty()) {
            if(!backdropPath.toLowerCase().contains("http://")){
                return "http://image.tmdb.org/t/p/original" + backdropPath;
            }else{
                return backdropPath;
            }

        }
        return null;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Nullable
    public String getPosterPath() {
        if (posterPath != null && !posterPath.isEmpty()) {

            if(!posterPath.toLowerCase().contains("http://")){
                return "http://image.tmdb.org/t/p/w342" + posterPath;
            }else{
                return posterPath;
            }

        }
        return null;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

}
