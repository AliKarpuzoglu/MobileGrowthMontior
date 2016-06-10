package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.ExportView;
import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.GraphListView;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.GraphView;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.CameraView;

import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.PreCameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * ProfileView.Activity
 * Show selected Profiles - and Profile functions
 */

public class ProfileView extends BaseActivity {

    private int profile_Id;
    private int age;
    private ImageButton mProfileImage;
    private DbHelper dbHelper;
    private ProfileData profile;

    /**
     * Support is set to toolbar. Profile is fetched from database. Profile data is set to text
     * views.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_view);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        dbHelper = DbHelper.getInstance(getApplicationContext());
        profile = dbHelper.getProfile(profile_Id);

        TextView tvFirstname = (TextView) findViewById(R.id.tv_firstname);
        tvFirstname.setText(profile.firstname + ",");

        mProfileImage = (ImageButton) findViewById(R.id.ib_profilepic);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ImageView imGender = (ImageView) findViewById(R.id.iv_gender);
        if (profile.sex == 0) {
            Drawable dw = getApplicationContext().getResources().getDrawable(R.drawable.female_24);
            imGender.setImageDrawable(dw);
        } else {
            Drawable dw = getApplicationContext().getResources().getDrawable(R.drawable.male_24);
            imGender.setImageDrawable(dw);
        }

        TextView tvAge = (TextView) findViewById(R.id.tv_age);
        age =  Utils.getAge(profile.birthday);
        tvAge.setText(Integer.toString(age));
        setMeasurementTextViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (profile.profilepic != null) { // if profilepic is null it keeps the drawable
            Bitmap resizedBitmap = BitmapFactory.decodeFile(profile.profilepic);
            if (resizedBitmap.getWidth() > resizedBitmap.getHeight()) {
                resizedBitmap = Utils.rotateBitmap(resizedBitmap, 270);
            } else {
                resizedBitmap = getTheProperThumbnailBitmap(resizedBitmap);

            }

            mProfileImage.setImageBitmap(resizedBitmap);
        }
    }

    /**
     * Latest measurement is fetched from database and data is set to textviews in profile view.
     */
    private void setMeasurementTextViews() {

        MeasurementData measurementData = dbHelper.getLatestMeasurement(profile_Id);
        if (measurementData != null) {
            TextView tvLastMeasurement = (TextView) findViewById(R.id.tv_date_last_measurement);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar date = new GregorianCalendar();
                date.setTime(format.parse(measurementData.date));
                String text = format.format(date.getTime());
                tvLastMeasurement.setText(text);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView tvWeight = (TextView) findViewById(R.id.tv_weight);
            tvWeight.setText(measurementData.weight + " kg");

            TextView tvHeight = (TextView) findViewById(R.id.tv_height);
            tvHeight.setText(measurementData.height/100.0 + " m");
        }

    }

    /**
     * Selects photo to be displayed as profile picture. User can choose whether to take a photo,
     * choose a picture from the gallery or cancel action.
     */
    private void selectImage() {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileView.this);
        builder.setTitle(getString(R.string.add_picture));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.take_photo))) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 1);
                    }
                } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_view, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_profile) {
            dbHelper.deleteProfile(profile_Id);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
              /*  Uri selectedCamImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedCamImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String pictureCamPath = cursor.getString(columnIndex);
                cursor.close();*/
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
              //  mImageView.setImageBitmap(imageBitmap);

           //     dbHelper.setProfilePic(profile_Id, pictureCamPath);
                //Bitmap camBitmap = BitmapFactory.decodeFile(pictureCamPath);
                imageBitmap = getTheProperThumbnailBitmap(imageBitmap);

                mProfileImage.setImageBitmap(imageBitmap);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                dbHelper.setProfilePic(profile_Id, picturePath);
                Bitmap originalBitmap = BitmapFactory.decodeFile(picturePath);
                Bitmap resizedBitmap = getTheProperThumbnailBitmap(originalBitmap);

                if (resizedBitmap.getWidth() > resizedBitmap.getHeight()) {
                    resizedBitmap = Utils.rotateBitmap(resizedBitmap, 270);
                } else {
                    resizedBitmap = getTheProperThumbnailBitmap(resizedBitmap);

                }

                mProfileImage.setImageBitmap(resizedBitmap);


            }
        }else if (requestCode == 3) {
            setMeasurementTextViews();
        }
    }

    private int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

   private Bitmap getTheProperThumbnailBitmap(Bitmap originalBitmap) {
        int dimension = getSquareCropDimensionForBitmap(originalBitmap);

        Bitmap resizedBitmap = ThumbnailUtils.extractThumbnail(originalBitmap, dimension, dimension);

        resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, 200, 200, false);
        return resizedBitmap;
    }

    /**
     * OnClick method to start CameraView Activity
     *
     * @param view
     */
    public void startCamera(View view) {
        Intent intent = new Intent(this, PreCameraView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start GraphView Activity
     *
     * @param view
     */
    public void startGraph(View view) {
        Intent intent = new Intent(this, GraphListView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start GalleryView Activity
     *
     * @param view
     */
    public void startGallery(View view) {
        Intent intent = new Intent(this, GalleryView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start MeasurementView Activity
     *
     * @param view
     */
   public void startMeasurement(View view) {
        Intent intent = new Intent(this, MeasurementView.class);
        Log.v("ProfileView -> Measu", " " + profile_Id);
        intent.putExtra("profile_Id", profile_Id);
        intent.putExtra("profileAge", age);
        startActivityForResult(intent, 3);
    }

    /**
     * onClick method to start ExportView Activity
     *
     * @param view
     */
    public void startExport(View view) {
        Intent intent = new Intent(this, ExportView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainView.class);
        startActivity(i);
    }
}