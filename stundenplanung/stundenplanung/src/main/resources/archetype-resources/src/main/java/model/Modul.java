package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the modul database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Modul.findAll", query="SELECT m FROM Modul m"),
	@NamedQuery(name = "Modul.findByModID", query = "SELECT m FROM Modul m WHERE m.modID = :modID"),
	@NamedQuery(name = "Modul.findByModName", query = "SELECT m FROM Modul m WHERE m.modName = :modName"),
    @NamedQuery(name="Modul.updateModul", query="UPDATE Modul m SET m.modID=:modID, m.modName=:modName, m.modKuerzel=:modKuerzel, m.pcid=:pcid WHERE m.modID=:modID")})

public class Modul implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int modID;

	private String modKuerzel;

	private String modName;

	private Integer pcid;

	//bi-directional many-to-one association to Sgmodul
	@OneToMany(mappedBy="modul")
	private List<Sgmodul> sgmoduls;

	public Modul() {
	}

	public int getModID() {
		return this.modID;
	}

	public void setModID(int modID) {
		this.modID = modID;
	}

	public String getModKuerzel() {
		return this.modKuerzel;
	}

	public void setModKuerzel(String modKuerzel) {
		this.modKuerzel = modKuerzel;
	}

	public String getModName() {
		return this.modName;
	}

	public void setModName(String modName) {
		this.modName = modName;
	}

	public Integer getPcid() {
		return this.pcid;
	}

	public void setPcid(Integer pcid) {
		this.pcid = pcid;
	}

	public List<Sgmodul> getSgmoduls() {
		return this.sgmoduls;
	}

	public void setSgmoduls(List<Sgmodul> sgmoduls) {
		this.sgmoduls = sgmoduls;
	}

	public Sgmodul addSgmodul(Sgmodul sgmodul) {
		getSgmoduls().add(sgmodul);
		sgmodul.setModul(this);

		return sgmodul;
	}

	public Sgmodul removeSgmodul(Sgmodul sgmodul) {
		getSgmoduls().remove(sgmodul);
		sgmodul.setModul(null);

		return sgmodul;
	}	

}