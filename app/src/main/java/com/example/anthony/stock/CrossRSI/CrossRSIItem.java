package com.example.anthony.stock.CrossRSI;

/**
 * Created by anthony on 28/3/2017.
 */

public class CrossRSIItem {
    private String dayStr;
    private int day;
    private int dayClose;
    private int dayOpen;
    private double longRsi;
    private double shortRsi;
    private int dayHigh;
    private int dayLow;
    private double raiseShortAverage;
    private double dropShortAverage;
    private double raiseLongAverage;
    private double dropLongAverage;
    private boolean sell;
    private boolean buy;
    private int buyPrice;
    private int sellPrice;
    private int winOrloss;

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public int getDay() {
        return day;
    }
    public void setDay(int day) {
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

    public double getLongRsi() {
        return longRsi;
    }

    public void setLongRsi(double longRsi) {
        this.longRsi = longRsi;
    }

    public double getShortRsi() {
        return shortRsi;
    }

    public void setShortRsi(double shortRsi) {
        this.shortRsi = shortRsi;
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

    public double getRaiseShortAverage() {
        return raiseShortAverage;
    }

    public void setRaiseShortAverage(double raiseShortAverage) {
        this.raiseShortAverage = raiseShortAverage;
    }

    public double getDropShortAverage() {
        return dropShortAverage;
    }

    public void setDropShortAverage(double dropShortAverage) {
        this.dropShortAverage = dropShortAverage;
    }

    public double getRaiseLongAverage() {
        return raiseLongAverage;
    }

    public void setRaiseLongAverage(double raiseLongAverage) {
        this.raiseLongAverage = raiseLongAverage;
    }

    public double getDropLongAverage() {
        return dropLongAverage;
    }

    public void setDropLongAverage(double dropLongAverage) {
        this.dropLongAverage = dropLongAverage;
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

    public int getWinOrloss() {
        return winOrloss;
    }

    public void setWinOrloss(int winOrloss) {
        this.winOrloss = winOrloss;
    }
}
