package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sgmodul database table.
 * 
 */
@Entity
@NamedQueries({
@NamedQuery(name="Sgmodul.findAll", query="SELECT s FROM Sgmodul s"),
@NamedQuery(name="Sgmodul.findAllOrderBy", query="SELECT s FROM Sgmodul s GROUP BY s.modSemester ORDER BY s.modSemester"),
@NamedQuery(name = "Sgmodul.findBySgmid", query = "SELECT s FROM Sgmodul s WHERE s.sgmid = :sgmid"),
@NamedQuery(name = "Sgmodul.findByModSemester", query = "SELECT s FROM Sgmodul s WHERE s.modSemester = :modSemester"),
@NamedQuery(name = "Sgmodul.findBySGMNotiz", query = "SELECT s FROM Sgmodul s WHERE s.SGMNotiz = :SGMNotiz"),
@NamedQuery(name = "Sgmodul.findBySemesterAndStudiengang", query = "SELECT s FROM Sgmodul s JOIN s.studiengang g WHERE s.modSemester = :semester AND g.SGName = :SGName"),
@NamedQuery(name = "Sgmodul.findBySemesterAndStudiengangAndFaculty", query = "SELECT s FROM Sgmodul s JOIN s.studiengang g JOIN s.studiengang.faculty f WHERE s.modSemester = :semester AND g.SGName = :SGName AND f.facName = :facName"),
@NamedQuery(name="Sgmodul.updateSgmodul", query="UPDATE Sgmodul s SET s.sgmid=:sgmid, s.modSemester=:modSemester, s.SGMNotiz=:SGMNotiz WHERE s.sgmid=:sgmid")})
public class Sgmodul implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int sgmid;

	private Integer modSemester;

	private String SGMNotiz;

	//bi-directional many-to-one association to Dozenten
	@ManyToOne
	@JoinColumn(name="FK_DID")
	private Dozenten dozenten;

	//bi-directional many-to-one association to Modul
	@ManyToOne
	@JoinColumn(name="FK_ModID")
	private Modul modul;

	//bi-directional many-to-one association to Studiengang
	@ManyToOne
	@JoinColumn(name="FK_SGID")
	private Studiengang studiengang;

	//bi-directional many-to-one association to Stundenplaneintrag
	@OneToMany(mappedBy="sgmodul")
	private List<Stundenplaneintrag> stundenplaneintrags;

	public Sgmodul() {
	}

	public int getSgmid() {
		return this.sgmid;
	}

	public void setSgmid(int sgmid) {
		this.sgmid = sgmid;
	}

	public Integer getModSemester() {
		return this.modSemester;
	}

	public void setModSemester(Integer modSemester) {
		this.modSemester = modSemester;
	}

	public String getSGMNotiz() {
		return this.SGMNotiz;
	}

	public void setSGMNotiz(String SGMNotiz) {
		this.SGMNotiz = SGMNotiz;
	}

	public Dozenten getDozenten() {
		return this.dozenten;
	}

	public void setDozenten(Dozenten dozenten) {
		this.dozenten = dozenten;
	}

	public Modul getModul() {
		return this.modul;
	}

	public void setModul(Modul modul) {
		this.modul = modul;
	}

	public Studiengang getStudiengang() {
		return this.studiengang;
	}

	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}

	public List<Stundenplaneintrag> getStundenplaneintrags() {
		return this.stundenplaneintrags;
	}

	public void setStundenplaneintrags(List<Stundenplaneintrag> stundenplaneintrags) {
		this.stundenplaneintrags = stundenplaneintrags;
	}

	public Stundenplaneintrag addStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().add(stundenplaneintrag);
		stundenplaneintrag.setSgmodul(this);

		return stundenplaneintrag;
	}

	public Stundenplaneintrag removeStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().remove(stundenplaneintrag);
		stundenplaneintrag.setSgmodul(null);

		return stundenplaneintrag;
	}

}