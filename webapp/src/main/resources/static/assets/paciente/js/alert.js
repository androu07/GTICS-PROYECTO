(function () {
    'use strict';

    var alertNodeList = document.querySelectorAll('.alert-dismissible .btn-close');
    for (var i = 0; i < alertNodeList.length; i++) {
        alertNodeList[i].addEventListener('click', function (event) {
            var alert = this.closest('.alert');
            if (alert) {
                alert.classList.remove('show');
                alert.classList.add('fade');
                setTimeout(function () {
                    alert.remove();
                }, 150);
            }
        });
    }
})();