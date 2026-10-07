/**
 * getElementsByClassName polyfill for HtmlUnitDriver.
 * Provides document.getElementsByClassName() and element.getElementsByClassName() support.
 */
if (document && !document.getElementsByClassName) {
  var elementsByClassName = function(className) {
    var elements = [];
    var allElements = this.getElementsByTagName("*");
    var classNames = className.split(/\s+/);
    
    for (var i = 0; i < allElements.length; i++) {
      var element = allElements[i];
      var elementClasses = (element.className || "").split(/\s+/);
      
      var hasAllClasses = true;
      for (var j = 0; j < classNames.length; j++) {
        if (elementClasses.indexOf(classNames[j]) === -1) {
          hasAllClasses = false;
          break;
        }
      }
      
      if (hasAllClasses) {
        elements.push(element);
      }
    }
    
    return elements;
  };
  
  document.getElementsByClassName = elementsByClassName;
}

// Add to HTMLElement if supported
if (typeof HTMLElement !== "undefined" && !HTMLElement.prototype.getElementsByClassName) {
  HTMLElement.prototype.getElementsByClassName = function(className) {
    var elements = [];
    var allElements = this.getElementsByTagName("*");
    var classNames = className.split(/\s+/);
    
    for (var i = 0; i < allElements.length; i++) {
      var element = allElements[i];
      var elementClasses = (element.className || "").split(/\s+/);
      
      var hasAllClasses = true;
      for (var j = 0; j < classNames.length; j++) {
        if (elementClasses.indexOf(classNames[j]) === -1) {
          hasAllClasses = false;
          break;
        }
      }
      
      if (hasAllClasses) {
        elements.push(element);
      }
    }
    
    return elements;
  };
}
