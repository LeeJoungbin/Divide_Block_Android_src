package com.pnu.cse.termspring2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


/* 프로그램을 실행하면 가장 먼저 나타나는 액티비티
   새로 시작하기, 이어하기, 점수확인, 업적확인 등을 선택할수 있어야 한다. 새로 시적하기 또는 이어하기 선택시 게임 화면으로, 점수확인과 업적확인은 팝업 생성
 */

public class StartActivity extends AppCompatActivity {
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        dbHelper = new DBHelper(this, "TermSpring", null, 1);

        Button btn_start = findViewById(R.id.btn_start);
        Button btn_cont = findViewById(R.id.btn_cont);
        final Button btn_score = findViewById(R.id.btn_score);
        final Button btn_achieve = findViewById(R.id.btn_achieve);
        final Button btn_login = findViewById(R.id.btn_login);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                CheckBox check_timer = findViewById(R.id.check_timer_mode);
                intent.putExtra("timerOn", check_timer.isChecked());
                startActivity(intent);
            }
        });

        btn_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                //TODO: 이어하기 차후구현
                startActivity(intent);
            }
        });

        //TODO: 차후 layout 구성. popupwindow의 location은 다시 생각해볼것.팝업후의 interaction을 추가해야함. dismiss()고려
        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupContent = LayoutInflater.from(StartActivity.this).inflate(R.layout.recyclerview, null);
                RecyclerView r = popupContent.findViewById(R.id.recycler);
                ScoreAdapter s = new ScoreAdapter(dbHelper.getScoreResult());
                r.addItemDecoration(new DividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL));
                r.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));
                r.setAdapter(s);

                PopupWindow popup = new PopupWindow(popupContent, 400, 600, true);
                popup.setAnimationStyle(R.style.popup_window_animation_fade);
                popup.showAtLocation(btn_score, Gravity.CENTER, 0, 0);
            }
        });

        btn_achieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupContent = LayoutInflater.from(StartActivity.this).inflate(R.layout.sub_achieve, null);
                PopupWindow popup = new PopupWindow(popupContent, 600, 600, true);
                popup.showAtLocation(btn_achieve, Gravity.CENTER, 0, 0);
            }
        });

        // 로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        if(!User.isLogin()) {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
