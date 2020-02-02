/**
 * @fileOverview "Event"" part of the simple web client of the event sourcing showcase
 * @version ${project.version}
 */

/**
 * eventsourcing_showcase namespace declaration.
 * It contains all modules for the showcase.
 * @default {}
 */
var eventsourcing_showcase = eventsourcing_showcase || {};

/**
 * The EventController module defines what the UI can do.
 * It encapsulates DOM access.
 * It translates domain actions to DOM actions.
 * It may not return DOM elements.
 *
 * @namespace
 */
eventsourcing_showcase.EventController = (function() {
  "use strict";
  var baseUri = "http://localhost:8080";
  var eventSource;

  function receiveNewEvent(event) {
    if (!event.origin.startsWith(baseUri)) {
      console.log("Origin is not " + baseUri + ": " + event.origin);
      return;
    }
    var listOfNicknameChanges = document.getElementById("nicknamechanges");
    var nicknameChangeEntry = document.createElement("li");
    var receivedObject = JSON.parse(event.data);
    var nickname = receivedObject.nickname.value;
    if (nickname !== "") {
      nicknameChangeEntry.innerText = nickname;
      listOfNicknameChanges.appendChild(nicknameChangeEntry);
    }
  }

  function streamAllEvents() {
    eventSource = new EventSource(baseUri + "/nicknameevents");
    eventSource.onmessage = function(event) {
      receiveNewEvent(event);
    };
    eventSource.onerror = function(event) {
      if (event.readyState === EventSource.OPENED) {
        console.log(event.data);
      }
    };
  }

  function close() {
	  eventSource.close();
  }

  /**
   * Public interface
   * @scope eventsourcing_showcase.EventController
   */
  return {
    load: streamAllEvents,
    close: close
  };
})();
