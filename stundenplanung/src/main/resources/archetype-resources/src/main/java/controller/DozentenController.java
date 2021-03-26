package controller;



import model.Dozenten;


import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

import java.util.List;



import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
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



import EJB.DozentenFacadeLocal;


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
	
	@EJB
	private DozentenFacadeLocal dozentenFacadeLocal;
	

	/**
	 * Initialisierung
	 */
	@PostConstruct
    public void init() {
		professorList = getDozentenList();

    }
 
	
	private String professorShortName;
	private String professorName;
	private String professorTitle;
	private String professorFirstName;
	private boolean professorShortNameOk = false;
	private boolean professorNameOk = false;

	List<Dozenten> professorList;
	
	private Dozenten professorSelected;
	
	// Getter und Setter
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
	    
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;
	
	/**
	 * Erstellen eines Dozenteneintrags
	 */
	public void createDozent() {
		String msg;
		Dozenten doz = new Dozenten();   
		doz.setDName(professorName);
		doz.setDVorname(professorFirstName);
		doz.setDTitel(professorTitle);
		doz.setDKurz(professorShortName);
		try {
			dozentenFacadeLocal.create(doz);
			msg = "Eintrag wurde erstellt.";
            addMessage("messages", msg);
		}catch(Exception e) {
			msg = "Eintrag wurde nicht erstellt.";
            addMessage("messages", msg);
		}
	    
	}
	
	/**
	 * Schaut ob Dozentenname und Dozentenküzel gesetzt worden ist, danach wird der Eintrag erstellt und zum Schluß wird die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoDozent() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(professorShortNameOk == true && professorNameOk == true) {
			createDozent();
			professorList = getDozentenList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Laden der Dozentenliste
	 * @return
	 */
	public List<Dozenten> getDozentenList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Dozenten> query = em.createNamedQuery("Dozenten.findAll", Dozenten.class);
		return query.getResultList();
	}
    
    /**
     * Ausgewählte Zeile wird in professorSelected gespeichert
     * @param e
     */
    public void onRowSelect(SelectEvent<Dozenten> e) {
    	FacesMessage msg = new FacesMessage("Dozenten ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        professorSelected = e.getObject();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    // 
    /**
     * Löschen eines Dozenteneintrags
     */
    public void deleteDozent() {
    	String msg;
    	professorList.remove(professorSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Dozenten> q = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
        q.setParameter("did", professorSelected.getDid());
        professor = (Dozenten)q.getSingleResult();
        try {
        	dozentenFacadeLocal.remove(professor);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
        
        }catch(Exception e) {
        	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
        }
        
		em.close();
    }
    
    /**
     * Bearbeiten eines Dozenten
     */
    public void addDozent(){
    	String msg;
        EntityManager em = emf.createEntityManager();
        try {
        	em.find(Dozenten.class, professorSelected.getDid());
        	professor.setDid(professorSelected.getDid());
            professor.setDKurz(professorSelected.getDKurz());
            professor.setDName(professorSelected.getDName());
            professor.setDVorname(professorSelected.getDVorname());
            professor.setDTitel(professorSelected.getDTitel());
            dozentenFacadeLocal.edit(professor);
        	msg = "Eintrag wurde bearbeitet.";
            addMessage("messages", msg);
        }catch(Exception e) {
        	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
        }
        
      	professorList = getDozentenList();
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