$("#manage-btn").click(function () {
    $("#manage-chat").css('display', 'block');
    $("#chat-users-ul").html('');
    $("#delete-chat-btn").hide();
    fetchManageSettings(currentChat);
    fetchUsers(currentChat);
});

$('.close-manage-chat').click(function () {
    $("#manage-chat").css('display', 'none');
});

$('.close-friend-list').click(function () {
    $("#friend-list").css('display', 'none');
});

$('#add-user-btn').click(addToChat);
$("#delete-chat-btn").click(deleteChat);

let friendTemplate = Handlebars.compile($("#friend-template").html());

function fetchManageSettings(chatId) {
    jQuery.get(url + "/fetchChatSettings/" + chatId, function (response) {
        $("#delete-chat-btn").show();
        if (response.can_delete)
            $("#delete-chat-btn").show();
    });
}

function fetchUsers(chatId) {
    jQuery.get(url + "/fetchUsers/" + chatId, function (users) {
        $userList = $("#chat-users-ul");
        $userList.html('');
        var html = $("#chat-user-template").html();
        var template = Handlebars.compile(html);

        users.forEach(user => {
            var context = {
                id: user.id,
                username: user.username
            };
            html = template(context);
            var wrapper = document.createElement('div');
            wrapper.innerHTML = html;

            if (user.managable == false) {
                let btn = $(wrapper).find('button');
                btn.remove();
            }

            if (user.username === currentUser) {
                let btn = $(wrapper).find('button');
                btn.html('Выйти');
                let nameSpan = $(wrapper).find('.chat-user-name');
                nameSpan.html(nameSpan.html() + ' (Вы)');
            }

            $("#btnAddProfile").html('Save');
            $userList.append(wrapper.innerHTML);
        });
    });
}

function kickUser(userId, username) {
    if (currentChat === null)
        return;

    if (username == currentUser) {
        leaveChat();
        $("#manage-chat").css('display', 'none');
        return;
    }

    stompClient.send("/app/chat/kick", {}, JSON.stringify({
        chat_id: currentChat,
        user_id: userId
    }));
}

function onUserKicked(response) {
    var chatId = response.body;
    if (currentChat == chatId) {
        fetchUsers(chatId);
    }
}

function addToChat() {
    $("#manage-chat").css('display', 'none');
    $("#friend-list").css('display', 'block');
    fetchFriendsToAdd();
}

function fetchFriendsToAdd() {
    jQuery.get(url + "/fetchFriends", function (friends) {
        $confFriends = $('#friend-list-ul');
        $confFriends.html('');

        console.log($("#friend-template").html());

        friends.forEach(friend => {
            var context = {
                id: friend.id,
                username: friend.name,
            };
            if (friend.confirmed)
                $confFriends.append(generateFriendTemplate(context));
        });
    });
}

function generateFriendTemplate(context) {
    var wrapper= document.createElement('div');
    wrapper.innerHTML = friendTemplate(context);
    var btn = $(wrapper).find('button');
    btn.html("Добавить");
    btn.attr('onclick', `addUserToChat(${context.id})`);
    return wrapper.innerHTML;
}

function addUserToChat(userId) {
    if (currentChat == null)
        return;

    stompClient.send("/app/chat/add", {}, JSON.stringify({
        chat_id: currentChat,
        user_id: userId
    }));
}

function deleteChat() {
    $("#manage-chat").css('display', 'none');
    stompClient.send("/app/chat/delete/" + currentChat, {});
}

function onChatDeleted(response) {
    let data = JSON.parse(response.body);
    var chatId = data.chat_id;
    unsubscribeChat(chatId);
    fetchChats();
    if (currentChat == chatId)
        currentChat = null;
    updateChatBox();
}