package com.example.anthony.stock.RSI;

/**
 * Created by chanyunyuen on 4/2/2017.
 */

public class RSIItem {
    private String day;
    private int dayClose;
    private int dayOpen;
    private double rsi;
    private int dayHigh;
    private int dayLow;
    private double raiseAverage;
    private double dropAverage;
    private boolean sell;
    private boolean buy;
    private int buyPrice;
    private int sellPrice;

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

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDayClose() {
        return dayClose;
    }

    public void setDayClose(int dayClose) {
        this.dayClose = dayClose;
    }

    public int getDayOpen() {
        return dayOpen;
    }

    public void setDayOpen(int dayOpen) {
        this.dayOpen = dayOpen;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public int getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(int dayHigh) {
        this.dayHigh = dayHigh;
    }

    public int getDayLow() {
        return dayLow;
    }

    public void setDayLow(int dayLow) {
        this.dayLow = dayLow;
    }

    public double getRaiseAverage() {
        return raiseAverage;
    }

    public void setRaiseAverage(double raiseAverage) {
        this.raiseAverage = raiseAverage;
    }

    public double getDropAverage() {
        return dropAverage;
    }

    public void setDropAverage(double dropAverage) {
        this.dropAverage = dropAverage;
    }
}
