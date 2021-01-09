package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the lehrveranstaltungsart database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Lehrveranstaltungsart.findAll", query="SELECT l FROM Lehrveranstaltungsart l"),
	@NamedQuery(name="Lehrveranstaltungsart.findByLvname", query="SELECT l FROM Lehrveranstaltungsart l WHERE l.lvname = :lvname"),
	@NamedQuery(name="Lehrveranstaltungsart.findByLvid", query="SELECT l FROM Lehrveranstaltungsart l WHERE l.lvid = :lvid")})
public class Lehrveranstaltungsart implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int lvid;

	private String lvdauer;

	private String lvkurz;

	private String lvname;

	public Lehrveranstaltungsart() {
	}

	public int getLvid() {
		return this.lvid;
	}

	public void setLvid(int lvid) {
		this.lvid = lvid;
	}

	public String getLvdauer() {
		return this.lvdauer;
	}

	public void setLvdauer(String lvdauer) {
		this.lvdauer = lvdauer;
	}

	public String getLvkurz() {
		return this.lvkurz;
	}

	public void setLvkurz(String lvkurz) {
		this.lvkurz = lvkurz;
	}

	public String getLvname() {
		return this.lvname;
	}

	public void setLvname(String lvname) {
		this.lvname = lvname;
	}

}