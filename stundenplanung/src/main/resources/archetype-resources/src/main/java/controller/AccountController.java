package controller;
import model.Benutzergruppe;
import model.Account;
import model.Faculty;


import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.transaction.UserTransaction;


import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import EJB.AccountFacadeLocal;

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
	
	@EJB
	private AccountFacadeLocal accFacadeLocal;
	
    ArrayList<String> facultyList = new ArrayList<>();
    ArrayList<String> userGroupList = new ArrayList<>();
	
	/**
	 * Initialisierung
	 */
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
	
	// Getter und Setter
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
			FacesMessage message = new FacesMessage("Accountemail ist bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accEmail_reg", message);
	    }
	}
	  
	public String getAccountName() {
		return accountName;
	}
	  
	public void setAccountName(String accountName) {
		if(accountName!=null){
            if(checkName(accountName)==false){
                this.accountName = accountName;
                accountNameOk=true;
            }
		    else{
		    	FacesMessage message = new FacesMessage("Accountname ist bereits vorhanden.");
	            FacesContext.getCurrentInstance().addMessage("AccountForm:accName_reg", message);
		    }
		}
	}
	  
	public String getAccountPassword() {
		return accountPassword;
	}
	  
	public void setAccountPassword(String accountPassword) {
		if(accountPassword!=null){
	        this.accountPassword = accountPassword;
	        accountPasswordOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Passwort ist bereits vorhanden.");
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
	
	// Erstellen eines Accounteintrags
	/*public void createAccount() throws Exception  {
	 * String msg;
		EntityManager em = emf.createEntityManager();
		Account acc = new Account();  
		acc.setAccName(accountName);
		acc.setAccPwd(accountPassword);
		acc.setAccEmail(accountEmail);
		acc.setBenutzergruppe(findBG(userGroupName));
		acc.setFaculty(findFac(facultyName));
		try {
			accFacadeLocal.create(acc);
			msg = "Eintrag wurde erstellt.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	       msg = "Eintrag wurde nicht erstellt.";
           addMessage("messages", msg);
	    }
		em.close();
	}
	
	// Schaut ob Accountname, Accountpasswort und Accountemail gesetzt worden sind, danach wird der Eintrag erstellt und zum Schluß wird die Liste aktualisiert.
	public void createDoAccount() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(accountNameOk == true && accountPasswordOk == true && accountEmailOk) {
			createAccount();
			accountList = getAccountListAll();
		}
	}*/
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	// 
	/**
	 * Laden der Accountliste
	 * @return
	 */
	public List<Account> getAccountListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
		return query.getResultList();
	}
	
	/**
	 * Ausgewählte Zeile wird in accountSelected gespeichert sowie die Fremdschlüssel
	 * @param e
	 */
	public void onRowSelect(SelectEvent<Account> e) {
    	FacesMessage msg = new FacesMessage("Account ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        accountSelected = e.getObject();
        
        userGroupName = accountSelected.getBenutzergruppe().getBGName();
        facultyName = accountSelected.getFaculty().getFacName();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löschen eines Accounteintrags
     * @throws Exception
     */
    public void deleteAccount() throws Exception {
    	String msg;
    	accountList.remove(accountSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Account> q = em.createNamedQuery("Account.findByAccID",Account.class);
        q.setParameter("accID", accountSelected.getAccID());
        account = (Account)q.getSingleResult();
        
        try {
        	accFacadeLocal.remove(account);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
	       
	    }
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------

    /**
     * Finden einer Benutzergruppe anhand des Benutzerguppennamens
     * @param bg
     * @return
     */
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
    
    /**
     * Finden des Fachbereichs anhand des Fachbereichsnamens
     * @param fac
     * @return
     */
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
    
    /**
     * Bearbeiten des Accounts
     */
    public void addAccount(){
    	String msg;
    	EntityManager em = emf.createEntityManager();
      	 try {
	        em.find(Account.class, accountSelected.getAccID());
	        account.setAccID(accountSelected.getAccID());
	        account.setAccName(accountSelected.getAccName());
	        account.setAccPwd(accountSelected.getAccPwd());
	        account.setAccEmail(accountSelected.getAccEmail());
	        account.setBenutzergruppe(findBG(userGroupName));
            account.setFaculty(findFac(facultyName));
            accFacadeLocal.edit(account);
            msg = "Eintrag wurde bearbeitet.";
            addMessage("messages", msg);
   	    }
   	    catch (Exception e) {
   	    	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
   	    }
      	accountList = getAccountListAll();
      	em.close();
      }
	
	  
	/**
	 * Nachrichten an die View senden
	 * @param loginformidName
	 * @param msg
	 */
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
	
	//-------------------------------------------------------------------------------
	
	/**
	 * Erstellt einen Benutzer, vorher wird geschaut, ob der Benutzer schon vorhanden ist.
	 * @throws Exception
	 */
	public void registerUser2() throws Exception  {
		String msg;
        EntityManager em = emf.createEntityManager();
        List<Account> user_temp = new ArrayList<>();   
        try {
            TypedQuery<Account> queryGet = em.createNamedQuery("Account.findByAccName", Account.class).setParameter("accName", this.accountName);   
            user_temp = queryGet.getResultList();  
        } 
        catch (Exception e) {
        }
        if (user_temp.isEmpty()) {  
            Account newUser = new Account();  
            newUser.setAccName(accountName);    
            newUser.setAccPwd(accountPassword);      
            newUser.setAccEmail(accountEmail);
            newUser.setBenutzergruppe(findBG(userGroupName));
            //Dropdown Menü
            newUser.setFaculty(findFac(facultyName));
            try {
            	accFacadeLocal.create(newUser);
            	msg = "Eintrag wurde erstellt.";
                addMessage("messages", msg);
            }
            catch (Exception e) {
            	msg = "Eintrag wurde nicht erstellt.";
                addMessage("messages", msg);
            }
        } 
        else {
            msg = "Account ist bereits vorhanden.";
            addMessage("messages", msg);
        }
        accountList = getAccountListAll();
        em.close();
	}
	
    /**
     * Überprüfen ob der Name schon vergeben ist.
     * @param uName
     * @return
     */
    private boolean checkName(String uName) {
        boolean found = false;
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Account> query
                = em.createNamedQuery("Account.findByBname",Account.class);
            query.setParameter("accName", uName);
            account = (Account)query.getSingleResult();
            found=true; //Account gefunden!
        }
        catch(Exception e){   
        }
        return found;
    }

	
}