/**
 * Returns all function names of the given object.
 */
function functionNamesOf(objct) {
  console.log(Object.getOwnPropertyNames(objct));
  return Object.getOwnPropertyNames(objct).filter(function(property) {
    return typeof objct[property] === "function";
  });
}

describe("Account", function() {
  describe("UI", function() {
    var uiUnderTest = eventsourcing_showcase.AccountUI;
    var accountIdField;
    var createAccountButton;

    beforeAll(function() {
      var body = document.getElementsByTagName("body")[0];

      createAccountButton = document.getElementById("create_account");
      if (createAccountButton == null) {
        createAccountButton = document.createElement("button");
        createAccountButton.id = "create_account";
        createAccountButton.hidden = true; // doesn't need to be shown on test page
        body.appendChild(createAccountButton);
      }

      accountIdField = document.createElement("input");
      accountIdField.id = "accountid";
      accountIdField.hidden = true; // doesn't need to be shown on test page
      body.appendChild(accountIdField);
    });

    it("disables button, when account creation requested", function() {
      createAccountButton.disabled = false;
      uiUnderTest.accountCreationRequested();
      expect(createAccountButton.disabled).toBe(true);
    });
    it("adds spinner to the button, when account creation requested", function() {
      createAccountButton.classList.remove("spinner");
      uiUnderTest.accountCreationRequested();
      expect(createAccountButton.classList).toContain("spinner");
    });
    it("toggles from 'noaction' to 'action', when account creation requested", function() {
      createAccountButton.classList.add("noaction");
      createAccountButton.classList.remove("action");
      uiUnderTest.accountCreationRequested();
      expect(createAccountButton.classList).not.toContain("noaction");
      expect(createAccountButton.classList).toContain("action");
    });

    it("enables button, when account creation was successful", function() {
      createAccountButton.disabled = true;
      uiUnderTest.accountCreatedSuccessfully();
      expect(createAccountButton.disabled).toBe(false);
    });
    it("removes spinner, when account creation was successful", function() {
      createAccountButton.classList.add("spinner");
      uiUnderTest.accountCreatedSuccessfully();
      expect(createAccountButton.classList).not.toContain("spinner");
    });
    it("toggles from 'noaction' to 'action', when account creation was successful", function() {
      createAccountButton.classList.add("noaction");
      createAccountButton.classList.remove("action");
      uiUnderTest.accountCreatedSuccessfully();
      expect(createAccountButton.classList).not.toContain("noaction");
      expect(createAccountButton.classList).toContain("action");
    });

    it("enables button, when account creation failed (for another try)", function() {
      createAccountButton.disabled = true;
      uiUnderTest.accountCreationFailed();
      expect(createAccountButton.disabled).toBe(false);
    });
    it("removes spinner, when account creation failed", function() {
      createAccountButton.classList.add("spinner");
      uiUnderTest.accountCreationFailed();
      expect(createAccountButton.classList).not.toContain("spinner");
    });
    it("toggles from 'action' to 'noaction', to indicated failed account creation", function() {
      createAccountButton.classList.add("action");
      createAccountButton.classList.remove("noaction");
      uiUnderTest.accountCreationFailed();
      expect(createAccountButton.classList).not.toContain("action");
      expect(createAccountButton.classList).toContain("noaction");
    });

    it("gets the accountId", function() {
      accountIdField.value = "TestAccountId1";
      expect(uiUnderTest.getAccountId()).toBe(accountIdField.value);
    });
    it("sets the accountId", function() {
      var newAccountId = "TestNewAccountId";
      accountIdField.value = "TestAccountId2";
      uiUnderTest.setAccountId(newAccountId);
      expect(accountIdField.value).toBe(newAccountId);
    });
    it("clears accountId to reset it", function() {
      accountIdField.value = "AnythingElseButEmpty";
      uiUnderTest.resetAccountId();
      expect(accountIdField.value).toBe("");
    });
  });

  describe("Repository", function() {
    var repositoryUnderTest = eventsourcing_showcase.AccountRepository;
    var restClient, request;
    var onSuccess = function(data) {};
    var onError = function(error) {};

    beforeAll(function() {
      restClient = jasmine.createSpyObj("restClient", functionNamesOf(eventsourcing_showcase.RestClient));
      request = jasmine.createSpyObj("request", ["open", "send", "setRequestHeader"]);
      restClient.prepareRequest.and.returnValue(request);
      repositoryUnderTest.setRestClient(restClient);
    });

    beforeEach(function() {
      restClient.prepareRequest.calls.reset();
    });

    it("attempts to send a POST request to {baseURI}/accounts to create an account", function() {
      repositoryUnderTest.createAccount(onSuccess, onError);
      expect(request.open).toHaveBeenCalledWith("POST", repositoryUnderTest.getBaseUri() + "/accounts");
    });
  });

  describe("Controller", function() {
    var controllerUnderTest = eventsourcing_showcase.AccountController;
    var ui;
    var repository;

    beforeAll(function() {
      ui = jasmine.createSpyObj("ui", functionNamesOf(eventsourcing_showcase.AccountUI));
      controllerUnderTest.setUI(ui);

      repository = jasmine.createSpyObj("repository", functionNamesOf(eventsourcing_showcase.AccountRepository));
      controllerUnderTest.setRepository(repository);

      controllerUnderTest.load();
    });

    beforeEach(function() {
      repository.changeNickname.calls.reset();
      repository.queryNickname.calls.reset();
    });

    function createAccount() {
      return ui.addEventListenerForAccountCreation.calls.argsFor(0)[0]();
    }
    function accountCreatedSucessfullyWithData(data) {
      repository.createAccount.calls.argsFor(0)[0](data);
    }
    function accountCreationFailedWithError(error) {
      repository.createAccount.calls.argsFor(0)[1](error);
    }
    function nicknameQueryFailedWithError(error) {
      repository.queryNickname.calls.argsFor(0)[2](error);
    }

    function changeNickname() {
      return ui.addEventListenerForNicknameChange.calls.argsFor(0)[0]();
    }
    function nicknameChangedSucessfullyWithData(data) {
      repository.changeNickname.calls.argsFor(0)[2](data);
    }
    function nicknameChangeFailedWithError(error) {
      repository.changeNickname.calls.argsFor(0)[3](error);
    }

    function resetNickname() {
      return ui.addEventListenerForNicknameReset.calls.argsFor(0)[0]();
    }

    it("registers an event listener for account creation", function() {
      expect(ui.addEventListenerForAccountCreation).toHaveBeenCalled();
    });
    it("notifies the UI, when account creation was requested", function() {
      createAccount();
      expect(ui.accountCreationRequested).toHaveBeenCalled();
    });
    it("calls repository to create an account", function() {
      createAccount();
      expect(repository.createAccount).toHaveBeenCalled();
    });
    it("resets the accountId on the UI, when account creation fails", function() {
      createAccount();
      accountCreationFailedWithError("error");
      expect(ui.resetAccountId).toHaveBeenCalled();
    });
    it("notifies the UI, when account creation fails", function() {
      createAccount();
      accountCreationFailedWithError("error");
      expect(ui.accountCreationFailed).toHaveBeenCalled();
    });
    it("selects the accountId, when account had been created successfully", function() {
      createAccount();
      var data = { accountId: "test_id1" };
      accountCreatedSucessfullyWithData(data);
      expect(ui.setAccountId).toHaveBeenCalledWith(data.accountId);
    });
  });
});

