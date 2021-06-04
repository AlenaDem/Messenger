const url = 'http://localhost:8081';
let friendTemplate = Handlebars.compile($("#profile-friend-template").html());
fetchFriends();

$('#start-find-user').on("click", findUsers);

function findUsers() {
    var name = document.getElementById('find-user-input').value.toString();

    if (name == "")
        return;

    $.get(url + "/getUsers/" + name, function (users) {
        $userList = $('#found-users-ul');
        $userList.html('');
        var html = $("#friends-user-template").html();
        var template = Handlebars.compile(html);

        users.forEach(user => {
            var context = {
                id: user.id,
                username: user.username
            };
            $userList.append(template(context));
        });
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