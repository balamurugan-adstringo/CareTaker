package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private Utils utils;
    private SessionManager sessionManager;
    private Button btnLovedOne, btnGoToWho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new Utils(MainActivity.this);
        utils.setStatusBarColor("#2196f3");
        btnLovedOne = (Button) findViewById(R.id.buttonLovedOne);
        btnGoToWho = (Button) findViewById(R.id.buttonGoToWho);


        //CrashLogger.getInstance().init(MainActivity.this);
//        try {
//            CareTaker.dbCon = DbCon.getInstance(getApplicationContext());
//        } catch (Exception e) {
//            e.getMessage();
//        }
        try {

            sessionManager = new SessionManager(MainActivity.this);
            if (sessionManager.isLoggedIn()) {
                btnGoToWho.setVisibility(View.GONE);
                btnLovedOne.setVisibility(View.GONE);

            }
            new LoadDataTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


       /* try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.app_bg, Config.intScreenWidth, Config.intScreenHeight));

                //CrashLogger.getInstance().init(MainActivity.this);
            }

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }*/
    }


    public void goToDashboard() {

        Config.intSelectedMenu = Config.intDashboardScreen;


        Config.boolIsLoggedIn = true;
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    public class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CareTaker.dbCon = DbCon.getInstance(getApplicationContext());
                //exportDB();
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                if (sessionManager.isLoggedIn()) {
                    Config.strUserName = sessionManager.getEmail();
                    utils.fetchCustomer(new ProgressDialog(MainActivity.this),1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.hdfc.caretaker" +"/databases/"+ DbHelper.DATABASE_NAME;
        String backupDBPath = DbHelper.DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void goToWho(View v) {
      /*  Intent selection = new Intent(MainActivity.this, CareSelectionActivity.class);
        startActivity(selection);*/

        Intent selection = new Intent(MainActivity.this, ActivityGuruPersonalInfo.class);
        startActivity(selection);
    }

    public void goToLogin(View v) {
        Intent selection = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(selection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (CareTaker.dbCon != null) {
            CareTaker.dbCon.close();
        }

        moveTaskToBack(true);
        finish();
    }
}
