package it.polito.tdp.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento>
{
	enum TIPO
	{
		AGENTE_DEVE_PARTIRE,
		AGENTE_ARRIVA,
		AGENTE_LIBERO
	}
	
	private LocalDateTime t;
	private LocalDateTime t_reported;
	private int distretto;
	private TIPO tipo;
	private String categoria;
	
	
	public Evento(LocalDateTime t,LocalDateTime t2, int distretto, TIPO tipo, String categoria)
	{
		super();
		this.t = t;
		this.setT_reported(t2);
		this.distretto = distretto;
		this.tipo = tipo;
		this.categoria = categoria;
	}


	public LocalDateTime getT() {
		return t;
	}


	public void setT(LocalDateTime t) {
		this.t = t;
	}


	public int getDistretto() {
		return distretto;
	}


	public void setDistretto(int distretto) {
		this.distretto = distretto;
	}


	public TIPO getTipo() {
		return tipo;
	}


	public void setTipo(TIPO tipo) {
		this.tipo = tipo;
	}


	public String getCategoria() {
		return categoria;
	}


	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	public LocalDateTime getT_reported() {
		return t_reported;
	}


	public void setT_reported(LocalDateTime t_arrival) {
		this.t_reported = t_arrival;
	}


	@Override
	public int compareTo(Evento o) 
	{
		if (this.t.isBefore(o.t))
			return -1;
		else
			return 1;
	}


	@Override
	public String toString() {
		return "Evento [t=" + t + ", t_reported=" + t_reported + ", distretto=" + distretto + ", tipo=" + tipo
				+ ", categoria=" + categoria + "]";
	}
	
	
	
	
	
	
	
	
}
