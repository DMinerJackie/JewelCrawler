package com.jackie.crawler.doubanmovie.entity;

import org.springframework.stereotype.Component;

/**
 * Created by Jackie on 2016/9/25 0025.
 */
@Component
public class Movie {
    private int movieId;
    private String name;
    private String director;
    private String scenarist;
    private String actors;
    private String type;
    private String country;
    private String language;
    private String releaseDate;
    private String runtime;
    private String ratingNum;
    private String tags;

    public Movie(int movieId) {
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScenarist() {
        return scenarist;
    }

    public void setScenarist(String scenarist) {
        this.scenarist = scenarist;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(String ratingNum) {
        this.ratingNum = ratingNum;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", name='" + name + '\'' +
                ", director='" + director + '\'' +
                ", scenarist='" + scenarist + '\'' +
                ", actors='" + actors + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", language='" + language + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", runtime='" + runtime + '\'' +
                ", ratingNum='" + ratingNum + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
