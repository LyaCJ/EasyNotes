package com.example.madey.easynotes;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends AppCompatActivity {

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                    break;
                case R.id.fab2:
                    fab2.setVisibility(View.GONE);
                    break;
                case R.id.fab3:
                    fab2.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuRed = (FloatingActionMenu) findViewById(R.id.menu_red);

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        menuRed.setClosedOnTouchOutside(true);

        menuRed.setVisibility(View.VISIBLE);

        menuRed.hideMenuButton(false);
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuRed.isOpened()) {
                    Toast.makeText(MainActivity.this, menuRed.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                menuRed.toggle(true);
            }
        });

        menuRed.showMenuButton(true);

    }
}
