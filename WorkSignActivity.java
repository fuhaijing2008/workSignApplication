package com.example.esc.worksigninapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class WorkSignActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mTokenView;
    private EditText mLatView,mLngView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    public String SIGN_IN = "checkin";
    public String SIGN_OUT = "checkout";
    String type = "checkin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        // Set up the login form.
        mTokenView = (AutoCompleteTextView) findViewById(R.id.tvToken);
        populateAutoComplete();

        mLatView = (EditText) findViewById(R.id.tvLat);
        mLngView = (EditText) findViewById(R.id.tvLng);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
              //  attemptLogin();

                attemptLogin2();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        mLatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandomLat();
            }
        });
        mLngView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandomLng();
            }
        });

        setDafultToken();
        setRandomLat();
        setRandomLng();
        setSignType();

    }

    private void attemptLogin2() {

        String token = mTokenView.getText().toString();
        String lat = mLatView.getText().toString();
        String lng = mLngView.getText().toString();
        String uri  =NetConfig.getSignUri(token,type,lat,lng);
        saveTokenToPerfercen(token);
        Intent intent = new Intent(WorkSignActivity.this, SignWebviewActivity.class);
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    private void setSignType() {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if(hour>12) {
            type = SIGN_OUT;
        }else
            type = SIGN_IN;
        mEmailSignInButton.setText(type);
    }

    private void setDafultToken(){
        String token  = readTokenFromPerference();
        mTokenView.setText(token);
    }
    private void saveToken(String token){
        saveTokenToPerfercen(token);
    }

    private void setRandomLng() {
        String lng = LatLngGenerateUtils.generateRandomLng();
        mLngView.setText(lng);
    }

    private void setRandomLat() {
        String lat = LatLngGenerateUtils.generateRandomLat();
        mLatView.setText(lat);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mTokenView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String token = mTokenView.getText().toString();
        String lat = mLatView.getText().toString();
        String lng = mLngView.getText().toString();


        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.

         mAuthTask = new UserLoginTask(token, lat,lng);
         mAuthTask.execute((Void) null);

    }

      @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(WorkSignActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mTokenView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        private final String lat;
        private final String lng;

        UserLoginTask(String token2, String lat2,String lng2) {
            token = token2;
            lat = lat2;
            lng = lng2;
        }
        String result = "ddd";
        @Override
        protected Boolean doInBackground(Void... params) {
                String uri2 ="https://www.baidu.com";
                try {
                    result =token+lat+lng;
                    saveTokenToPerfercen(token);
                    String uri  =NetConfig.getSignUri(token,type,lat,lng);
                    result =  requestByGet(uri2,"UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            if(result.contains("116"))
                return true;
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Toast.makeText(WorkSignActivity.this,"成功"+result,Toast.LENGTH_LONG).show();

            }else
                Toast.makeText(WorkSignActivity.this,"失败"+result,Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


    // Get方式请求
    public static String requestByGet(String server_address,String ucode) throws Exception {
        String path =server_address;
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        String TAG_GET = null ;
        //urlConn.setRequestMethod("GET");
        String data = null ;
        // 判断请求是否成功
        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // 获取返回的数据
            data =InputStreamTOString(urlConn.getInputStream(),ucode);// readStream(urlConn.getInputStream());
        } else {
            //Log.i("ss", "Get方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
        return data;
    }

    /**
     * 将InputStream转换成某种字符编码的String
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in, String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while((count = in.read(data,0,1024)) != -1)
            outStream.write(data, 0, count);
        data = null;
        return new String(outStream.toByteArray(),encoding);
    }
    private String readTokenFromPerference(){
        return SaveToeknPerferenceUtils.getInstance(this).getLoginName();
    }

    private void saveTokenToPerfercen(String token) {
        SaveToeknPerferenceUtils.getInstance(this).SetLoginName(token);
    }
}

