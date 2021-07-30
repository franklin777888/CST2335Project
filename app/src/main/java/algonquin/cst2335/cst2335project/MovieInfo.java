package algonquin.cst2335.cst2335project;

public class MovieInfo {
    String title;
    String year;
    String rating;
    String runtime;
    String actor;
    String plot;
    String imageURL;
    long id;


    public MovieInfo(String title, String year, String rating, String runtime, String actors, String plot, String imageURL, long id){
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
        this.actor = actors;
        this.plot = plot;
        this.imageURL = imageURL;
        setId(id);
    }

    public void setId(long l){
        id = l;
    }

    public long getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }

    public String getRuntime() { return runtime; }

    public String getActor() {
        return actor;
    }

    public String getPlot() {
        return plot;
    }

    public String getImageURL() {
        return imageURL;
    }

    public MovieInfo(String title, String year, String rating, String runtime, String actors, String plot, String imageURL) {
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
        this.actor = actors;
        this.plot = plot;
        this.imageURL = imageURL;
    }

    public MovieInfo(String imageURL, String title, String year) {
        this.title = title;
        this.year = year;
        this.imageURL = imageURL;
    }
}
