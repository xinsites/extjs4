package com.xinsite.core.model.design;

public class ItemsModel {
    public double columnWidth;
    public String editor_form;
    public String val_anchor;

    public ItemsModel() {
    }

    public ItemsModel(double columnWidth, String editor_form, String val_anchor) {
        this.columnWidth = columnWidth;
        this.editor_form = editor_form;
        this.val_anchor = val_anchor;
    }
}
