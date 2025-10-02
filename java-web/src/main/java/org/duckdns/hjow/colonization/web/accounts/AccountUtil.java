package org.duckdns.hjow.colonization.web.accounts;

import java.io.File;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.duckdns.hjow.colonization.web.key.Key;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.SecurityUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/** Colonization WEB 서비스 계정 관리를 위한 Util */
public class AccountUtil {
    
    public static Account load(String id) {
        id = Account.removeProhibitedChars(id);
        File file = new File(getAccountRootDirectory().getAbsolutePath() + File.separator + id + ".colacc");
        if(! file.exists()) return null;
        
        try {
            String strJson = FileUtil.readString(file, "UTF-8", GZIPInputStream.class);
            JsonObject json = (JsonObject) JsonObject.parseJson(strJson); strJson = null;
            Account acc = new Account();
            acc.fromJson(json);
            return acc;
        } catch(Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    
    public static void save(Account acc) {
        try {
            File file = new File(getAccountRootDirectory().getAbsolutePath() + File.separator + acc.getId() + ".colacc");
            FileUtil.writeString(file, "UTF-8", acc.toJson().toJSON(), GZIPOutputStream.class);
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }
    
    public static File getAccountRootDirectory() {
        File f = new File(System.getProperty("user.home") + File.separator + "." + "colonizationweb" + File.separator + "accounts");
        if(! f.exists()) f.mkdirs();
        return f;
    }
    
    protected static Algorithm algJwt;
    protected static String    verifyingClaim;
    protected static void jwtPreSettings() {
    	if(algJwt == null || verifyingClaim == null) {
    		String jwtKey = "Hello";
            try {
                Key key = (Key) Class.forName("org.duckdns.hjow.colonization.web.key.CurrentKey").newInstance();
                jwtKey = key.getJWTKey();
                verifyingClaim = key.getJWTVerifyingClaim();
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            algJwt = Algorithm.HMAC384(SecurityUtil.hash( jwtKey , "SHA-384"));
    	}
    }
    
    public static Algorithm getJWTAlgorithm() {
    	jwtPreSettings();
    	return algJwt;
    }
    
    public static String getVerifyingClaim() {
    	jwtPreSettings();
    	return verifyingClaim;
    }
    
    /** 새 JWT 토큰 빌드 */
    public static String buildJWT(Account acc, Map<String, Object> headerContent) {
    	return JWT.create().withHeader(headerContent).withClaim("auth", AccountUtil.getVerifyingClaim()).withClaim("id", acc.getId()).withClaim("key", String.valueOf(acc.getKey())).withClaim("when", String.valueOf(System.currentTimeMillis())).sign(AccountUtil.getJWTAlgorithm());
    }
    
    /** JWT 토큰을 받아 Account 객체 반환 */
    public static Account verifyJWT(String jwt) {
    	if(jwt == null) return null;
    	jwtPreSettings();
    	
    	try {
            JWTVerifier veri = JWT.require(algJwt).withClaim("auth", verifyingClaim).build();
            
            if(jwt != null) {
                DecodedJWT decoded = veri.verify(jwt);
                
                Claim claim = decoded.getClaim("id");
                String id = claim.asString();
                
                Account acc = load(id);
                if(acc != null) {
                    return acc;
                }
            }
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
    	return null;
    }
    
}
