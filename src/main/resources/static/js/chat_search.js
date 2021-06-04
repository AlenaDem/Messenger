$("#find-chat-btn").click(function () {
    $("#find-chat").css('display', 'block');
    $("#found-chat-ul").html('');
    findChat();
});

$('.close-find-chat').click(function () {
    $("#find-chat").css('display', 'none');
});

$('#start-find-chat').on("click", findChat);

function findChat() {
    var chat_name = document.getElementById('find-chat-input').value.toString();

    if (chat_name == "")
        chat_name = "*";

    $.get(url + "/getChats/" + chat_name, function (chats) {
        $chatList = $('#found-chat-ul');
        $chatList.html('');
        var html = $("#find-chat-tile-template").html();
        var template = Handlebars.compile(html);

        chats.forEach(chat => {
            var context = {
                id: chat.id,
                name: chat.name,
                count: chat.user_count
            };
            $chatList.append(template(context));
        });
    });
}

function joinChat(chatId) {
    console.log("Enter to: " + chatId);
    stompClient.send("/app/chat/join/" + chatId, {});
    $("#find-chat").css('display', 'none');
}

function onJoinChat(response) {
    let data = JSON.parse(response.body);
    var chatId = data.chat_id;
    var chatName = data.chat_name;
    fetchChats();
    selectChat(chatId, chatName);
}