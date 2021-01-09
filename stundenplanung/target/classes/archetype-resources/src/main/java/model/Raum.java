package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the raum database table.
 * 
 */
@Entity
@NamedQueries ({
@NamedQuery(name="Raum.findAll", query="SELECT r FROM Raum r"),
@NamedQuery(name="Raum.findByRid", query="SELECT r FROM Raum r WHERE r.rid = :rid")})
public class Raum implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int rid;

	private int kapazitaet;

	private String nachbarRaum;

	private String RName;

	//bi-directional many-to-one association to Location
	@ManyToOne
	@JoinColumn(name="FK_LID")
	private Location location;

	//bi-directional many-to-one association to Stundenplaneintrag
	@OneToMany(mappedBy="raum")
	private List<Stundenplaneintrag> stundenplaneintrags;

	public Raum() {
	}

	public int getRid() {
		return this.rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getKapazitaet() {
		return this.kapazitaet;
	}

	public void setKapazitaet(int kapazitaet) {
		this.kapazitaet = kapazitaet;
	}

	public String getNachbarRaum() {
		return this.nachbarRaum;
	}

	public void setNachbarRaum(String nachbarRaum) {
		this.nachbarRaum = nachbarRaum;
	}

	public String getRName() {
		return this.RName;
	}

	public void setRName(String RName) {
		this.RName = RName;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Stundenplaneintrag> getStundenplaneintrags() {
		return this.stundenplaneintrags;
	}

	public void setStundenplaneintrags(List<Stundenplaneintrag> stundenplaneintrags) {
		this.stundenplaneintrags = stundenplaneintrags;
	}

	public Stundenplaneintrag addStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().add(stundenplaneintrag);
		stundenplaneintrag.setRaum(this);

		return stundenplaneintrag;
	}

	public Stundenplaneintrag removeStundenplaneintrag(Stundenplaneintrag stundenplaneintrag) {
		getStundenplaneintrags().remove(stundenplaneintrag);
		stundenplaneintrag.setRaum(null);

		return stundenplaneintrag;
	}

}