let $chatHistory;
let $chatHistoryList;
let $sendMsgButton;
let $msgTextArea;

function init() {
    cacheDOM();
    bindEvents();
}

function bindEvents() {
    $sendMsgButton.on('click', addMessage.bind(this));
    $msgTextArea.on('keyup', addMessageEnter.bind(this));
}

function cacheDOM() {
    $chatHistory = $('.chat-history');
    $sendMsgButton = $('#sendBtn');
    $msgTextArea = $('#message-to-send');
    $chatHistoryList = $chatHistory.find('ul');
}

function renderIncomingMessage(message, userName) {
    scrollToBottom();
    
    var templateResponse = Handlebars.compile($("#message-response-template").html());
    var contextResponse = {
        inMessage: message,
        time: getCurrentTime(),
        userName: userName
    };

    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        scrollToBottom();
    }.bind(this), 1500);
}

function sendMessage(message) {
    sendMsg(message);
    scrollToBottom();
    if (message.trim() !== '') {
        var template = Handlebars.compile($("#message-template").html());
        var context = {
            outMessage: message,
            time: getCurrentTime(),
        };

        $chatHistoryList.append(template(context));
        scrollToBottom();
        $msgTextArea.val('');
    }
}

function scrollToBottom() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight);
}

function getCurrentTime() {
    //return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
    return new Date().toISOString().slice(0,10);
}

function addMessage() {
    sendMessage($msgTextArea.val());
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addMessage();
    }
}

init();

