package org.duckdns.hjow.colonization.elements.products.food;

public class NutritionBlock extends Food {
	private static final long serialVersionUID = 74752871747134632L;

	@Override
	protected String getDefaultNamePrefix() {
		return "영양블록";
	}

	@Override
	public String getTitle() {
		return "영양블록";
	}

	@Override
	public int getComportGrade() {
		return 0;
	}

}
