const url = 'http://localhost:8081';
let stompClient = null;
let selectedUser;
let newMessages = new Map();
let currentChat = null;
let currentUser = null

function init() {
    if (stompClient === null) {
        console.log("Connecting to chat...")
        let socket = new SockJS(url + '/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log("Connected to: " + frame);
            console.log("Your username: " + frame.headers['user-name']);
            currentUser = frame.headers['user-name'];
            
        });
        preFetch();
    }
}

function preFetch() {
    fetchChats();
}

function activeChatUpdated(chatId) {
    currentChat = chatId;
    stompClient.subscribe("/topic/messages/" + currentChat, newChatMessage);
}

function connectToChat(userName) {
    console.log("Connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected to: " + frame);
        stompClient.subscribe("/topic/messages/" + userName, newChatMessage);
    });
}

function newChatMessage(response) {
    let data = JSON.parse(response.body);
    let chatId = data.chat_id;
    if (currentChat === data.chat_id && currentUser != data.sender) {
        renderIncomingMessage(data.message, data.sender);
    } else {
        newMessages.set(data.fromLogin, data.message);
        $('#userNameAppender_' + data.fromLogin).append('<span id="newMessage_' + data.fromLogin + '" style="color: red">+1</span>');
    }
}

function sendMsg(text) {
    if (currentChat !== null) {
        stompClient.send("/app/chat/" + currentChat, {}, JSON.stringify({
            message: text
        }));
    }
}

function registration() {
    let userName = document.getElementById("userName").value;
    $.get(url + "/registration/" + userName, function (response) {
        connectToChat(userName);
    }).fail(function (error) {
        if (error.status === 400) {
            alert("Login is already busy!")
        }
    })
}

function fetchChats() {
    $.get(url + "/fetchChats", function (response) {
        let chats = response;
        let chatTemplateHTML = "";
        for (let i = 0; i < chats.length; i++) {
            chatTemplateHTML = chatTemplateHTML + 
            '<a href="#" onclick="selectChat(\'' + chats[i].id + '\', \'' + chats[i].name + '\')">' +
                    '<li class="clearfix">' +
                        '<div class="about">' +
                            '<div id="chatName_' + chats[i].id + '" class="name">' + chats[i].name + '</div>' +
            '</div></li></a>';
        }
        $('#chatList').html(chatTemplateHTML);
    });
}

function selectChat(chatId, chatName) {
    console.log("Open chat: " + chatName);
    activeChatUpdated(chatId);

    $('#selectedChatId').html('');
    $('#selectedChatId').append(chatName);
}

function selectUser(chatName) {
    console.log("Open chat: " + chatName);
    currentChat = chatName
    // let isNew = document.getElementById("newMessage_" + userName) !== null;
    // if (isNew) {
    //     let element = document.getElementById("newMessage_" + userName);
    //     element.parentNode.removeChild(element);
    //     render(newMessages.get(userName), userName);
    // }

}