Ext.define('Ext.ux.PagesizeCombo', {
    fieldLabel: '每页记录数',
    hideLabel: false, //是否隐藏label
    hideRefresh: false, //隐藏刷新按钮
    data: [['20', '20'], ['40', '40'], ['60', '60'], ['80', '80'], ['100', '100'], ['200', '200']],
    cookieName: null, //每页记录数保存在cookies中，没有该值不保存
    constructor: function (config) {
        if (config) {
            Ext.apply(this, config);
        }
    },
    init: function (pbar) {
        var idx = pbar.items.indexOf(pbar.child("#refresh")),
            pagecombo,
            cookieName = this.cookieName;
        pagecombo = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: this.fieldLabel,
            hideLabel: this.hideLabel,
            displayField: 'value', valueField: 'value',
            labelWidth: 80, width: (this.hideLabel ? 60 : 140),
            editable: false, value: pbar.store.pageSize,
            queryMode: 'local', store: this.data,
            listeners: {
                'change': function (combocox, eOpts) {
                    pbar.store.pageSize = combocox.getValue();
                    pbar.store.load();
                    if (cookieName) setGridPageSize(cookieName, combocox.getValue());
                }
            }
        });
        if (this.hideRefresh) {
            pbar.items.items[idx - 1].setVisible(false);  //隐藏刷新按钮前面的|
            pbar.items.items[idx].setVisible(false);  //隐藏刷新按钮
        }

        pbar.insert(++idx, '-');
        pbar.insert(++idx, pagecombo);
        pbar.on({
            beforedestroy: function () {
                pagecombo.destroy();
            },
            change: function (pb, data) {
            }
        });
    }
});
