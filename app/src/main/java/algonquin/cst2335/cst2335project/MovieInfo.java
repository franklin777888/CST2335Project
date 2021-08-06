package algonquin.cst2335.cst2335project;

/**
 * This class stores all the variables of a movie.
 */
public class MovieInfo
{
    String title;
    String year;
    String rating;
    String runtime;
    String actor;
    String plot;
    String imageURL;
    long id;


    /**
     * Constructor
     * @param title title of movie
     * @param year year of movie
     * @param rating rating of movie
     * @param runtime run time of movie
     * @param actors actors of movie
     * @param plot plot of movie
     * @param imageURL image of movie
     * @param id id being created/incremented when saved
     */
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

    /**
     * setter of id
     * @param l id of movie
     */
    public void setId(long l){
        id = l;
    }

    /**
     * getter of id
     * @return id of movie
     */
    public long getId(){
        return id;
    }

    /**
     * getter of movie title
     * @return title of movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * getter of movie year
     * @return year of movie
     */
    public String getYear() {
        return year;
    }

    /**
     * getter of movie rating
     * @return rating of movie
     */
    public String getRating() {
        return rating;
    }

    /**
     * getter of run time of movie
     * @return run time of movie
     */
    public String getRuntime() { return runtime; }

    /**
     * getter of actors of movie
     * @return actors of movie
     */
    public String getActor() {
        return actor;
    }

    /**
     * getter of plot of movies
     * @return plot of movies
     */
    public String getPlot() {
        return plot;
    }

    /**
     * getter of image of movie
     * @return image of movie
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Constructor
     * @param title title of movie
     * @param year year of movie
     * @param rating rating of movie
     * @param runtime run time of movie
     * @param actors actors of movie
     * @param plot plot of movie
     * @param imageURL image of movie
     */
    public MovieInfo(String title, String year, String rating, String runtime, String actors, String plot, String imageURL) {
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
        this.actor = actors;
        this.plot = plot;
        this.imageURL = imageURL;
    }

    /**
     * Constructor
     * @param imageURL image of the movie
     * @param title title of the movie
     * @param rating rating of the movie
     */
    public MovieInfo(String imageURL,String title, String rating) {
        this.title = title;
        this.rating = rating;
        this.imageURL = imageURL;
    }
}


