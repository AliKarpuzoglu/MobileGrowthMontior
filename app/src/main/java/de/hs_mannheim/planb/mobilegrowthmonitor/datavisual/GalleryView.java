package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.profiles.ProfileView;


public class GalleryView extends AppCompatActivity {
    public static final String TAG = GalleryView.class.getSimpleName();
    public static ArrayList<String> pathList;
    GridView imageGrid;
    protected int profile_Id;
    protected static String profileName;
    private ProfileData profile;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);

        Bundle extras = getIntent().getExtras();
        this.profile_Id = extras.getInt("profile_Id");

        dbHelper = DbHelper.getInstance(getApplicationContext());
        profile = dbHelper.getProfile(profile_Id);
        profileName = profile.firstname;


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //    onWindowFocusChanged(true);
    }

    /**
     * this is used to reload the pictures
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("Gallery", "onFocusChanged");

        if (pathList == null) {
            pathList = new ArrayList<>(
            );
        }
        if (hasFocus) {
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "growpics" + File.separator + profileName);
            if (folder.isDirectory()) {

                refreshView();
            } else {
                if (pathList.size() == 0) {
                    refreshView();
                }
                imageGrid = (GridView) findViewById(R.id.gridview);
                imageGrid.setAdapter(new ImageAdapter(this, pathList));
            }
        }
    }

    /**
     * get images and set adapter
     */
    public void refreshView() {
        getFromSdCard();
        imageGrid = (GridView) findViewById(R.id.gridview);
        imageGrid.setAdapter(new ImageAdapter(this, pathList));
    }

    /**
     * reads all profile specific images from the folder and saves them in the pathlist
     */
    public static void getFromSdCard() {
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "growpics" + File.separator + profileName);

        if (folder.isDirectory()) {
            File[] listFile = folder.listFiles();
            pathList.clear();
            for (int i = 0; i < listFile.length; i++) {
                try {
                    pathList.add(0, listFile[i].getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * this is used to read images and create a Bitmapp
     *
     * @param imageUrl the path to the image
     * @param hiRes    true : high resolution, false: a third of high resolution
     * @return a Bitmap from imageUrl
     * @throws Exception
     */
    protected static Bitmap urlImageToBitmap(String imageUrl, boolean hiRes) throws Exception {
        Bitmap result = null;
        if (imageUrl != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (hiRes) {
                options.inSampleSize = 1;
            } else {
                options.inSampleSize = 3;

            }
            result = BitmapFactory.decodeFile(imageUrl, options);

        }
        return result;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);

    }

    /**
     * uses the Animated gif encoder to create a gif
     *
     * @return
     */
    public static byte[] generateGIF() throws IllegalArgumentException {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (pathList == null) {
            File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics" + File.separator + profileName);
            pathList = new ArrayList<>();
            if (folder.isDirectory()) {
                File[] listFile = folder.listFiles();
                for (int i = 0; i < listFile.length; i++) {

                    try {
                        pathList.add(listFile[i].getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (pathList.size() >= 2) {
            Log.i(TAG, "pathlist" + pathList.size());
            for (String s : pathList) {
                try {
                    Bitmap b = GalleryView.urlImageToBitmap(s, false);
                    bitmaps.add(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.setRepeat(0);
            encoder.delay = 50;
            encoder.start(bos);
            for (Bitmap bitmap : bitmaps) {
                encoder.addFrame(bitmap);
            }
            encoder.finish();
            return bos.toByteArray();
        } else {
            throw new IllegalArgumentException();
        }

    }

    /**
     * initiates the writing of a gif to a specified path
     */
    public static void writeGif() {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/growpics/gif.gif");
            outStream.write(generateGIF());
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}