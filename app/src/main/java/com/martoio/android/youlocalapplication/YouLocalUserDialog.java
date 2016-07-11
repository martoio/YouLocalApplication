package com.martoio.android.youlocalapplication;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Martin on 7/9/2016 for YouLocalApplication.
 */
public class YouLocalUserDialog extends DialogFragment {

    private CircularImageView mAvatar;
    private TextView mAbout;
    private TextView mName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments(); //get the arguments sent from the login screen;

        //inflate the dialog layout;
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.user_dialog, null);

        //initialize the views
        initializeViews(args, v);

        // download + attach The Image in the CircularImageView
        new DownloadImage(mAvatar)
                .execute(args.getString("avatar"));

        //Create the user AlertDialog
        AlertDialog dialog =  new AlertDialog.Builder(getActivity())
                .setView(v).create();
        //Set the animation of the AlertDialog to the sliding up animation defined in styles.xml
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //Additional settings needed to get the rounded corner shape to work;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;

    }
    /*
    * Convenience method to separate out the view initialization
    * */
    private void initializeViews(Bundle args, View v) {
        mAvatar = (CircularImageView) v.findViewById(R.id.avatar);
        mAbout = (TextView) v.findViewById(R.id.about_user);
        mName = (TextView) v.findViewById(R.id.username);

        mAbout.setText(args.getString("about"));
        mName.setText(args.getString("name"));
    }

    /*
    * Downloading the Image from the web and assigning it to the CircularImageView source;
    * */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView mImage;

        //Constructor
        public DownloadImage(ImageView mImage) {
            this.mImage = mImage;
        }

        //Download the user avatar into a Bitmap
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        //assign the Bitmap into the CircularImageView source;
        protected void onPostExecute(Bitmap result) {
            mImage.setImageBitmap(result);
        }
    }

}
