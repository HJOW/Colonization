package org.duckdns.hjow.colonization.elements.city;

import java.io.IOException;

import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.policies.PowerEfficiencyProtocol;
import org.duckdns.hjow.commons.json.JsonObject;

/** 일반 도시 구현 클래스 */
public final class NormalCity extends AbstractCity {
    private static final long serialVersionUID = -393966678915786643L;

    public NormalCity() {
        super();
    }
    
    public NormalCity(JsonObject json) throws IOException {
        super(json);
    }
    
    /** 정책 목록 초기화 */
    public void resetPolicies() {
        policies.clear();
        policies.add(new PowerEfficiencyProtocol());
    }
    
    /** 도시 건설 비용 */
    public static long getBuildingNewCityFee(Colony col) { return 1000000L; }
}
