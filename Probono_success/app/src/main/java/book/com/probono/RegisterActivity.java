package book.com.probono;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;



/**
 * Created by 수민 오 on 2015-08-24.
 */
public class RegisterActivity extends Activity {
    Button btnRegister, btnLinkToLogin;
    EditText edtname, edtphone, edtpass;
    public static String input;
    private static final String SERVER_ADDRESS = "http://192.168.200.134:80";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button)findViewById(R.id.btnLinkToLoginScreen);
        edtname=(EditText)findViewById(R.id.name);
        edtphone=(EditText)findViewById(R.id.phonenumber);
        edtpass=(EditText)findViewById(R.id.password);

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(edtname.getText().toString().equals("")||edtphone.getText().toString().equals("")||edtpass.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this, "입력오류입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = edtname.getText().toString();
                        //input = edtname.getText().toString();
                        String phonenumber = edtphone.getText().toString();
                        String password = edtpass.getText().toString();

                        try{
                            URL url = new URL(SERVER_ADDRESS+"/sos.php?"+"name="+ URLEncoder.encode(name, "UTF-8")+"&phone="+URLEncoder.encode(phonenumber, "UTF-8")+"&password="+URLEncoder.encode(password, "UTF-8"));
                            url.openStream();

                            String result = getXmlData("insertresult.xml", "result");

                            Log.e(null, "3");

                            if(result.equals("1")){
                                Toast.makeText(RegisterActivity.this, "로그인 창으로 이동합니다!",Toast.LENGTH_SHORT).show();
                                edtname.setText("");
                                edtphone.setText("");
                                edtpass.setText("");
//                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "가입에 실패하셨습니다!", Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }catch (Exception e){
                            Log.e("ERROR", e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private String getXmlData(String filename, String str){
        String rss = SERVER_ADDRESS + "/";
        String ret = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(rss+filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    if(xpp.getName().equals(str)){
                        ret = xpp.nextText();
                    }
                }
                eventType = xpp.next();
            }
        }catch (Exception e){
            Log.e("Error", e.getMessage());
        }
        return ret;
    }
}
