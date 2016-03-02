var mwggPassera = {
    init: function() {
        mwggPassera.self = require("sdk/self");
        
        mwggPassera.passera = require("sdk/panel").Panel({
          width: 200,
          height: 400,
          contentURL: mwggPassera.self.data.url("passera.html"),
          contentScriptFile: mwggPassera.self.data.url("passera.js"),
          contentStyleFile: mwggPassera.self.data.url("style.css"),
          onHide: mwggPassera.handleHide
        });
        
        mwggPassera.button = require('sdk/ui/button/toggle').ToggleButton({
          id: "passera-button",
          label: "Passera",
          icon: {
            "16": "./icon-16.png",
            "32": "./icon-32.png",
            "64": "./icon-64.png"
          },
          onClick: mwggPassera.openPassera
        });
    },
    openPassera: function(state) {
        if (state.checked) {
          mwggPassera.passera.show({
            position: mwggPassera.button
          });
        }
    },
    handleHide: function() {
        mwggPassera.button.state('window', {checked: false});
    }


};

mwggPassera.init();