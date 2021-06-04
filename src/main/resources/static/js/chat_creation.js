$("#create-chat-btn").click(function () {
    $("#create-chat").css('display', 'block');
    $("#form-chat-type").trigger("reset");
});

$('.close-create-chat').click(function () {
    $("#create-chat").css('display', 'none');
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