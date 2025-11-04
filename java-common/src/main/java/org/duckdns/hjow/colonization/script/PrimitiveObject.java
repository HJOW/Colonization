package org.duckdns.hjow.colonization.script;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.data.Binary;
import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.script.ScriptObject;
import org.duckdns.hjow.commons.util.DataUtil;

/** 공통 Lib 의 PrimitiveObject 기능제한판 */
public class PrimitiveObject extends ScriptObject {
	private static final long serialVersionUID = -515055112892161618L;
	private static final PrimitiveObject uniqueObject = new PrimitiveObject();
	private PrimitiveObject() { super(); }
    public static PrimitiveObject getInstance() {
        return uniqueObject;
    }
    
    @Override
    public String getPrefixName() {
        return "priv";
    }
    @Override
    public String getInitScript(String accessKey) {
        StringBuilder initScript = new StringBuilder("");
        
        initScript = initScript.append("function String(obj) {                                                                     ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".convertString(obj);                   ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function Date(dateStr, formats) {                                                          ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".convertDate(dateStr, formats);        ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function convertDateFromTimeMills(timeMills) {                                             ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".convertDateFromTimeMills(timeMills);  ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function today() {                                                                         ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".today();                              ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function parseInt(obj) {                                                                   ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".parseInt(obj);                        ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function parseFloat(obj) {                                                                 ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".parseFloat(obj);                      ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function list() {                                                                          ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".list();                               ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function Array() {                                                                         ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".list();                               ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function map() {                                                                           ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".map();                                ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function Object() {                                                                        ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".map();                                ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function isEmpty(pbj) {                                                                    ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".isEmpty(obj);                         ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function isInteger(pbj) {                                                                  ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".isInteger(obj);                       ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function parseBoolean(pbj) {                                                               ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".parseBoolean(obj);                    ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function subtractDate(one, two) {                                                          ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".subtractDate(one, two);               ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function isNull(obj) {                                                                     ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".isNull(obj);                          ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function getClassType(obj) {                                                               ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".getClassType(obj);                    ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function isEquals(one, two) {                                                              ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".isEquals(one, two);                   ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function splitString(one, two) {                                                           ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".splitString(one, two);                ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function emptyBytes(size) {                                                                ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".createEmptyByteArray(size);           ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function newJsonObject() {                                                                 ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".newJsonObject();                      ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function newJsonArray() {                                                                  ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".newJsonArray();                       ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function parseJson(a) {                                                                    ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".parseJson(a);                         ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function translate(obj) {                                                                  ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".translate(obj);                       ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function formatInt(obj) {                                                                  ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".formatInt(obj);                       ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function formatRate(obj) {                                                                 ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".formatRate(obj);                      ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function maxMem() {                                                                        ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".maxMem();                             ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function freeMem() {                                                                       ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".freeMem();                            ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function memPer() {                                                                        ").append("\n");
        initScript = initScript.append("    return " + getPrefixName() + "_" + accessKey + ".memPer();                             ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        initScript = initScript.append("function gc() {                                                                            ").append("\n");
        initScript = initScript.append("    " + getPrefixName() + "_" + accessKey + ".gc();                                        ").append("\n");
        initScript = initScript.append("};                                                                                         ").append("\n");
        
        return initScript.toString();
    }
    
    public String convertString(Object obj) {
        if(obj instanceof byte[]) {
            try {
                return new String((byte[]) obj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if(obj instanceof Binary) {
            return convertString(((Binary) obj).toByteArray());
        }
        return String.valueOf(obj);
    }
    
    public Date convertDate(Object obj, Object formats) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(convertString(formats));
        return formatter.parse(convertString(obj));
    }
    public Date convertDateFromTimeMills(Object timeMills) throws ParseException {
        long timeMillVal = -1;
        if(timeMills instanceof Number) timeMillVal = ((Number) timeMills).longValue();
        else timeMillVal = Long.parseLong(String.valueOf(timeMills));
        return new Date(timeMillVal);
    }
    public Date today() {
        return Calendar.getInstance().getTime();
    }
    public int parseInt(Object obj) {
        return new BigDecimal(convertString(obj)).intValue();
    }
    public double parseFloat(Object obj) {
        return Double.parseDouble(convertString(obj));
    }
    public List<Object> list() {
        return new ArrayList<Object>();
    }
    public Map<Object, Object> map() {
        return new HashMap<Object, Object>();
    }
    public boolean isEmpty(Object ob) {
        return DataUtil.isEmpty(ob);
    }
    public boolean isInteger(Object ob) {
        return DataUtil.isInteger(ob);
    }
    public boolean isNumber(Object ob) {
        return DataUtil.isNumber(ob);
    }
    public boolean parseBoolean(Object ob) {
        return DataUtil.parseBoolean(ob);
    }
    public Date subtractDate(Object one, Object two) throws ParseException {
        Date oneDate = null;
        Date twoDate = null;
        
        if(one instanceof Date) oneDate = (Date) one;
        else oneDate = convertDate(one, "yyyyMMdd");
        
        if(two instanceof Date) twoDate = (Date) two;
        else twoDate = convertDate(two, "yyyyMMdd");
        
        return DataUtil.subtract(oneDate, twoDate);
    }
    public boolean isNull(Object obj) {
        return obj == null;
    }
    public String getClassType(Object obj) {
        if(obj == null) return "null";
        if(obj instanceof Object[])   return "array";
        if(obj instanceof JsonArray)  return "array";
        if(obj instanceof byte[])     return "bytes";
        if(obj instanceof Binary)     return "bytes";
        if(obj instanceof String)     return "string";
        if(obj instanceof JsonObject) return "object";
        if(obj instanceof List<?>)    return "list";
        if(obj instanceof Map<?, ?>)  return "map";
        return obj.getClass().getSimpleName();
    }
    public boolean isEquals(Object one, Object two) {
        return one.equals(two);
    }
    public List<String> splitString(Object originals, Object delimiters) {
        StringTokenizer tokenizer = new StringTokenizer(String.valueOf(originals), String.valueOf(delimiters));
        List<String> results = new ArrayList<String>();
        while(tokenizer.hasMoreTokens()) {
            results.add(tokenizer.nextToken());
        }
        return results;
    }
    public JsonObject newJsonObject() {
        return new JsonObject();
    }
    public JsonArray newJsonArray() {
        return new JsonArray();
    }
    public Object parseJson(Object jsonString) throws Throwable {
        if(jsonString == null) return null;
        if(jsonString instanceof Number ) return jsonString;
        if(jsonString instanceof Boolean) return jsonString;
        return JsonObject.parseJson(String.valueOf(jsonString));
    }
    public String formatInt(Object n) {
    	if(n instanceof BigInteger) return ColonyManager.formatInt( (BigInteger) n);
    	return ColonyManager.formatInt(new BigInteger(String.valueOf(n).replace(",", "").trim()));
    }
    
    public String formatRate(Object n) {
    	if(n instanceof BigDecimal) return ColonyManager.formatRate(  (BigDecimal) n );
    	return ColonyManager.formatRate(new BigDecimal(String.valueOf(n).replace(",", "").trim()));
    }
    public BigDecimal maxMem() {
    	return new BigDecimal(String.valueOf(Runtime.getRuntime().maxMemory()));
    }
    public BigDecimal freeMem() {
    	return new BigDecimal(String.valueOf(Runtime.getRuntime().freeMemory()));
    }
    public BigDecimal memPer() {
    	BigDecimal max = maxMem();
    	if(max.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
    	return freeMem().multiply(new BigDecimal("100")).divide(max, 50, RoundingMode.HALF_UP);
    }
    
    private transient long lastGcTime = 0L;
    public void gc() {
    	long now = System.currentTimeMillis();
    	if(lastGcTime <= now - 8000L) return; // 너무 자주 호출 못하게
    	
    	lastGcTime = now;
    	Runtime.getRuntime().gc();
    }
    public String translate(Object obj) {
        return ColonyManager.t(obj.toString());
    }
}
