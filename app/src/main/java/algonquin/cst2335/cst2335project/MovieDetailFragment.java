package algonquin.cst2335.cst2335project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

/**
 * This class to show details of searched movie information in fragment.
 */
public class MovieDetailFragment extends Fragment {

    MovieInfo searchResult;
    Bitmap image;
    int detailType;

    MovieMyOpenHelper opener;
    SQLiteDatabase db;

    ImageView addButton;

    /**
     * Constructor
     * @param searchResult search result of the movie
     * @param image image of the movie
     * @param detailType a number to identify if this movie saved or not. equals 2 if already saved.
     */
    public MovieDetailFragment(MovieInfo searchResult, Bitmap image, int detailType){
        this.searchResult = searchResult;
        this.image = image;
        this.detailType = detailType;
    }

    /**
     * This method creates and returns the view associated with the fragment which is the details of the searched movie.
     * @param inflater layout inflater of XML file
     * @param container container that used to contain other views, i.e. the different textview(s)
     * @param savedInstanceState the saved instance in the bundle
     * @return detailsView of the searched movie
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.movie_detail, container, false);

        ImageView posterView = detailsView.findViewById(R.id.poster);
        TextView titleView = detailsView.findViewById(R.id.titleView);
        TextView yearView = detailsView.findViewById(R.id.yearView);
        TextView ratingView = detailsView.findViewById(R.id.movie_rating_text);
        TextView runtimeView = detailsView.findViewById(R.id.runtimeView);
        TextView actorsView = detailsView.findViewById(R.id.actorsView);
        TextView plotView = detailsView.findViewById(R.id.plotView);

        posterView.setImageBitmap(image);
        titleView.setText("Movie Title: " + searchResult.getTitle());
        yearView.setText("Year: " + searchResult.getYear());
        ratingView.setText("Rating: " + searchResult.getRating());
        runtimeView.setText("Run time: " + searchResult.getRuntime());
        actorsView.setText("Main actors: " + searchResult.getActor());
        plotView.setText("Plot: " + searchResult.getPlot());

        ImageView closeButton = detailsView.findViewById(R.id.goBack);

        if(detailType == 2){
            closeButton.setOnClickListener(goBackClicked -> {
                getParentFragmentManager().beginTransaction().remove(this).commit();
                MovieSearch parentActivity = (MovieSearch) getContext();

            });
        }
        closeButton.setOnClickListener(goBackClicked -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        addButton = detailsView.findViewById(R.id.favorite);
        if(detailType == 2){
            addButton.setVisibility(View.INVISIBLE);
        }
        addButton.setOnClickListener(addClicked -> {
            notifyMessageAdded(searchResult);
        });

        return detailsView;
    }

    /**
     * This method is set to add a selected movie to favourite.
     * User will be notified if this movie is already saved.
     * If new, newID will be generated and notify user.
     * @param searchResult
     */
    public void notifyMessageAdded(MovieInfo searchResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );

        builder.setMessage("Add it to your favourite: "+ searchResult.getTitle())
                .setTitle(R.string.movie_add_to_favorite)
                .setNegativeButton(R.string.cancel_add_to_favorite, (dialog, cl) ->{ })
                .setPositiveButton(R.string.add_to_favorite, (dialog, cl) ->{
                opener = new MovieMyOpenHelper(getContext());
                db = opener.getWritableDatabase();

                Cursor result=db.query(MovieMyOpenHelper.TABLE_NAME,new String[]{MovieMyOpenHelper.col_title},MovieMyOpenHelper.col_title+"=?",new String[]{searchResult.getTitle()},null,null,null);
                if(result.moveToNext()){
                        Toast.makeText(getContext(), R.string.movie_duplicated_add, Toast.LENGTH_LONG).show();
                }else {
                        ContentValues newRow = new ContentValues();
                        newRow.put(MovieMyOpenHelper.col_title, searchResult.getTitle());
                        newRow.put(MovieMyOpenHelper.col_year, searchResult.getYear());
                        newRow.put(MovieMyOpenHelper.col_rating, searchResult.getRating());
                        newRow.put(MovieMyOpenHelper.col_runtime, searchResult.getRuntime());
                        newRow.put(MovieMyOpenHelper.col_actors, searchResult.getActor());
                        newRow.put(MovieMyOpenHelper.col_plot, searchResult.getPlot());
                        newRow.put(MovieMyOpenHelper.col_image, searchResult.getImageURL());
                        newRow.put(MovieMyOpenHelper.col_image, searchResult.getImageURL());

                        long newId = db.insert(MovieMyOpenHelper.TABLE_NAME, MovieMyOpenHelper.col_title, newRow);

                        Snackbar.make(addButton, "You save movie " + searchResult.getTitle(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo_movie_add_to_favorite, clk -> {
                                    db.delete(MovieMyOpenHelper.TABLE_NAME, "_id=?",new String[]{Long.toString(searchResult.getId())});
                                })
                                .show();
                }})
                .create().show();
    }
}
