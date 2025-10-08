

/**
 * 문자열을 HEX 문자열로 인코딩 or 반대로 디코딩 (리포트 출력 시 인코딩해 전달해 한글깨짐 방지 목적)
 * 
 * 사용 예)  
 * var hexEncoder = new HexEncoder();
 * var encoded    = hexEncoder.encode('안녕하세요 여러분');
 * alert(encoded);
 * alert(hexEncoder.decode(encoded));
 */
function HexEncoder() {
    this.encode = function encode(originalStr) {
        var utf8Str = unescape(encodeURIComponent(originalStr));
        var hexStr = '';
        
        for (let i = 0; i < utf8Str.length; i++) {
            hexStr += utf8Str.charCodeAt(i).toString(16).padStart(2, '0');
        }
        
        return hexStr;
    };
    
    this.decode = function decode(hexString) {
        let utf8Str = '';
        
        for (let i = 0; i < hexString.length; i += 2) {
            utf8Str += String.fromCharCode(parseInt(hexString.substr(i, 2), 16));
        }
        
        return decodeURIComponent(escape(utf8Str));
    }
}

$.col = {};
$.colonizaion = $.col;

$.col.log = function(msg) {
    try { console.log(msg); } catch(ignores) {}
};

$.col.ajax = function(options) {
    var headers = null;
    if(! $.col.isEmpty(options.sessionKey)) {
        var token = sessionStorage.getItem(options.sessionKey);
        if(! $.col.isEmpty(token)) {
            headers = {};
            headers['jwt'] = token;
        }
    }
    if(headers != null) {
        options.headers = headers;
    }

    return $.ajax(options);
};

$.col.isEmpty = function(value) {
    if(value == null) return true;
    if(typeof(value) == 'undefined') return true;
    var str = String(value).trim();
}

/* 
   Polyfills (startsWith 메소드는 ECMAScript 2015 명세)
   출처 : https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/String/startsWith
*/
if(!String.prototype.startsWith) {
    String.prototype.startsWith = function(search, pos) {
        return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
    };
}

/* 
   Polyfills (endsWith 메소드는 ECMAScript 2015 명세)
   출처 : https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/String/endsWith
*/
if(!String.prototype.endsWith) {
    String.prototype.endsWith = function(searchString, position){
        var subjectString = this.toString();
        if(typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}

/*
   Polyfills (trim 메소드는 ECMAScript 5.1 명세)
   출처 : https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/String/trim
*/
if(!String.prototype.trim) {
    String.prototype.trim = function() {
        return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
    };
}

/*
    Polyfills (ECMAScript 5.1 명세)
    출처 : https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Array/isArray
*/
if (!Array.isArray) {
    Array.isArray = function(arg) {
        return Object.prototype.toString.call(arg) === '[object Array]';
    };
}