package it.polito.tdp.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.model.Evento.TIPO;

public class Simulatore 
{
	// stato del mondo
	Graph<Integer, DefaultWeightedEdge> grafo;
	List<Event> allEvents;
	
	//parametri di simulazione
	int nAgentiDisponibili;
	LocalDate  dataSelezionata;
	int distretto_partenza;
	
	Map<Integer, Integer>agentiStanzialiLiberi; //agenti stanziali per ogni distretto
	
	//output
	int malGestiti;
	
	PriorityQueue<Evento> queue = new PriorityQueue<Evento>();
	
	public void init(SimpleWeightedGraph<Integer, DefaultWeightedEdge> grafo, List<Event> allEvents, List<Integer> districts, int nAgenti, LocalDate dataSelezionata, int distretto_partenza)
	{
		// inizializzo i parametri
		this.grafo = grafo;
		this.allEvents = allEvents;
		this.nAgentiDisponibili = nAgenti;
		this.dataSelezionata = dataSelezionata;
		this.distretto_partenza = distretto_partenza;
		this.malGestiti = 0;
		
		// mi serve?
		/*LocalTime aus = LocalTime.of(0, 0);
		LocalDateTime t_inizio = LocalDateTime.of(dataSelezionata, aus);*/
		
		// alla partenza tutti gli agenti sono nello stesso posto
		agentiStanzialiLiberi = new TreeMap<Integer,Integer>();
		for (int d : districts)
		{
			if (d != distretto_partenza)
				agentiStanzialiLiberi.put(d, 0);
			else
				agentiStanzialiLiberi.put(d, nAgenti);
		}
		
		//aggiungo ordinatamente gli eventi alla coda
		//Collections.sort(allEvents);
		for (Event e : allEvents)
		{
			if (dataSelezionata.isEqual(e.getReported_date().toLocalDate()))
			{
				//genero evento
				this.queue.add(new Evento(e.getReported_date(),e.getReported_date(), e.getDistrict_id(), TIPO.AGENTE_DEVE_PARTIRE, e.getOffense_category_id()));
			}
		}
		
	}
	
	public void run()
	{
		// Estraggo un evento per volta dalla coda e lo eseguo finchè coda non si svuota
		Evento e;
		while ((e = queue.poll()) != null)
		{
System.out.println("evento: " + e);
			switch (e.getTipo())
			{
				case AGENTE_DEVE_PARTIRE:
					//ci sono agenti liberi
					if (nAgentiDisponibili > 0)
					{
						//agente libero su posto
						if (agentiStanzialiLiberi.get(e.getDistretto()) > 0)
						{
							//genero nuovo evento agente arriva con tempo 0 
							this.queue.add(new Evento(e.getT().plusSeconds(1),e.getT_reported(), e.getDistretto(), TIPO.AGENTE_ARRIVA, e.getCategoria()));
							agentiStanzialiLiberi.put(e.getDistretto(), agentiStanzialiLiberi.get(e.getDistretto())-1);
							nAgentiDisponibili--;
						}
						else
						{
							//agente libero più vicino
							double peso_min = Integer.MAX_VALUE;
							int distretto_agente_min = -1;
							for (int dis : agentiStanzialiLiberi.keySet())
							{
								if (agentiStanzialiLiberi.get(dis) > 0)
								{
									double peso_aus = 0;
									if (e.getDistretto() != dis)
									{
										peso_aus = grafo.getEdgeWeight(grafo.getEdge(e.getDistretto(), dis));
										if (peso_aus < peso_min)
										{
											peso_min = peso_aus;
											distretto_agente_min = dis;
										}
									}
								}
							}
							
							//trovato agente utile (controllo forse ridondante)
							if (distretto_agente_min != -1)
							{
								//aggiorno agenti stanziali
								agentiStanzialiLiberi.put(distretto_agente_min, agentiStanzialiLiberi.get(distretto_agente_min)-1);
								
								//diminuisco agenti disponibili
								nAgentiDisponibili--;
								
								//tempo di viaggio
								Duration tempoViaggio = Duration.ofSeconds((long) ((peso_min/60)*3600));
								
								//nuovo evento
								this.queue.add(new Evento(e.getT().plus(tempoViaggio.toMinutes(), ChronoUnit.MINUTES),e.getT_reported(), e.getDistretto(), TIPO.AGENTE_ARRIVA, e.getCategoria()));
							}
						}
					}
					else
					{
						System.out.println("\nNon ci sono agenti disponibili");
						malGestiti++;
					}
						
					
					break;
				case AGENTE_ARRIVA:
					//agente deve gestire evento
					
					//tempo di gestione evento
					int tempoGestione = 0;
					if (e.getCategoria().compareTo("all_crimes") == 0)
					{
						Random rnd = new Random();
						tempoGestione = rnd.nextInt(1)+1; //tra 1 e 2 ore
					}
					else
						tempoGestione = 2;
					
					//nuovo evento
					this.queue.add(new Evento(e.getT().plusHours(tempoGestione),e.getT_reported(), e.getDistretto(), TIPO.AGENTE_LIBERO, e.getCategoria()));
					
					//malgestito?
					if (e.getT().isAfter(e.getT_reported().plusMinutes(15)))
						malGestiti++;
			
					break;
				case AGENTE_LIBERO:
					//rimane in zona selezionata
					//aggiorno agenti stanziali
					agentiStanzialiLiberi.put(e.getDistretto(), agentiStanzialiLiberi.get(e.getDistretto())+1);
					
					//aumento agenti disponibili
					nAgentiDisponibili++;
				default:
					break;
			}
		}
		System.out.println("Numero di crimes malgestiti: " + malGestiti);
	}

}
