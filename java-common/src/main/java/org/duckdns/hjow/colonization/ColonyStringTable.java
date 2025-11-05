package org.duckdns.hjow.colonization;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.duckdns.hjow.commons.resource.BrokerStringTable;
import org.duckdns.hjow.commons.resource.FileStringTable;
import org.duckdns.hjow.commons.resource.StringTable;
import org.duckdns.hjow.commons.util.DataUtil;

/** Colonization 다국어 구현을 위한 스트링 테이블 */
public class ColonyStringTable extends BrokerStringTable {
    private static final long serialVersionUID = -1072075853155908566L;
    
    public ColonyStringTable() { super(); }
    public ColonyStringTable(StringTable stringTable) { super(stringTable); }
    public ColonyStringTable(String name, StringTable stringTable) { super(name, stringTable); }
    
    protected List<BigInteger> stringList = new Vector<BigInteger>();

    @Override
    public synchronized String t(String originals) {
        if(originals == null) originals = "";
        
        Properties prop = null;
        if(originalInstance != null) prop = originalInstance.getData();
        if(prop == null) {
            prop = new Properties();
        }
        
        String res = null;
        if(prop.containsKey(originals)) res = prop.getProperty(originals);
        
        if(res == null) {
            if(originalInstance != null) {
                BigInteger uniqNo = DataUtil.getStringUniqueNumber(originals);
                if(! stringList.contains(uniqNo)) {
                    if(originalInstance instanceof FileStringTable) {
                        FileStringTable fileTable = (FileStringTable) originalInstance;
                        fileTable.set(originals, originals);
                    } else {
                        prop = originalInstance.getData();
                        prop.setProperty(originals, originals);
                    }
                    stringList.add(uniqNo);
                }
            }
            return originals;
        }
        return res;
    }
    
    @Override
    public String getName() {
        if(originalInstance == null) return "ColonizationStringTable";
        return originalInstance.getName();
    }
}
