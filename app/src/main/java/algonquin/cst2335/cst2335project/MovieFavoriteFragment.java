package algonquin.cst2335.cst2335project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This class to create the favourite movie fragment.
 */
public class MovieFavoriteFragment extends Fragment {
    ArrayList<MovieInfo> messages = new ArrayList<>();
    MyMovieAdapter adt= new MyMovieAdapter();

    MovieMyOpenHelper opener;
    SQLiteDatabase db;

    Bitmap image;

    /**
     * This method creates and returns the view associated with the fragment which is the list of favourite movies.
     * @param inflater layout inflater of XML file
     * @param container container that used to contain other views
     * @param savedInstanceState the saved instance in the bundle
     * @return movieLayout of the list of favourite movies
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View movieLayout = inflater.inflate(R.layout.movie_list,container, false);

        RecyclerView movieList = movieLayout.findViewById(R.id.movieRecycler);

        //adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        movieList.setLayoutManager(layoutManager);
        adt = new MyMovieAdapter();
        movieList.setAdapter(adt);

        opener = new MovieMyOpenHelper(getContext());
        db = opener.getWritableDatabase();

        Cursor results = db.rawQuery("Select * from " + MovieMyOpenHelper.TABLE_NAME + ";", null);

        int _idCol = results.getColumnIndex("_id");
        int titleCol = results.getColumnIndex(MovieMyOpenHelper.col_title);
        int yearCol = results.getColumnIndex(MovieMyOpenHelper.col_year);
        int ratingCol = results.getColumnIndex(MovieMyOpenHelper.col_rating);
        int runtimeCol = results.getColumnIndex(MovieMyOpenHelper.col_runtime);
        int actorsCol = results.getColumnIndex(MovieMyOpenHelper.col_actors);
        int plotCol = results.getColumnIndex(MovieMyOpenHelper.col_plot);
        int imageCol = results.getColumnIndex(MovieMyOpenHelper.col_image);

        while(results.moveToNext()){
            long id = results.getInt(_idCol);
            String title = results.getString(titleCol);
            String year = results.getString(yearCol);
            String rating = results.getString(ratingCol);
            String runtime = results.getString(runtimeCol);
            String actors = results.getString(actorsCol);
            String plot = results.getString(plotCol);
            String imageURL = results.getString(imageCol);
            messages.add(new MovieInfo(title, year, rating, runtime, actors, plot, imageURL, id));
        }
        Collections.sort(messages,(MovieInfo m1, MovieInfo m2)->{return m2.getRating().compareTo(m1.getRating());});

        return movieLayout;
    }

    /**
     * This method to show rows of favourite movie list,
     * and allow user to view details of selected movie,
     * or delete selected movie from favourite list.
     */
    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView titleText;
        TextView ratingText;
        ImageView imageURLText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);
            titleText =  itemView.findViewById(R.id.movie_title_text);
            ratingText = itemView.findViewById(R.id.movie_rating_text);
            imageURLText = itemView.findViewById(R.id.movieimage_text);

            itemView.setOnClickListener( click ->{
                int chosenPosition = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setMessage(R.string.view_or_delete )
                        .setNegativeButton(R.string.delete_movie_from_favorite, (dialog, cl) ->{
                            MovieInfo removedMessage = messages.get(chosenPosition);
                            messages.remove(chosenPosition);
                            adt.notifyItemRemoved(chosenPosition);

                            db.delete(MovieMyOpenHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(removedMessage.getId())});

                            Snackbar.make(imageURLText, "You deleted movie: " + removedMessage.getTitle(), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo_movie_add_to_favorite, clk ->{
                                        messages.add(chosenPosition, removedMessage);
                                        adt.notifyItemInserted(chosenPosition);
                                        ContentValues values=new ContentValues();
                                        values.put(MovieMyOpenHelper.col_id,removedMessage.getId());
                                        values.put(MovieMyOpenHelper.col_title,removedMessage.getTitle());
                                        values.put(MovieMyOpenHelper.col_year,removedMessage.getYear());
                                        values.put(MovieMyOpenHelper.col_rating,removedMessage.getRating());
                                        values.put(MovieMyOpenHelper.col_runtime,removedMessage.getRuntime());
                                        values.put(MovieMyOpenHelper.col_actors,removedMessage.getActor());
                                        values.put(MovieMyOpenHelper.col_plot,removedMessage.getPlot());
                                        values.put(MovieMyOpenHelper.col_image,removedMessage.getImageURL());

                                        db.insert(MovieMyOpenHelper.TABLE_NAME,null,values);
                                    })
                                    .show();
                        })
                        .setPositiveButton(R.string.view_detail_movie, (dialog, cl) ->{
                            MovieInfo searchResult = messages.get(chosenPosition);
                            int detailType = 2;
                            image = BitmapFactory.decodeFile(getContext().getFilesDir() + "/" + messages.get(chosenPosition).getTitle() + ".png");
                            MovieMainActivity parentActivity = (MovieMainActivity) getContext();
                            parentActivity.userClickedMessage(searchResult,image, detailType);
                        })
                        .create().show();
            });
        }
        public void setPosition(int p) {
            position = p;
        }
    }

    /**
     * This class sets up the view for each movie in the data set
     */
    private class MyMovieAdapter extends RecyclerView.Adapter<MyRowViews>{

        /**
         * This method to create a new ViewHolder object whenever the RecyvlerView needs a new one.
         * The row layout will be inflated and passed to the ViewHolder object so that each child view
         * can be found and stored.
         * @param parent The ViewGroup is the parent view that will hold the cell that are about to create
         * @param viewType the view type of the new View
         * @return initRow, which is the object of saved movies
         */
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();// LayoutInflater is for loading XML layouts
            int layoutID;
            layoutID=  R.layout.favorite_movie_item;

            View loadedRow = inflater.inflate(layoutID, parent, false);// parent is for how much room
            MyRowViews initRow = new MyRowViews(loadedRow);
            return initRow;// will initialize the TextView
        }

        /**
         * This method to update the contents of the itemView, which is the list of favourite movies
         * @param holder holder of MyRowViews
         * @param position the row position of a movie saved in favourite
         */
        @Override //says ViewHolder, but it's actually MyRowViews object; position is the row we're building
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(messages.get(position).getTitle());
            holder.ratingText.setText(messages.get(position).getRating());

            image = BitmapFactory.decodeFile(getContext().getFilesDir() + "/" + messages.get(position).getTitle() + ".png");
            holder.imageURLText.setImageBitmap(image);
            holder.setPosition(position);
        }

        /**
         * This method to get the size of the Array of movie list
         * @return size of the Array of movie list
         */
        @Override // how many items to show
        public int getItemCount() {
            return messages.size();
        }
    }
}
