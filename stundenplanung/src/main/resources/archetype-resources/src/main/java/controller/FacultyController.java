package controller;


import model.Faculty;


import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

import java.util.List;



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

import javax.persistence.PersistenceUnit;

import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;


import org.primefaces.event.SelectEvent;


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
	
	/**
	 * Initialisierung
	 */
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
	
	// Getter und Setter
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
			FacesMessage message = new FacesMessage("Fachbereichsname ist bereits vorhanden.");
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
	    	FacesMessage message = new FacesMessage("Fachbereichskürzel ist bereits vorhanden.");
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
	
	/**
	 * Erstellen eines Fachbereichs
	 * @throws Exception
	 */
	public void createFaculty() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Faculty fac = new Faculty();  
		fac.setFacName(facultyName);    
		fac.setFacShortName(facultyShortName);      
		try {
			facFacadeLocal.create(fac);
			msg = "Eintrag wurde erstellt.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht erstellt.";
            addMessage("messages", msg);
	       
	    }
		em.close();
	}
	
	/**
	 * Schaut ob Fachbereichsname und Fachbereichskurzform gesetzt worden sind, danach wird der Eintrag erstellt und zum Schluß wird die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoFaculty() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(facultyNameOk == true && facultyShortNameOk == true ) {
			createFaculty();
			facultyList = getFacultyListAll();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Laden der Fachbereichsliste
	 * @return
	 */
	public List<Faculty> getFacultyListAll(){		
		List<Faculty> listFac;
		listFac = facFacadeLocal.findAll();
		return listFac;
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löschen eines Fachbereichseintrags
     * @throws Exception
     */
    public void deleteFaculty() throws Exception {
    	String msg;
        facultyList.remove(facultySelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Faculty> q = em.createNamedQuery("Faculty.findByFbid",Faculty.class);
        q.setParameter("fbid", facultySelected.getFbid());
        faculty = (Faculty)q.getSingleResult();
        
        try {
        	this.facFacadeLocal.remove(faculty);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
	        
	    }       
		em.close();
    }
     
    /**
     * Ausgewählte Zeile wird in facultySelected gespeichert.
     * @param e
     */
    public void onRowSelect(SelectEvent<Faculty> e) {
    	FacesMessage msg = new FacesMessage("Fakultät ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        facultySelected = e.getObject();
        
    }
    
    /**
     * Bearbeiten eines Fachbereichseintrags
     */
    public void addFaculty(){
    	EntityManager em = emf.createEntityManager();
    	String msg;
    	 try { 
 	       em.find(Faculty.class, facultySelected.getFbid());
 	       faculty.setFbid(facultySelected.getFbid());
 	       faculty.setFacName(facultySelected.getFacName());
 	       faculty.setFacShortName(facultySelected.getFacShortName());
 	       facFacadeLocal.edit(faculty);
 	       msg = "Eintrag wurde bearbeitet.";
           addMessage("messages", msg);
 	    }
 	    catch (Exception e) {
 	    	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
 	    }
    	facultyList = getFacultyListAll();
    	em.close();
    }
    
   // ---------------------------------------------------------------------------------------------------------------------
	  
	/**
	 * Nachrichten an die View senden
	 * @param loginformidName
	 * @param msg
	 */
	private void addMessage(String loginformidName, String msg) {
	   FacesMessage message = new FacesMessage(msg);
	   FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
	}

  
}