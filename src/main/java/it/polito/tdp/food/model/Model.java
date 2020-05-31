package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	
	private List<Food> cibiSelezionati;
	private Graph<Food, DefaultWeightedEdge> grafo;
	private FoodDao dao;
	
	public Model() {
		this.dao = new FoodDao();
	}

	public List<Food> getFoods(int porzioni) {
		this.cibiSelezionati = this.dao.getFoodsByPortions(porzioni);
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Aggiungi i vertici
		Graphs.addAllVertices(this.grafo, this.cibiSelezionati);
		
		//Aggiungi gli archi
		for(Food f1 : this.cibiSelezionati) {
			for(Food f2 : this.cibiSelezionati) {
				if(!f1.equals(f2) && f1.getFood_code() < f2.getFood_code()) { //Non tutte le coppie possibili
					Double peso = this.dao.calorieCongiunte(f1, f2);
					
					if(peso != null) {
						Graphs.addEdgeWithVertices(this.grafo, f1, f2, peso);
					}
				}
			}
		}
		System.out.println(this.grafo);
		
		return this.cibiSelezionati;
	}
	
	public List<FoodCalories> elencoCibiConnessi(Food f) {
		List<FoodCalories> res = new ArrayList<>();
		
		List<Food> vicini = Graphs.neighborListOf(this.grafo, f);
		
		for(Food v : vicini) { //Arco che va da f a v
			Double calories = this.grafo.getEdgeWeight(this.grafo.getEdge(f, v));
			res.add(new FoodCalories(v, calories));
		}
		
		Collections.sort(res);
		return res;
	}
	
}
