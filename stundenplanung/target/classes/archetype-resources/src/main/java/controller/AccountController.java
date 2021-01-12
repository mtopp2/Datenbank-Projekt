package controller;
import model.Benutzergruppe;
import model.Account;
import model.Faculty;
import model.Raum;
import model.Sgmodul;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sun.javafx.logging.Logger;
import org.primefaces.event.CellEditEvent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import controller.MessageForPrimefaces;

/**
*
* @author Anil
*/

@Named(value="accountController")
@SessionScoped
public class AccountController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;

	@Inject 
	private Account account;
	private Faculty faculty;
	private Benutzergruppe userGroup;
	
    ArrayList<String> facultyList = new ArrayList<>();
    ArrayList<String> userGroupList = new ArrayList<>();
	
	@PostConstruct
    public void init() {
		accountList = getAccountListAll();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Faculty.findAll");
        List FList = q.getResultList();
        for (Object FListitem : FList)
        {
            Faculty fac =(Faculty)FListitem;
            facultyList.add(fac.getFacName());
        }
        Query b = em.createNamedQuery("Benutzergruppe.findAll");
        List BList = b.getResultList();
        for (Object BListitem : BList)
        {
            Benutzergruppe bg =(Benutzergruppe)BListitem;
            userGroupList.add(bg.getBGName());
        }
    }
	
	private String accountName;
	private String accountPassword;
	private String accountEmail;
	private String userGroupName;
	private String facultyName;
	private boolean accountEmailOk = false;
	private boolean accountNameOk = false;
	private boolean accountPasswordOk = false;
	
	List<Account> accountList;
	
	private Account accountSelected;
	
	public Account getAccountSelected() {
		return accountSelected;
	}
	  
	public void setAccountSelected(Account accountSelected) {
		this.accountSelected = accountSelected;
	}
	
	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}
	  
    public List<Account> getAccountList() {
        return accountList;
    }
    
    public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
		
	}
    
	public Account getAccount() {
		return account;
	}
	  
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public ArrayList<String> getFacultyList() {
        return facultyList;
    }

    public void setFacultyList(ArrayList<String> facultyList) {
        this.facultyList = facultyList;
    }
    public ArrayList<String> getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(ArrayList<String> userGroupList) {
        this.userGroupList = userGroupList;
    }
	  
	public String getAccountEmail() {
		return accountEmail;
	}
	  
	public void setAccountEmail(String accountEmail) {
		if(accountEmail!=null){
			this.accountEmail = accountEmail;
			accountEmailOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Accountemail bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accEmail_reg", message);
	    }
	}
	  
	public String getAccountName() {
		return accountName;
	}
	  
	public void setAccountName(String accountName) {
	    if(accountName!=null){
	        this.accountName = accountName;
	        accountNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Accountname bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accName_reg", message);
	    }
	}
	  
	public String getAccountPassword() {
		return accountPassword;
	}
	  
	public void setAccPwd(String accountPassword) {
		if(accountPassword!=null){
	        this.accountPassword = accountPassword;
	        accountPasswordOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("PWD bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accPwd_reg", message);
	    }
	}
	
	public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
    
    public Benutzergruppe getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Benutzergruppe userGroup) {       
        this.userGroup = userGroup;
    }
	  
	private UIComponent reg;
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public void createAccount() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Account acc = new Account();  
		acc.setAccName(accountName);
		acc.setAccPwd(accountPassword);
		acc.setAccEmail(accountEmail);
		acc.setBenutzergruppe(findBG(userGroupName));
		acc.setFaculty(findFac(facultyName));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(acc);  
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
	}
	
	public void createDoAccount() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(accountNameOk == true && accountPasswordOk == true && accountEmailOk) {
			createAccount();
			accountList = getAccountListAll();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Account> getAccountListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
		accountList = query.getResultList();
		return query.getResultList();
	}
	
	public void onRowSelect(SelectEvent<Account> e) {
    	FacesMessage msg = new FacesMessage("Account ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        accountSelected = e.getObject();
        
        userGroupName = accountSelected.getBenutzergruppe().getBGName();
        facultyName = accountSelected.getFaculty().getFacName();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteAccount() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
    	accountList.remove(accountSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Account> q = em.createNamedQuery("Account.findByAccID",Account.class);
        q.setParameter("accID", accountSelected.getAccID());
        account = (Account)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(account);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    private Benutzergruppe findBG(String bg) {
    	try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Benutzergruppe> query
                = em.createNamedQuery("Benutzergruppe.findByBGName",Benutzergruppe.class);
            query.setParameter("BGName", bg);
            userGroup = (Benutzergruppe)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return userGroup;
    }
    
    private Faculty findFac(String fac) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Faculty> query
                = em.createNamedQuery("Faculty.findByFacName",Faculty.class);
            query.setParameter("facName", fac);
            faculty = (Faculty)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return faculty;
    }
    
    public void addAccount(){
      	 try {
      		ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Account.class, accountSelected.getAccID());
	        account.setAccID(accountSelected.getAccID());
	        account.setAccName(accountSelected.getAccName());
	        account.setAccPwd(accountSelected.getAccPwd());
	        account.setAccEmail(accountSelected.getAccEmail());
	        account.setBenutzergruppe(findBG(userGroupName));
            account.setFaculty(findFac(facultyName));
	        em.merge(account);
	        ut.commit(); 
   	    }
   	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
   	        try {
   	            ut.rollback();
   	        } 
   	        catch (IllegalStateException | SecurityException | SystemException ex) {
   	        }
   	    }
      	accountList = getAccountListAll();
      }
	
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}