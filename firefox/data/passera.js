var pw1 = document.getElementById("pw1");
var pw2 = document.getElementById("pw2");
var pwlen = document.getElementById("pwlen");
var charsbox = document.getElementById("charsbox");
var showing = document.getElementById("showing");
var show = document.getElementById("show");
var hide = document.getElementById("hide");

show.addEventListener('click', pwGen, false);

hide.addEventListener('click', function(e) {
    showing.innerHTML="&nbsp;";
    hide.innerHTML="&nbsp;";
}, false);

function pwGen() {
    var phrase = pw1.value;
    var phraserep = pw2.value;
    var len = pwlen.value;
    var chars = charsbox.checked;
    
    if (phrase != phraserep && phraserep.length > 0) {
        showing.innerHTML="Passwords did not match.";
        return;
    }
    
    if (len > 64) {
        len = 64;
        pwlen.value = "64";
    }
    
    if (len < 4) {
        len = 4
        pwlen.value = "4";
    }
    
    pw1.value="";
    pw2.value="";
    
    showing.innerHTML="Please wait...";
    
    var worker = new Worker('hasher.js');
    worker.postMessage( {"phrase":phrase, "len":len, "chars":chars} );
    
    worker.addEventListener('message', function(e) {
        showing.innerHTML=e.data.pwd;
        hide.innerHTML="[hide]";
    }, false);
}