package com.example.johnny.mycoin;

import android.media.Image;
import android.view.View;

/**
 * Created by Johnny on 22/5/2017.
 */

public class Coin {

    double coinValue;
    int coinImage;
    double coinSize;

    public double getCoinSize() {
        return coinSize;
    }

    public Coin(double coinValue, int coinImage, double coinSize) {
        this.coinValue = coinValue;
        this.coinImage = coinImage;
        this.coinSize = coinSize;
    }

    public double getCoinValue() {
        return coinValue;
    }

    public int getCoinImage() {
        return coinImage;
    }

}



