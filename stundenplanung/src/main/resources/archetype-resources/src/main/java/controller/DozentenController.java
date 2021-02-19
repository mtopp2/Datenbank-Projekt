package controller;

import model.Account;

import model.Dozenten;
import model.Location;
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
import javax.ejb.EJB;
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

import EJB.DozentenFacadeLocal;
import EJB.ModulFacadeLocal;

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

@Named(value = "dozentenController")
@SessionScoped
public class DozentenController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Dozenten professor;
	private Account account;
	
	@EJB
	private DozentenFacadeLocal dozentenFacadeLocal;
	
	List<Account> accountList ;
	
	@PostConstruct
    public void init() {
		professorList = getDozentenList();
		accountList = getAccountList();
    }
 
	
	private int accountId;
	private String professorShortName;
	private String professorName;
	private String professorTitle;
	private String professorFirstName;
	private String accountName;
	private boolean professorShortNameOk = false;
	private boolean professorNameOk = false;

	
	
	List<Dozenten> professorList;
	
	private Dozenten professorSelected;
	
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	public Dozenten getProfessorSelected() {
		return professorSelected;
	}
	  
	public void setProfessorSelected(Dozenten professorSelected) {
		this.professorSelected = professorSelected;
	}
	
  
    public List<Dozenten> getProfessorList() {
        return professorList;
    }
    
    public void setProfessorList(List<Dozenten> professorList) {
		this.professorList = professorList;
		
	}
    
	public Dozenten getProfessor() {
		return professor;
	}
	  
	public void setProfessor(Dozenten professor) {
		this.professor = professor;
	}
	  
	
	
	public String getProfessorShortName() {
		return professorShortName;
	}
	  
	public void setProfessorShortName(String professorShortName) {
		if(professorShortName!=null){
			this.professorShortName = professorShortName;
			professorShortNameOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Bitte Dozentenkürzel eingeben.");
            FacesContext.getCurrentInstance().addMessage("DozentenForm:DKurz_reg", message);
	    }
	}
	  
	public String getProfessorName() {
		return professorName;
	}
	  
	public void setProfessorName(String professorName) {	   
	        if(professorName!=null){
	        	this.professorName = professorName;
	        	professorNameOk = true;
			}
			else{
				FacesMessage message = new FacesMessage("Bitte Dozentennamen eingeben.");
	            FacesContext.getCurrentInstance().addMessage("DozentenForm:DName_reg", message);
		    }
	}
	  
	public String getProfessorTitle() {
		return professorTitle;
	}
	  
	public void setProfessorTitle(String professorTitle) {
	        this.professorTitle = professorTitle;	   
	}
	
	public String getProfessorFirstName() {
		return professorFirstName;
	}
	  
	public void setProfessorFirstName(String professorFirstName) {
	        this.professorFirstName = professorFirstName;
	}
	 public String getAccountName() {
	        return accountName;
	    }

	public void setAccountName(String accountName) {
	        this.accountName = accountName;
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
	public void createDozent() {
		Dozenten doz = new Dozenten();   
		doz.setDName(professorName);
		doz.setDVorname(professorFirstName);
		doz.setDTitel(professorTitle);
		doz.setDKurz(professorShortName);   
		doz.setAccount(findAcc(accountId));
	    dozentenFacadeLocal.create(doz);
	}
	
	public void createDoDozent() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(professorShortNameOk == true && professorNameOk == true) {
			createDozent();
			professorList = getDozentenList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Dozenten> getDozentenList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Dozenten> query = em.createNamedQuery("Dozenten.findAll", Dozenten.class);
		professorList = query.getResultList();
		return query.getResultList();
	}
    
    public void onRowSelect(SelectEvent<Dozenten> e) {
    	FacesMessage msg = new FacesMessage("Dozenten ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        professorSelected = e.getObject();
        
        accountName = professorSelected.getAccount().getAccName();
        if (accountName == null) {
        	this.accountName = null;
        }
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteDozent() {
    	professorList.remove(professorSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Dozenten> q = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
        q.setParameter("did", professorSelected.getDid());
        professor = (Dozenten)q.getSingleResult();
        dozentenFacadeLocal.remove(professor);
		em.close();
    }
    
    public List<Account> getAccountList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
		accountList = query.getResultList();
		return accountList;
	}
    
    private Account findAcc(int accountId) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Account> query
                = em.createNamedQuery("Account.findByAccID",Account.class);
            query.setParameter("accID", accountId);
            account = (Account)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return account;
    }
    
    public void addDozent(){
        EntityManager em = emf.createEntityManager();
        em.find(Dozenten.class, professorSelected.getDid());
        professor.setDid(professorSelected.getDid());
        professor.setDKurz(professorSelected.getDKurz());
        professor.setDName(professorSelected.getDName());
        professor.setDVorname(professorSelected.getDVorname());
        professor.setDTitel(professorSelected.getDTitel());
        professor.setAccount(findAcc(accountId));
        dozentenFacadeLocal.edit(professor);
      	professorList = getDozentenList();
		em.close();
      }
    
   // ---------------------------------------------------------------------------------------------------------------------
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}