package com.example.anthony.stock.KDJ;

/**
 * Created by chanyunyuen on 14/4/2017.
 */

public class KDJItem {
    private int Date;
    private int close;
    private int high;
    private int low;
    private int open;
    private int volume;
    private String strDate;
    private double valueK;
    private double valueD;
    private double valueJ;
    private double shortMA;
    private double longMA;
    private int buyPrice;
    private int sellPrice;
    private int winOrLossValue;
    private boolean stockOnHand;

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

    public double getValueK() {
        return valueK;
    }

    public void setValueK(double valueK) {
        this.valueK = valueK;
    }

    public double getValueD() {
        return valueD;
    }

    public void setValueD(double valueD) {
        this.valueD = valueD;
    }

    public double getValueJ() {
        return valueJ;
    }

    public void setValueJ(double valueJ) {
        this.valueJ = valueJ;
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

    public int getWinOrLossValue() {
        return winOrLossValue;
    }

    public void setWinOrLossValue(int winOrLossValue) {
        this.winOrLossValue = winOrLossValue;
    }

    public double getShortMA() {
        return shortMA;
    }

    public void setShortMA(double shortMA) {
        this.shortMA = shortMA;
    }

    public double getLongMA() {
        return longMA;
    }

    public void setLongMA(double longMA) {
        this.longMA = longMA;
    }

    public boolean isStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(boolean stockOnHand) {
        this.stockOnHand = stockOnHand;
    }
}
