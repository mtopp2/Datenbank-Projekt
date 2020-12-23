package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the faculty database table.
 * 
 */
@Entity


@NamedQueries({
	@NamedQuery(name="Faculty.findAll", query="SELECT f FROM Faculty f"),
	@NamedQuery(name="Faculty.findByFacName", query = "SELECT f FROM Faculty f WHERE f.facName = :facName"),
	@NamedQuery(name = "Faculty.findByFbid", query = "SELECT f FROM Faculty f WHERE f.fbid = :fbid"),
    @NamedQuery(name="Faculty.updateFaculty", query="UPDATE Faculty f SET f.fbid=:fbid, f.facName=:facName, f.facShortName=:facShortName WHERE f.fbid=:fbid")})
public class Faculty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int fbid;

	private String facName;

	private String facShortName;

	//bi-directional many-to-one association to Account
	@OneToMany(mappedBy="faculty")
	private List<Account> accounts;

	//bi-directional many-to-one association to Studiengang
	@OneToMany(mappedBy="faculty")
	private List<Studiengang> studiengangs;

	public Faculty() {
	}

	public int getFbid() {
		return this.fbid;
	}

	public void setFbid(int fbid) {
		this.fbid = fbid;
	}

	public String getFacName() {
		return this.facName;
	}

	public void setFacName(String facName) {
		this.facName = facName;
	}

	public String getFacShortName() {
		return this.facShortName;
	}

	public void setFacShortName(String facShortName) {
		this.facShortName = facShortName;
	}

	public List<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public Account addAccount(Account account) {
		getAccounts().add(account);
		account.setFaculty(this);

		return account;
	}

	public Account removeAccount(Account account) {
		getAccounts().remove(account);
		account.setFaculty(null);

		return account;
	}

	public List<Studiengang> getStudiengangs() {
		return this.studiengangs;
	}

	public void setStudiengangs(List<Studiengang> studiengangs) {
		this.studiengangs = studiengangs;
	}

	public Studiengang addStudiengang(Studiengang studiengang) {
		getStudiengangs().add(studiengang);
		studiengang.setFaculty(this);

		return studiengang;
	}

	public Studiengang removeStudiengang(Studiengang studiengang) {
		getStudiengangs().remove(studiengang);
		studiengang.setFaculty(null);

		return studiengang;
	}

}