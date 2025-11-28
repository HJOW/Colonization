package org.duckdns.hjow.colonization.elements.facilities;

import java.util.Vector;

import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.ship.Ship;

/** 우주공항 */
public interface Port extends Facility {
    /** 소속 함선 수 반환 (건조 수 포함) */
    public int getShipCount();
    
    /** 소속 함선 수 반환 (건조 수 제외) */
    public int getLiveShipCount();
    
    /** 소속 함선들 반환 (격납 중인 함선이 아님 ! 파견되어 있더라도 소속이 이 곳이면 여전히 조회됨) - 건조 중인 함선 제외 */
	public Vector<Ship> getShipsLive();
	
	/** 건조 중인 함선들 반환 */
	public Vector<Ship> getShipsBuilding();

    /** 소속 함선들 반환 (격납 중인 함선이 아님 ! 파견되어 있더라도 소속이 이 곳이면 여전히 조회됨) - 건조 중인 함선 포함 */
	public Vector<Ship> getShips();
	
	/** 함선 목록 자체를 변경 (직접호출 비추천 !) */
	public void setShips(Vector<Ship> ships);
	
	/** 함선을 이 우주공항에 정박 (원래 있던 곳에서 제거는 따로 해주어야 함 !) */
	public Port addShip(Ship s);
	
	/** 함선 이 우주공항에서 제거 */
	public void removeShip(Ship ship);
	
	/** 건조 / 업그레이드 중인 함선 수 반환 */
	public int getBuildingShipCount();
	
	/** 함선 건조 / 업그레이드 라인 수 반환 */
	public int getBuildingLineCount();
	
	/** 함선 격납공간 사용량 */
	public int usingShipSpaces();
	
	/** 함선 격납공간 남은 공간 */
	public int leftShipSpaces();
}
