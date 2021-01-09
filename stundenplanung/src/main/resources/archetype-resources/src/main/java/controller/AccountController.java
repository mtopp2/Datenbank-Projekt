package controller;
import model.Benutzergruppe;
import model.Account;
import model.Faculty;
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

import com.sun.javafx.logging.Logger;

import org.primefaces.event.CellEditEvent;
//import org.primefaces.event.


import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import controller.MessageForPrimefaces;

/**
*
* @author Anil
*/

//@ManagedBean(name="AccountController")
@Named(value="accountController")
//@SessionScoped
@SessionScoped
public class AccountController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	//@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject 
	private Account account;
	private Faculty faculty;
	private Benutzergruppe benutzergruppe;
	
    //private ArrayList StudiengangNamensListe;
    ArrayList<String> FacultyListe = new ArrayList<>();
    ArrayList<String> BenutzergruppeListe = new ArrayList<>();
	
	@PostConstruct
    public void init() {
        acclist = getAccountList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Faculty.findAll");
        List FList = q.getResultList();
        for (Object FListitem : FList)
        {
            Faculty fac =(Faculty)FListitem;
            FacultyListe.add(fac.getFacName());
        }
        Query b = em.createNamedQuery("Benutzergruppe.findAll");
        List BList = b.getResultList();
        for (Object BListitem : BList)
        {
            Benutzergruppe bg =(Benutzergruppe)BListitem;
            BenutzergruppeListe.add(bg.getBGName());
        }
    }
 
	
	private String BGName;
	private String accEmail;
	private String accName;
	private String accPwd;
	private String facName;
	private boolean accEmail_ok = false;
	private boolean accName_ok = false;
	private boolean accPwd_ok = false;
	
	List<Account> acclist;
	
	//modlist.add(getModulList());
	
	private Account selectedaccount;
	
	public Account getSelectedaccount() {
		return selectedaccount;
	}
	  
	public void setSelectedaccount(Account selectedaccount) {
		this.selectedaccount = selectedaccount;
	}
	
	public String getBGName() {
		return BGName;
	}

	public void setBGName(String bGName) {
		this.BGName = bGName;
	}
	  
    public List<Account> getAcclist() {
        return acclist;
    }
    
    public void setAcclist(List<Account> acclist) {
		this.acclist = acclist;
		
	}
    
	public Account getAccount() {
		return account;
	}
	  
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public ArrayList<String> getFacultyListe() {
        return FacultyListe;
    }

    public void setFacultyListe(ArrayList<String> facultyListe) {
        this.FacultyListe = facultyListe;
    }
    public ArrayList<String> getBenutzergruppeListe() {
        return BenutzergruppeListe;
    }

    public void setBenutzergruppeListe(ArrayList<String> benutzergruppeListe) {
        this.BenutzergruppeListe = benutzergruppeListe;
    }
	  
	public String getAccEmail() {
		return accEmail;
	}
	  
	public void setAccEmail(String accEmail) {
		if(accEmail!=null){
			this.accEmail = accEmail;
			accEmail_ok = true;
		}
		else{
			FacesMessage message = new FacesMessage("Accountemail bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accEmail_reg", message);
	        //String msg = "Modulkürzel bereits vorhanden.";
	        //addMessage("modKuerzel_reg",msg);
	    }
	}
	  
	public String getAccName() {
		return accName;
	}
	  
	public void setAccName(String accName) {
	    if(accName!=null){
	        this.accName = accName;
	        accName_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Accountname bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accName_reg", message);
	        //String msg = "Modulname bereits vorhanden.";
	        //addMessage("modName_reg",msg);
	    }
	}
	  
	public String getAccPwd() {
		return accPwd;
	}
	  
	public void setAccPwd(String accPwd) {
		if(accPwd!=null){
	        this.accPwd = accPwd;
	        accPwd_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("PWD bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("AccountForm:accPwd_reg", message);
	        //String msg = "Prüfcodeid bereits vorhanden.";
	        //addMessage("pcid_reg",msg);
	    }
	}
	
	public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
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
    
    public Benutzergruppe getBenutzergruppe() {
        return benutzergruppe;
    }

    public void setBenutzergruppe(Benutzergruppe benutzergruppe) {       
        this.benutzergruppe = benutzergruppe;
    }
	  
	private UIComponent reg;  
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Account> getAccountList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
		acclist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Account> event) {
        
        FacesMessage msg = new FacesMessage("Account Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Account newacc = new Account();
        newacc = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        //em.joinTransaction();
	        em.find(Account.class, newacc.getAccID());
	        //em.persist(q)
	        account.setAccID(newacc.getAccID());
	        account.setAccName(newacc.getAccName());
	        account.setAccPwd(newacc.getAccPwd());
	        account.setAccEmail(newacc.getAccEmail());
	        account.setBenutzergruppe(findBG(newacc.getBenutzergruppe().getBGName()));
            account.setFaculty(findFac(newacc.getFaculty().getFacName()));
	        
	        
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
    }
     
    public void onRowCancel(RowEditEvent<Account> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Cancelled", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Cancelled", event.getObject().getModID());
    	FacesMessage msg = new FacesMessage("Account Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteAccount() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        acclist.remove(selectedaccount);
        //selectedmodul = null;
        //updateModul(modlist);
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Account> q = em.createNamedQuery("Account.findByAccID",Account.class);
        q.setParameter("accID", selectedaccount.getAccID());
        account = (Account)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        //em.persist(q);
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
        selectedaccount = null;
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    private Benutzergruppe findBG(String bg) {
    	try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Benutzergruppe> query
                = em.createNamedQuery("Benutzergruppe.findByBGName",Benutzergruppe.class);
            query.setParameter("BGName", bg);
            benutzergruppe = (Benutzergruppe)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return benutzergruppe;
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
	
	  
		//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}