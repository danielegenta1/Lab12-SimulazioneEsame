package it.polito.tdp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;

public class Model 
{
	private List<Event>events;
	private EventsDao dao;
	private SimpleWeightedGraph<Integer, DefaultWeightedEdge> grafo;
	List<Integer> districts;
	
	//punto 2
	Simulatore sim;
	
	public Model()
	{
		dao = new EventsDao();
		events = dao.listAllEvents();
		grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//punto 2
		Simulatore sim = new Simulatore();
	}


	//TODO stamapare in modo più carino
	public String handleCreaReteCittadina(int annoSelezionato)
	{
		// posso anche fare semplice query
		districts = new LinkedList<Integer>();
		for (Event e : events)
		{
			int district_id = e.getDistrict_id();
			if (!districts.contains(district_id))
				districts.add(district_id);
		}
		
		// determino centro geografico per ogni distretto
		// mappa che ha come chiave district_id e come valore epicentro per tale distretto in tale anno
		Map<Integer, LatLng> centroDistretto = new TreeMap<Integer, LatLng>();
		double sumLat = 0;
		double sumLon = 0;
		int cnt = 0;
		double centro_i_lat = 0;
		double centro_i_lon = 0;
		for (int district : districts)
		{
			sumLat = 0;
			sumLon = 0;
			cnt = 0;
			centro_i_lat = 0;
			centro_i_lon = 0;
			// posso anche far media con query
			for (Event e : events)
			{
				if (e.getDistrict_id() == district && e.getReported_date().getYear() == annoSelezionato)
				{
					sumLat += e.getGeo_lat();
					sumLon += e.getGeo_lon();
					cnt++;
				}
			}
			centro_i_lat = sumLat / cnt;
			centro_i_lon = sumLon / cnt;
			centroDistretto.put(district, new LatLng(centro_i_lat, centro_i_lon));
		}
		
		// creo grafo
		
		// creo vertici
		Graphs.addAllVertices(this.grafo, centroDistretto.keySet());
		
		// creo archi => su pochi vertici questo metodo va bene 
		for (Integer distretto : grafo.vertexSet())
		{
			for (Integer distretto2 : grafo.vertexSet())
			{
				if (!distretto.equals(distretto2))
				{
					double peso = LatLngTool.distance(centroDistretto.get(distretto), centroDistretto.get(distretto2), LengthUnit.KILOMETER);
					if (this.grafo.getEdge(distretto, distretto2) != null)
					{
						grafo.addEdge(distretto, distretto2);
						grafo.setEdgeWeight(grafo.getEdge(distretto, distretto2), peso);
					}
				}
			}
		}
		
		
		//per ogni distretto distretti adiacenti
		// per stampare in ordine mi dovrei creare una classe vicino
		String res = "";
		for (int distretto : centroDistretto.keySet())
		{
			res+=("Vicini di: " + distretto);
			List<Vicino> vicini = new LinkedList<Vicino>();
			
			List<Integer> aus = Graphs.neighborListOf(this.grafo, distretto);
			for (int i : aus)
			{
				if (i != distretto)
					vicini.add(new Vicino(i, grafo.getEdgeWeight(grafo.getEdge(distretto, i))));
			}
			Collections.sort(vicini);
			res+=("\n"+vicini+"\n");
		}
		
		return res;
	}
	
	public List<Integer> loadYears() 
	{
		List<Integer>years = new LinkedList<Integer>();
		for (Event e : events)
		{
			int anno = e.getReported_date().getYear();
			if (!years.contains(anno))
				years.add(anno);
		}
		Collections.sort(years);
		return years;
	}

	public List<Integer> loadMonths()
	{
		List<Integer>months = new LinkedList<Integer>();
		for (Event e : events)
		{
			int mese = e.getReported_date().getMonth().getValue();
			if (!months.contains(mese))
				months.add(mese);
		}
		Collections.sort(months);
		return months;
	}

	public List<Integer> loadDays()
	{
		List<Integer>days = new LinkedList<Integer>();
		for (Event e : events)
		{
			int giorno = e.getReported_date().getDayOfMonth();
			if (!days.contains(giorno))
				days.add(giorno);
		}
		Collections.sort(days);
		return days;
	}


	public void handleSimula(int n, int giorno, int mese, int anno)
	{
		// Trovo distretto a minor criminalita NELLO SCORSO ANNO
		// trovo punto di partenza 
		int cnt_distretto_best = Integer.MAX_VALUE;
		int distretto_best = -1;
		for (int d : districts)
		{
			int cnt = 0;
			for (Event e : events)
			{
				if (e.getReported_date().getYear() == (anno-1))
					cnt++;
			}
			if (cnt < cnt_distretto_best)
			{
				cnt_distretto_best = cnt;
				distretto_best = d;
			}
		}
		
		LocalDate dataInteresse = LocalDate.of(anno, mese, giorno);
		sim = new Simulatore();
		sim.init(grafo, events, districts, n, dataInteresse, distretto_best);
		sim.run();
	}
	
}
