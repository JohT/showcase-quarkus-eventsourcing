/**
 * @fileOverview "Account"" part of the simple web client of the event sourcing showcase
 * @version ${project.version}
 */

/**
 * eventsourcing_showcase namespace declaration.
 * It contains all modules for the showcase.
 * @default {}
 */
var eventsourcing_showcase = eventsourcing_showcase || {};

/**
 * The AccountUI module defines what the UI can do.
 * It encapsulates DOM access.
 * It translates domain actions to DOM actions.
 * It may not return DOM elements.
 *
 * @namespace
 */
eventsourcing_showcase.AccountUI = (function() {
  "use strict";

  function getCreateAccountButton() {
    return document.getElementById("create_account");
  }

  function addEventListenerForAccountCreation(callback) {
    getCreateAccountButton().addEventListener("click", callback);
  }

  function actionStarted(element) {
    element.disabled = true;
    element.classList.add("spinner");
    element.classList.remove("noaction");
    element.classList.add("action");
  }

  function actionSuccessful(element) {
    element.disabled = false;
    element.classList.remove("spinner");
    element.classList.remove("noaction");
    element.classList.add("action");
  }

  function actionFailed(element) {
    element.disabled = false;
    element.classList.remove("spinner");
    element.classList.remove("action");
    element.classList.add("noaction");
  }

  function accountCreationRequested() {
    actionStarted(getCreateAccountButton());
  }

  function accountCreatedSuccessfully() {
    actionSuccessful(getCreateAccountButton());
    forEachNicknamePanelElement(function(element) {
      element.classList.remove("hide");
    });
  }

  function accountCreationFailed() {
    actionFailed(getCreateAccountButton());
    forEachNicknamePanelElement(function(element) {
      element.classList.add("hide");
    });
  }

  function getAccountIdField() {
    return document.getElementById("accountid");
  }

  function getAccountId(accountId) {
    return getAccountIdField().value;
  }

  function setAccountId(accountId) {
    getAccountIdField().value = accountId;
  }

  function resetAccountId() {
    getAccountIdField().value = "";
  }

  function forEachNicknamePanelElement(elementfunction) {
    [].forEach.call(document.getElementsByClassName("nickname_panel"), function(element) {
      elementfunction(element);
      [].forEach.call(element.getElementsByTagName("*"), elementfunction);
    });
  }

  function getNicknameChangeButton() {
    return document.getElementById("nickname_confirm");
  }

  function addEventListenerForNicknameChange(callback) {
    getNicknameChangeButton().addEventListener("click", callback);
  }

  function getNicknameResetButton() {
    return document.getElementById("nickname_reset");
  }

  function addEventListenerForNicknameReset(callback) {
    getNicknameResetButton().addEventListener("click", callback);
  }

  function getNicknameInput() {
    return document.getElementById("nickname");
  }

  function getNickname() {
    return getNicknameInput().value;
  }

  function setNickname(newValue) {
    return (getNicknameInput().value = newValue);
  }

  function nicknameQuerySuccessful() {
    getNicknameInput().classList.remove("noaction");
  }

  function nicknameQueryFailed() {
    getNicknameInput().classList.add("noaction");
  }

  function getNicknameOld() {
    return document.getElementById("nickname_old");
  }

  function setNicknameOld(newValue) {
    getNicknameOld().value = newValue;
  }

  function nicknameChangeRequested() {
    actionStarted(getNicknameChangeButton());
  }

  function nicknameUpdated() {
    actionSuccessful(getNicknameChangeButton());
    getNicknameInput().classList.remove("noaction");
    setNicknameOld(getNickname());
  }

  function nicknameChangeFailed() {
    actionFailed(getNicknameChangeButton());
  }

  function resetNickname() {
    getNicknameInput().value = getNicknameOld().value;
    nicknameUpdated();
  }

  function getNicknameReplayButton() {
    return document.getElementById("replay_nicknames");
  }

  function getNicknameChangesList() {
    return document.getElementById("nicknamechanges");
  }

  function addEventListenerForNicknameReplay(callback) {
    getNicknameReplayButton().addEventListener("click", callback);
  }

  function resetNicknameChanges() {
    var list = getNicknameChangesList();
    if (list === null) {
      return;
    }
    while (list.firstChild) {
      list.removeChild(list.firstChild);
    }
  }

  function nicknameReplayRequested() {
    resetNicknameChanges();
    actionStarted(getNicknameReplayButton());
  }

  function nicknameReplaySuccessful() {
    actionSuccessful(getNicknameReplayButton());
  }

  function nicknameReplayFailed() {
    actionFailed(getNicknameReplayButton());
  }

  /**
   * Public interface
   * @scope eventsourcing_showcase.AccountUI
   */
  return {
    addEventListenerForAccountCreation: addEventListenerForAccountCreation,
    accountCreationRequested: accountCreationRequested,
    accountCreatedSuccessfully: accountCreatedSuccessfully,
    accountCreationFailed: accountCreationFailed,
    getAccountId: getAccountId,
    setAccountId: setAccountId,
    resetAccountId: resetAccountId,
    getNickname: getNickname,
    setNickname: setNickname,
    addEventListenerForNicknameChange: addEventListenerForNicknameChange,
    addEventListenerForNicknameReset: addEventListenerForNicknameReset,
    nicknameChangeRequested: nicknameChangeRequested,
    nicknameUpdated: nicknameUpdated,
    nicknameChangeFailed: nicknameChangeFailed,
    nicknameQuerySuccessful: nicknameQuerySuccessful,
    nicknameQueryFailed: nicknameQueryFailed,
    resetNickname: resetNickname,
    addEventListenerForNicknameReplay: addEventListenerForNicknameReplay,
    nicknameReplayRequested: nicknameReplayRequested,
    nicknameReplaySuccessful: nicknameReplaySuccessful,
    nicknameReplayFailed: nicknameReplayFailed
  };
})();

