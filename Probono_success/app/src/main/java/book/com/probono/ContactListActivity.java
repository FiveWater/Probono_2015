package book.com.probono;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 수민 오 on 2015-09-30.
 */



public class ContactListActivity extends Activity {
    ListView listPerson;
    private final String TAG = "FRIEND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);

        listPerson = (ListView) findViewById(R.id.listPerson);

        getList();
    }

    public void getList() {

        // File Open
        File oFile = new File("/sdcard/phone2.txt");

        // File Reader를 위한 객체 생성
        FileReader frd = null;
        BufferedReader brd = null;

        //정보를 담을 array 설정
        ArrayList<String> persons = new ArrayList<String>();

        // 라인 단위 저장 및 카운트를 위한 변수 정의
        String rLine = null;
        int lineNum = 0;
        boolean hasMore = true;

        try {
            frd = new FileReader(oFile);
            brd = new BufferedReader(frd);

            while (hasMore) {
                if ((rLine = brd.readLine()) != null) {
                    // ArrayList에 읽은 라인 추가
                    persons.add(rLine);
                    lineNum++;
                    hasMore = true;
                } else
                    hasMore = false;
            }

            frd.close();
            brd.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        // 라인단위 출력(for loop)
        lineNum = persons.size();
        for(int i=0; i<lineNum; i++) {
            Log.v(null,"Line No. "+ i +": "+persons.get(i));
        }
        //리스트에 연결할 adapter 설정
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, persons);


        //리스트뷰에 표시
        listPerson.setAdapter(adp);
        //리스트 중 한개 클릭시 이벤트
        listPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactListActivity.this);
                //알림창의 속성
                builder.setTitle("친구추가 요청 확인").setMessage("내 앨범을 같이 관리할 친구요청을 하시겠습니까?").setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //확인버튼클릭시
                                Object vo = (Object)parent.getAdapter().getItem(position);
                                try{
                                    Log.v(null, "1");
                                    HttpClient httpclient=new DefaultHttpClient();
                                    Log.v(null, "2");
                                    JSONObject jsonObject = new JSONObject();
                                    Log.v(null, "3");
                                    HttpPost httpPost = new HttpPost("http://192.168.21.102/Main.php");
                                    Log.v(null, "4");
                                    jsonObject.put("tag", TAG);
                                    Log.v(null, "5");
                                    jsonObject.put("value", vo);
                                    Log.v(null, "6");
                                    jsonObject.put("uid", LoginActivity.uid);
                                    Log.v(null, "7");
                                    String str = jsonObject.toString();
                                    Log.v(null, "8");
                                    Log.v(null, str);
                                    Log.v(null, "9");
                                    JSONArray jsonArray = new JSONArray();
                                    Log.v(null, "10");
                                    jsonArray.put(jsonObject);
                                    Log.v(null, "11");
                                    String str1 = jsonArray.toString();
                                    Log.v(null, "12");
                                    Log.v(null, str1);
                                    Log.v(null, "13");

                                    String json = str1;
                                    Log.v(null, "14");
                                    StringEntity se = new StringEntity(json, "UTF-8");
                                    httpPost.setEntity(se);Log.v(null, "15");
                                    httpPost.setHeader("Accept", "application/json;");
                                    httpPost.setHeader("Content-type", "application/json;");
                                    httpPost.setHeader("Accept-Charset", "UTF-8");
                                    Log.v(null, "16");
                                    httpclient.execute(httpPost);
                                    Log.v(null, "17");


                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //1번
//                                Log.v(null, "111");
//
//                                //기본적인 설정
//                                DefaultHttpClient client = new DefaultHttpClient();
//                                Log.v(null, "222");
//                                HttpPost post = new HttpPost("http://192.168.6.153/Main.php");
//                                Log.v(null, "333");
//                                HttpParams params = client.getParams();
//                                Log.v(null, "444");
//                                HttpConnectionParams.setConnectionTimeout(params, 3000);
//                                Log.v(null, "555");
//                                HttpConnectionParams.setSoTimeout(params, 3000);
//                                Log.v(null, "666");
//                                post.setHeader("Content-type", "application/json; charset-utf-8");
//                                Log.v(null, "777");
//
//                                //JSON Object를 생성하고 데이터를 입력한다.
//                                JSONObject jObj = new JSONObject();
//                                Log.v(null, "888");
//                                try{
//                                    jObj.put("tag", TAG);
//                                    jObj.put("value", vo);
//                                    jObj.put("uid", LoginActivity.uid);
//                                    Log.v(null, "999");
//                                    Log.v(null, TAG);
//                                    Log.v(null, (String)vo);
//                                    Log.v(null, LoginActivity.uid);
//                                }catch (JSONException e){
//                                    e.printStackTrace();
//                                }
//
//                                try{
//                                    //JSON을 String 형변환해서 httpEntity에 넣어준다.
//                                    StringEntity se;
//                                    Log.v(null, "1212");
//                                    se = new StringEntity(jObj.toString());
//                                    Log.v(null, "1313");
//                                    HttpEntity he = se;
//                                    Log.v(null, "1414");
//                                    post.setEntity(he);
//                                    Log.v(null, "1515");
//                                }catch (UnsupportedEncodingException e){
//                                    e.printStackTrace();
//                                }

                                //2번
//                                try {
//                                    DefaultHttpClient httpclient = new DefaultHttpClient();
//                                    HttpPost httppost = new HttpPost("http://192.168.6.153:80/Main.php");
//                                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
//                                    nameValuePairs.add(new BasicNameValuePair("tag", TAG));
//                                    nameValuePairs.add(new BasicNameValuePair("value", (String) vo));
//                                    nameValuePairs.add(new BasicNameValuePair("uid", LoginActivity.uid));
//                                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                                }catch(Exception e){
//                                    Log.v(null, "exception");
//                                }

                                Toast.makeText(ContactListActivity.this, "친구요청성공했습니다!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //취소버튼클릭시
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
