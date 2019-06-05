package it.polito.tdp.model;

public class Vicino implements Comparable<Vicino>
{
	private int district_id;
	private double distanza;
	
	public Vicino(int district_id, double d)
	{
		super();
		this.district_id = district_id;
		this.distanza = d;
	}

	public int getDistrict_id() {
		return district_id;
	}

	public void setDistrict_id(int district_id) {
		this.district_id = district_id;
	}

	public double getDistanza() {
		return distanza;
	}

	public void setDistanza(double distanza) {
		this.distanza = distanza;
	}

	@Override
	public int compareTo(Vicino o) 
	{
		return (int) (this.distanza - o.distanza);
	}

	@Override
	public String toString() {
		return "Vicino [district_id=" + district_id + ", distanza=" + distanza + "]";
	}
	
	
	
	
	

}
