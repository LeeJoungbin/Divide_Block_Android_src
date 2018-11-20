package com.pnu.cse.termspring2018;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    DBHelper dbHelper;

    boolean isOverlap = true;
    String successId = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this, "TermSpring", null, 1);

        AppCompatButton b = findViewById(R.id.btn_register);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id = findViewById(R.id.input_id);
                EditText pw = findViewById(R.id.input_password);
                EditText name = findViewById(R.id.input_name);

                if(isOverlap) {
                    Toast.makeText(RegisterActivity.this, "아이디 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if(!successId.equals(id.getText().toString())) {
                        Toast.makeText(RegisterActivity.this, "아이디 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(id.toString().isEmpty() || pw.toString().isEmpty() || name.toString().isEmpty()) {
                    Toast.makeText(v.getContext(), "정보를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();

                    return;
                }

                dbHelper.insert(id.getText().toString(), pw.getText().toString(), name.getText().toString());
                finish();
            }
        });

        Button overlap = findViewById(R.id.overlap_id);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id = findViewById(R.id.input_id);
                isOverlap = dbHelper.isExist(id.getText().toString());

                if(isOverlap) {
                    Toast.makeText(RegisterActivity.this, "이미 존재하는 회원입니다", Toast.LENGTH_SHORT).show();
                } else {
                    successId = id.getText().toString();
                    Toast.makeText(RegisterActivity.this, "가입가능한 회원입니다", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
