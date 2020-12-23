package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the stundenplaneintrag database table.
 * 
 */
@Entity
@NamedQueries ({
		@NamedQuery(name="Stundenplaneintrag.findAll", query="SELECT s FROM Stundenplaneintrag s"),
		@NamedQuery(name="Stundenplaneintrag.findById", query="SELECT s FROM Stundenplaneintrag s WHERE s.spid = :spid"),
		@NamedQuery(name="Stundenplaneintrag.findAllPlan", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.studiengang c WHERE b.modSemester = :semester AND c.SGName = :stgang")})
public class Stundenplaneintrag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int spid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date SPEStartZeit;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date SPEEndZeit;

	private int SPTermin;

	private int studierendenzahl;

	private Timestamp zeitStempel;

	//bi-directional many-to-one association to Lehrveranstaltungsart
	@ManyToOne
	@JoinColumn(name="FK_LVID")
	public Lehrveranstaltungsart lehrveranstaltungsart;

	//bi-directional many-to-one association to Raum
	@ManyToOne
	@JoinColumn(name="FK_RID")
	public Raum raum;

	//bi-directional many-to-one association to Sgmodul
	@ManyToOne
	@JoinColumn(name="FK_SGMID")
	public Sgmodul sgmodul;

	public Stundenplaneintrag() {
	}

	public int getSpid() {
		return this.spid;
	}

	public void setSpid(int spid) {
		this.spid = spid;
	}

	public Date getSPEStartZeit() {
		return this.SPEStartZeit;
	}

	public void setSPEStartZeit(Date SPEStartZeit) {
		this.SPEStartZeit = SPEStartZeit;
	}

	public Date getSPEEndZeit() {
		return SPEEndZeit;
	}

	public void setSPEEndZeit(Date sPEEndZeit) {
		SPEEndZeit = sPEEndZeit;
	}

	public int getSPTermin() {
		return this.SPTermin;
	}

	public void setSPTermin(int SPTermin) {
		this.SPTermin = SPTermin;
	}

	public int getStudierendenzahl() {
		return this.studierendenzahl;
	}

	public void setStudierendenzahl(int studierendenzahl) {
		this.studierendenzahl = studierendenzahl;
	}

	public Timestamp getZeitStempel() {
		return this.zeitStempel;
	}

	public void setZeitStempel(Timestamp zeitStempel) {
		this.zeitStempel = zeitStempel;
	}

	public Lehrveranstaltungsart getLehrveranstaltungsart() {
		return this.lehrveranstaltungsart;
	}

	public void setLehrveranstaltungsart(Lehrveranstaltungsart lehrveranstaltungsart) {
		this.lehrveranstaltungsart = lehrveranstaltungsart;
	}

	public Raum getRaum() {
		return this.raum;
	}

	public void setRaum(Raum raum) {
		this.raum = raum;
	}

	public Sgmodul getSgmodul() {
		return this.sgmodul;
	}

	public void setSgmodul(Sgmodul sgmodul) {
		this.sgmodul = sgmodul;
	}

}