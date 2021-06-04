const url = 'http://localhost:8081';
let friendTemplate = Handlebars.compile($("#profile-friend-template").html());

if (myprofile) {
    fetchFriends();
}

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
            $(".avatar").attr("src", "/avatar");
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

function fetchFriends() {
    jQuery.get(url + "/fetchFriends", function (friends) {
        $confFriends = $('#conf-friends-ul');
        $pendFriends = $('#pend-friends-ul');
        $confFriends.html('');
        $pendFriends.html('');

        friends.forEach(friend => {
            var context = {
                id: friend.id,
                username: friend.name,
                confirmed: friend.confirmed
            };
            if (friend.confirmed)
                $confFriends.append(generateFriendTemplate(context));
            else
                $pendFriends.append(generateFriendTemplate(context));
        });
    });
}

function generateFriendTemplate(context) {
    var wrapper= document.createElement('div');
    wrapper.innerHTML = friendTemplate(context);

    var btn = $(wrapper).find('button');
    if (context.confirmed) {
        btn.html("Удалить");
        btn.attr('onclick', `removeFriend(${context.id})`);
    }
    else {
        btn.html("Подтвердить");
        btn.attr('onclick', `confirmFriend(${context.id})`);
    }

    return wrapper.innerHTML;
}

function removeFriend(userid) {
    jQuery.ajax( {
        url: '/removefriend/' + userid,
        type: 'post',
        success: function() {
            console.log('Removed from friends' + userid);
            fetchFriends();
        }
    });
}

function confirmFriend(userid) {
    jQuery.ajax( {
        url: '/confirmfriend/' + userid,
        type: 'post',
        success: function() {
            console.log('Confirmed friend' + userid);
            fetchFriends();
        }
    });
}