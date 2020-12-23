package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-11-27T17:20:40.311+0100")
@StaticMetamodel(Account.class)
public class Account_ {
	public static volatile SingularAttribute<Account, Integer> accID;
	public static volatile SingularAttribute<Account, String> accEmail;
	public static volatile SingularAttribute<Account, String> accName;
	public static volatile SingularAttribute<Account, String> accPwd;
	public static volatile SingularAttribute<Account, Benutzergruppe> benutzergruppe;
	public static volatile SingularAttribute<Account, Faculty> faculty;
	public static volatile ListAttribute<Account, Dozenten> dozentens;
}
