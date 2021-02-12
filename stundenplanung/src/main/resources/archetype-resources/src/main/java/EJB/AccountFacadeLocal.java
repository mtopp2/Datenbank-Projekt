package EJB;

import java.util.List;

import javax.ejb.Local;

import model.Account;

@Local
public interface AccountFacadeLocal {
	
void create(Account account);
	
	void edit(Account account);
	
	void remove(Account account);
	
	Account find(Object id);
	
	List<Account> findAll();
	
	List<Account> findRange(int[] range);
	
	int count();

}
