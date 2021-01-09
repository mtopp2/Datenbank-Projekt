package controller;

import model.Account;
import model.Faculty;
import model.Modul;
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

//@ManagedBean(name="FacultyController")
@Named(value="facultyController")
//@SessionScoped
@SessionScoped
public class FacultyController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	//@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject 
	private Faculty faculty;
	
	@PostConstruct
    public void init() {
        faclist = getFacultyList();
    }
 
	
	
	private String facName;
	private String facShortName;
	
	private boolean facName_ok = false;
	private boolean facShortName_ok = false;
	
	
	List<Faculty> faclist;
	
	//modlist.add(getModulList());
	
	private Faculty selectedfaculty;
	
	public Faculty getSelectedfaculty() {
		return selectedfaculty;
	}
	  
	public void setSelectedfaculty(Faculty selectedfaculty) {
		this.selectedfaculty = selectedfaculty;
	}
	
	
	  
    public List<Faculty> getFacList() {
        return faclist;
    }
    
	public Faculty getFaculty() {
		return faculty;
	}
	  
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	  
	public String getFacName() {
		return facName;
	}
	  
	public void setFacName(String facName) {
		if(facName!=null){
			this.facName = facName;
			facName_ok = true;
		}
		else{
			FacesMessage message = new FacesMessage("Faculty bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("FacultyForm:facName_reg", message);
	        //String msg = "Modulkürzel bereits vorhanden.";
	        //addMessage("modKuerzel_reg",msg);
	    }
	}
	  
	public String getFacShortName() {
		return facShortName;
	}
	  
	public void setFacShortName(String facShortName) {
	    if(facShortName!=null){
	        this.facShortName = facShortName;
	        facShortName_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Facultykürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("FacultyForm:facShortName_reg", message);
	        //String msg = "Modulname bereits vorhanden.";
	        //addMessage("modName_reg",msg);
	    }
	}
	  
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createFaculty() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Faculty fac = new Faculty();  
		fac.setFacName(facName);    
		fac.setFacShortName(facShortName);      
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(fac);  
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
	
	public String createDoFaculty() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(facName_ok == true && facShortName_ok == true ) {
			createFaculty();
			return "index.xhtml";
		}
		else{
			return "index.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Faculty> getFacultyList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Faculty> query = em.createNamedQuery("Faculty.findAll", Faculty.class);
		faclist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Faculty> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Edited", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Edited", event.getObject().getModID());
        FacesMessage msg = new FacesMessage("Faculty Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Faculty newfac = new Faculty();
        newfac = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        //em.joinTransaction();
	        em.find(Faculty.class, 279);
	        //em.persist(q)
	        
	        faculty.setFbid(newfac.getFbid());
	        faculty.setFacName(newfac.getFacName());
	        faculty.setFacShortName(newfac.getFacShortName());
	        
	        
	        
	        em.merge(faculty);
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
     
    public void onRowCancel(RowEditEvent<Faculty> event) {
        //MessageForPrimefaces msg = new MessageForPrimefaces("Modul Cancelled", event.getObject().getModID());
        //FacesMessage msg = new FacesMessage("Modul Cancelled", event.getObject().getModID());
    	FacesMessage msg = new FacesMessage("FAculty Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteFaculty() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        faclist.remove(selectedfaculty);
        //selectedmodul = null;
        //updateModul(modlist);
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Faculty> q = em.createNamedQuery("Faculty.findByFbid",Faculty.class);
        q.setParameter("fbid", selectedfaculty.getFbid());
        faculty = (Faculty)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        //em.persist(q);
	        em.remove(faculty);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedfaculty = null;
		em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
		//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}