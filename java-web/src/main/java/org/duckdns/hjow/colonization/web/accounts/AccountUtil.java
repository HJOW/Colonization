package org.duckdns.hjow.colonization.web.accounts;

import java.io.File;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;

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
    
}
