package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-03-10T18:14:23.512+0100")
@StaticMetamodel(Stundenplanstatus.class)
public class Stundenplanstatus_ {
	public static volatile SingularAttribute<Stundenplanstatus, Integer> spstid;
	public static volatile SingularAttribute<Stundenplanstatus, String> PColor;
	public static volatile SingularAttribute<Stundenplanstatus, String> SPSTBezeichnung;
	public static volatile SingularAttribute<Stundenplanstatus, String> SPSTHint;
	public static volatile ListAttribute<Stundenplanstatus, Stundenplansemester> stundenplansemesters;
}
