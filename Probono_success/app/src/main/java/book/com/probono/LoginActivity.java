package book.com.probono;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 수민 오 on 2015-08-24.
 */
public class LoginActivity extends Activity {
    public static String uid;
    //public static String name;

    ViewFlipper Vf;
    Button btnregister, btnlogin;
    //EditText inputPHONE, inputPW;
    EditText inputNAME, inputPW;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    public static String name;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnlogin = (Button)findViewById(R.id.btnLogin);
        btnregister = (Button)findViewById(R.id.btnLinkToRegisterScreen);
        inputNAME = (EditText)findViewById(R.id.name);
        inputPW = (EditText)findViewById(R.id.password);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this, "","로딩중...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        login();
                        Looper.loop();
                    }
                }).start();
            }
        });
    }

    void login(){
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://192.168.200.134:80/logcheck.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            String str = new String(inputNAME.getText().toString().getBytes("utf-8"), "8859_1");
            //nameValuePairs.add(new BasicNameValuePair("name",inputNAME.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("name",str));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response" + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });

            if(response.equalsIgnoreCase("no such user found")){
                Toast.makeText(LoginActivity.this, "로그인 실패하였습니다!", Toast.LENGTH_SHORT).show();
            }
            else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그인 성공하였습니다!", Toast.LENGTH_SHORT).show();
                        name = response;
                        Log.e(null, name);
                    }
                });
                startActivity(new Intent(LoginActivity.this, AlbumActivity.class));
                finish();
            }
        }catch (Exception e){
            dialog.dismiss();
            System.out.println("Exception:"+e.getMessage());
        }
    }
}





