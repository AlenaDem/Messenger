
var create_window = document.getElementById("create-chat");
var btn = document.getElementById("create-chat-btn");
var close_span = document.getElementsByClassName("close-create-chat")[0];

btn.onclick = function () {
    create_window.style.display = "block";
}

close_span.onclick = function () {
    create_window.style.display = "none";
}

window.onclick = function (event) {
    if (event.target == create_window) {
        create_window.style.display = "none";
    }
}

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
}

function newChatCreated(response) {
    fetchChats();
}