package com.example.johnny.mycoin;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by Johnny on 26/5/2017.
 */

public class SelectionMode extends AppCompatActivity implements View.OnClickListener {

    private Button btnMode1;
    private Button btnMode2;
    private Button btnMode3;
    private Button btnExit;
    private Button btnSetting;
    private MediaPlayer mp;
    private MediaPlayer welcomeSound;
    private int mode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mode_selection);

        btnMode1 = (Button) findViewById(R.id.btn_mode_1);
        btnMode2 = (Button) findViewById(R.id.btn_mode_2);
        btnMode3 = (Button) findViewById(R.id.btn_mode_3);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnSetting = (Button) findViewById(R.id.btn_setting);

        btnMode1.setOnClickListener(this);
        btnMode2.setOnClickListener(this);
        btnMode3.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

        mp = MediaPlayer.create(this, R.raw.knob);
        welcomeSound = MediaPlayer.create(this, R.raw.arpeggio);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        welcomeSound.start();

    }

    @Override
    public void onClick(View v) {

        PopupMenu diffPopUpMenu = new PopupMenu(this, v);
        diffPopUpMenu.getMenuInflater().inflate(R.menu.difficulty, diffPopUpMenu.getMenu());

        switch (v.getId()) {

            case (R.id.btn_mode_1): {

                //teach mode selection
                Intent i = new Intent(this, TeachActivity.class);
                this.startActivity(i);
                mp.start();
                break;
            }

            case (R.id.btn_mode_2): {

                //guess game activity selection
                //Pop Up menu for guess game difficulty selection
                diffPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();

                        switch (id) {
                            case (R.id.easy): {
                                mode = 1;
                                break;
                            }
                            case (R.id.medium): {
                                mode = 2;
                                break;
                            }
                            case (R.id.hard): {
                                mode = 3;
                                break;
                            }
                        }

                        //play click sound
                        mp.start();

                        //open guess game activity
                        Intent i2 = new Intent(SelectionMode.this, GuessActivity.class);
                        i2.putExtra("diff", mode);
                        i2.putExtra("timer",0);
                        SelectionMode.this.startActivity(i2);

                        //return statement for the popup menu
                        return true;

                    }

                });
                diffPopUpMenu.show();
                break;
            }

            case (R.id.btn_mode_3): {

                //Time game activity Selection
                //Pop Up menu for time game difficulty selection
                diffPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case (R.id.easy): {
                                mode = 1;
                                break;
                            }
                            case (R.id.medium): {
                                mode = 2;
                                break;
                            }
                            case (R.id.hard): {
                                mode = 3;
                                break;
                            }
                        }

                        //play click sound
                        mp.start();

                        //opens time activity
                        Intent i2 = new Intent(SelectionMode.this, GuessActivity.class);
                        i2.putExtra("diff", mode);
                        i2.putExtra("timer",1);
                        SelectionMode.this.startActivity(i2);

                        //return statement for the popup menu
                        return true;
                    }
                });

                diffPopUpMenu.show();
                break;
            }

            case (R.id.btn_exit): {
                //exit the application
                mp.start();
                this.finish();
                System.exit(0);
            }

            case (R.id.btn_setting):{



                //inflate a the popup xml
                View popLayout = getLayoutInflater().inflate(R.layout.setting, null);

                PopupWindow changeSortPopUp = new PopupWindow(this);
                changeSortPopUp.setContentView(popLayout);
                changeSortPopUp.setWidth((int) getResources().getDimension(R.dimen.setting_popup_width) );
                changeSortPopUp.setHeight((int) getResources().getDimension(R.dimen.setting_popup_height) );
                changeSortPopUp.setBackgroundDrawable(getDrawable(R.drawable.background));
                changeSortPopUp.setFocusable(true);

                // Displaying the popup at the specified location, + offsets.

                LinearLayout centerLayout = (LinearLayout) findViewById(R.id.centerLayout);
                changeSortPopUp.showAtLocation(centerLayout, Gravity.CENTER, 0, 0);
           }
        }

    }

}
