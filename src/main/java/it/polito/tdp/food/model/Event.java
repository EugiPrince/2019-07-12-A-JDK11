package it.polito.tdp.food.model;

public class Event implements Comparable<Event>{
	
	public enum EventType {
		INIZIO_PREPARAZIONE, //Quando viene assegnato un cibo ad una stazione
		FINE_PREPARAZIONE, //Completata la preparazione di un cibo
	}

	private Double time; //Tempo in minuti
	private Stazione stazione;
	private Food food;
	private EventType type;
	
	/**
	 * @param time
	 * @param stazione
	 * @param food
	 */
	public Event(Double time, Stazione stazione, Food food, EventType type) {
		super();
		this.time = time;
		this.stazione = stazione;
		this.food = food;
		this.type = type;
	}

	public double getTime() {
		return time;
	}

	public Stazione getStazione() {
		return stazione;
	}

	public Food getFood() {
		return food;
	}

	public EventType getType() {
		return type;
	}

	@Override
	public int compareTo(Event o) {
		return this.time.compareTo(o.time);
	}
	
}

/*
[ F1 ] [ F2 ] [ F3 ]
		
		FINE_PREPARAZIONE(F2, stazione1)  T
		INIZIO_PREPARZIONE(F2 ciboConcluso , stazione1)  T
			scelgo il prossimo cibo, calcolo la durata e schedulo l'evento di fine passandogli il cibo nuovo scelto
		FINE_PREPARAZIONE(F4, stazione1)  T+DELTA  DELTA(peso di F2 -- F4)
			rischedulo evento di INIZIO allo stesso istante, ricordando il cibo terminato
*/