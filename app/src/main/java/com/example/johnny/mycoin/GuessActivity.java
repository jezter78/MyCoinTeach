package com.example.johnny.mycoin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class GuessActivity extends AppCompatActivity implements View.OnClickListener
                                                                , View.OnTouchListener {

    private Button startButton;
    private Button submitButton;
    private Button homeButton;

    private ImageView resultIcon;    //display correct or wrong icon after result

    private TextView totalScore;    //display total score
    private TextView modeDisplay;   //display difficulty mode
    private TextView showTimeLeft;
    private TextView finalScoreView;    //display final score

    private EditText answer;        //answer input

    private Coin[] coinArry = new Coin[5]; //register 5 types of sg coins

    private String totalCoinRounded;    //round the double
    private int totalAttempt;           //number of attempt
    private int totalPass;              //pass score
    private int numberOfCoins;          //number of coins generated in each difficulty
    private int maxTime;                //maxtime given based on difficulty

    private float dX;   //motion control x
    private float dY;   //motion control y

    MediaPlayer correctSound;
    MediaPlayer wrongSound;

    final static String HARD_MODE = "Hard Mode";
    final static String MEDIUM_MODE = "Medium Mode";
    final static String EASY_MODE = "Easy Mode";
    final static int EASY_MODE_COIN_QUANTITY = 5;
    final static int MEDIUM_MODE_COIN_QUANTITY = 10;
    final static int HARD_MODE_COIN_QUANTITY = 20;
    final static int EASY_MODE_TIME = 30000;
    final static int MEDIUM_MODE_TIME = 20000;
    final static int HARD_MODE_TIME = 10000;
    final static double COIN_SIZE_DENOMINATOR = 16.75; //act as denomination to calculate other coin sizes
    final static int TRANSLATE_DISTANCE = 1000;       //coin move in distance in pixel

    private RelativeLayout coinRelativeLayout; //game play window

    /* onCreate and initialization*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        // initialise views
        startButton = (Button) findViewById(R.id.btn_guess_start);
        submitButton = (Button) findViewById(R.id.btn_guess_submit);
        homeButton = (Button) findViewById(R.id.btn_home);
        resultIcon = (ImageView) findViewById(R.id.img_result);
        totalScore = (TextView) findViewById(R.id.guess_score);
        modeDisplay = (TextView) findViewById(R.id.txt_mode);
        showTimeLeft = (TextView) findViewById(R.id.txt_timer);
        finalScoreView = (TextView) findViewById(R.id.txt_finalscore) ;
        answer = (EditText) findViewById(R.id.guess_editText1);

        coinRelativeLayout = new RelativeLayout(this);
        coinRelativeLayout = (RelativeLayout) findViewById(R.id.coinRelativeLayout);

        resultIcon.setVisibility(View.INVISIBLE);

        //initialise coins
        String[] coinNameArr = {
                "5 cents",
                "10 cents",
                "20 cents",
                "50 cents",
                "1 dollar"
        };

        double[] coinSizeArr = {16.75,18.50,21.00,23.00,24.65};  //diameter of the coins in mm
        double [] coinValueArr = {0.05,0.10,0.20,0.50,1.0};     //face value of coins

        int[] coinImgArr = {
                R.drawable.cents5,
                R.drawable.cents10,
                R.drawable.cents20,
                R.drawable.cents50,
                R.drawable.dollar1,
        };

        for (int i = 0; i < 5; i++) {
            coinArry[i] = new Coin(coinValueArr[i],coinImgArr[i],coinSizeArr[i]);
        }

        totalAttempt = 0;
        totalPass = 0;

        totalScore.setText("Score:  " + totalPass + "/" + totalAttempt);

        //start
        answer.setEnabled(false);
        startButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);

        //for media volume control when the side volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //initialize sounds
        wrongSound = MediaPlayer.create(this, R.raw.attention);
        correctSound = MediaPlayer.create(this, R.raw.arpeggio);

        //difficulty settings
        Intent intent = this.getIntent();
        int diffSetting = intent.getIntExtra("diff", 0);
        int timerModeFlag = intent.getIntExtra("timer",0);

        if (diffSetting == 1) {
            modeDisplay.setText(EASY_MODE);
            numberOfCoins = EASY_MODE_COIN_QUANTITY;
            maxTime = EASY_MODE_TIME;

        } else if (diffSetting == 2) {
            modeDisplay.setText(MEDIUM_MODE);
            numberOfCoins = MEDIUM_MODE_COIN_QUANTITY;
            maxTime = MEDIUM_MODE_TIME;
        } else {
            modeDisplay.setText(HARD_MODE);
            numberOfCoins = HARD_MODE_COIN_QUANTITY;
            maxTime = HARD_MODE_TIME;
        }

        if (timerModeFlag == 1){
            startTimer();
        }
        else{
            showTimeLeft.setVisibility(View.INVISIBLE);
        }

    }

    /* Game Play */
    public void rollCoin() {

        //generate coins
        double totalCoinValue = 0;
        Random rnd = new Random();
        int generateRand;

        //generate coins
        coinRelativeLayout.removeAllViews();

        int posX; //final position x
        int posY; //final position y
        int parentWidth = coinRelativeLayout.getWidth();
        int parentHeight = coinRelativeLayout.getHeight();

        for (int k = 0; k < numberOfCoins; k++) {
            generateRand = rnd.nextInt(5);
            totalCoinValue = totalCoinValue + coinArry[generateRand].getCoinValue();

            //adding coin images layout using program
            //coin generated according to size ratio
            int imagewidth = (int)( getResources().getDimension(R.dimen.coin_width) * coinArry[generateRand].getCoinSize()/COIN_SIZE_DENOMINATOR);

            ImageView myImageView = new ImageView(this);
            RelativeLayout.LayoutParams coinparam = new RelativeLayout.LayoutParams(imagewidth, imagewidth);
            myImageView.setLayoutParams(coinparam);
            myImageView.setImageResource(coinArry[generateRand].getCoinImage());
            myImageView.setOnTouchListener(this);

            //to make sure that the final position of the coin is within the maximum side of the border
            int coinViewWidth = parentWidth - imagewidth;
            int coinViewHeight = parentHeight - imagewidth;

            //to randomly generate the coin within the border
            posX = rnd.nextInt(coinViewWidth);
            posY = rnd.nextInt(coinViewHeight);

            //calculate the coin move in direction
            if(posX <= parentWidth/2) {
                posX = posX - TRANSLATE_DISTANCE;
                myImageView.animate().translationXBy(TRANSLATE_DISTANCE);
            }
            else {
                posX = posX + TRANSLATE_DISTANCE;
                myImageView.animate().translationXBy(-TRANSLATE_DISTANCE);
            }

            if(posY <= parentHeight/2){
                posY=posY - TRANSLATE_DISTANCE;
                myImageView.animate().translationYBy(TRANSLATE_DISTANCE);
            }

            else{
                posY=posY + TRANSLATE_DISTANCE;
                myImageView.animate().translationYBy(-TRANSLATE_DISTANCE);
            }

            //spawn the coin at start of movement position
            coinparam.leftMargin = posX;
            coinparam.topMargin = posY;
            coinRelativeLayout.addView(myImageView);

            //animate the coin
            myImageView.animate()
                    .setDuration(500)
                    .start();

        }

        //round sum of coin value in string
        totalCoinRounded = String.format("%.2f", totalCoinValue);

        //activate submit button and disable start button
        submitButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);

    }

    /* move coin */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        //set boundaries of the parent view
        int topBound = 0;
        int leftBound = 0;
        int rightBound = coinRelativeLayout.getWidth() - view.getWidth();
        int bottomBound = coinRelativeLayout.getHeight() - view.getHeight();

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
                break;
            default:
                return false;
        }
        return true;
    }

    /* Display Total Score*/
    public void getResult(String finalCoin) {

        double answerResult;
        String answerResultRounded;

        //check for input and round the answer to 2 decimal points
        if (answer.getText().toString().equals("")) {
            answerResult = 0.0;
        } else {
            answerResult = Double.valueOf(answer.getText().toString());
        }

        answerResultRounded = String.format("%.2f", answerResult);

        totalAttempt++;

        //calculate results
        if (finalCoin.equals(answerResultRounded)) {

            totalPass++;
            resultIcon.setImageResource(R.drawable.correct);
            resultIcon.setVisibility(View.VISIBLE);
            correctSound.start();

        } else {

            resultIcon.setImageResource(R.drawable.failure);
            resultIcon.setVisibility(View.VISIBLE);
            wrongSound.start();
        }

        totalScore.setText("Score:  " + totalPass + "/" + totalAttempt);
        startCountAnimation();

    }

    /* Handle button clicks*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.btn_guess_start): {
                resultIcon.setVisibility(View.INVISIBLE);
                rollCoin();
                answer.setEnabled(true);
                answer.setText("");
                break;
            }

            case (R.id.btn_guess_submit): {

                getResult(totalCoinRounded);
                answer.setEnabled(false);
                startButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);
                break;
            }

            case(R.id.btn_home):{
                Intent intent = new Intent(this,SelectionMode.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity( intent );
            }

        }
    }

    public void startTimer(){

        new CountDownTimer(maxTime, 1000) {

            public void onTick(long millisUntilFinished) {
                showTimeLeft.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                showTimeLeft.setText("0");
                startButton.setEnabled(false);
                submitButton.setEnabled(false);
                answer.setEnabled(false);
            }

        }.start();
    }

    private void startCountAnimation() {
        finalScoreView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(0, 600);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                finalScoreView.setText(animation.getAnimatedValue().toString());
            }
        });

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                finalScoreView.setVisibility(View.INVISIBLE);
            }
        });

        animator.start();
    }
}

