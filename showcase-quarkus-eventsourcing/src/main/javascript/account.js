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
    fetch(accountUri, {
      method: "post"
    })
      .then(function(response) {
        var data = {};
        data.accountId = getLastUrlPath(response.headers.get("location"));
        return data;
      })
      .then(function(data) {
        onAccountCreated(data);
      })
      ['catch'](function(error) {
        onServiceError(error);
      });
  }
  function queryNickname(accountId, onNicknameChangedSuccessfully, onServiceError) {
    fetch(accountUri + "/" + accountId + "/nickname")
      .then(function(response) {
        return response.json();
      })
      .then(function(data) {
        onNicknameChangedSuccessfully(data);
      })
      ['catch'](function(error) {
        onServiceError(error);
      });
  }
  function changeNickname(accountId, new_nickname, onNicknameChangedSuccessfully, onServiceError) {
    fetch(accountUri + "/" + accountId + "/nickname", {
      method: "put",
      headers: new Headers({
        "Content-Type": "application/json; charset=utf-8"
      }),
      body: JSON.stringify({"value" : new_nickname})
    })
      .then(function(response) {
        onNicknameChangedSuccessfully(new_nickname);
      })
      ['catch'](function(error) {
        onServiceError(error);
      });
  }

  function replayNicknames(onReplayStarted, onServiceError) {
    fetch(nicknamesUri + "/projection", {
      method: "delete"
    })
      .then(function(response) {
        onReplayStarted({});
      })
      ['catch'](function(error) {
        onServiceError(error);
      });
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
    replayNicknames: replayNicknames
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
