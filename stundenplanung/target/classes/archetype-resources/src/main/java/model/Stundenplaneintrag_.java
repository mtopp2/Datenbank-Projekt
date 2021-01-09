package model;

import java.sql.Timestamp;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-01-07T13:49:04.527+0100")
@StaticMetamodel(Stundenplaneintrag.class)
public class Stundenplaneintrag_ {
	public static volatile SingularAttribute<Stundenplaneintrag, Integer> spid;
	public static volatile SingularAttribute<Stundenplaneintrag, Date> SPEStartZeit;
	public static volatile SingularAttribute<Stundenplaneintrag, Date> SPEEndZeit;
	public static volatile SingularAttribute<Stundenplaneintrag, Integer> SPTermin;
	public static volatile SingularAttribute<Stundenplaneintrag, Integer> studierendenzahl;
	public static volatile SingularAttribute<Stundenplaneintrag, Timestamp> zeitStempel;
	public static volatile SingularAttribute<Stundenplaneintrag, Lehrveranstaltungsart> lehrveranstaltungsart;
	public static volatile SingularAttribute<Stundenplaneintrag, Raum> raum;
	public static volatile SingularAttribute<Stundenplaneintrag, Sgmodul> sgmodul;
}
