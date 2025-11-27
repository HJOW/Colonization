package org.duckdns.hjow.colonization.ui;

import java.awt.Image;
import java.io.Serializable;
import java.util.Set;

/** 이미지 리소스를 담을 클래스용 인터페이스, 이미지 리소스는 별도 프로젝트로 만들어 별도 jar 파일이 나오도록 만들 예정 */
public interface ImageResourcePackage extends Serializable {
	/** 해당 이미지 반환 */
    public Image getImage(String imageName);
    
    /** 이 패키지에 포함된 이미지 이름들 반환 */
    public Set<String> getImageNames();
}
