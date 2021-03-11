package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the stundenplanstatus database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Stundenplanstatus.findAll", query="SELECT s FROM Stundenplanstatus s"),
	@NamedQuery(name = "Stundenplanstatus.findBySpsid", query = "SELECT s FROM Stundenplanstatus s WHERE s.spstid = :spstid"),
	@NamedQuery(name = "Stundenplanstatus.findBySPSTBezeichnung", query = "SELECT s FROM Stundenplanstatus s WHERE s.SPSTBezeichnung = :SPSTBezeichnung")})
public class Stundenplanstatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int spstid;

	private String PColor;

	private String SPSTBezeichnung;

	private String SPSTHint;

	//bi-directional many-to-one association to Stundenplansemester
	@OneToMany(mappedBy="stundenplanstatus")
	private List<Stundenplansemester> stundenplansemesters;

	public Stundenplanstatus() {
	}

	public int getSpstid() {
		return this.spstid;
	}

	public void setSpstid(int spstid) {
		this.spstid = spstid;
	}

	public String getPColor() {
		return this.PColor;
	}

	public void setPColor(String PColor) {
		this.PColor = PColor;
	}

	public String getSPSTBezeichnung() {
		return this.SPSTBezeichnung;
	}

	public void setSPSTBezeichnung(String SPSTBezeichnung) {
		this.SPSTBezeichnung = SPSTBezeichnung;
	}

	public String getSPSTHint() {
		return this.SPSTHint;
	}

	public void setSPSTHint(String SPSTHint) {
		this.SPSTHint = SPSTHint;
	}

	public List<Stundenplansemester> getStundenplansemesters() {
		return this.stundenplansemesters;
	}

	public void setStundenplansemesters(List<Stundenplansemester> stundenplansemesters) {
		this.stundenplansemesters = stundenplansemesters;
	}

	public Stundenplansemester addStundenplansemester(Stundenplansemester stundenplansemester) {
		getStundenplansemesters().add(stundenplansemester);
		stundenplansemester.setStundenplanstatus(this);

		return stundenplansemester;
	}

	public Stundenplansemester removeStundenplansemester(Stundenplansemester stundenplansemester) {
		getStundenplansemesters().remove(stundenplansemester);
		stundenplansemester.setStundenplanstatus(null);

		return stundenplansemester;
	}

}