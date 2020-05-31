package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.food.model.Event.EventType;
import it.polito.tdp.food.model.Food.StatoPreparazione;

public class Simulator {

	//MODELLO DEL MONDO
	private List<Stazione> stazioni;
	private List<Food> cibi;
	
	private Graph<Food, DefaultWeightedEdge> grafo;
	private Model model;
	
	//PARAMETRI DI SIMULAZIONE
	private int K = 5; //Numero stazioni disponibili (Default 5 ma può essere modificato dall'utente)
	
	//RISULTATI CALCOLATI
	private Double tempoPreparazione;
	private int cibiPreparati;
	
	//CODA DEGLI EVENTI
	private PriorityQueue<Event> queue;
	
	public Simulator(Graph<Food, DefaultWeightedEdge> grafo, Model model) {
		this.grafo = grafo;
		this.model = model;
	}

	public void init(Food partenza) {
		this.cibi = new ArrayList<>(this.grafo.vertexSet());
		for(Food cibo : this.cibi)
			cibo.setPreparazione(StatoPreparazione.DA_FARE);
		
		this.stazioni = new ArrayList<>();
		for(int i=0; i<this.K; i++)
			this.stazioni.add(new Stazione(true, null)); //Stazioni tutte libere e che quindi non stanno preparando
		
		this.tempoPreparazione = 0.0;
		this.cibiPreparati = 0;
		this.queue = new PriorityQueue<>();
		
		List<FoodCalories> vicini = this.model.elencoCibiConnessi(partenza);
		
		//fino a max stazioni disponibli o fino a quando i vicini non finiscono
		for(int i=0; i<this.K && i<vicini.size(); i++) {
			this.stazioni.get(i).setLibera(false); //La i-esima stazione diventa occupata
			this.stazioni.get(i).setFood(vicini.get(i).getFood());
			
			Event e = new Event(vicini.get(i).getCalories(), this.stazioni.get(i), vicini.get(i).getFood(), EventType.FINE_PREPARAZIONE);
			this.queue.add(e);
		}
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}
	
	private void processEvent(Event e) {
		switch(e.getType()) {
		case INIZIO_PREPARAZIONE:
			List<FoodCalories> vicini = this.model.elencoCibiConnessi(e.getFood()); //Vicini del cibo appena tolto dalla stazione
			FoodCalories prossimo = null;
			
			for(FoodCalories vicino : vicini) {
				if(vicino.getFood().getPreparazione()==StatoPreparazione.DA_FARE) {
					prossimo = vicino;
					break; //Esci dal ciclo, non continuare
				}
			}
			//Non devo aggiornare stato del sistema ecc.. e non devo schedulare nuovi eventi da questa stazione, se
			//prossimo rimane a null, sennò:
			if(prossimo != null) {
				prossimo.getFood().setPreparazione(StatoPreparazione.IN_CORSO); //Se sarà adiacente ad altri vertici
				//di cui è appena terminata la preparazione, non verrà preso una seconda volta
				e.getStazione().setLibera(false); //La stazione è occupata
				e.getStazione().setFood(prossimo.getFood()); //Deve ricordarsi cosa sta preparando
				
				Event e2 = new Event(e.getTime()+prossimo.getCalories(), e.getStazione(), 
						prossimo.getFood(), EventType.FINE_PREPARAZIONE);
				this.queue.add(e2);
			}
			break;
			
		case FINE_PREPARAZIONE:
			this.cibiPreparati++;
			this.tempoPreparazione = e.getTime(); //Sovrascrivo ogni volta il valore, alla fine avrò il tempo max
			e.getStazione().setLibera(true); //Finita la preparazione, stazione libera
			e.getFood().setPreparazione(StatoPreparazione.PREPARATO);
			
			Event e2 = new Event(e.getTime(), e.getStazione(), e.getFood(), EventType.INIZIO_PREPARAZIONE);
			this.queue.add(e2);
			break;
		}
	}
	
	public Double getTempoPreparazione() {
		return tempoPreparazione;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}
	
	public int getCibiPreparati() {
		return this.cibiPreparati;
	}
}
