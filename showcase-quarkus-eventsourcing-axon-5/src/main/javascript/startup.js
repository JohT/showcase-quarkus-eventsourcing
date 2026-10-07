// ----------------------------------------------------
// Main registration and entry point for the application
window.addEventListener("load", eventsourcing_showcase.AccountController.load);
window.addEventListener("load", eventsourcing_showcase.EventController.load);
window.addEventListener("beforeunload", eventsourcing_showcase.EventController.close);
// ----------------------------------------------------
