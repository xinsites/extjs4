/*
This file is part of Ext JS 4.2
*/
(function () {
    function getQueryParam(name) {
        var regex = RegExp('[?&]' + name + '=([^&]*)');

        var match = regex.exec(location.search) || regex.exec(path);
        return match && decodeURIComponent(match[1]);
    }

    function hasOption(opt, queryString) {
        var s = queryString || location.search;
        var re = new RegExp('(?:^|[&?])' + opt + '(?:[=]([^&]*))?(?:$|[&])', 'i');
        var m = re.exec(s);

        return m ? (m[1] === undefined || m[1] === '' ? true : m[1]) : false;
    }

    function getCookieValue(name) {
        var cookies = document.cookie.split('; '),
            i = cookies.length,
            cookie, value;

        while (i--) {
            cookie = cookies[i].split('=');
            if (cookie[0] === name) {
                value = cookie[1];
            }
        }

        return value;
    }
    var theme = $.cookie("selectedCss") || 'neptune',
        neptune = (theme === 'neptune'),
        repoDevMode = getCookieValue('ExtRepoDevMode');
    var scripts = document.getElementsByTagName('script'),
        localhostTests = [
            /^localhost$/,
            /\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(:\d{1,5})?\b/ // IP v4
        ],
        host = window.location.hostname,
        isDevelopment = null,
        queryString = window.location.search,
        test, path, i, ln, scriptSrc, match;

    for (i = 0, ln = scripts.length; i < ln; i++) {
        scriptSrc = scripts[i].src;

        match = scriptSrc.match(/bootstrap\.js$/);

        if (match) {
            path = scriptSrc.substring(0, scriptSrc.length - match[0].length);
            break;
        }
    }

    if (queryString.match('(\\?|&)debug') !== null) {
        isDevelopment = true;
    }
    else if (queryString.match('(\\?|&)nodebug') !== null) {
        isDevelopment = false;
    }

    if (isDevelopment === null) {
        for (i = 0, ln = localhostTests.length; i < ln; i++) {
            test = localhostTests[i];

            if (host.search(test) !== -1) {
                isDevelopment = true;
                break;
            }
        }
    }

    if (isDevelopment === null && window.location.protocol === 'file:') {
        isDevelopment = false;
    }
    if (host.indexOf("localhost") >= 0) isDevelopment = true;
    else isDevelopment = false;
    if (!path) path = "javascript/extjs4/";
    document.write('<script type="text/javascript" charset="UTF-8" src="' +
        path + 'ext-all' + (isDevelopment ? '-dev' : '') + '.js"></script>');

    if (neptune) {
        neptunePath = path + 'packages/ext-theme-neptune/build/ext-theme-neptune' +
                                    (isDevelopment ? '-dev' : '') + '.js';
        if (repoDevMode && window.ActiveXObject) {
            Ext = {
                _beforereadyhandler: function () {
                    Ext.Loader.loadScript({ url: neptunePath });
                }
            };
        } else {
            document.write('<script type="text/javascript" src="' + neptunePath + '" defer></script>');
        }
    }
    //$(document).find("#JS_neptune").attr("src", path + 'packages/ext-theme-neptune/build/ext-theme-neptune' + (neptune ? '' : '-blank') + '.js');
})();
