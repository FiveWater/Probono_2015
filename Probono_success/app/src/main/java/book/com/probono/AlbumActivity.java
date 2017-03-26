package book.com.probono;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by ���� �� on 2015-09-04.
 */
public class AlbumActivity extends Activity{

    private static int RESULT_LOAD_IMG = 1;
    private Button btnUpload;
    private ImageView imgView;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    Uri imageUri;
    private ImageButton btnPhone;
    private TextView text;
    private ImageButton settings;

    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    MediaPlayer mp=new MediaPlayer();
    RegisterActivity register = new RegisterActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //사용자이름에맞게 변경
        text = (TextView) findViewById(R.id.topText);
        text.setText(LoginActivity.name+" 님의 공간");

        //설정
        settings = (ImageButton)findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialogSelectOption();
            }
        });



        imgView = (ImageView) findViewById(R.id.imgView);
        btnUpload = (Button) findViewById(R.id.btnUpload);
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bitmap == null) {
//                    Toast.makeText(getApplicationContext(),
//                            "Please select image", Toast.LENGTH_SHORT).show();
//                } else {
//                    dialog = ProgressDialog.show(AlbumActivity.this, "Uploading",
//                            "Please wait...", true);
//                    //openGallery(RESULT_LOAD_IMG);
//                }
//            }
//        });
//        btnPhone = (ImageButton)findViewById(R.id.btnPhone);
//        btnPhone.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent intent = new Intent(AlbumActivity.this, AddFriendActivity.class);
//                startActivity(intent);
//            }
//        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            spinnerOption();
        }
    });

    }

    private void spinnerOption(){
        final String[] choice = new String[]{"사진", "동영상", "메모"};

        new AlertDialog.Builder(this).setTitle("").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(choice[which].equalsIgnoreCase("사진")){
                    Toast.makeText(AlbumActivity.this, "갤러리에 접근합니다~", Toast.LENGTH_SHORT).show();
                    openGallery();
                }
                else if(choice[which].equalsIgnoreCase("동영상")){

                }
                else if(choice[which].equalsIgnoreCase("메모")){

                }
            }
        }).setNegativeButton("", null).show();
    }

    public void openGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(0){
            case 0:
                try{
                    Uri imgUri = data.getData();
                    Bitmap selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }


    private void dialogSelectOption(){
        final String items[] = {"로그아웃", "탈퇴"};
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("회원설정");
        ab.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(AlbumActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });
        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equalsIgnoreCase("로그아웃")){
                    //로그아웃
                    logout();
                }
                else if(items[which].equalsIgnoreCase("탈퇴")){
                    //탈퇴
//                    ab.setTitle("탈퇴확인").setMessage("정말 탈퇴하시겠습니까?").setCancelable(false)
//                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //탈퇴
//                                    startActivity(new Intent(AlbumActivity.this, DeleteActivity.class));
//                                    finish();
//                                }
//                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //취소버튼클릭시
//                            dialog.cancel();
//                        }
//                    });
                    startActivity(new Intent(AlbumActivity.this, DeleteActivity.class));
                    finish();
                }
            }
        });
        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = ab.create();
        dialog.show();
    }

    void logout(){
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://192.168.200.134:80/logout.php");
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response" + response);
            Toast.makeText(AlbumActivity.this, "로그아웃 성공하였습니다!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AlbumActivity.this, LoginActivity.class));
            finish();
        }catch (Exception e){
            dialog.dismiss();
            System.out.println("Exception:"+e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()) {
            case  R.id.GPS :
                break;
            case R.id.logout :
                break;
            case R.id.delete :
                break;
         }
        return true;
    }
}