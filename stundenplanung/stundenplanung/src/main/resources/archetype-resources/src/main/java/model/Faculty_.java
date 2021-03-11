package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-02-15T14:32:14.081+0100")
@StaticMetamodel(Faculty.class)
public class Faculty_ {
	public static volatile SingularAttribute<Faculty, Integer> fbid;
	public static volatile SingularAttribute<Faculty, String> facName;
	public static volatile SingularAttribute<Faculty, String> facShortName;
	public static volatile ListAttribute<Faculty, Account> accounts;
	public static volatile ListAttribute<Faculty, Studiengang> studiengangs;
}
