package com.example.anthony.stock.Moving;

/**
 * Created by chanyunyuen on 26/3/2017.
 */

public class MovingItem {
    private int Date;
    private int close;
    private int high;
    private int low;
    private int open;
    private int volume;
    private String strDate;
    private int stortMA;
    private int longMA;
    private int buyPrice;
    private int sellPrice;
    private int winOrLoss;

    public int getDate() {
        return Date;
    }

    public void setDate(int date) {
        Date = date;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public int getStortMA() {
        return stortMA;
    }

    public void setStortMA(int stortMA) {
        this.stortMA = stortMA;
    }

    public int getLongMA() {
        return longMA;
    }

    public void setLongMA(int longMA) {
        this.longMA = longMA;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getWinOrLoss() {
        return winOrLoss;
    }

    public void setWinOrLoss(int winOrLoss) {
        this.winOrLoss = winOrLoss;
    }
}
