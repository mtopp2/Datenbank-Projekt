package model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the pruefcodes database table.
 * 
 */
@Entity
@Table(name="pruefcodes")
@NamedQueries({
@NamedQuery(name="Pruefcode.findAll", query="SELECT p FROM Pruefcode p ORDER BY p.prCode"),
@NamedQuery(name = "Pruefcode.findByPcid", query = "SELECT p FROM Pruefcode p WHERE p.pcid = :pcid"),
@NamedQuery(name = "Pruefcode.findByPrCode", query = "SELECT p FROM Pruefcode p WHERE p.prCode = :prCode")})

public class Pruefcode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int pcid;

	
	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="FK_SgID")
	private Studiengang studiengang;
	
	//bi-directional many-to-one association to Sgmodul
	@OneToMany(mappedBy="pruefcode")
	private List<Modul> moduls;

	private String pflichtOderWahl;

	private int prCode;

	private String vertiefungsrichtungShortName;

	public Pruefcode() {
	}

	public int getPcid() {
		return this.pcid;
	}

	public void setPcid(int pcid) {
		this.pcid = pcid;
	}

	public Studiengang getStudiengang() {
		return this.studiengang;
	}

	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}

	public String getPflichtOderWahl() {
		return this.pflichtOderWahl;
	}

	public void setPflichtOderWahl(String pflichtOderWahl) {
		this.pflichtOderWahl = pflichtOderWahl;
	}

	public int getPrCode() {
		return this.prCode;
	}

	public void setPrCode(int prCode) {
		this.prCode = prCode;
	}

	public String getVertiefungsrichtungShortName() {
		return this.vertiefungsrichtungShortName;
	}

	public void setVertiefungsrichtungShortName(String vertiefungsrichtungShortName) {
		this.vertiefungsrichtungShortName = vertiefungsrichtungShortName;
	}
	
	public List<Modul> getModuls() {
		return this.moduls;
	}

	public void setModuls(List<Modul> moduls) {
		this.moduls = moduls;
	}

	public Modul addModul(Modul modul) {
		getModuls().add(modul);
		modul.setPruefcode(this);

		return modul;
	}

	public Modul removeModul(Modul modul) {
		getModuls().remove(modul);
		modul.setPruefcode(null);

		return modul;
	}

}