/**
 * Local REST Client to create, prepare and preset the XMLHttpRequest.
 * It encapsulates XMLHttpRequest setup in a general, domain independent way.
 * It does not use the DOM at all.
 * @namespace
 */
eventsourcing_showcase.RestClient = (function() {
  "use strict";

  var requestTimeout = 10000;

  function isHttpSuccessful(request) {
    return request.status >= 200 && request.status <= 299 && request.readyState === 4;
  }

  function getHeaderMap(headerstring) {
    var headerlines = headerstring.trim().split(/[\r\n]+/);
    var headers = {};
    headerlines.forEach(function(line) {
      var parts = line.split(": ");
      var header = parts.shift();
      var value = parts.join(": ");
      headers[header] = value;
    });
    return headers;
  }

  function isEmpty(str) {
    return !str || 0 === str.length;
  }

  function parseResponse(request, onSuccess, onError) {
    if (isHttpSuccessful(request)) {
      try {
        var data = {};
        if (!isEmpty(request.responseText)) {
          data = JSON.parse(request.responseText);
        }
        data.headers = getHeaderMap(request.getAllResponseHeaders());
        onSuccess(data);
      } catch (e) {
        onError(e);
      }
    } else {
      onError(new Error("unexpected_status_code_" + request.status + "_" + request.statusText));
    }
  }

  function prepareRequest(onSuccess, onError) {
    var request = new XMLHttpRequest();
    request.timeout = requestTimeout;
    request.onerror = function(error) {
      onError(new Error("network_error"));
    };
    request.ontimeout = function(error) {
      onError(new Error("timeout_error"));
    };
    request.onload = function() {
      parseResponse(request, onSuccess, onError);
    };
    return request;
  }

  /**
   * Public interface
   * @scope eventsourcing_showcase.RestClient
   */
  return {
    prepareRequest: prepareRequest
  };
})();

/**
 * The AccountRepository creates, reads and updates Accounts.
 * It encapsulates RestClient calls.
 * It does not use the DOM at all.
 *
 * @namespace
 */
