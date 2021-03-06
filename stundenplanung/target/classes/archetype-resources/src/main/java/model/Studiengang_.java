package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-03-04T17:26:33.730+0100")
@StaticMetamodel(Studiengang.class)
public class Studiengang_ {
	public static volatile SingularAttribute<Studiengang, Integer> sgid;
	public static volatile SingularAttribute<Studiengang, Integer> semester;
	public static volatile SingularAttribute<Studiengang, String> SGKurz;
	public static volatile SingularAttribute<Studiengang, String> SGName;
	public static volatile ListAttribute<Studiengang, Sgmodul> sgmoduls;
	public static volatile SingularAttribute<Studiengang, Faculty> faculty;
}
