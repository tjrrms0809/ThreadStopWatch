package com.mrhi.threadstopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    TimeThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv= findViewById(R.id.text1);
    }

    public void clickStart(View v){

        if(thread==null){ //스레드가 비어있다면 처음스타트 버튼을 클릭한상태.
            thread= new TimeThread();
            thread.start();
        }else{//일시정지된 상태로 간주..
            //스레드를 이어하기(resume)
            thread.resumeThread();
        }

    }

    public void clickStop(View v){

        if(thread!=null){
            thread.stopThread();
            thread=null;
        }

    }

    public void clickPause(View v){
        if(thread!=null) thread.pauseThread();
    }

    //Timer를 카운팅하는 Inner 스레드클래스 선언..///////
    class TimeThread extends Thread{

        boolean isRun=true;
        boolean isWait= false;

        int min, sec, millis;

        @Override
        public void run() {

            while (isRun){

                millis++;
                if(millis>=100){
                    millis=0;
                    sec++;
                    if(sec>=60){
                        sec=0;
                        min++;
                    }
                }

                //텍스트뷰에 시간값을 설정..
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s=  String.format("%02d:%02d:%02d", min, sec, millis);
                        tv.setText(s);
                    }
                });


                //10ms대기
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //일시정지를 할것인가??
                synchronized (this){
                    if(isWait){
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }//while..

        }//run Method....

        void stopThread(){
            //while문을 종료..되면 run Method가 끝나므로..
            isRun= false;

            //혹시 휴게실(waitPool or SleepPool)에서 쉴수도 있으므로..복귀..
            synchronized (this){
                this.notify();
            }

        }

        void pauseThread(){
            isWait= true;
        }

        void resumeThread(){

            isWait= false;

            synchronized (this){
                this.notify();
            }

        }

    }//TimeThread class.....
    ///////////////////////////////////////////

}//MainActivity class...
