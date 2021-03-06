package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the studiengang database table.
 * 
 */
@Entity

@NamedQueries({
@NamedQuery(name="Studiengang.findAll", query="SELECT s FROM Studiengang s"),
@NamedQuery(name="Studiengang.findBySgid", query="SELECT s FROM Studiengang s WHERE s.sgid = :sgid"),
@NamedQuery(name="Studiengang.findBySGKurz", query="SELECT s FROM Studiengang s WHERE s.SGKurz = :SGKurz"),
@NamedQuery(name="Studiengang.findBySGName", query="SELECT s FROM Studiengang s WHERE s.SGName = :SGName"),
@NamedQuery(name="Studiengang.findBySemester", query="SELECT s FROM Studiengang s WHERE s.semester = :semester")})

public class Studiengang implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int sgid;

	private int semester;

	private String SGKurz;

	private String SGName;

	//bi-directional many-to-one association to Sgmodul
	@OneToMany(mappedBy="studiengang")
	private List<Sgmodul> sgmoduls;

	//bi-directional many-to-one association to Faculty
	@ManyToOne
	@JoinColumn(name="FK_FBID")
	private Faculty faculty;
	
	@OneToMany(mappedBy="studiengang")
	private List<Pruefcode> pruefcodes;


	public Studiengang() {
	}

	public int getSgid() {
		return this.sgid;
	}

	public void setSgid(int sgid) {
		this.sgid = sgid;
	}

	public int getSemester() {
		return this.semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getSGKurz() {
		return this.SGKurz;
	}

	public void setSGKurz(String SGKurz) {
		this.SGKurz = SGKurz;
	}

	public String getSGName() {
		return this.SGName;
	}

	public void setSGName(String SGName) {
		this.SGName = SGName;
	}

	public List<Sgmodul> getSgmoduls() {
		return this.sgmoduls;
	}

	public void setSgmoduls(List<Sgmodul> sgmoduls) {
		this.sgmoduls = sgmoduls;
	}
	
	public List<Pruefcode> getPruefcodes() {
		return this.pruefcodes;
	}

	public void setPruefcodes(List<Pruefcode> pruefcodes) {
		this.pruefcodes = pruefcodes;
	}
	
	public Pruefcode addPruefcode(Pruefcode pruefcode) {
		getPruefcodes().add(pruefcode);
		pruefcode.setStudiengang(this);

		return pruefcode;
	}

	public Pruefcode removePruefcode(Pruefcode pruefcode) {
		getPruefcodes().remove(pruefcode);
		pruefcode.setStudiengang(null);

		return pruefcode;
	}
	
	public Sgmodul addSgmodul(Sgmodul sgmodul) {
		getSgmoduls().add(sgmodul);
		sgmodul.setStudiengang(this);

		return sgmodul;
	}

	public Sgmodul removeSgmodul(Sgmodul sgmodul) {
		getSgmoduls().remove(sgmodul);
		sgmodul.setStudiengang(null);

		return sgmodul;
	}

	public Faculty getFaculty() {
		return this.faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	

}