describe("Nickname", function() {
  describe("UI", function() {
    var uiUnderTest = eventsourcing_showcase.AccountUI;
    var nicknameInput, nicknameOld;
    var nicknameChangeButton, nicknameReplayButton;

    beforeAll(function() {
      var body = document.getElementsByTagName("body")[0];

      createAccountButton = document.getElementById("create_account");
      if (createAccountButton == null) {
        createAccountButton = document.createElement("button");
        createAccountButton.id = "create_account";
        createAccountButton.hidden = true; // doesn't need to be shown on test page
        body.appendChild(createAccountButton);
      }

      var nickknamePanel = document.createElement("div");
      nickknamePanel.classList.add("nickname_panel");
      body.appendChild(nickknamePanel);

      nicknameInput = document.createElement("input");
      nicknameInput.id = "nickname";
      nicknameInput.hidden = true; // doesn't need to be shown on test page
      nickknamePanel.appendChild(nicknameInput);

      nicknameOld = document.createElement("input");
      nicknameOld.id = "nickname_old";
      nicknameOld.hidden = true; // doesn't need to be shown on test page
      nickknamePanel.appendChild(nicknameOld);

      nicknameChangeButton = document.createElement("button");
      nicknameChangeButton.id = "nickname_confirm";
      nicknameChangeButton.hidden = true; // doesn't need to be shown on test page
      nickknamePanel.appendChild(nicknameChangeButton);

      nicknameReplayButton = document.createElement("button");
      nicknameReplayButton.id = "replay_nicknames";
      nicknameReplayButton.hidden = true; // doesn't need to be shown on test page
      nickknamePanel.appendChild(nicknameReplayButton);
    });

    it("shows nickname panel, when account creation was successful", function() {
      nicknameInput.classList.add("hide");
      uiUnderTest.accountCreatedSuccessfully();
      expect(nicknameInput.classList).not.toContain("hide");
    });
    it("hides nickname panel, when account creation failed", function() {
      nicknameInput.classList.remove("hide");
      uiUnderTest.accountCreationFailed();
      expect(nicknameInput.classList).toContain("hide");
    });

    it("overwrites the value of the nickname, when nickname is set", function() {
      var nickname = "newNicknameToSet";
      nicknameInput.value = "";
      uiUnderTest.setNickname(nickname);
      expect(nicknameInput.value).toBe(nickname);
    });
    it("disables button, when nickname change requested", function() {
      nicknameChangeButton.disabled = false;
      uiUnderTest.nicknameChangeRequested();
      expect(nicknameChangeButton.disabled).toBe(true);
    });
    it("adds spinner to the button, when nickname change requested", function() {
      nicknameChangeButton.classList.remove("spinner");
      uiUnderTest.nicknameChangeRequested();
      expect(nicknameChangeButton.classList).toContain("spinner");
    });
    it("toggles from 'noaction' to 'action', when nickname change requested", function() {
      nicknameChangeButton.classList.add("noaction");
      nicknameChangeButton.classList.remove("action");
      uiUnderTest.nicknameChangeRequested();
      expect(nicknameChangeButton.classList).not.toContain("noaction");
      expect(nicknameChangeButton.classList).toContain("action");
    });

    it("enables nickname change button, when nickname is updated", function() {
      nicknameOld.value = "testOldNickanme";
      nicknameChangeButton.disabled = true;
      uiUnderTest.nicknameUpdated();
      expect(nicknameChangeButton.disabled).toBe(false);
    });
    it("removes spinner, when nickname is updated", function() {
      nicknameOld.value = "testOldNickanme";
      nicknameChangeButton.classList.add("spinner");
      uiUnderTest.nicknameUpdated();
      expect(nicknameChangeButton.classList).not.toContain("spinner");
    });
    it("removes error indicator, when nickname is updated", function() {
      nicknameOld.value = "testOldNickanme";
      nicknameInput.classList.add("noaction");
      uiUnderTest.nicknameUpdated();
      expect(nicknameInput.classList).not.toContain("noaction");
    });
    it("toggles from 'noaction' to 'action', when nickname is updated", function() {
      nicknameChangeButton.classList.add("noaction");
      nicknameChangeButton.classList.remove("action");
      uiUnderTest.nicknameUpdated();
      expect(nicknameChangeButton.classList).not.toContain("noaction");
      expect(nicknameChangeButton.classList).toContain("action");
    });
    it("updates old nickname, when nickname is updated", function() {
      nicknameOld.value = "testOldNickanme";
      nicknameInput.value = "testNewNickname";
      uiUnderTest.nicknameUpdated();
      expect(nicknameOld.value).toBe(nicknameInput.value);
    });

    it("enables button, when nickname change failed", function() {
      nicknameChangeButton.disabled = true;
      uiUnderTest.nicknameChangeFailed();
      expect(nicknameChangeButton.disabled).toBe(false);
    });
    it("removes spinner, when nickname change failed", function() {
      nicknameChangeButton.classList.add("spinner");
      uiUnderTest.nicknameChangeFailed();
      expect(nicknameChangeButton.classList).not.toContain("spinner");
    });
    it("toggles from 'action' to 'noaction', to indicated failed nickname change", function() {
      nicknameChangeButton.classList.add("action");
      nicknameChangeButton.classList.remove("noaction");
      uiUnderTest.nicknameChangeFailed();
      expect(nicknameChangeButton.classList).not.toContain("action");
      expect(nicknameChangeButton.classList).toContain("noaction");
    });

    it("removes 'noaction' from nickname input, when nickname query was successful", function() {
      nicknameInput.classList.add("noaction");
      uiUnderTest.nicknameQuerySuccessful();
      expect(nicknameInput.classList).not.toContain("noaction");
    });
    it("adds 'noaction' to nickname input, to indicated failed nickname query", function() {
      nicknameInput.classList.remove("noaction");
      uiUnderTest.nicknameQueryFailed();
      expect(nicknameInput.classList).toContain("noaction");
    });

    it("overwrites the current nickname with the old one, when reset is requested", function() {
      nicknameOld.value = "testOldNickanme";
      nicknameInput.value = "testNewNickname";
      uiUnderTest.resetNickname();
      expect(nicknameOld.value).toBe("testOldNickanme");
      expect(nicknameInput.value).toBe("testOldNickanme");
    });
    it("updates nickname, when reset is requested", function() {
      nicknameChangeButton.disabled = true;
      uiUnderTest.resetNickname();
      expect(nicknameChangeButton.disabled).toBe(false);
    });

    it("disables button, when nickname replay started", function() {
      nicknameReplayButton.disabled = false;
      uiUnderTest.nicknameReplayRequested();
      expect(nicknameReplayButton.disabled).toBe(true);
    });
    it("adds spinner to the button, when nickname replay started", function() {
      nicknameReplayButton.classList.remove("spinner");
      uiUnderTest.nicknameReplayRequested();
      expect(nicknameReplayButton.classList).toContain("spinner");
    });
    it("toggles from 'noaction' to 'action', when nickname replay started", function() {
      nicknameReplayButton.classList.add("noaction");
      nicknameReplayButton.classList.remove("action");
      uiUnderTest.nicknameReplayRequested();
      expect(nicknameReplayButton.classList).not.toContain("noaction");
      expect(nicknameReplayButton.classList).toContain("action");
    });

    it("enables button, when nickname replay was successful", function() {
      nicknameReplayButton.disabled = true;
      uiUnderTest.nicknameReplaySuccessful();
      expect(nicknameReplayButton.disabled).toBe(false);
    });
    it("removes spinner, when nickname replay was successful", function() {
      nicknameReplayButton.classList.add("spinner");
      uiUnderTest.nicknameReplaySuccessful();
      expect(nicknameReplayButton.classList).not.toContain("spinner");
    });
    it("toggles from 'noaction' to 'action', when nickname replay was successful", function() {
      nicknameReplayButton.classList.add("noaction");
      nicknameReplayButton.classList.remove("action");
      uiUnderTest.nicknameReplaySuccessful();
      expect(nicknameReplayButton.classList).not.toContain("noaction");
      expect(nicknameReplayButton.classList).toContain("action");
    });

    it("enables button, when nickname replay failed", function() {
      nicknameReplayButton.disabled = true;
      uiUnderTest.nicknameReplayFailed();
      expect(nicknameReplayButton.disabled).toBe(false);
    });
    it("removes spinner, when nickname replay failed", function() {
      nicknameReplayButton.classList.add("spinner");
      uiUnderTest.nicknameReplayFailed();
      expect(nicknameReplayButton.classList).not.toContain("spinner");
    });
    it("toggles from 'action' to 'noaction', when nicknamereplay failed", function() {
      nicknameReplayButton.classList.add("action");
      nicknameReplayButton.classList.remove("noaction");
      uiUnderTest.nicknameReplayFailed();
      expect(nicknameReplayButton.classList).not.toContain("action");
      expect(nicknameReplayButton.classList).toContain("noaction");
    });
  });

  describe("Controller", function() {
    var controllerUnderTest = eventsourcing_showcase.AccountController;
    var ui;
    var repository;

    beforeAll(function() {
      ui = jasmine.createSpyObj("ui", functionNamesOf(eventsourcing_showcase.AccountUI));
      controllerUnderTest.setUI(ui);

      repository = jasmine.createSpyObj("repository", functionNamesOf(eventsourcing_showcase.AccountRepository));
      controllerUnderTest.setRepository(repository);

      controllerUnderTest.load();
    });

    beforeEach(function() {
      repository.changeNickname.calls.reset();
      repository.queryNickname.calls.reset();
    });

    function createAccount() {
      return ui.addEventListenerForAccountCreation.calls.argsFor(0)[0]();
    }
    function accountCreatedSucessfullyWithData(data) {
      repository.createAccount.calls.argsFor(0)[0](data);
    }
    function nicknameQueriedSucessfullyWithData(data) {
        repository.queryNickname.calls.argsFor(0)[1](data);
    }
    function nicknameQueryFailedWithError(error) {
      repository.queryNickname.calls.argsFor(0)[2](error);
    }

    function changeNickname() {
      return ui.addEventListenerForNicknameChange.calls.argsFor(0)[0]();
    }
    function nicknameChangedSucessfullyWithData(data) {
      repository.changeNickname.calls.argsFor(0)[2](data);
    }
    function nicknameChangeFailedWithError(error) {
      repository.changeNickname.calls.argsFor(0)[3](error);
    }
    function resetNickname() {
      return ui.addEventListenerForNicknameReset.calls.argsFor(0)[0]();
    }
    function replayNicknames() {
      return ui.addEventListenerForNicknameReplay.calls.argsFor(0)[0]();
    }
    function nicknameReplaySucessfulWithData() {
      return repository.replayNicknames.calls.argsFor(0)[0]();
    }
    function nicknameReplayFailedWithError() {
      return repository.replayNicknames.calls.argsFor(0)[1]();
    }

    it("registers an event listener for nickname changes", function() {
      expect(ui.addEventListenerForNicknameChange).toHaveBeenCalled();
    });
    it("registers an event listener for nickname resets", function() {
      expect(ui.addEventListenerForNicknameReset).toHaveBeenCalled();
    });
    it("queries the nickname by account ID, after account creation succeeded", function() {
      var data = { accountId: "test_id2" };
      ui.getAccountId.and.returnValue(data.accountId);
      createAccount();
      accountCreatedSucessfullyWithData(data);
      var accountIdForQuery = repository.queryNickname.calls.argsFor(0)[0];
      expect(accountIdForQuery).toBe(data.accountId);
    });
    it("notifies the UI, when the nickname query succeeded", function() {
      var data = { accountId: "test_id3", value: "test_nickname" };
      ui.getAccountId.and.returnValue(data.accountId);
      createAccount();
      accountCreatedSucessfullyWithData(data);
      nicknameQueriedSucessfullyWithData(data);
      expect(ui.nicknameQuerySuccessful).toHaveBeenCalled();
    });
    it("sets the nickname on the UI, when nickname query succeeded", function() {
      var data = { accountId: "test_id3", value: "test_nickname" };
      ui.getAccountId.and.returnValue(data.accountId);
      createAccount();
      accountCreatedSucessfullyWithData(data);
      nicknameQueriedSucessfullyWithData(data);
      expect(ui.nicknameUpdated).toHaveBeenCalled();
      expect(ui.setNickname).toHaveBeenCalledWith(data.value);
    });
    it("notifies the UI when nickname query failed", function() {
      var data = { accountId: "test_id3", value: "test_nickname" };
      ui.getAccountId.and.returnValue(data.accountId);
      createAccount();
      accountCreatedSucessfullyWithData(data);
      nicknameQueryFailedWithError("test error");
      expect(ui.nicknameQueryFailed).toHaveBeenCalled();
    });

    it("notifies UI when nickname changed", function() {
      changeNickname();
      expect(ui.nicknameChangeRequested).toHaveBeenCalled();
    });
    it("uses currently selected accountId to change the nickname", function() {
      var accountId = "test_id4";
      ui.getAccountId.and.returnValue(accountId);
      changeNickname();
      var accountIdForNicknameChange = repository.changeNickname.calls.argsFor(0)[0];
      expect(accountIdForNicknameChange).toBe(accountId);
    });
    it("changes the nickname based on its UI value", function() {
      var nickname = "new_test_nickname";
      ui.getNickname.and.returnValue(nickname);
      changeNickname();
      var usedNicknameForChange = repository.changeNickname.calls.argsFor(0)[1];
      expect(usedNicknameForChange).toBe(nickname);
    });
    it("notifies the UI about updated nickname", function() {
      var nickname = "new_test_nickname";
      ui.getNickname.and.returnValue(nickname);
      changeNickname();
      nicknameChangedSucessfullyWithData("any data");
      expect(ui.nicknameUpdated).toHaveBeenCalled();
    });
    it("notifies the UI about failed nickname change", function() {
      var nickname = "new_test_nickname2";
      ui.getNickname.and.returnValue(nickname);
      changeNickname();
      nicknameChangeFailedWithError("test_error5");
      expect(ui.nicknameChangeFailed).toHaveBeenCalled();
    });
    
    it("notifies the UI to reset the nickname", function() {
      resetNickname();
      expect(ui.resetNickname).toHaveBeenCalled();
    });

    it("notifies the UI, when nickname replay succeeded", function() {
      replayNicknames();
      nicknameReplaySucessfulWithData("error");
      expect(ui.nicknameReplaySuccessful).toHaveBeenCalled();
    });
    it("notifies the UI, when nickname replay failed", function() {
      replayNicknames();
      nicknameReplayFailedWithError("error");
      expect(ui.nicknameReplayFailed).toHaveBeenCalled();
    });

  });

  describe("Repository", function() {
    var repositoryUnderTest = eventsourcing_showcase.AccountRepository;
    var restClient, request;
    var onSuccess = function(data) {};
    var onError = function(error) {};

    beforeAll(function() {
      restClient = jasmine.createSpyObj("restClient", functionNamesOf(eventsourcing_showcase.RestClient));
      request = jasmine.createSpyObj("request", ["open", "send", "setRequestHeader"]);
      restClient.prepareRequest.and.returnValue(request);
      repositoryUnderTest.setRestClient(restClient);
    });

    beforeEach(function() {
      restClient.prepareRequest.calls.reset();
    });

    it("attempts to send a POST request to {baseURI}/accounts to create an account", function() {
      repositoryUnderTest.createAccount(onSuccess, onError);
      expect(request.open).toHaveBeenCalledWith("POST", repositoryUnderTest.getBaseUri() + "/accounts");
    });
    it("attempts to send a GET request to {baseURI}/accounts/{id}/nickname to query the nickname", function() {
      var accountId = "test_account_id_for_get";
      repositoryUnderTest.queryNickname(accountId, onSuccess, onError);
      var expectedUri = repositoryUnderTest.getBaseUri() + "/accounts/" + accountId + "/nickname";
      expect(request.open).toHaveBeenCalledWith("GET", expectedUri);
    });
    it("attempts to send a PUT request to {baseURI}/accounts/{id}/nickname to change the nickname", function() {
      var accountId = "test_account_id_for_put";
      var nickname = "testNewNickname";
      repositoryUnderTest.changeNickname(accountId, nickname, onSuccess, onError);
      var expectedUri = repositoryUnderTest.getBaseUri() + "/accounts/" + accountId + "/nickname";
      expect(request.open).toHaveBeenCalledWith("PUT", expectedUri);
      expect(request.send).toHaveBeenCalledWith(JSON.stringify({ value: nickname }));
    });
    it("attempts to send a DELETE request to {baseURI}/nicknames/projection to replay all nickname changes", function() {
      repositoryUnderTest.replayNicknames(onSuccess, onError);
      expect(request.open).toHaveBeenCalledWith("DELETE", repositoryUnderTest.getBaseUri() + "/nicknames/projection");
    });
  });
});
