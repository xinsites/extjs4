package com.xinsite.common.uitls.office.excel;

import com.xinsite.common.uitls.lang.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class ExcelData {
    private int row;         //Excel单元格行
    private int column;      //Excel单元格列
    private String location; //坐标位置，如A1,C2
    private String key;      //替换的关键字，为空时，坐标位置放value
    private Object value;    //替换的文本
    private XSSFCellStyle cellStyle;   //单元格样式
    private float rowHeight;  //行高

    public ExcelData() {
    }

//    public ExcelData(int row, int column, String key, Object value) {
//        this.row = row;
//        this.column = column;
//        this.key = key;
//        this.value = value;
//    }
//
//    public ExcelData(int row, int column, Object value) {
//        this.row = row;
//        this.column = column;
//        this.value = value;
//    }

    public ExcelData(String location, Object value) {
        this.location = location;
        this.value = value;
        SetLocation(location);
    }

//    public ExcelData(String location, String key, Object value) {
//        this.location = location;
//        this.key = key;
//        this.value = value;
//        SetLocation(location);
//    }

    public ExcelData(String location, Object value, XSSFCellStyle cellStyle) {
        this.location = location;
        this.value = value;
        this.cellStyle = cellStyle;
        SetLocation(location);
    }

    public ExcelData(String location, Object value, XSSFCellStyle cellStyle, float rowHeight) {
        this.location = location;
        this.value = value;
        this.cellStyle = cellStyle;
        this.rowHeight = rowHeight;
        SetLocation(location);
    }

    public void SetLocation(String location) {
        try {
            if (!StringUtils.isEmpty(location)) {
                CellReference cr = new CellReference(location);
                this.column = cr.getCol();
                this.row = cr.getRow();
            }
        } catch (Exception e) {

        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getKey() {
        return key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public XSSFCellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(XSSFCellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }

    public float getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(float rowHeight) {
        this.rowHeight = rowHeight;
    }
}
