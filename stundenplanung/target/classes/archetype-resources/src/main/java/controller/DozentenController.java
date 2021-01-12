package controller;

import model.Account;

import model.Dozenten;
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
	
	ArrayList<String> accountList = new ArrayList<>();
	
	@PostConstruct
    public void init() {
		professorList = getDozentenList();
		//professorList.add(null);
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Account.findAll");
        List AList = q.getResultList();
        for (Object AListitem : AList)
        {
            Account acc =(Account)AListitem;
            accountList.add(acc.getAccName());
        }
    }
 
	
	
	private String professorShort;
	private String professorName;
	private String professorTitle;
	private String professorFirstName;
	private String accountName;
	private boolean professorShortOk = false;
	private boolean professorNameOk = false;

	
	
	List<Dozenten> professorList;
	
	private Dozenten professorSelected;
	
	
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
	  
	public ArrayList<String> getAccountList() {
        return accountList;
    }

    public void setAccountList(ArrayList<String> accountList) {
        this.accountList = accountList;
    }
	
	public String getProfessorShort() {
		return professorShort;
	}
	  
	public void setProfessorShort(String professorShort) {
		if(professorShort!=null){
			this.professorShort = professorShort;
			professorShortOk = true;
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
	public void createDozent() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Dozenten doz = new Dozenten();   
		doz.setDName(professorName);
		doz.setDVorname(professorFirstName);
		doz.setDTitel(professorTitle);
		doz.setDKurz(professorShort);   
		doz.setAccount(findAcc(accountName));
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
	
	public void createDoDozent() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(professorShortOk == true && professorNameOk == true) {
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
    
    public void deleteDozent() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
    	professorList.remove(professorSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Dozenten> q = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
        q.setParameter("did", professorSelected.getDid());
        professor = (Dozenten)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(professor);
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
    
    public void addDozent(){
      	 try {
      		 ut.begin();
		        EntityManager em = emf.createEntityManager();
		        em.find(Dozenten.class, professorSelected.getDid());
		        professor.setDid(professorSelected.getDid());
		        professor.setDKurz(professorSelected.getDKurz());
		        professor.setDName(professorSelected.getDName());
		        professor.setDVorname(professorSelected.getDVorname());
		        professor.setDTitel(professorSelected.getDTitel());
		        professor.setAccount(findAcc(accountName));
		        em.merge(professor);
		        ut.commit();
   	    }
   	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
   	        try {
   	            ut.rollback();
   	        } 
   	        catch (IllegalStateException | SecurityException | SystemException ex) {
   	        }
   	    }
      	professorList = getDozentenList();
      	//professorList.add(null);
      }
    
   // ---------------------------------------------------------------------------------------------------------------------
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}