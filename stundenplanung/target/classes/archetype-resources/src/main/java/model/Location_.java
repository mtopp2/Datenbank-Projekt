package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-03-10T18:14:23.442+0100")
@StaticMetamodel(Location.class)
public class Location_ {
	public static volatile SingularAttribute<Location, Integer> lid;
	public static volatile SingularAttribute<Location, String> LCity;
	public static volatile SingularAttribute<Location, String> LStreet;
	public static volatile ListAttribute<Location, Raum> raums;
}
