package com.hdfc.caretaker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.hdfc.views.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by Admin on 24-06-2016.
 */

public class DependentDetailPersonal extends AppCompatActivity {
    public static RoundedImageView imgButtonCamera;
    public static DependentModel dependentModel = null;
    public static Uri uri;
    public static String strImageName = "";
    public static Bitmap bitmapImg = null;
    public static String strContactNo, strAddress, strEmail, strDob, relation;
    public static String strDependantName = "";
    public static String dependantImgName = "";
    public static ArrayList<String> dependentNames = new ArrayList<>();
    static Boolean editflag = false;
    static int mPosition = -1;
    private static ProgressDialog mProgress = null;
    private static boolean isCamera = false;
    private static Thread backgroundThreadCamera;
    private static Handler backgroundThreadHandler;
    Button buttonContinue;
    private SearchView searchView;
    private EditText editName, editContactNo, editAddress, editDependantEmail, editTextDate;
    private Spinner spinnerRelation;
    private Utils utils;
    private String strRelation;
    private Context mContext;
    private boolean isSelected = false;
    private PermissionHelper permissionHelper;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            String strDate = Utils.writeFormatActivityYear.format(date);
            //String _strDate = Utils.readFormat.format(date);
            // DependentDetailsMedical.date = date;

            int strAge = Integer.parseInt(utils.getAge(date));
            if (utils.ageValidationDependents(strAge)) {
                // String _strDate = Utils.readFormat.format(date);
                editTextDate.setText(strDate);
            } else {
                editTextDate.setText("");
                utils.toast(2, 2, mContext.getString(R.string.validation_age_dependnets));
            }
           /* iDate = date.getDate();
            iMonth = date.getMonth();
            iYear = date.getYear();*/
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };
    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            bitmapImg = bitmap;
            mProgress.dismiss();

            //Utils.log(strImageName, " strImageName 0 ");
            if (bitmap != null)
                imgButtonCamera.setImageBitmap(bitmap);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionHelper.finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependent_detail_personal);

        utils = new Utils(DependentDetailPersonal.this);
        utils.setStatusBarColor("#2196f3");

        permissionHelper = PermissionHelper.getInstance(DependentDetailPersonal.this);

        isSelected = false;
        editflag = false;
        editName = (EditText) findViewById(R.id.editDependName);
        editContactNo = (EditText) findViewById(R.id.editContNo);
        editAddress = (EditText) findViewById(R.id.editAddr);
        editTextDate = (EditText) findViewById(R.id.editDateofbirth);
        mContext = this;
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -60);
                Date daysBeforeDate = cal.getTime();
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setMaxDate(new Date())
                        .setInitialDate(daysBeforeDate)
                        .build()
                        .show();
            }
        });

        spinnerRelation = (Spinner) findViewById(R.id.editRelations);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Config.strRelations);

        if (spinnerRelation != null) {
            spinnerRelation.setAdapter(adapter);

            spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    strRelation = (String) parent.getItemAtPosition(position);
                    relation = spinnerRelation.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        editDependantEmail = (EditText) findViewById(R.id.editDependEmail);
        mProgress = new ProgressDialog(DependentDetailPersonal.this);

        buttonContinue = (Button) findViewById(R.id.btn1continue);

        imgButtonCamera = (RoundedImageView) findViewById(R.id.img1ButtonCamera);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);

        if (buttonBack != null) {
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        if (buttonContinue != null) {
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateDependant();

                }
            });
        }

        Calendar calendar = Calendar.getInstance();
        dependantImgName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (!isFinishing()) {

                        permissionHelper.verifyPermission(
                                new String[]{getString(R.string.permission_storage_rationale)},
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        utils.selectImage(dependantImgName, null,
                                                DependentDetailPersonal.this);
                                        isCamera = true;
                                    }

                                    @Override
                                    public void permissionRefused() {
                                    }
                                }
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setupSearchView();

        //utils.setStatusBarColor("#cccccc");

        try {
            Bundle getBundle = getIntent().getExtras();
            if (getBundle != null) {
                editflag = getBundle.getBoolean("editflag");
                mPosition = getBundle.getInt("childposition");
            } else {
                editflag = false;
                mPosition = -1;
                dependentModel = null;
            }

            if (editflag && mPosition > -1) {

                //if (Config.dependentModel != null) {
                dependentModel = Config.dependentModels.get(mPosition);
                // }

                editDependantEmail.setEnabled(false);
                editDependantEmail.setFocusable(false);
                editDependantEmail.setFocusableInTouchMode(false);
                editDependantEmail.setKeyListener(null);
                editDependantEmail.setClickable(false);

                editContactNo.setEnabled(false);
                editContactNo.setFocusableInTouchMode(false);
                editContactNo.setFocusable(false);
                editContactNo.setKeyListener(null);
                editContactNo.setClickable(false);
            }

            if (!isFinishing()) {

                permissionHelper.verifyPermission(
                        new String[]{getString(R.string.permission_storage_rationale),
                                getString(R.string.permission_contacts_rationale)},
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS},
                        new PermissionCallback() {
                            @Override
                            public void permissionGranted() {
                            }

                            @Override
                            public void permissionRefused() {
                                utils.toast(2, 2, getString(R.string.permission_general_rationale));
                            }
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBack() {
        Intent dashboardIntent = new Intent(DependentDetailPersonal.this,
                DashboardActivity.class);
        Config.intSelectedMenu = Config.intAccountScreen;
        startActivity(dashboardIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.setIconified(true);

        }
        goBack();

    }

    private void validateDependant() {
        editName.setError(null);
        editContactNo.setError(null);
        editAddress.setError(null);
        editTextDate.setError(null);
        //editRelation.setError(null);

        strDependantName = editName.getText().toString().trim();
        strContactNo = editContactNo.getText().toString().trim();
        strAddress = editAddress.getText().toString().trim();
        strEmail = editDependantEmail.getText().toString().trim();
        strDob = editTextDate.getText().toString().trim();
        relation = spinnerRelation.getSelectedItem().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strImageName) && dependentModel == null) {
            utils.toast(2, 2, getString(R.string.warning_profile_pic));
            focusView = imgButtonCamera;
            cancel = true;
        }

        if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
            strImageName = strImageName.trim();
        }

        if (editflag && mPosition > -1) {

        } else {
            if (TextUtils.isEmpty(strContactNo)) {
                editContactNo.setError(getString(R.string.error_field_required));
                focusView = editContactNo;
                cancel = true;
            } else if (!utils.validateMobile(strContactNo)) {
                editContactNo.setError(getString(R.string.error_invalid_contact_no));
                focusView = editContactNo;
                cancel = true;
            }
        }
        if (TextUtils.isEmpty(relation) || relation.equalsIgnoreCase("Select a Relation")) {
                /*editRelation.setError(getString(R.string.error_field_required));
                focusView = editRelation;*/
            cancel = true;
            utils.toast(2, 2, getString(R.string.select_relation));
        }

        if (TextUtils.isEmpty(strAddress)) {
            editAddress.setError(getString(R.string.error_field_required));
            focusView = editAddress;
            cancel = true;
        }

//        if (TextUtils.isEmpty(strContactNo)) {
//            editContactNo.setError(getString(R.string.error_field_required));
//            focusView = editContactNo;
//            cancel = true;
//        } else if (!utils.validateMobile(strContactNo)) {
//            editContactNo.setError(getString(R.string.error_invalid_contact_no));
//            focusView = editContactNo;
//            cancel = true;
//        }

        if (TextUtils.isEmpty(strDob)) {
            editTextDate.setError(getString(R.string.error_field_required));
            focusView = editTextDate;
            cancel = true;
        }

        if (TextUtils.isEmpty(strEmail)) {
            editDependantEmail.setError(getString(R.string.error_field_required));
            focusView = editDependantEmail;
            cancel = true;
        } else {
            if (!utils.isEmailValid(strEmail)) {
                editDependantEmail.setError(getString(R.string.error_invalid_email));
                focusView = editDependantEmail;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(strDependantName)) {
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }


        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
        } else {
            try {
                // if (utils.isConnectingToInternet()) {

                mProgress.setMessage(getResources().getString(R.string.text_loader_processing));
                mProgress.setCancelable(false);
                mProgress.show();

                boolean bContinue = true;

                if (editflag) {
                    // dependentModel = new DependentModel();

                    //strDependantName, strRelation, strImageName, "", "",
                    // strAddress, strContactNo, strEmail, "", 0

                    dependentModel.setStrName(strDependantName);
                    dependentModel.setStrRelation(relation);
                    dependentModel.setStrImagePath(strImageName);
                    dependentModel.setStrAddress(strAddress);
                    dependentModel.setStrContacts(strContactNo);
                    dependentModel.setStrEmail(strEmail);
                    dependentModel.setStrDob(strDob);

                    //
                    dependentModel.setStrImagePath(strImageName);


                } else {

                    if (!Config.dependentNames.contains(strContactNo)) {
                        dependentModel = new DependentModel();

                        dependentModel.setStrName(strDependantName);
                        dependentModel.setStrRelation(relation);
                        dependentModel.setStrImagePath(strImageName);
                        dependentModel.setStrAddress(strAddress);
                        dependentModel.setStrContacts(strContactNo);
                        dependentModel.setStrEmail(strEmail);
                        dependentModel.setStrDob(strDob);

                        Config.dependentNames.add(strContactNo);

                    } else {
                        utils.toast(1, 1, getString(R.string.dpndnt_details_not_saved));
                        bContinue = false;
                    }


                       /* if (dependentModel != null &&
                                Config.dependentNames.contains(strContactNo)
                                && dependentModel.getStrContacts().equalsIgnoreCase(strContactNo)) {*/


                        /*} else {
                            utils.toast(1, 1, getString(R.string.dpndnt_details_not_saved));
                            bContinue=false;
                        }*/
                }

                mProgress.dismiss();

                if (bContinue) {


                    strImageName = "";
                    Intent selection = new Intent(DependentDetailPersonal.this,
                            DependentDetailsMedical.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("editflag", true);
                    bundle.putInt("childposition", mPosition);
                    selection.putExtras(bundle);
                    startActivity(selection);
                    finish();
                }

//                }else {
//                    if (mProgress.isShowing())
//                        mProgress.dismiss();
//                    utils.toast(2, 2, getString(R.string.warning_internet));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView1);

        ComponentName cn = new ComponentName(this, DependentDetailPersonal.class);

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(cn);
        searchView.setSearchableInfo(searchableInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {

        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            //handles suggestion clicked query
            readContacts(intent);

        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            editName.setText(getResources().getString(R.string.search_contacts));
        }
    }

    public void readContacts(Intent intent) {

        try {

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(intent.getData(), new String[]{ContactsContract.Contacts._ID,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_URI,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER}, null, null, null);

            String phone = "";
            String emailContact = null;
            String image_uri = "";
            String id = "";
            String name = "";

            editName.setText("");
            editContactNo.setText("");
            editDependantEmail.setText("");

            imgButtonCamera.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.camera));

            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    image_uri = cur.getString(cur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                phone =
                                        pCur.getString(pCur.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            pCur.close();
                        }
                    } else phone = "";
                }

                Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id},
                        null);

                if (emailCur != null && emailCur.getCount() > 0) {
                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emailCur.close();
                }

                if (image_uri != null) {
                    try {
                        mProgress.setMessage(getString(R.string.text_loader_processing));
                        mProgress.show();
                        uri = Uri.parse(image_uri);
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        Thread backgroundThread = new BackgroundThread();
                        backgroundThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    imgButtonCamera.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.camera));
                    strImageName = "";
                }

                if (!cur.isClosed())
                    cur.close();

            } else {
                name = "";
                emailContact = "";
                phone = "";
                strImageName = "";
            }


            try {
                if (phone != null && phone.length() > 0) {
                    phone = phone.replaceAll(" ", "");
                    editContactNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(phone.length())});
                }
                editContactNo.setText(phone);
            } catch (Exception e) {
                e.printStackTrace();
            }
            editName.setText(name);

            editDependantEmail.setText(emailContact);

            searchView.setFocusable(false);
            searchView.clearFocus();

            searchView.setIconified(true);
            searchView.setIconified(true);

            editName.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Utils.log(strImageName, " strImageName 1 ");


        if (Config.customerModel != null && !Config.customerModel.getStrName().equalsIgnoreCase("")
                && dependentModel != null && !isSelected) {
            //&& !strDependantName.equalsIgnoreCase("")

            editName.setText(dependentModel.getStrName());
            try {
                if (dependentModel.getStrContacts() != null && dependentModel.getStrContacts().length() > 0) {

                    editContactNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(dependentModel.getStrContacts().length())});
                }
                editContactNo.setText(dependentModel.getStrContacts());
            } catch (Exception e) {
                e.printStackTrace();
            }

            editAddress.setText(dependentModel.getStrAddress());
            //editRelation.setText(dependentModel.getStrRelation());
            editDependantEmail.setText(dependentModel.getStrEmail());
            editTextDate.setText(dependentModel.getStrDob());

                /*drawable = Config.dependentModel.getStrImagePath();
                int resID = getResources().getIdentifier(drawable, "drawable", getPackageName());
                //imgButtonCamera.setImageResource(resID);

                Drawable drawable = getResources().getDrawable(resID);
                imgButtonCamera.setImageDrawable(drawable);*/

            //
            spinnerRelation.setSelection(Config.strRelationsList.indexOf(dependentModel.getStrRelation()));
            //
            strImageName = dependentModel.getStrImagePath();
            if (strImageName == null || strImageName.length() == 0 || strImageName.equalsIgnoreCase("")) {
                strImageName = "";
                loadImageSimpleTarget(dependentModel.getStrImageUrl());
            } else {
                loadImageSimpleTarget(strImageName);
            }

            //!strDependantName.equalsIgnoreCase("") &&
            if (!isCamera) {

//                File fileImage = utils.getInternalFileImages(dependentModel.getStrDependentID());
//
//                strImageName = fileImage.getAbsolutePath();
//                backgroundThreadHandler = new BackgroundThreadHandler();
//                backgroundThreadCamera = new BackgroundThreadCamera();
//                backgroundThreadCamera.start();


            } else isCamera = false;
        } else isCamera = false;

        //} else isCamera = false;


    }

    private void loadImageSimpleTarget(String url) {

        try {
            Glide.with(DependentDetailPersonal.this)
                    .load(url)
                    .asBitmap()
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            if (dependentModel != null && dependentModel.getStrImageUrl() != null && dependentModel.getStrImageUrl().length() > 0) {

                                loadImageSimpleTarget(dependentModel.getStrImageUrl());
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .override(Config.intWidth, Config.intHeight)
                    .transform(new CropCircleTransformation(DependentDetailPersonal.this))
                    .placeholder(R.drawable.person_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageSimpleTarget(Uri url) {

        try {
            Glide.with(DependentDetailPersonal.this)
                    .load(url)
                    .asBitmap()
                    .centerCrop()
                    .override(Config.intWidth, Config.intHeight)
                    .transform(new CropCircleTransformation(DependentDetailPersonal.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.person_icon)
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        permissionHelper.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                mProgress.setMessage(getString(R.string.text_loader_processing));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strImageName = Utils.customerImageUri.getPath();
                        isSelected = true;
                        showImageUsingCamera();
//                        backgroundThreadHandler = new BackgroundThreadHandler();
//                        backgroundThreadCamera = new BackgroundThreadCamera();
//                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            isSelected = true;
                            showImageUsingGallery();
//                            backgroundThreadHandler = new BackgroundThreadHandler();
//                            backgroundThread = new BackgroundThread();
//                            backgroundThread.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showImageUsingGallery() {
        try {
            if (uri != null) {
                Calendar calendar = Calendar.getInstance();
                String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

                File galleryFile = utils.createFileInternalImage(strFileName);
                strImageName = galleryFile.getAbsolutePath();
                InputStream is = getContentResolver().openInputStream(uri);
                utils.copyInputStreamToFile(is, galleryFile);
//                utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
//                bitmapImg = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);

                loadImageSimpleTarget(uri);
            }

        } catch (IOException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showImageUsingCamera() {
        try {
            if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                loadImageSimpleTarget(strImageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {
                    Calendar calendar = Calendar.getInstance();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    InputStream is = getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, galleryFile);
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    bitmapImg = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    bitmapImg = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            //Utils.log(strImageName, " strImageName 0 ");
            if (bitmapImg != null)
                imgButtonCamera.setImageBitmap(bitmapImg);
            /*else
                imgButtonCamera.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.camera_icon));*/
        }
    }
}
