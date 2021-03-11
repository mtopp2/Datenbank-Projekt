package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dozenten database table.
 * 
 */
@Entity

@NamedQueries({
@NamedQuery(name="Dozenten.findAll", query="SELECT d FROM Dozenten d"),
@NamedQuery(name="Dozenten.findByDName", query = "SELECT d FROM Dozenten d WHERE d.DName = :DName"),
@NamedQuery(name = "Dozenten.findByDid", query = "SELECT d FROM Dozenten d WHERE d.did = :did"),
@NamedQuery(name = "Dozenten.findByDTitel", query = "SELECT d FROM Dozenten d WHERE d.DTitel = :DTitel"),
@NamedQuery(name = "Dozenten.findByDVorname", query = "SELECT d FROM Dozenten d WHERE d.DVorname = :DVorname"),
@NamedQuery(name = "Dozenten.findByDKurz", query = "SELECT d FROM Dozenten d WHERE d.DKurz = :DKurz")})

public class Dozenten implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int did;

	private String DKurz;

	private String DName;

	private String DTitel;

	private String DVorname;

	//bi-directional many-to-one association to Sgmodul
	@OneToMany(mappedBy="dozenten")
	private List<Sgmodul> sgmoduls;

	public Dozenten() {
	}

	public int getDid() {
		return this.did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public String getDKurz() {
		return this.DKurz;
	}

	public void setDKurz(String DKurz) {
		this.DKurz = DKurz;
	}

	public String getDName() {
		return this.DName;
	}

	public void setDName(String DName) {
		this.DName = DName;
	}

	public String getDTitel() {
		return this.DTitel;
	}

	public void setDTitel(String DTitel) {
		this.DTitel = DTitel;
	}

	public String getDVorname() {
		return this.DVorname;
	}

	public void setDVorname(String DVorname) {
		this.DVorname = DVorname;
	}

	public List<Sgmodul> getSgmoduls() {
		return this.sgmoduls;
	}

	public void setSgmoduls(List<Sgmodul> sgmoduls) {
		this.sgmoduls = sgmoduls;
	}

	public Sgmodul addSgmodul(Sgmodul sgmodul) {
		getSgmoduls().add(sgmodul);
		sgmodul.setDozenten(this);

		return sgmodul;
	}

	public Sgmodul removeSgmodul(Sgmodul sgmodul) {
		getSgmoduls().remove(sgmodul);
		sgmodul.setDozenten(null);

		return sgmodul;
	}

}