let $chatHistory;
let $chatHistoryList;
let $sendMsgButton;
let $msgTextArea;

var recieveTemplate = Handlebars.compile($("#message-template").html());
var sendTemplate = Handlebars.compile($("#my-message-template").html());
var imageId = 0;

init();

function init() {
    cacheDOM();
    initListeners();
}

function initListeners() {
    $sendMsgButton.on('click', addMessage.bind(this));
    $msgTextArea.on('keyup', addMessageEnter.bind(this));
}

function cacheDOM() {
    $chatHistory = $('.chat-history');
    $sendMsgButton = $('#send-btn');
    $msgTextArea = $('#message-to-send');
    $chatHistoryList = $('#msg-list-ul');
}

function renderMessages(messages) {
    $chatHistoryList.html('');
    messages.forEach(msg => {
        renderMessageCloud(msg);
    });
    scrollToBottom();
}

function renderMessageCloud(msg) {
    var context = {
        id: msg.id,
        time: msg.time,
        username: msg.username,
        message: msg.text,
        type: msg.type,
        filename: msg.type === "file" ? msg.filename : "",
        imagename: msg.type === "image" ? msg.filename : ""
    };
    $chatHistoryList.append(generateTemplate(context));
    scrollToBottom();
}

function generateTemplate(context) {
    var template = (context.username === currentUser ? sendTemplate(context) : recieveTemplate(context));
    var wrapper= document.createElement('div');
    wrapper.innerHTML = template;
    if (context.type == 'text') {
        var img = $(wrapper).find('.image-holder');
        img.remove();
        var file = $(wrapper).find('.file-holder');
        file.remove(); 
    }
    if (context.type == 'image') {
        var file = $(wrapper).find('.file-holder');
        file.remove();

        var img = $(wrapper).find('img')[0];
        img.onload = function() {
            scrollToBottom();
        };
    }
    if (context.type == 'file') {
        var img = $(wrapper).find('.image-holder');
        img.remove(); 
    }

    return wrapper.innerHTML;
}

function sendMessage(message) {
    if (currentChat === null)
        return;

    if (fileReadyToSend == false) {
        window.setTimeout(sendMessage(message), 100);
    }
    else {
        stompClient.send("/app/chat/message/" + currentChat, {}, JSON.stringify({
            text: message,
            file_name: uploadedFileName,
            file_type: uploadedFileType,
            file_content: uploadedFileContent
        }));
        resetUploadFile();
        $msgTextArea.val('');
    }
}

function scrollToBottom() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight);
}

function addMessage() {
    sendMessage($msgTextArea.val());
}

function addMessageEnter(event) {
    if (event.keyCode === 13) {
        addMessage();
    }
}