var self = require("sdk/self");
var { ActionButton } = require("sdk/ui/button/action");
var { Panel } = require("sdk/panel");
var { ToggleButton } = require('sdk/ui/button/toggle');

var passera = Panel({
  width: 200,
  height: 400,
  contentURL: self.data.url("passera.html"),
  contentScriptFile: self.data.url("passera.js"),
  contentStyleFile: self.data.url("style.css"),
  onHide: handleHide
});

var button = ToggleButton({
  id: "passera-button",
  label: "Passera",
  icon: {
    "16": "./icon-16.png",
    "32": "./icon-32.png",
    "64": "./icon-64.png"
  },
  onClick: openPassera
});

function openPassera(state) {
    if (state.checked) {
    passera.show({
      position: button
    });
  }
}

function handleHide() {
  button.state('window', {checked: false});
}
