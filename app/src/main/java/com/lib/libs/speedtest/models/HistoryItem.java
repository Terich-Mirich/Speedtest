package com.lib.libs.speedtest.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(name = "HistoryItem")
public class HistoryItem extends Model {

    @Column(name = "Type")
    public String type;

    @Column(name = "Date")
    public Date date;

    @Column(name = "DMbps")
    public Float dmbps;

    @Column(name = "UMbps")
    public Float umbps;

    public HistoryItem() {
        super();
    }

    public HistoryItem(String type, Date date, Float dmbps, Float umbps) {
        super();
        this.type = type;
        this.date = date;
        this.dmbps = dmbps;
        this.umbps = umbps;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Float getDmbps() {
        return dmbps;
    }

    public Float getUmbps() {
        return umbps;
    }

    public static List<HistoryItem> getAll() {
        return new Select()
                .from(HistoryItem.class)
                .orderBy("Date DESC")
                .execute();
    }
}
