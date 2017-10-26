package com.example.johnny.mycoin;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class TeachActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, View.OnTouchListener{

    final static double COIN_SIZE_DENOMINATOR = 16.75; //act as denomination to calculate other coin sizes

    private RelativeLayout mainLayout;
    private Button btnRollCoin;
    private Spinner spinCoinQty;

    private static ArrayList<Coin> coinList; //array list to store the coin type to generate based on checkboxes
    private int coinQty; //number of coins to generate
    private double totalCoin; //total coin for display

    private TextView coinTotalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);

        //initialize views
        mainLayout = (RelativeLayout) findViewById(R.id.teach_mainLayout);

        spinCoinQty = (Spinner) findViewById(R.id.spinner_quantity);
        //adapter to populate the spinner value
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.coin_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCoinQty.setAdapter(adapter);

        btnRollCoin = (Button) findViewById(R.id.btn_gen_coin);
        coinTotalText = (TextView) findViewById(R.id.teach_show_text);

        //button onClicklistener
        btnRollCoin.setOnClickListener(this);

        //spinner onItemSelectListener
        spinCoinQty.setOnItemSelectedListener(this);
    }

    /* generate a new coin*/
    private void generateCoin() {

        Random rand = new Random();
        int mainLayoutWidth = mainLayout.getWidth();
        int mainLayoutHeight = mainLayout.getHeight();

        ArrayList<Rect> coinRectList = new ArrayList<Rect>();

        for (int i = 0; i < coinQty; i++) {

            //randomly generate a coin
            int genRand = rand.nextInt(coinList.size());

            Coin currentCoin = coinList.get(genRand);

            //image of the coin with onClickListener
            int imageSize = (int) (getResources().getDimension(R.dimen.coin_width) * currentCoin.getCoinSize() / COIN_SIZE_DENOMINATOR);
            ImageView coinView = new ImageView(this);
            RelativeLayout.LayoutParams coinViewParms = new RelativeLayout.LayoutParams(imageSize, imageSize);
            coinView.setImageResource(currentCoin.getCoinImage());
            coinView.setLayoutParams(coinViewParms);
            coinView.setOnTouchListener(this);
            coinView.setTag(currentCoin.getCoinValue());

            //randomly generated position
            int firstCornerX = rand.nextInt(mainLayoutWidth - 2 * imageSize);
            int firstCornerY = rand.nextInt(mainLayoutHeight - imageSize);
            coinView.setX(firstCornerX + imageSize); //cater room for shift in x position
            coinView.setY(firstCornerY);

            mainLayout.addView(coinView);
        }

    }

    private float dX,dY;
    private boolean isMove = false;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //set boundaries of the parent view
        int topBound = 0;
        int leftBound = 0;
        int rightBound = mainLayout.getWidth() - view.getWidth();
        int bottomBound = mainLayout.getHeight() - view.getHeight();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
               break;

            //animate the drag with boundaries
            case MotionEvent.ACTION_MOVE:

                //horizontal boundaries during dragging
                if (event.getRawX() + dX < leftBound)
                    view.animate().x(0);
                else if (event.getRawX() + dX > rightBound)
                    view.animate().x(rightBound);
                else
                    view.animate().x(event.getRawX() + dX);

                //vertical boundaries during dragging
                if (event.getRawY() + dY < topBound)
                    view.animate().y(0);
                else if (event.getRawY() + dY > bottomBound)
                    view.animate().y(bottomBound);
                else
                    view.animate().y(event.getRawY() + dY);

                //start displaying dragging animation
                view.animate()
                        .setDuration(0)
                        .start();
                isMove = true;
                break;

            case MotionEvent.ACTION_UP:

                // does not change the state of the coin when the move has taken place
                if(!isMove) {
                    //set total coin value on textbox on every click on the coin
                    totalCoin += (double) view.getTag();

                    coinTotalText.setText(String.format("$ " + "%.2f", Math.abs( totalCoin)));

                    //set negative/positive value of the coin and set alpha at per click of the coin
                    view.setTag(-1 * (double) view.getTag());

                    if ((double) view.getTag() < 0)
                        view.setAlpha(0.5f);
                    else
                        view.setAlpha(1f);
                }

                isMove =false;
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        coinQty = position + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        coinQty = 1;
    }

    @Override
    public void onClick(View v) {

        //generate coins to appear on screen button
        if (v == btnRollCoin) {

            totalCoin = 0;
            coinTotalText.setText(String.format("$ " + "%.2f", totalCoin));

            //remove coin imageViews
            mainLayout.removeAllViews();

            CheckBox cbxCents5 = (CheckBox) findViewById(R.id.checkBox_cents5);
            CheckBox cbxCents10 = (CheckBox) findViewById(R.id.checkBox_cents10);
            CheckBox cbxCents20 = (CheckBox) findViewById(R.id.checkBox_cents20);
            CheckBox cbxCents50 = (CheckBox) findViewById(R.id.checkBox_cents50);
            CheckBox cbxDollar1 = (CheckBox) findViewById(R.id.checkBox_dollar);

            //initialise coins based on checkbox
            coinList = new ArrayList<Coin>();
            if (cbxCents5.isChecked())
                coinList.add(new Coin(0.05, R.drawable.cents5, 16.75));
            if (cbxCents10.isChecked())
                coinList.add(new Coin(0.10, R.drawable.cents10, 18.50));
            if (cbxCents20.isChecked())
                coinList.add(new Coin(0.20, R.drawable.cents20, 21.00));
            if (cbxCents50.isChecked())
                coinList.add(new Coin(0.50, R.drawable.cents50, 23.00));
            if (cbxDollar1.isChecked())
                coinList.add(new Coin(1.00, R.drawable.dollar1, 24.65));

            if (coinList.isEmpty()) {
                //prompt user
            } else {
                generateCoin();
                totalCoin = 0;
            }
        }

    }
}
