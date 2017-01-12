package com.example.anthony.stock.Bolling;

/**
 * Created by chanyunyuen on 2/1/2017.
 */

public class BollingItem {
    String Date;
    int MA20;
    int upper;
    int lower;
    int upper5Percent;
    int lower5Percent;
    int typicalC;
    int open;
    int close;
    int dayHigh;
    int dayLow;
    int totalWin;
    int cutlostValue;
    boolean buy;
    boolean sell;
    boolean compensateBuy;
    boolean compensateSell;
    boolean cutLostBuy;
    boolean isCutLostSell;
    boolean normalBuy;
    boolean normalSell;

    public boolean isCompensateBuy() {
        return compensateBuy;
    }

    public void setCompensateBuy(boolean compensateBuy) {
        this.compensateBuy = compensateBuy;
    }

    public boolean isCompensateSell() {
        return compensateSell;
    }

    public void setCompensateSell(boolean compensateSell) {
        this.compensateSell = compensateSell;
    }

    public boolean isCutLostBuy() {
        return cutLostBuy;
    }

    public void setCutLostBuy(boolean cutLostBuy) {
        this.cutLostBuy = cutLostBuy;
    }

    public boolean isCutLostSell() {
        return isCutLostSell;
    }

    public void setCutLostSell(boolean cutLostSell) {
        isCutLostSell = cutLostSell;
    }

    public boolean isNormalBuy() {
        return normalBuy;
    }

    public void setNormalBuy(boolean normalBuy) {
        this.normalBuy = normalBuy;
    }

    public boolean isNormalSell() {
        return normalSell;
    }

    public void setNormalSell(boolean normalSell) {
        this.normalSell = normalSell;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getMA20() {
        return MA20;
    }

    public void setMA20(int MA20) {
        this.MA20 = MA20;
    }

    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    public int getLower() {
        return lower;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }

    public int getUpper5Percent() {
        return upper5Percent;
    }

    public void setUpper5Percent(int upper5Percent) {
        this.upper5Percent = upper5Percent;
    }

    public int getLower5Percent() {
        return lower5Percent;
    }

    public void setLower5Percent(int lower5Percent) {
        this.lower5Percent = lower5Percent;
    }

    public int getTypicalC() {
        return typicalC;
    }

    public void setTypicalC(int typicalC) {
        this.typicalC = typicalC;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
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

    public int getTotalWin() {
        return totalWin;
    }

    public void setTotalWin(int totalWin) {
        this.totalWin = totalWin;
    }

    public int getCutlostValue() {
        return cutlostValue;
    }

    public void setCutlostValue(int cutlostValue) {
        this.cutlostValue = cutlostValue;
    }
}
