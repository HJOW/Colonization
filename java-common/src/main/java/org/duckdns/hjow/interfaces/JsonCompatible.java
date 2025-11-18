package org.duckdns.hjow.interfaces;

import org.duckdns.hjow.commons.json.JsonObject;

/** JSON 변환이 상호 가능한 객체임을 표시 */ // TODO 공통 lib 로 이관
public interface JsonCompatible extends CloneableObject {
	/** JSON 데이터로부터 객체 데이터를 불러옮 */
	public void fromJson(JsonObject json);
	/** 이 객체를 JSON 형태로 출력 */
    public JsonObject toJson();
}