eventsourcing_showcase.AccountRepository = (function() {
  "use strict";

  var baseUri = "http://localhost:8080";
  var accountUri = baseUri + "/accounts";
  var nicknamesUri = baseUri + "/nicknames";
  var restClient = eventsourcing_showcase.RestClient;

  function amendAccountId(wrappedCallback) {
    return function(data) {
      data.accountId = getLastUrlPath(data.headers["location"]);
      wrappedCallback(data);
    };
  }

  function getLastUrlPath(url) {
    return url.substring(url.lastIndexOf("/") + 1, url.length);
  }

  function setRestClient(newRestClient) {
    restClient = newRestClient;
  }
  function getBaseUri() {
    return baseUri;
  }
  function createAccount(onAccountCreated, onServiceError) {
    var request = restClient.prepareRequest(amendAccountId(onAccountCreated), onServiceError);
    request.open("POST", accountUri);
    request.send();
  }
  function queryNickname(accountId, onNicknameChangedSuccessfully, onServiceError) {
    var request = restClient.prepareRequest(onNicknameChangedSuccessfully, onServiceError);
    request.open("GET", accountUri + "/" + accountId + "/nickname");
    request.send();
  }
  function changeNickname(accountId, new_nickname, onNicknameChangedSuccessfully, onServiceError) {
    var nicknameChange = {};
    nicknameChange.value = new_nickname;

    var request = restClient.prepareRequest(onNicknameChangedSuccessfully, onServiceError);
    request.open("PUT", accountUri + "/" + accountId + "/nickname");
    request.setRequestHeader("Content-type", "application/json; charset=utf-8");
    request.send(JSON.stringify(nicknameChange));
  }
  function replayNicknames(onReplayStarted, onServiceError) {
    var request = restClient.prepareRequest(onReplayStarted, onServiceError);
    request.open("DELETE", nicknamesUri + "/projection");
    request.send();
  }

  /**
   * Public interface
   * @scope eventsourcing_showcase.AccountRepository
   */
  return {
    getBaseUri: getBaseUri,
    createAccount: createAccount,
    queryNickname: queryNickname,
    changeNickname: changeNickname,
    replayNicknames: replayNicknames,
    /**
     * Only for test purposes. Exchanges the UI.
     */
    setRestClient: setRestClient
  };
})();

/**
 * The AccountController coordinates UI, Repository and others to fullfill the use cases.
 *
 * @namespace
 */
eventsourcing_showcase.AccountController = (function() {
  "use strict";

  var ui = eventsourcing_showcase.AccountUI;
  var repository = eventsourcing_showcase.AccountRepository;

  function setUI(newUI) {
    ui = newUI;
  }

  function setRepository(newRepository) {
    repository = newRepository;
  }

  function onAccountIdSelected(accountId) {
    ui.setAccountId(accountId);
    repository.queryNickname(ui.getAccountId(), onNicknameQuerySucessfull, onNicknameQueryFailed);
  }

  function onAccountCreated(data) {
    onAccountIdSelected(data.accountId);
    ui.accountCreatedSuccessfully();
  }

  function onAccoutCreationFailed(error) {
    console.error("account creation failed because of " + error);
    ui.resetAccountId();
    ui.accountCreationFailed();
  }

  function onNicknameChanged(data) {
    ui.nicknameUpdated();
  }

  function onNicknameChangeFailed(error) {
    console.error("nickname change failed because of " + error);
    ui.nicknameChangeFailed();
  }

  function onNicknameQuerySucessfull(data) {
    ui.nicknameQuerySuccessful();
    ui.setNickname(data.value);
    ui.nicknameUpdated();
  }

  function onNicknameQueryFailed(error) {
    ui.nicknameQueryFailed();
    console.error("nickname query not successful because of " + error);
  }

  function onReplaySuccessful(data) {
    ui.nicknameReplaySuccessful();
  }

  function onReplayFailed(error) {
    console.error("nickname replay failed because of " + error);
    ui.nicknameReplayFailed();
  }

  function load() {
    ui.addEventListenerForAccountCreation(function() {
      ui.accountCreationRequested();
      repository.createAccount(onAccountCreated, onAccoutCreationFailed);
    });
    ui.addEventListenerForNicknameChange(function() {
      ui.nicknameChangeRequested();
      repository.changeNickname(ui.getAccountId(), ui.getNickname(), onNicknameChanged, onNicknameChangeFailed);
    });
    ui.addEventListenerForNicknameReset(function() {
      ui.resetNickname();
    });
    ui.addEventListenerForNicknameReplay(function() {
      ui.nicknameReplayRequested();
      repository.replayNicknames(onReplaySuccessful, onReplayFailed);
    });
  }
  /**
   * Public interface
   * @scope eventsourcing_showcase.AccountController
   */
  return {
    load: load,
    /**
     * Only for test purposes. Exchanges the UI.
     */
    setUI: setUI,
    /**
     * Only for test purposes. Exchanges the Repository.
     */
    setRepository: setRepository
  };
})();
