!(function(window, $, undefined){
    'use strict';

    var conf = {
        url: window.basePath + '/service/location',
        data: []
    };

    function location(){};

    function getLocation(pid, callback){
        if (conf.data[pid]) {
            !callback||callback();
            return;
        }
        $.ajax({
            url: conf.url,
            data: {pid: pid},
            dataType: 'json'
        }).done(function(data){
            conf.data[pid] = data;
            !callback||callback();
        });
    };

    function renderOption(dom, pid, callback){
        getLocation(pid, function(){
            var html = '<option value="">请选择';
            var data = conf.data[pid];
            for(var i=0; i<data.length; i++) html += '<option value="'+data[i].id+'">' + data[i].name;
            dom.html(html);
            !callback||callback();
        });
    }

    location.prototype = {
        renderOption: renderOption
    };

    window.locationKit = new location();
})(window, jQuery);