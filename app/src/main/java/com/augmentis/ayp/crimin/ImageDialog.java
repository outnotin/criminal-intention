package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;

/**
 * Created by Noppharat on 8/4/2016.
 */
public class ImageDialog extends DialogFragment implements DialogInterface.OnClickListener{

    private ImageView imageView;
    private static File _photoFile;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog, null);
        imageView = (ImageView) v.findViewById(R.id.image_view_dialog);

        Bitmap bitmap = PictureUtils.getScaleBitmap(_photoFile.getPath(), getActivity());
        imageView.setImageBitmap(bitmap);



        builder.setView(v);
        builder.setPositiveButton(android.R.string.ok, this);

        return builder.create();

    }

    public static ImageDialog newInstance(File photoFile) {
        ImageDialog imd = new ImageDialog();
        Bundle args = new Bundle();
        _photoFile = photoFile;
        args.putSerializable("ARG_IMAGE", photoFile);
        imd.setArguments(args);
        return imd;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
