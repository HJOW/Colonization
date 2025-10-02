package org.duckdns.hjow.colonization.web.db;

import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.duckdns.hjow.commons.util.ClassUtil;

/** DB 액세스를 위한 Util, MyBatis 사용 */
public class DBUtil {
    private static final DBUtil INSTANCES = new DBUtil();
    public static SqlSession openSession()                   { return INSTANCES.sessionFactory.openSession();           }
    public static SqlSession openSession(boolean autoCommit) { return INSTANCES.sessionFactory.openSession(autoCommit); }
    
    protected SqlSessionFactory sessionFactory;
    public DBUtil() {
        Reader rd = null;
        try {
            Resources.setCharset(Charset.forName("UTF-8"));
            rd = Resources.getResourceAsReader("mybatis.xml");
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            sessionFactory = builder.build(rd);
            rd.close(); rd = null;
        } catch(Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            ClassUtil.closeAll(rd);
            rd = null;
        }
    }
}
