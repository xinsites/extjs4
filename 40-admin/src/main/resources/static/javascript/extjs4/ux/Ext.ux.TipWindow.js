Ext.define('Ext.ux.TipWindow', {
    extend: 'Ext.Window',
    //alias: 'widget.tipwindow',
    title: "信息提示",
    width: 300,
    height: 180,
    resizable: false,
    draggable: false,
    shadow: false,
    autoHide: false, //是否自动隐藏，false则不自动隐藏，设置成Int，多少秒后自动关闭
    //    constructor: function () {
    //        //alert('先构造函数启动...');
    //        this.callParent();
    //    },
    initComponent: function () {
        var win = this;
        win.eBody = Ext.getBody();
        win.setPosition(win.eBody.getWidth() - win.width, win.eBody.getHeight());
        Ext.EventManager.onWindowResize(function () {
            win.autoPosition();
        });
        win.on({
            'beforeclose': function (win) {
                win.flyOut();
                return false;
            },
            'show': function (win) {
                if (false !== win.autoHide) {
                    var title = win.title, count = parseInt(win.autoHide) || 3;
                    var run_task = {
                        run: function () {
                            if (count < 1) {
                                Ext.TaskManager.stop(run_task);
                                win.flyOut();
                            }
                            win.setTitle(title + "- " + count-- + "秒后关闭");
                        },
                        interval: 1000
                    }
                    Ext.TaskManager.start(run_task);
                    win.getEl().on({
                        "mouseover": function (e) {
                            Ext.TaskManager.stop(run_task);
                        },
                        "mouseout": function (e) {
                            Ext.TaskManager.start(run_task);
                        }
                    });
                }
            }
        });
        this.callParent(arguments);
    },
    flyIn: function () {
        var win = this;
        win.show();
        win.getEl().shift({
            x: win.eBody.getWidth() - win.getWidth(),
            y: win.eBody.getHeight() - win.getHeight(),
            opacity: 80,
            easing: 'easeOut',
            duration: 500
        });
        win.isFlyIn = true;
    },
    flyOut: function () {
        var win = this;
        try {
            win.getEl().shift({
                y: win.eBody.getHeight(),
                duration: 300
            });
            win.isFlyIn = false;
            setTimeout(function () {
                win.destroy();
            }, 1000);
        } catch (e) { }
    },
    autoPosition: function () {
        var win = this;
        if (win.isFlyIn) {
            win.flyIn();
        } else {
            win.flyOut();
        }
    }
});