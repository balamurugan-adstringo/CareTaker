package com.hdfc.newzeal.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.views.CustomViewPager;
import com.hdfc.views.RoundedImageView;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GuruDetailsFragment extends Fragment {

    public static RoundedImageView imgButtonCamera;

    public static String strCustomerImgName = "";
    public static String strCustomerImgNameCamera;
    public static Bitmap bitmap = null;
    public static Uri uri;
    private static Thread backgroundThreadCamera;
    private static Handler backgroundThreadHandler;
    private static String strName, strEmail, strConfirmPass, strContactNo, strAddress;
    private static ProgressDialog mProgress = null;
    private EditText editName, editEmail, editPass, editConfirmPass, editContactNo, editAddress;
    private Libs libs;

    public GuruDetailsFragment() {
    }

    public static GuruDetailsFragment newInstance() {
        GuruDetailsFragment fragment = new GuruDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libs = new Libs(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_guru_details, container, false);
        imgButtonCamera = (RoundedImageView) rootView.findViewById(R.id.imageButtonCamera);

        editName = (EditText) rootView.findViewById(R.id.editName);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        editPass = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPass = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editContactNo = (EditText) rootView.findViewById(R.id.editContactNo);
        Button buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);
        editAddress = (EditText) rootView.findViewById(R.id.editAddress);

        mProgress = new ProgressDialog(getActivity());

        strCustomerImgNameCamera = String.valueOf(new Date().getDate() + "" + new Date().getTime()) + ".jpeg";

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libs.selectImage(strCustomerImgNameCamera, GuruDetailsFragment.this, null);
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SignupActivity.strUserId != null && !SignupActivity.strUserId.equalsIgnoreCase("")) {

            editName.setText(SignupActivity.strCustomerName);
            editEmail.setText(SignupActivity.strCustomerEmail);
            editContactNo.setText(SignupActivity.strCustomerContactNo);
            editAddress.setText(SignupActivity.strCustomerAddress);

            backgroundThreadHandler = new BackgroundThreadHandler();
            backgroundThreadCamera = new BackgroundThreadCamera();
            backgroundThreadCamera.start();
        }
    }

    private void validateUser() {

        editName.setError(null);
        editEmail.setError(null);
        editPass.setError(null);
        editConfirmPass.setError(null);
        editContactNo.setError(null);
        //editAddress.setError(null);

        strName = editName.getText().toString().trim();
        strEmail = editEmail.getText().toString().trim();
        String strPass = editPass.getText().toString().trim();
        strConfirmPass = editConfirmPass.getText().toString().trim();
        strContactNo = editContactNo.getText().toString().trim();
        strAddress = editAddress.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strCustomerImgName) && SignupActivity.strUserId.equalsIgnoreCase("")) {
            libs.toast(1, 1, getString(R.string.warning_profile_pic));
            focusView = imgButtonCamera;
            cancel = true;

        } else {

            if (TextUtils.isEmpty(strContactNo)) {
                editContactNo.setError(getString(R.string.error_field_required));
                focusView = editContactNo;
                cancel = true;
            } else if (!libs.validCellPhone(strContactNo)) {
                editContactNo.setError(getString(R.string.error_invalid_contact_no));
                focusView = editContactNo;
                cancel = true;
            }

        /*if (TextUtils.isEmpty(strAddress)) {
            editAddress.setError(getString(R.string.error_field_required));
            focusView = editAddress;
            cancel = true;
        }
*/
            if (!TextUtils.isEmpty(strPass) && libs.isPasswordValid(strPass) && !TextUtils.isEmpty(strConfirmPass) && libs.isPasswordValid(strConfirmPass)) {

                if (!strPass.trim().equalsIgnoreCase(strConfirmPass.trim())) {
                    editConfirmPass.setError(getString(R.string.error_confirm_password));
                    focusView = editConfirmPass;
                    cancel = true;
                }
            }

            if (TextUtils.isEmpty(strConfirmPass)) {
                editConfirmPass.setError(getString(R.string.error_field_required));
                focusView = editConfirmPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strPass)) {
                editPass.setError(getString(R.string.error_field_required));
                focusView = editPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strEmail)) {
                editEmail.setError(getString(R.string.error_field_required));
                focusView = editEmail;
                cancel = true;
            } else if (!libs.isEmailValid(strEmail)) {
                editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;
            }

            if (TextUtils.isEmpty(strName)) {
                editName.setError(getString(R.string.error_field_required));
                focusView = editName;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                //
                mProgress.setMessage(getResources().getString(R.string.loading));
                mProgress.setCancelable(false);
                mProgress.show();

                UserService userService = new UserService(getActivity());

                userService.getUser(strEmail, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        mProgress.dismiss();
                        libs.toast(2, 2, getString(R.string.email_exists));
                    }

                    @Override
                    public void onException(Exception e) {

                        String strMess = "";

                        try {
                            JSONObject jsonObject = new JSONObject(e.getMessage());
                            JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                            strMess = jsonObjectError.getString("message");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        if (strMess.equalsIgnoreCase("Not Found")) {

                            SignupActivity.strUserId = strEmail;
                            CustomViewPager.setPagingEnabled(true);

                            SignupActivity.strCustomerName = strName;
                            SignupActivity.strCustomerEmail = strEmail;
                            SignupActivity.strCustomerContactNo = strContactNo;
                            SignupActivity.strCustomerAddress = strAddress;
                            SignupActivity.strCustomerImg = strCustomerImgName;

                            String strPass = null;
                            try {
                                strPass = AESCrypt.encrypt(Config.string, strConfirmPass);
                            } catch (GeneralSecurityException gSe) {
                                gSe.printStackTrace();
                            }

                            if (strPass != null && !strPass.equalsIgnoreCase(""))
                                SignupActivity.strCustomerPass = strPass;

                            //chk this
                            libs.retrieveDependants();//strEmail
                            AddDependentFragment.adapter.notifyDataSetChanged();

                            libs.retrieveConfirmDependants();
                            ConfirmFragment.adapter.notifyDataSetChanged();
                            mProgress.dismiss();

                            libs.toast(1, 1, getString(R.string.your_details_saved));

                            SignupActivity._mViewPager.setCurrentItem(1);

                            Libs.log(e.getMessage(), "");
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                backgroundThreadHandler = new BackgroundThreadHandler();
                //Libs.toast(1, 1, "Getting Image...");
                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Libs.customerImageUri.getPath();
                        backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            if (imgButtonCamera != null && strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("") && bitmap != null)
                imgButtonCamera.setImageBitmap(bitmap);
        }
    }

    //
    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = libs.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
                    libs.copyInputStreamToFile(is, galleryFile);
                    bitmap = libs.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                    bitmap = libs.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
