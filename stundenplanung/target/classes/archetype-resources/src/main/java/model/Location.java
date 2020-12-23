package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the location database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Location.findAll", query="SELECT l FROM Location l"),
	@NamedQuery(name="Location.findByLid", query = "SELECT l FROM Location l WHERE l.lid = :lid"),
	@NamedQuery(name="Location.findByLStreet", query = "SELECT l FROM Location l WHERE l.LStreet = :LStreet")})
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int lid;

	private String LCity;

	private String LStreet;

	//bi-directional many-to-one association to Raum
	@OneToMany(mappedBy="location")
	private List<Raum> raums;

	public Location() {
	}

	public int getLid() {
		return this.lid;
	}

	public void setLid(int lid) {
		this.lid = lid;
	}

	public String getLCity() {
		return this.LCity;
	}

	public void setLCity(String LCity) {
		this.LCity = LCity;
	}

	public String getLStreet() {
		return this.LStreet;
	}

	public void setLStreet(String LStreet) {
		this.LStreet = LStreet;
	}

	public List<Raum> getRaums() {
		return this.raums;
	}

	public void setRaums(List<Raum> raums) {
		this.raums = raums;
	}

	public Raum addRaum(Raum raum) {
		getRaums().add(raum);
		raum.setLocation(this);

		return raum;
	}

	public Raum removeRaum(Raum raum) {
		getRaums().remove(raum);
		raum.setLocation(null);

		return raum;
	}

}