package com.xinsite.common.uitls.office.excel;

import com.xinsite.common.uitls.lang.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;

public class ExcelMerged {
    private int firstRow;
    private int lastRow;
    private int firstCol;
    private int lastCol;
    private String firstLocation; //起始坐标位置，如A1,C2
    private String lastLocation;  //结束坐标位置，如A1,C2
    private int border;   //边框值，为0不设置边框
    private BorderStyle borderStyle;   //边框样式

    public ExcelMerged() {
    }

    public ExcelMerged(String firstLocation, String lastLocation) {
        this.firstLocation = firstLocation;
        this.lastLocation = lastLocation;
        SetLocation(firstLocation, 0);
        SetLocation(lastLocation, 1);
    }

    public ExcelMerged(String firstLocation, String lastLocation, int border) {
        this.firstLocation = firstLocation;
        this.lastLocation = lastLocation;
        this.border = border;
        SetLocation(firstLocation, 0);
        SetLocation(lastLocation, 1);
    }

    public ExcelMerged(String firstLocation, String lastLocation, BorderStyle borderStyle) {
        this.firstLocation = firstLocation;
        this.lastLocation = lastLocation;
        this.borderStyle = borderStyle;
        SetLocation(firstLocation, 0);
        SetLocation(lastLocation, 1);
    }

    public void SetLocation(String location, int type) {
        try {
            if (!StringUtils.isEmpty(location)) {
                CellReference cr = new CellReference(location);
                if (type == 0) {
                    this.firstRow = cr.getRow();
                    this.firstCol = cr.getCol();
                } else {
                    this.lastRow = cr.getRow();
                    this.lastCol = cr.getCol();
                }
            }
        } catch (Exception e) {

        }
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public int getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    public int getLastCol() {
        return lastCol;
    }

    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    public String getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(String firstLocation) {
        this.firstLocation = firstLocation;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }
}
