package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.MyLinearView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ImagesFragment extends Fragment {

    //private static Handler threadHandler;

    public static Fragment newInstance(Context context, int pos,
                                       float scale) {
        Bundle b = null;
        try {
            b = new Bundle();
            b.putInt("pos", pos);
            b.putFloat("scale", scale);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Fragment.instantiate(context, ImagesFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout l = null;
        try {
            if (container == null) {
                return null;
            }

            l = (LinearLayout)
                    inflater.inflate(R.layout.fragment_images, container, false);
            //RelativeLayout loadingPanel = (RelativeLayout) l.findViewById(R.id.loadingPanel);

            ImageView imageView = (ImageView) l.findViewById(R.id.content);

            Utils utils = new Utils(getActivity());

            int intPosition = this.getArguments().getInt("pos");

            try {

                Utils.log(" 3 ", " IN ");

                //loadingPanel.setVisibility(View.VISIBLE);
                //   Config.dependentModels.add(DependentDetailPersonal.dependentModel);

                //            Bitmap bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                //                    utils.replaceSpace(Config.dependentModels.get(intPosition).getStrDependentID())).getAbsolutePath(),
                //                    Config.intWidth, Config.intHeight);
                //
                //            if (bitmap != null)
                //                imageView.setImageBitmap(bitmap);
                //            else
                //                imageView.setImageBitmap(Utils.noBitmap);

                //            Glide.with(getActivity()).load(Config.dependentModels.get(intPosition).getStrImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                //                   .into(imageView);


                Glide.with(getActivity())
                        .load(Config.dependentModels.get(intPosition).getStrImageUrl())
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.person_icon)
                        .crossFade()
                        .override(Config.intWidth, Config.intHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);

                //imageLoader.displayImage(Config.dependentModels.get(intPosition).getStrImageUrl(), imageView, CareTaker.defaultOptions);

                //loadingPanel.setVisibility(View.GONE);

              /*  threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();*/

            } catch (Exception e) {
                //loadingPanel.setVisibility(View.GONE);
                e.printStackTrace();
            }

            MyLinearView root = (MyLinearView) l.findViewById(R.id.root);
            float scale = this.getArguments().getFloat("scale");
            root.setScaleBoth(scale);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.log(" 4 ", " IN ");

    }

 /*   public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            loadingPanel.setVisibility(View.GONE);
            Utils.log(" 5 ", " IN ");
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageBitmap(Utils.noBitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(intPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);
                Utils.log(Config.dependentModels.get(intPosition).getStrDependentID() + " ~ " +
                        Config.dependentModels.get(intPosition).getStrName(), " Pah ");
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }*/
}
