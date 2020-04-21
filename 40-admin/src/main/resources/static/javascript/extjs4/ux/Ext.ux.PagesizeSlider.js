Ext.define('Ext.ux.PagesizeSlider', {
    tipText: '每页{0}条记录',
    hideRefresh: false, //隐藏刷新按钮
    minValue: 1, //滑块最小值，默认1
    maxValue: 100, //滑块最大值，默认100
    cookieName: null, //每页记录数保存在cookies中，没有该值不保存
    //    requires: [
    //        'Ext.slider.Single',
    //        'Ext.slider.Tip'
    //    ],
    constructor: function (config) {
        if (config) {
            Ext.apply(this, config);
        }
    },
    init: function (pbar) {
        var idx = pbar.items.indexOf(pbar.child("#refresh")),
            slider,
            tt = this.tipText,
            cookieName = this.cookieName;
        if (this.maxValue < pbar.store.pageSize)
            this.maxValue = pbar.store.pageSize;
        if (cookieName)
            pbar.store.pageSize = getGridPageSize(cookieName);

        var slider = Ext.create('Ext.slider.Single', {
            width: 114,
            value: pbar.store.pageSize,
            minValue: this.minValue,
            maxValue: this.maxValue,
            hideLabel: true,
            tipText: function (thumb) {
                return Ext.String.format(tt, thumb.value);
            },
            listeners: {
                changecomplete: function (s, v) {
                    pbar.store.pageSize = v;
                    var totalCount = pbar.store.getTotalCount();
                    var start = (pbar.store.currentPage - 1) * v;
                    if (start >= totalCount) {
                        start = 0;
                        pbar.store.currentPage = 1;
                    }
                    pbar.store.load({ start: start, limit: v });
                    if (cookieName) setGridPageSize(cookieName, v);
                }
            }
        });
        if (this.hideRefresh) {
            pbar.items.items[idx - 1].setVisible(false);  //隐藏刷新按钮前面的|
            pbar.items.items[idx].setVisible(false);  //隐藏刷新按钮
        }

        pbar.insert(++idx, '-');
        pbar.insert(++idx, slider);
        pbar.on({
            beforedestroy: function () {
                slider.destroy();
            },
            change: function (pb, data) {
            }
        });
    }
});
