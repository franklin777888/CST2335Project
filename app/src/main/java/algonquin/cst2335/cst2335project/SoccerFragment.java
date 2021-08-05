package algonquin.cst2335.cst2335project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * this class is to set up the details fragment, extends Fragment
 */
public class SoccerFragment extends Fragment {

    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    ArrayList<SoccerNews> favSoccerList = new ArrayList<>();
    SQLiteDatabase socDb;
    Button addSoccerFav;
    Button goToFav;
    SoccerOpenHelper mySoccerOpenHelper;
    boolean isHere;
    ImageView bmImage;
    String dataImg;
    String dataTitle;
    String dataDate;
    String dataDesc;
    String dataLink;
    long newId;

    /**
     * this method is to creat the view
     * @param inflater layoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return result
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        View result =  inflater.inflate(R.layout.soccer_fragment, container, false);
        bmImage =(ImageView)result.findViewById(R.id.soccerNewsImg);

        dataImg = dataFromActivity.getString(SoccerMainActivity.SOCCER_IMG).replace("http:","https:");
        Executor newThread= Executors.newSingleThreadExecutor();
        newThread.execute(()->{
            Bitmap image=null;
            try {
                URL imageUrl=new URL(dataImg);
                HttpURLConnection connection= (HttpURLConnection) imageUrl.openConnection();
                connection.connect();
                int responseCode=connection.getResponseCode();
                if(responseCode==200||responseCode==301){
                    image=BitmapFactory.decodeStream(connection.getInputStream());
                    Bitmap finalImage = image;
                    parentActivity.runOnUiThread(()->{
                        ImageView imageView=result.findViewById(R.id.soccerNewsImg);
                        imageView.setImageBitmap(finalImage);
                    });

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        TextView socTitle = (TextView)result.findViewById(R.id.soccerNewsTitle);
        dataTitle = dataFromActivity.getString(SoccerMainActivity.SOCCER_TITLE);
        socTitle.setText(dataTitle);

        TextView socDate = (TextView)result.findViewById(R.id.soccerDate);
        dataDate =dataFromActivity.getString(SoccerMainActivity.SOCCER_DATE);
        socDate.setText(dataDate);

        TextView socDesc = (TextView)result.findViewById(R.id.soccerDescription);
        dataDesc = dataFromActivity.getString(SoccerMainActivity.SOCCER_DESC);
        socDesc.setText(dataDesc);

        TextView socLink = (TextView)result.findViewById(R.id.soccerLink);
        dataLink = dataFromActivity.getString(SoccerMainActivity.SOCCER_LINK);
        socLink.setText(dataLink);

        goToFav = result.findViewById(R.id.goToSoccerFavBtn);
        Intent goToFavList = new Intent(container.getContext(),SoccerFavNews.class);
        goToFav.setOnClickListener(click ->{
            startActivity(goToFavList);
        });

        Button socBrowserBtn = (Button)result.findViewById(R.id.soccerLinkBrsBtn);
        socBrowserBtn.setOnClickListener(click -> {
            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(dataFromActivity.getString(SoccerMainActivity.SOCCER_LINK)));
            startActivity(intent);
        });

        addSoccerFav = (Button)result.findViewById(R.id.soccerSaveBtn);
        mySoccerOpenHelper = new SoccerOpenHelper(container.getContext());
        socDb = mySoccerOpenHelper.getWritableDatabase();
        changeBtnText();

        FrameLayout frameLayout = result.findViewById(R.id.soccerFragmentLocation);
        addSoccerFav.setOnClickListener(v -> {
            if(isHere){
                SoccerNews soccerDel = new SoccerNews();
                soccerDel.setSoccerImg(dataImg);
                soccerDel.setSoccerTitle(dataTitle);
                soccerDel.setSoccerDate(dataDate);
                soccerDel.setSoccerDescription(dataDesc);
                soccerDel.setSoccerLink(dataLink);
                favSoccerList.remove(soccerDel);
                deleteSoccer(soccerDel);
                changeBtnText();
            }else{
                ContentValues newRowValues = new ContentValues();
                newRowValues.put(SoccerOpenHelper.COL_SOCCERIMG, dataImg);
                newRowValues.put(SoccerOpenHelper.COL_SOCCERTITLE, dataTitle);
                newRowValues.put(SoccerOpenHelper.COL_SOCCERDATE, dataDate);
                newRowValues.put(SoccerOpenHelper.COL_SOCCERDESC, dataDesc);
                newRowValues.put(SoccerOpenHelper.COL_SOCCERLINK, dataLink);
                newId = socDb.insert(SoccerOpenHelper.TABLE_NAME, null, newRowValues);
                changeBtnText();
            }
        });
        return result;
    }

    /**
     * create method to query and match the result wih the database,and change button text
     */
    private void changeBtnText(){
        Cursor results = socDb.query(false, SoccerOpenHelper.TABLE_NAME, new String[]{SoccerOpenHelper.COL_SOCCERTITLE, SoccerOpenHelper.COL_SOCCERDATE},
                SoccerOpenHelper.COL_SOCCERTITLE +" = ? and " + SoccerOpenHelper.COL_SOCCERDATE+"= ?" ,
                new String[]{String.valueOf(dataTitle),String.valueOf(dataDate)}, null, null, null, null);
        if(results.getCount()>0){
            isHere=true;
            addSoccerFav.setText(getResources().getString(R.string.soccerRemoveFavBtn));
        }else{
            isHere=false;
            addSoccerFav.setText(getResources().getString(R.string.soccerSaveFavBtn));
        }
    }

    /**
     * create method to delete the specific row matched the query result in the table
     * @param soccerNews object of the Song class
     */
    protected void deleteSoccer(SoccerNews soccerNews)
    {
        socDb.delete(SoccerOpenHelper.TABLE_NAME, SoccerOpenHelper.COL_SOCCERTITLE +" = ? and " + SoccerOpenHelper.COL_SOCCERDATE+" = ?",  new String[]{String.valueOf(dataTitle),String.valueOf(dataDate)});
    }


    /**
     * this method is to set up the  onAttach function
     * @param context context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /**
         * context will either be FragmentExample for a tablet, or EmptyActivity for phone
         */
        parentActivity = (AppCompatActivity)context;
    }

}
