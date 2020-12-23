package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-12-04T14:52:26.126+0100")
@StaticMetamodel(Stundenplansemester.class)
public class Stundenplansemester_ {
	public static volatile SingularAttribute<Stundenplansemester, Integer> spsid;
	public static volatile SingularAttribute<Stundenplansemester, Integer> SPJahr;
	public static volatile SingularAttribute<Stundenplansemester, Integer> SPKw;
	public static volatile SingularAttribute<Stundenplansemester, String> SPSemester;
	public static volatile SingularAttribute<Stundenplansemester, Date> startDatum;
	public static volatile ListAttribute<Stundenplansemester, Studiengang> studiengangs;
	public static volatile SingularAttribute<Stundenplansemester, Stundenplanstatus> stundenplanstatus;
}
