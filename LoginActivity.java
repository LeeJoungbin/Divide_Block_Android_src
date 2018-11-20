package com.pnu.cse.termspring2018;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    DBHelper dbHelper;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        dbHelper = new DBHelper(this, "TermSpring", null, 1);

        AppCompatButton b = findViewById(R.id.btn_login);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id = findViewById(R.id.input_id);
                EditText pw = findViewById(R.id.input_password);

                String name = dbHelper.getResult(id.getText().toString(), pw.getText().toString());

                if(name.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "회원정보가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                } else {
                    User.getInstance().setUser(name);
                    finish();
                }
            }
        });

        TextView t = findViewById(R.id.login_register);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // second login enabled back button
            if(User.isLogin()) finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
