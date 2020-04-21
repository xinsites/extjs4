/**
* This file includes the required ext-all js and css files based upon "theme" and "direction"
* url parameters.  It first searches for these parameters on the page url, and if they
* are not found there, it looks for them on the script tag src query string.
* For example, to include the neptune flavor of ext from an index page in a subdirectory
* of extjs/examples/:
* <script type="text/javascript" src="../../examples/shared/include-ext.js?theme=neptune"></script>
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

    var scriptEls = document.getElementsByTagName('script'),
        path = scriptEls[scriptEls.length - 1].src,
        rtl = getQueryParam('rtl'),
        theme = $.cookie("selectedCss") || 'neptune',
        includeCSS = !hasOption('nocss', path),
        neptune = (theme === 'neptune'),
        repoDevMode = getCookieValue('ExtRepoDevMode'),
        suffix = [],
        i = 3,
        neptunePath;

    rtl = rtl && rtl.toString() === 'true'

    while (i--) {
        path = path.substring(0, path.lastIndexOf('/'));
    }

    if (theme && theme !== 'classic') {
        suffix.push(theme);
    }
    if (rtl) {
        suffix.push('rtl');
    }

    suffix = (suffix.length) ? ('-' + suffix.join('-')) : '';
    //    if (includeCSS) {
    //        document.write('<link rel="stylesheet" type="text/css" href="' + path + '/resources/css/ext-all' + suffix + '-debug.css"/>');
    //    }
    //    document.write('<script type="text/javascript" src="' + path + '/ext-all' + (rtl ? '-rtl' : '') + '.js"></script>');

    if (neptune) {
        //        neptunePath = (repoDevMode ? path + '/..' : path) +
        //            '/packages/ext-theme-neptune/build/ext-theme-neptune' +
        //            (repoDevMode ? '-dev' : '') + '.js';
        //        if (repoDevMode && window.ActiveXObject) {
        //            Ext = {
        //                _beforereadyhandler: function () {
        //                    Ext.Loader.loadScript({ url: neptunePath });
        //                }
        //            };
        //        } else {
        //            document.write('<script type="text/javascript" src="' + neptunePath + '" defer></script>');
        //        }
    }

})();