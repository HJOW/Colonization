package org.duckdns.hjow.colonization.web.db;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.commons.util.ClassUtil;

/** DB 액세스를 위한 Util, MyBatis 사용 */
public class DBUtil {
    private static final DBUtil INSTANCES = new DBUtil();
    public static void init() {};
    public static SqlSession openSession()                   { return INSTANCES.sessionFactory.openSession();           }
    public static SqlSession openSession(boolean autoCommit) { return INSTANCES.sessionFactory.openSession(autoCommit); }
    
    /** Mapper 받기 */
    public static ColonizationMapper openMapper() {
        SqlSession session = openSession();
        return session.getMapper(ColonizationMapper.class);
    }
    
    /** Mapper 받기 */
    public static ColonizationMapper openMapper(boolean autoCommit) {
        SqlSession session = openSession(autoCommit);
        return session.getMapper(ColonizationMapper.class);
    }
    
    protected SqlSessionFactory sessionFactory;
    private DBUtil() {
        InputStream inp1 = null;
        try {
            File roots = ColonyManager.getHomeDir("colonizationweb", "db");
            if(! roots.exists()) roots.mkdirs();
            
            File dbFile = new File(roots.getAbsolutePath() + File.separator + "db");
            String jdbcUrl = "jdbc:hsqldb:file:" + ( dbFile.getAbsolutePath().replace("\\", "/") );
            
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("org.hsqldb.jdbcDriver");
            dataSource.setUrl(jdbcUrl);
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            dataSource.setPoolMaximumActiveConnections(10);
            dataSource.setPoolMaximumIdleConnections(5);
            dataSource.setPoolPingEnabled(true);
            dataSource.setPoolPingQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
            
            Resources.setCharset(Charset.forName("UTF-8"));
            inp1 = Resources.getResourceAsStream("/mapper/colonization.xml");
            
            Environment env = new Environment("development", new JdbcTransactionFactory(), dataSource);
            Configuration conf = new Configuration(env);
            conf.addMapper(ColonizationMapper.class);
            conf.setMapUnderscoreToCamelCase(true);
            conf.setJdbcTypeForNull(JdbcType.NULL);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inp1, conf, "/mapper/colonization.xml", conf.getSqlFragments());
            mapperParser.parse();
            
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            sessionFactory = builder.build(conf);
        } catch(Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            ClassUtil.closeAll(inp1);
            inp1 = null;
        }
    }
}
