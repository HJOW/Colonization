package org.duckdns.hjow.colonization.elements.products;

import java.util.List;

import org.duckdns.hjow.colonization.elements.ColonyElements;

/** 원자재 / 생산품 */
public interface Product extends ColonyElements {
	public String getType();
	public long getPrice();
	
	/** 이 Product 의 표시 이름 반환 */
    public String getTitle();
    
    /** 이 Product 생산에 필요한 재료로 어떤 Product가 필요한지를 반환, 중복 가능 (같은 종류 여러 개가 필요한 경우). 재료가 필요없는 경우 빈 List 객체 반환. getSourceProductsStatic 와 동일. */
    public List<Product> getSourceProducts();
}
