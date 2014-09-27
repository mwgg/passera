var mwggPasseraGen = {
    init: function() {
        mwggPasseraGen.pw1 = document.getElementById("pw1");
        mwggPasseraGen.pw2 = document.getElementById("pw2");
        mwggPasseraGen.pwlen = document.getElementById("pwlen");
        mwggPasseraGen.charsbox = document.getElementById("charsbox");
        mwggPasseraGen.showing = document.getElementById("showing");
        mwggPasseraGen.show = document.getElementById("show");
        mwggPasseraGen.hide = document.getElementById("hide");
        
        mwggPasseraGen.show.addEventListener('click', mwggPasseraGen.pwGen, false);
        
        mwggPasseraGen.hide.addEventListener('click', function(e) {
            mwggPasseraGen.showing.innerHTML="&nbsp;";
            mwggPasseraGen.hide.innerHTML="&nbsp;";
        }, false);

    },
    pwGen: function() {
        this.phrase = mwggPasseraGen.pw1.value;
        this.phraserep = mwggPasseraGen.pw2.value;
        this.len = mwggPasseraGen.pwlen.value;
        this.chars = mwggPasseraGen.charsbox.checked;
        
        if (this.phrase != this.phraserep && this.phraserep.length > 0) {
            mwggPasseraGen.showing.innerHTML="Passwords did not match.";
            return;
        }
        
        if (this.len > 64) {
            this.len = 64;
            mwggPasseraGen.pwlen.value = "64";
        }
        
        if (this.len < 4) {
            this.len = 4
            mwggPasseraGen.pwlen.value = "4";
        }
        
        mwggPasseraGen.pw1.value="";
        mwggPasseraGen.pw2.value="";
        mwggPasseraGen.showing.innerHTML="Please wait...";
        
        this.worker = new Worker('hasher.js');
        this.worker.postMessage( {"phrase":this.phrase, "len":this.len, "chars":this.chars} );

        this.worker.onmessage = function (e) {
            while (mwggPasseraGen.showing.firstChild) {
                mwggPasseraGen.showing.removeChild(mwggPasseraGen.showing.firstChild);
            }
            mwggPasseraGen.showing.appendChild(document.createTextNode(e.data.pwd));
            mwggPasseraGen.hide.innerHTML="[hide]";
        };
    }
};

mwggPasseraGen.init();