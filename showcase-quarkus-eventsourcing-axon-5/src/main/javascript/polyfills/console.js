/**
 * Console polyfill for HtmlUnitDriver.
 * Provides basic console object support.
 */
if (typeof console === "undefined") {
  window.console = {
    log: function() {
      // no-op in test environment
    },
    warn: function() {
      // no-op in test environment
    },
    error: function() {
      // no-op in test environment
    },
    info: function() {
      // no-op in test environment
    },
    debug: function() {
      // no-op in test environment
    }
  };
}
