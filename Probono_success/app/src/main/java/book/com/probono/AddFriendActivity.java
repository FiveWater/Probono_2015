package book.com.probono;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.jar.Attributes;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by 수민 오 on 2015-09-22.
 */
public class AddFriendActivity extends Activity {

    String tag = null;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    //LoginActivity l = new LoginActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_addfriend);

        //주소록 URI
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //검색할 컬럼 정하기
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        //쿼리 날려서 커서 얻기
        String[] selectionArgs = null;
        //정렬
        //String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + "COLLATE LOCALIZED ASC";
        //Cursor cursor = managedQuery(uri, projection, null, null, null);
        Cursor cursor = managedQuery(uri, projection, null, selectionArgs, null);

        int end = cursor.getCount(); // 전화번호부의 갯수 세기
        Log.d(tag, "end = " + end);

        //String[] name = new String[end]; // 전화번호부의 이름을 저장할 배열 선언
        String[] number = new String[end]; //전화번호부의 번호를 저장할 배열 선언

        int count = 0;
        if (cursor.moveToFirst()) {

            //이름, 번호 저장할 배열 선언
            Map<String, Object> hmap = null;
            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            // 컬럼명으로 컬럼 인덱스 찾기
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER);

            do {
                // 요소값 얻기
                int id = cursor.getInt(idIndex);
                //name[count] = cursor.getString(nameIndex);
                number[count] = cursor.getString(phoneIndex);
                // LogCat에 로그 남기기
                //Log.d(tag, "id=" + id + ", name[" + count + "]=" + name[count]);
                Log.d(tag, "id=" + id + ", number[" + count + "]=" + number[count]);

                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                while (phones.moveToNext()) {
                    number[count] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            // do something with the Home number here...
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            // do something with the Mobile number here...
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            // do something with the Work number here...
                            break;
                    }
                }
                Log.d(tag, "id=" + id + ", number[" + count + "]=" + number[count]);

                hmap = new HashMap<String, Object>();
                //hmap.put("one", name[count]);
                hmap.put("two", number[count]);
                list.add(hmap);

                count++;

            } while (cursor.moveToNext() || count > end);
            try {
                CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream("/sdcard/phone.txt"), "EUC-KR") );
                try {
                    for (Map<String, Object> m : list) {
                        //배열을 이용하여 row 를 CSVWriter 객체에 write
                        //cw.writeNext(new String[]{String.valueOf(m.get("one")), String.valueOf(m.get("two"))});
                        cw.writeNext(new String[]{String.valueOf(m.get("two"))});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //무조건 CSVWriter 객체 close
                    cw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(tag, "success");
        upforIt(); //ftp 서버에 업로드 함수 호출
    }

    public void upforIt() { //ftp 서버에 업로드

//        try{
//            String TAG = "AddFr";
//            LoginActivity l = new LoginActivity();
//            httpclient = new DefaultHttpClient();
//            httppost = new HttpPost("http://192.168.11.38");
//            nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("TAG", TAG ));
//            nameValuePairs.add(new BasicNameValuePair("uid", l.uid));
//
//            Log.v(null, "222");
////            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
////            response = httpclient.execute(httppost);
////            ResponseHandler<String> responseHandler = new BasicResponseHandler();
////            final String response = httpclient.execute(httppost, responseHandler);
////            System.out.println("Response" + response);
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    dialog.dismiss();
////                }
////            });
//        }catch (Exception e){
//            dialog.dismiss();
//            System.out.println("Exception:"+e.getMessage());
//        }
        try {
            FTPClient con = new FTPClient();


            con.connect("192.168.21.102");


            if (con.login("altogether", "geon")) {


                con.enterLocalPassiveMode();
                con.setFileType(FTP.ASCII_FILE_TYPE);
                String data = "/sdcard/phone.txt";

                //con.cwd(l.uid);
                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("/"+LoginActivity.uid+"/phone.txt", in);
                //boolean result = con.storeFile("/phone.txt", in);
                in.close();
                if (result)
                    Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        downforIt();
    }

    public void downforIt(){

        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("192.168.21.102");

            Log.v(null, "666");

            if (con.login("altogether", "geon"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.ASCII_FILE_TYPE);
                String data = "/sdcard/phone2.txt";

                OutputStream out = new FileOutputStream(new File(data));
                boolean result = con.retrieveFile("/"+LoginActivity.uid+"/phone_f.txt", out);
                out.close();
                if (result) Log.v("download result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            Log.v("download result", "failed");
            e.printStackTrace();
        }

        Intent intent = new Intent(AddFriendActivity.this, ContactListActivity.class);
        startActivity(intent);
        finish();
    }
}


