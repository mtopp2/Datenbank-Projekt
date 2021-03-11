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
		@NamedQuery(name="Stundenplaneintrag.findAllPlan", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.studiengang c WHERE b.modSemester = :semester AND c.SGName = :stgang"),
		@NamedQuery(name="Stundenplaneintrag.findAllPlanB", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.studiengang c JOIN c.faculty f WHERE b.modSemester = :semester AND c.SGName = :stgang AND f.facName = :facName"),
		@NamedQuery(name="Stundenplaneintrag.findAllPlanC", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.studiengang c JOIN c.faculty f WHERE c.SGName = :stgang AND f.facName = :facName"),
		@NamedQuery(name="Stundenplaneintrag.findAllPlanD", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.studiengang c JOIN c.faculty f JOIN a.stundenplansemester s WHERE c.SGName = :stgang AND f.facName = :facName AND s.spsid = :spsid"),
		@NamedQuery(name="Stundenplaneintrag.findAllProf", query = "SELECT a FROM Stundenplaneintrag a JOIN a.sgmodul b JOIN b.dozenten c JOIN a.stundenplansemester s WHERE c.did = :did AND s.spsid = :spsid")})
public class Stundenplaneintrag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int spid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date SPEStartZeit;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date SPEEndZeit;
	
	private String wochentag;

	private int SPTermin;

	private int studierendenzahl;

	private Timestamp zeitStempel;

	//bi-directional many-to-one association to Lehrveranstaltungsart
	@ManyToOne
	@JoinColumn(name="FK_LVID")
	private Lehrveranstaltungsart lehrveranstaltungsart;

	//bi-directional many-to-one association to Raum
	@ManyToOne
	@JoinColumn(name="FK_RID")
	private Raum raum;

	//bi-directional many-to-one association to Sgmodul
	@ManyToOne
	@JoinColumn(name="FK_SGMID")
	private Sgmodul sgmodul;

	//bi-directional many-to-one association to Stundenplansemester
	@ManyToOne
	@JoinColumn(name="FK_SPSID")
	private Stundenplansemester stundenplansemester;

	public Stundenplaneintrag() {
	}

	public int getSpid() {
		return this.spid;
	}

	public void setSpid(int spid) {
		this.spid = spid;
	}

	public Date getSPEEndZeit() {
		return this.SPEEndZeit;
	}

	public void setSPEEndZeit(Date SPEEndZeit) {
		this.SPEEndZeit = SPEEndZeit;
	}

	public Date getSPEStartZeit() {
		return this.SPEStartZeit;
	}

	public void setSPEStartZeit(Date SPEStartZeit) {
		this.SPEStartZeit = SPEStartZeit;
	}
	
	public String getWochentag() {
		return this.wochentag;
	}

	public void setWochentag(String wochentag) {
		this.wochentag = wochentag;
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

	public Stundenplansemester getStundenplansemester() {
		return this.stundenplansemester;
	}

	public void setStundenplansemester(Stundenplansemester stundenplansemester) {
		this.stundenplansemester = stundenplansemester;
	}

}