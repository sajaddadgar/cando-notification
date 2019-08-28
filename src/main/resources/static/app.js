var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/notif');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, "1",  function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function subscribe() {
	stompClient.subscribe('/user/notification', function (auctionStatus) {
		showGreeting(auctionStatus);
	});




}

function unsubscribe() {
    stompClient.unsubscribe('/topic/auction/' + $("#auction").val());
}

function sendName() {
    stompClient.send("/app/bookmark", {}, JSON.stringify({'dueDate': $("#name").val()}));
}

function broadcast() {
    stompClient.send("/app/start", {}, "");
}

function showGreeting(maxBid, activeUserCount) {
    $("#greetings").append("<tr><td>" + maxBid, activeUserCount + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#subscribe" ).click(function() { subscribe(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#broadcast" ).click(function() { broadcast(); });
    $( "#unsubscribe" ).click(function() { unsubscribe(); });
});