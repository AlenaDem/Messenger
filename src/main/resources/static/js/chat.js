const url = 'http://localhost:8081';
var stompClient = null;
var currentChat = null;
var currentUser = null
var subscribedChats = new Set();
init();

function init() {
    if (stompClient === null) {
        console.log("Connecting to chat...")
        let socket = new SockJS(url + '/msg');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log("Connected to: " + frame);
            console.log("Your username: " + frame.headers['user-name']);
            currentUser = frame.headers['user-name'];

            fetchChats();
            stompClient.subscribe("/topic/chat/created/" + currentUser, newChatCreated);
            stompClient.subscribe("/topic/chat/updated/" + currentUser, fetchChats);
            stompClient.subscribe("/topic/chat/leave/" + currentUser, onLeaveChat);
            stompClient.subscribe("/topic/chat/joined/" + currentUser, onJoinChat);
            stompClient.subscribe("/topic/chat/kicked/" + currentUser, onUserKicked);
        });
    }

    initListeners();
}

function initListeners() {
    $('#leave-btn').on("click", leaveChat.bind(this));
}

function subscribeChat(chatId) {
    chatId = chatId.toString();
    if (!subscribedChats.has(chatId)) {
        stompClient.subscribe("/topic/chat/message/created/" + chatId, newChatMessage);
        stompClient.subscribe("/topic/chat/message/deleted/" + chatId, onMessageDeleted);
        stompClient.subscribe("/topic/chat/deleted/" + chatId, onChatDeleted);
        subscribedChats.add(chatId);
    }
}

function unsubscribeChat(chatId) {
    chatId = chatId.toString();
    if (subscribedChats.has(chatId)) {
        stompClient.unsubscribe("/topic/chat/message/created/" + chatId, newChatMessage);
        stompClient.unsubscribe("/topic/chat/message/deleted/" + chatId, onMessageDeleted);
        stompClient.unsubscribe("/topic/chat/deleted/" + chatId, onChatDeleted);
        subscribedChats.delete(chatId);
    }
}

function newChatMessage(response) {
    let data = JSON.parse(response.body);
    if (currentChat != null && currentChat.toString() === data.chat_id.toString())
        renderMessageCloud(data);
    else
        showNewMessageIndicator(data.chat_id, true)
}

function onMessageDeleted(response) {
    var chatId = response.body;
    if (currentChat == chatId)
        loadChat(chatId);
}

function showNewMessageIndicator(chatId, show) {
    var tile = document.getElementById('chat-tile-' + chatId);
    if (tile === null)
        return;
    var indicator = tile.querySelector('.newMessageIndicator');
    if (indicator != null) {
        if (show) {
            indicator.style.visibility = 'visible'
            return
        }
        indicator.style.visibility = 'hidden'
    }
}

function fetchChats() {
    jQuery.get(url + "/fetchChats", function (chats) {
        $chatList = $('#chat-list-ul');
        $chatList.html('');
        var html = $("#chat-tile-template").html();
        var template = Handlebars.compile(html);

        chats.forEach(chat => {
            subscribeChat(chat.id);
            var context = {
                id: chat.id,
                name: chat.name
            };
            $chatList.append(template(context));
        });
    });
}

function loadChat(chatId) {
    $.get(url + "/fetchMessages/" + chatId, function (messages) {
        renderMessages(messages);
    });
}

function updateChatBox() {
    var visible = currentChat !== null;
    document.getElementById("chat-box").style.display = (visible ? 'block' : 'none');
}

function selectChat(chatId, chatName) {
    chatId = chatId.toString();
    if (chatId === currentChat)
        return;

    console.log("Open chat: " + chatName);
    currentChat = chatId;
    loadChat(chatId);
    showNewMessageIndicator(chatId, false);
    $('#chat-title').html('');
    $('#chat-title').append(chatName);
    updateChatBox()
}

function leaveChat() {
    if (currentChat === null)
        return;
    stompClient.send("/app/chat/leave/" + currentChat, {});
}

function onLeaveChat(response) {
    var chatId = response.body;
    unsubscribeChat(chatId);
    fetchChats();
    currentChat = null;
    updateChatBox();
}
