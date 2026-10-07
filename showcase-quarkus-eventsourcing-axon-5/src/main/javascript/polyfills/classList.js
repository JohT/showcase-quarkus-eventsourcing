/**
 * classList polyfill for HtmlUnitDriver.
 * Provides DOMTokenList-like functionality for element.classList.
 */
if (!Element.prototype.classList) {
  Object.defineProperty(Element.prototype, "classList", {
    get: function() {
      var element = this;
      var classList = [];
      
      function getClasses() {
        return (element.className || "").split(/\s+/).filter(function(c) { return c; });
      }
      
      function updateClasses() {
        classList.length = 0;
        var classes = getClasses();
        for (var i = 0; i < classes.length; i++) {
          classList[i] = classes[i];
        }
      }
      
      updateClasses();
      
      classList.add = function(token) {
        if (!this.contains(token)) {
          var classes = getClasses();
          classes.push(token);
          element.className = classes.join(" ");
          updateClasses();
        }
      };
      
      classList.remove = function(token) {
        var classes = getClasses().filter(function(c) { return c !== token; });
        element.className = classes.join(" ");
        updateClasses();
      };
      
      classList.toggle = function(token) {
        if (this.contains(token)) {
          this.remove(token);
          return false;
        } else {
          this.add(token);
          return true;
        }
      };
      
      classList.contains = function(token) {
        return getClasses().indexOf(token) >= 0;
      };
      
      return classList;
    }
  });
}
