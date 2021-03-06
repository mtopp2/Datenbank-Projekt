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
	@NamedQuery(name="Stundenplansemester.findAllGroupBySpsemester", query="SELECT s FROM Stundenplansemester s GROUP BY s.SPSemester"),
	@NamedQuery(name="Stundenplansemester.findAllGroupBySpJahr", query="SELECT s FROM Stundenplansemester s GROUP BY s.SPJahr"),
	@NamedQuery(name="Stundenplansemester.findBySpsid", query="SELECT s FROM Stundenplansemester s WHERE s.spsid=:spsid"),
	@NamedQuery(name="Stundenplansemester.findBySPSemester", query="SELECT s FROM Stundenplansemester s WHERE s.SPSemester=:SPSemester"),
	@NamedQuery(name="Stundenplansemester.findBySPSemesterAndYear", query="SELECT s FROM Stundenplansemester s WHERE s.SPSemester=:SPSemester AND s.SPJahr=:SPJahr")})
public class Stundenplansemester implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int spsid;

	private int SPJahr;

	private Integer SPKw;

	private String SPSemester;

	@Temporal(TemporalType.DATE)
	private Date startDatum;
	
	@Temporal(TemporalType.DATE)
	private Date endDatum;

	//bi-directional many-to-one association to Stundenplaneintrag
	@OneToMany(mappedBy="stundenplansemester")
	private List<Stundenplaneintrag> stundenplaneintrags;

	//bi-directional many-to-one association to Stundenplanstatus
	@ManyToOne
	@JoinColumn(name="FK_SPSTID")
	private Stundenplanstatus stundenplanstatus;

	public Stundenplansemester() {
	}

	public int getSpsid() {
		return this.spsid;
	}

	public void setSpsid(int spsid) {
		this.spsid = spsid;
	}

	public Date getEndDatum() {
		return this.endDatum;
	}

	public void setEndDatum(Date endDatum) {
		this.endDatum = endDatum;
	}

	public int getSPJahr() {
		return this.SPJahr;
	}

	public void setSPJahr(int SPJahr) {
		this.SPJahr = SPJahr;
	}

	public int getSPKw() {
		return this.SPKw;
	}

	public void setSPKw(int SPKw) {
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

	public List<Stundenplaneintrag> getStundenplaneintrags() {
		return this.stundenplaneintrags;
	}

	public void setStundenplaneintrags(List<Stundenplaneintrag> stundenplaneintrags) {
		this.stundenplaneintrags = stundenplaneintrags;
	}

	public Stundenplaneintrag addStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().add(stundenplaneintrag);
		stundenplaneintrag.setStundenplansemester(this);

		return stundenplaneintrag;
	}

	public Stundenplaneintrag removeStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().remove(stundenplaneintrag);
		stundenplaneintrag.setStundenplansemester(null);

		return stundenplaneintrag;
	}

	public Stundenplanstatus getStundenplanstatus() {
		return this.stundenplanstatus;
	}

	public void setStundenplanstatus(Stundenplanstatus stundenplanstatus) {
		this.stundenplanstatus = stundenplanstatus;
	}

}