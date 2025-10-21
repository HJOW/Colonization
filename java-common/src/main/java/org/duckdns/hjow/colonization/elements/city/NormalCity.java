package org.duckdns.hjow.colonization.elements.city;

import java.io.IOException;

import org.duckdns.hjow.commons.json.JsonObject;

/** 일반 도시 구현 클래스 */
public final class NormalCity extends City {
	private static final long serialVersionUID = -393966678915786643L;

	public NormalCity() {
        super();
    }
    
    public NormalCity(JsonObject json) throws IOException {
        super(json);
    }
}
