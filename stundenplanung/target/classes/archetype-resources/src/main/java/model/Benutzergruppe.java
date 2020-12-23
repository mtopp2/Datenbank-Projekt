package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the benutzergruppe database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Benutzergruppe.findAll", query="SELECT b FROM Benutzergruppe b"),
	@NamedQuery(name="Benutzergruppe.findByID", query="SELECT b FROM Benutzergruppe b WHERE b.groupID = :groupID"),
	@NamedQuery(name="Benutzergruppe.findByBGName", query = "SELECT b FROM Benutzergruppe b WHERE b.BGName = :BGName"),
	@NamedQuery(name="Benutzergruppe.updateBenutzergruppe", query="UPDATE Benutzergruppe b SET b.groupID=:groupID, b.BGName=:BGName, b.BGShortName=:BGShortName, b.BGRechte=:BGRechte WHERE b.groupID=:groupID")})
public class Benutzergruppe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int groupID;

	private String BGName;

	private int BGRechte;

	private String BGShortName;

	//bi-directional many-to-one association to Account
	@OneToMany(mappedBy="benutzergruppe")
	private List<Account> accounts;

	public Benutzergruppe() {
	}

	public int getGroupID() {
		return this.groupID;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public String getBGName() {
		return this.BGName;
	}

	public void setBGName(String BGName) {
		this.BGName = BGName;
	}

	public int getBGRechte() {
		return this.BGRechte;
	}

	public void setBGRechte(Integer BGRechte) {
		this.BGRechte = BGRechte;
	}

	public String getBGShortName() {
		return this.BGShortName;
	}

	public void setBGShortName(String BGShortName) {
		this.BGShortName = BGShortName;
	}

	public List<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public Account addAccounts(Account accounts) {
		getAccounts().add(accounts);
		accounts.setBenutzergruppe(this);

		return accounts;
	}

	public Account removeAccounts(Account accounts) {
		getAccounts().remove(accounts);
		accounts.setBenutzergruppe(null);

		return accounts;
	}

}