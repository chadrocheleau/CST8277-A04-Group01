package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2023-03-29T11:27:04.712-0400")
@StaticMetamodel(SecurityUser.class)
public class SecurityUser_ {
	public static volatile SingularAttribute<SecurityUser, String> username;
	public static volatile SingularAttribute<SecurityUser, String> pwHash;
	public static volatile SingularAttribute<SecurityUser, Student> student;
	public static volatile SetAttribute<SecurityUser, SecurityRole> roles;
	public static volatile SingularAttribute<SecurityUser, Integer> id;
}
