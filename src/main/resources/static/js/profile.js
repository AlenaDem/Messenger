const url = 'http://localhost:8081';

$('#avatar-form').submit(function(e) {
    e.preventDefault();
    jQuery.ajax( {
        url: '/avatar',
        type: 'post',
        data: new FormData($('#avatar-form')[0]),
        processData: false,
        contentType: false,
        enctype: 'multipart/form-data',
        success: function() {
            setTimeout(function() {
                location.reload();
            }, 500);
         }
    });
});

$('#add-friend').on('click', addFriend);
$('#change-role-btn').on('click', changeRole);

function addFriend() {
    jQuery.ajax( {
        url: '/addfriend/' + userid,
        type: 'post'
    });
}

function changeRole() {
    jQuery.ajax( {
        url: '/change_role',
        type: 'post',
        data: {
            user_id: userid,
            promote:  ready_to_promote,
        }
    });
}