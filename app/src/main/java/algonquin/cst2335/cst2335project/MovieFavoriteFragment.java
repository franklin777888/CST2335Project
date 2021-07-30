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

public class MovieFavoriteFragment extends Fragment {
    ArrayList<MovieInfo> messages = new ArrayList<>();
    MyMovieAdapter adt= new MyMovieAdapter();

    MovieMyOpenHelper opener;
    SQLiteDatabase db;

    Bitmap image;

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
        int imdbIDCol = results.getColumnIndex(MovieMyOpenHelper.col_id);

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
        Collections.sort(messages,(MovieInfo m1, MovieInfo m2)->{return m1.getTitle().compareTo(m2.getTitle());});

        return movieLayout;
    }

    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView titleText;
        TextView ratingText;
        ImageView imageURLText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);
            titleText =  itemView.findViewById(R.id.movieName);
            ratingText = itemView.findViewById(R.id.ratingView);
            imageURLText = itemView.findViewById(R.id.movieImage);

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
                            MovieSearch parentActivity = (MovieSearch) getContext();
                            parentActivity.userClickedMessage(searchResult,image, detailType);
                        })
                        .create().show();
            });
        }
        public void setPosition(int p) {
            position = p;
        }
    }

    private class MyMovieAdapter extends RecyclerView.Adapter<MyRowViews>{
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();// LayoutInflater is for loading XML layouts
            int layoutID;
            layoutID=  R.layout.favorite_movie_item;

            View loadedRow = inflater.inflate(layoutID, parent, false);// parent is for how much room
            MyRowViews initRow = new MyRowViews(loadedRow);
            return initRow;// will initialize the TextView
        }

        @Override //says ViewHolder, but it's actually MyRowViews object; position is the row we're building
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(messages.get(position).getTitle());
            holder.ratingText.setText(messages.get(position).getRating());

            image = BitmapFactory.decodeFile(getContext().getFilesDir() + "/" + messages.get(position).getTitle() + ".png");
            holder.imageURLText.setImageBitmap(image);
            holder.setPosition(position);
        }

        @Override // how many itmes to show
        public int getItemCount() {
            return messages.size();
        }
    }
}
