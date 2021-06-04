$("#create-chat-btn").click(function () {
    $("#create-chat").css('display', 'block');
    $("#form-chat-type").trigger("reset");
});

$('.close-create-chat').click(function () {
    $("#create-chat").css('display', 'none');
});

$('.close-select-friend').click(function () {
  $("#select-friend").css('display', 'none');
});

$("#form-chat-type").change(function() {
    if ($(this).val() == "PERSONAL") {
      $('#username-field').show();
    } else {
      $('#username-field').hide();
    }
  });
$("#form-chat-type").trigger("change");

function createChat() {
    var chat_name = document.getElementById('form-chat-name').value;
    var chat_type = document.getElementById('form-chat-type').value;
    var chat_user = document.getElementById('form-chat-user').value;

    if (chat_name == "")
      return;

    if (chat_type == "PERSONAL" && chat_user == "")
      return;

    stompClient.send("/app/chat/create", {}, JSON.stringify(
        {
        chatname: chat_name,
        username: chat_user,
        type: chat_type
        }
    ));
    $("#create-chat").css('display', 'none');
}

function newChatCreated(response) {
    fetchChats();
}

function selectFriend() {
  jQuery.get(url + "/fetchFriends", function (friends) {
    $("#select-friend").css('display', 'block');
    $friends = $('#select-friend-ul');
    $friends.html('');
    var friendTemplate = Handlebars.compile($("#select-friend-template").html());

    friends.forEach(friend => {
        var context = {
            id: friend.id,
            username: friend.name,
        };
        if (friend.confirmed)
            $friends.append(friendTemplate(context));
    });
  });
}

function friendSelected(username) {
  $('#form-chat-user').val(username);
  $("#select-friend").css('display', 'none');
}