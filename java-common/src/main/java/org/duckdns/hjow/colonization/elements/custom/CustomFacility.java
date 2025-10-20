package org.duckdns.hjow.colonization.elements.custom;

import org.duckdns.hjow.colonization.elements.Facility;

/** 사용자 정의 시설임을 표시하기 위한 인터페이스
 *  사용자 정의 시설 개발 시
 *      일반 시설인 경우 DefaultFacility
 *      생산 시설인 경우 Factory
 *      주거 시설인 경우 Residence
 *      연구 시설인 경우 ResearchCenter
 *  를 같이 상속받아 개발해야 함.
  */
public interface CustomFacility extends Facility {

}
