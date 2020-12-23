package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-12-09T10:48:27.103+0100")
@StaticMetamodel(Sgmodul.class)
public class Sgmodul_ {
	public static volatile SingularAttribute<Sgmodul, Integer> sgmid;
	public static volatile SingularAttribute<Sgmodul, Integer> modSemester;
	public static volatile SingularAttribute<Sgmodul, String> SGMNotiz;
	public static volatile SingularAttribute<Sgmodul, Dozenten> dozenten;
	public static volatile SingularAttribute<Sgmodul, Modul> modul;
	public static volatile SingularAttribute<Sgmodul, Studiengang> studiengang;
	public static volatile ListAttribute<Sgmodul, Stundenplaneintrag> stundenplaneintrags;
}
