package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the stundenplansemester database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Stundenplansemester.findAll", query="SELECT s FROM Stundenplansemester s"),
	@NamedQuery(name="Stundenplansemester.findBySpsid", query="SELECT s FROM Stundenplansemester s WHERE s.spsid=:spsid"),
	@NamedQuery(name="Stundenplansemester.findBySPSemester", query="SELECT s FROM Stundenplansemester s WHERE s.SPSemester=:SPSemester")})
public class Stundenplansemester implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int spsid;

	private int SPJahr;

	private Integer SPKw;

	private String SPSemester;

	@Temporal(TemporalType.DATE)
	private Date startDatum;

	//bi-directional many-to-one association to Studiengang
	@OneToMany(mappedBy="stundenplansemester")
	private List<Studiengang> studiengangs;

	//bi-directional many-to-one association to Stundenplanstatus
	@ManyToOne
	@JoinColumn(name="FK_SPSTID")
	public Stundenplanstatus stundenplanstatus;

	public Stundenplansemester() {
	}

	public int getSpsid() {
		return this.spsid;
	}

	public void setSpsid(int spsid) {
		this.spsid = spsid;
	}

	public int getSPJahr() {
		return this.SPJahr;
	}

	public void setSPJahr(int SPJahr) {
		this.SPJahr = SPJahr;
	}

	public Integer getSPKw() {
		return this.SPKw;
	}

	public void setSPKw(Integer SPKw) {
		this.SPKw = SPKw;
	}

	public String getSPSemester() {
		return this.SPSemester;
	}

	public void setSPSemester(String SPSemester) {
		this.SPSemester = SPSemester;
	}

	public Date getStartDatum() {
		return this.startDatum;
	}

	public void setStartDatum(Date startDatum) {
		this.startDatum = startDatum;
	}

	public List<Studiengang> getStudiengangs() {
		return this.studiengangs;
	}

	public void setStudiengangs(List<Studiengang> studiengangs) {
		this.studiengangs = studiengangs;
	}

	public Studiengang addStudiengang(Studiengang studiengang) {
		getStudiengangs().add(studiengang);
		studiengang.setStundenplansemester(this);

		return studiengang;
	}

	public Studiengang removeStudiengang(Studiengang studiengang) {
		getStudiengangs().remove(studiengang);
		studiengang.setStundenplansemester(null);

		return studiengang;
	}

	public Stundenplanstatus getStundenplanstatus() {
		return this.stundenplanstatus;
	}

	public void setStundenplanstatus(Stundenplanstatus stundenplanstatus) {
		this.stundenplanstatus = stundenplanstatus;
	}

}