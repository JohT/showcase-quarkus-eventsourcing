function receiveNewEvent(event) {
	 if (!event.origin.startsWith("http://localhost")) {
		console.log('Origin is not http://localhost: ' + event.origin);
		return;
	 }
	document.getElementById("all-events").innerHTML += event.data + "<br>";
}

function streamAllEvents() {

	var source = new EventSource("nicknameevents");
	source.onmessage = function(event) {
		receiveNewEvent(event);
	};
	source.onerror = function(event) {
		if (event.readyState == EventSource.OPENED) {
			console.log(event.data);
		  }
	};
}

$(document).ready(function() {
	streamAllEvents();
});