

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