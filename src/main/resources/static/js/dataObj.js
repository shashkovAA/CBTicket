document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.datepicker');
    var options = {
        autoClose: true,
        showClearBtn: false,
        defaultDate: new Date(),
        format : "yyyy-mm-dd",
        firstDay : 1
    };
    var instances = M.Datepicker.init(elems, options);
});

document.addEventListener('DOMContentLoaded', function() {
      var elems = document.querySelectorAll('.timepicker');
      var options = {
                      twelveHour: false,
                      defaultTime: '00:00'
                  };
      var instances = M.Timepicker.init(elems, options);
});

document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('select.sel_custom');
    var options = {};
    var instances = M.FormSelect.init(elems,options);
});


