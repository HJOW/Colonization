package org.duckdns.hjow.colonization.web.db;

import java.util.List;
import java.util.Map;

/** MyBatis 액세스용 Mapper */
public interface ColonizationMapper {
    public List<Map<String, Object>> selectCustomSql(String sql);
}
