package controller;

import model.Account;

import model.Dozenten;
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

//@ManagedBean(name="DozentenController")
@Named(value = "dozentenController")
//@SessionScoped
@SessionScoped
public class DozentenController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	//@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject 
	private Dozenten dozenten;
	private Account account;
	
	ArrayList<String> AccountListe = new ArrayList<>();
	
	@PostConstruct
    public void init() {
        dozlist = getDozentenList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Account.findAll");
        List AList = q.getResultList();
        for (Object AListitem : AList)
        {
            Account acc =(Account)AListitem;
            AccountListe.add(acc.getAccName());
        }
    }
 
	
	
	private String DKurz;
	private String DName;
	private String DTitel;
	private String DVorname;
	private String accName;
	private boolean DKurz_ok = false;
	
	
	List<Dozenten> dozlist;
	
	//modlist.add(getModulList());
	
	private Dozenten selecteddozenten;
	
	
	public Dozenten getSelecteddozenten() {
		return selecteddozenten;
	}
	  
	public void setSelecteddozenten(Dozenten selecteddozenten) {
		this.selecteddozenten = selecteddozenten;
	}
	
  
    public List<Dozenten> getDozlist() {
        return dozlist;
    }
    
    public void setDozlist(List<Dozenten> dozlist) {
		this.dozlist = dozlist;
		
	}
    
	public Dozenten getDozenten() {
		return dozenten;
	}
	  
	public void setDozenten(Dozenten dozenten) {
		this.dozenten = dozenten;
	}
	  
	public ArrayList<String> getAccountListe() {
        return AccountListe;
    }

    public void setAccountListe(ArrayList<String> AccountListe) {
        this.AccountListe = AccountListe;
    }
	
	public String getDKurz() {
		return DKurz;
	}
	  
	public void setDKurz(String DKurz) {
		if(DKurz!=null){
			this.DKurz = DKurz;
			DKurz_ok = true;
		}
		else{
			FacesMessage message = new FacesMessage("Dozentenkürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("DozentenForm:DKurz_reg", message);
	        //String msg = "Modulkürzel bereits vorhanden.";
	        //addMessage("modKuerzel_reg",msg);
	    }
	}
	  
	public String getDName() {
		return DName;
	}
	  
	public void setDName(String DName) {	   
	        this.DName = DName;	        
	}
	  
	public String getDTitel() {
		return DTitel;
	}
	  
	public void setDTitel(String DTitel) {
	        this.DTitel = DTitel;	   
	}
	
	public String getDVorname() {
		return DVorname;
	}
	  
	public void setDVorname(String DVorname) {
	        this.DVorname = DVorname;
	}
	 public String getAccName() {
	        return accName;
	    }

	public void setAccName(String accName) {
	        this.accName = accName;
	    }
	    
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
    
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
	  
	private UIComponent reg;  
	public void createDozenten() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Dozenten doz = new Dozenten();  
		doz.setDKurz(DKurz);    
		doz.setDName(DName);
		doz.setDVorname(DVorname);
		doz.setDTitel(DTitel);
		doz.setAccount(findAcc(accName));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(doz);  
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
	
	public String createDoDozenten() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(DKurz_ok == true) {
			createDozenten();
			return "showdozenten.xhtml";
		}
		else{
			return "showdozenten.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Dozenten> getDozentenList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Dozenten> query = em.createNamedQuery("Dozenten.findAll", Dozenten.class);
		dozlist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Dozenten> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Edited", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Edited", event.getObject().getModID());
        FacesMessage msg = new FacesMessage("Dozenten Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Dozenten newdoz = new Dozenten();
        newdoz = event.getObject();
        if(newdoz.getAccount() != null) {
	        try {
		        ut.begin();
		        EntityManager em = emf.createEntityManager();
		        //em.joinTransaction();
		        em.find(Dozenten.class, newdoz.getDid());
		        //em.persist(q)
		        dozenten.setDid(newdoz.getDid());
		        dozenten.setDKurz(newdoz.getDKurz());
		        dozenten.setDName(newdoz.getDName());
		        dozenten.setDVorname(newdoz.getDVorname());
		        dozenten.setDTitel(newdoz.getDTitel());
		        dozenten.setAccount(findAcc(newdoz.getAccount().getAccName()));   
		        
		        em.merge(dozenten);
		        
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
        
    }
     
    public void onRowCancel(RowEditEvent<Dozenten> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Cancelled", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Cancelled", event.getObject().getModID());
    	FacesMessage msg = new FacesMessage("Dozenten Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteDozenten() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        dozlist.remove(selecteddozenten);
        //selectedmodul = null;
        //updateModul(modlist);
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Dozenten> q = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
        q.setParameter("did", selecteddozenten.getDid());
        dozenten = (Dozenten)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        //em.persist(q);
	        em.remove(dozenten);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selecteddozenten = null;
		em.close();
    }
    
    private Account findAcc(String acc) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Account> query
                = em.createNamedQuery("Account.findByAccName",Account.class);
            query.setParameter("accName", acc);
            account = (Account)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return account;
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
		//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}