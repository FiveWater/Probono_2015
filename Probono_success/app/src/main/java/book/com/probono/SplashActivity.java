package book.com.probono;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by 수민 오 on 2015-08-21.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new SplashHandler(), 3000);
    }
    private class SplashHandler implements Runnable{
        public void run(){
            //로딩이 끝난 후 이동할 activity
            startActivity(new Intent(getApplication(),LoginActivity.class ));
            //로딩페이지 activity stack 에서 제거
            SplashActivity.this.finish();
        }
    }
}
