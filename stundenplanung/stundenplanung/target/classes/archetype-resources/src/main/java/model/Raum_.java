package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-02-15T14:32:14.131+0100")
@StaticMetamodel(Raum.class)
public class Raum_ {
	public static volatile SingularAttribute<Raum, Integer> rid;
	public static volatile SingularAttribute<Raum, Integer> kapazitaet;
	public static volatile SingularAttribute<Raum, String> nachbarRaum;
	public static volatile SingularAttribute<Raum, String> RName;
	public static volatile SingularAttribute<Raum, Location> location;
	public static volatile ListAttribute<Raum, Stundenplaneintrag> stundenplaneintrags;
}
