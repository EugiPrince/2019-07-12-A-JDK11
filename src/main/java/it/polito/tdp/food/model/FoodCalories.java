package it.polito.tdp.food.model;

public class FoodCalories implements Comparable<FoodCalories>{

	private Food food;
	private Double calories;
	
	/**
	 * @param food
	 * @param calories
	 */
	public FoodCalories(Food food, Double calories) {
		super();
		this.food = food;
		this.calories = calories;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	@Override
	public int compareTo(FoodCalories o) {
		//Deve ritornare poi una lista in ordine decrescente (da max a min), quindi metterò il meno
		return -(this.calories.compareTo(o.calories));
	}
	
}
