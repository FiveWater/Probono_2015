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

import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by 수민 오 on 2015-12-20.
 */
public class DeleteActivity extends Activity {
    Button btnDelete;
    EditText edtname, edtphone, edtpass;
    private static final String SERVER_ADDRESS = "http://192.168.200.134:80";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnDelete = (Button)findViewById(R.id.btnDelete);
        edtname=(EditText)findViewById(R.id.name);
        edtphone=(EditText)findViewById(R.id.phonenumber);
        edtpass=(EditText)findViewById(R.id.password);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtname.getText().toString().equals("")||edtphone.getText().toString().equals("")||edtpass.getText().toString().equals("")){
                    Toast.makeText(DeleteActivity.this, "입력오류입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = edtname.getText().toString();
                        String phonenumber = edtphone.getText().toString();
                        String password = edtpass.getText().toString();

                        try{
                            URL url = new URL(SERVER_ADDRESS+"/delete.php?"+"name="+ URLEncoder.encode(name, "UTF-8")+"&phone="+URLEncoder.encode(phonenumber, "UTF-8")+"&password="+URLEncoder.encode(password, "UTF-8"));
                            url.openStream();

                            Toast.makeText(DeleteActivity.this, "회원탈퇴 되었습니다!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DeleteActivity.this, RegisterActivity.class));
                            finish();

                        }catch (Exception e){
                            Log.e("ERROR", e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
