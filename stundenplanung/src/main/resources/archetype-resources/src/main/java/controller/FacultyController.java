package controller;

import model.Account;
import model.Faculty;
import model.Location;
import model.Modul;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import EJB.FacultyFacadeLocal;

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

@Named(value="facultyController")
@SessionScoped
public class FacultyController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Faculty faculty;
	
	@EJB
	private FacultyFacadeLocal facFacadeLocal;
	
	
	@PostConstruct
    public void init() {
        facultyList = getFacultyListAll();
    }
 
	
	
	private String facultyName;
	private String facultyShortName;
	
	private boolean facultyNameOk = false;
	private boolean facultyShortNameOk = false;
	
	
	List<Faculty> facultyList;
	
	
	private Faculty facultySelected;
	
	public Faculty getFacultySelected() {
		return facultySelected;
	}
	  
	public void setFacultySelected(Faculty facultySelected) {
		this.facultySelected = facultySelected;
	}
	
	
	  
    public List<Faculty> getFacultyList() {
        return facultyList;
    }
    
	public Faculty getFaculty() {
		return faculty;
	}
	  
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	  
	public String getFacultyName() {
		return facultyName;
	}
	  
	public void setFacultyName(String facultyName) {
		if(facultyName!=null){
			this.facultyName = facultyName;
			facultyNameOk = true;
		}
		else{
			FacesMessage message = new FacesMessage("Faculty bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("FacultyList:facName_reg", message);
	    }
	}
	  
	public String getFacultyShortName() {
		return facultyShortName;
	}
	  
	public void setFacultyShortName(String facultyShortName) {
	    if(facultyShortName!=null){
	        this.facultyShortName = facultyShortName;
	        facultyShortNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Facultykürzel bereits vorhanden.");
            FacesContext.getCurrentInstance().addMessage("FacultyList:facShortName_reg", message);
	    }
	}
	  
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createFaculty() {
		Faculty fac = new Faculty();  
		fac.setFacName(facultyName);    
		fac.setFacShortName(facultyShortName);      
		facFacadeLocal.create(fac);
	}
	
	public void createDoFaculty() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(facultyNameOk == true && facultyShortNameOk == true ) {
			createFaculty();
			facultyList = getFacultyListAll();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Faculty> getFacultyListAll(){		
		List<Faculty> listFac;
		listFac = facFacadeLocal.findAll();
		return listFac;
	}
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteFaculty() throws Exception {
        facultyList.remove(facultySelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Faculty> q = em.createNamedQuery("Faculty.findByFbid",Faculty.class);
        q.setParameter("fbid", facultySelected.getFbid());
        faculty = (Faculty)q.getSingleResult();
        this.facFacadeLocal.remove(faculty);    
		em.close();
    }
    
    
    public void onRowSelect(SelectEvent<Faculty> e) {
    	FacesMessage msg = new FacesMessage("Fakultät ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        facultySelected = e.getObject();
        
    }
    
    public void addFaculty(){
 	        EntityManager em = emf.createEntityManager();
 	        em.find(Faculty.class, facultySelected.getFbid());
 	        faculty.setFbid(facultySelected.getFbid());
 	        faculty.setFacName(facultySelected.getFacName());
 	        faculty.setFacShortName(facultySelected.getFacShortName());
 	        facFacadeLocal.edit(faculty);
 	        em.close();

    }
    
    
    
   // ---------------------------------------------------------------------------------------------------------------------
    
	
	  
	//Nachrichten an die View senden
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}
  
  
  
  
}