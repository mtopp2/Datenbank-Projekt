package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-01-07T13:49:04.512+0100")
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
