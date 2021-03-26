package controller;

import model.Modul;

import model.Dozenten;
import model.Studiengang;
import model.Sgmodul;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.primefaces.event.SelectEvent;


import EJB.SgModulFacadeLocal;


/**
*
* @author Anil
*/

@Named(value="sgmodulController")
@SessionScoped
public class SgmodulController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Sgmodul sgmodul;
	private Studiengang course;
	private Modul module;
	private Dozenten professor;
	
	@EJB
	private SgModulFacadeLocal sgModulFacadeLocal;
	
	/**
	 * Initilisierung
	 */
	@PostConstruct
    public void init() {
		sgmodulList = getSgmodulListAll();
		
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Modul.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
        	Modul mod =(Modul)FListitem;
        	moduleList.add(mod);
        }
        Query s = em.createNamedQuery("Studiengang.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Studiengang sg =(Studiengang)SListitem;
        	courseList.add(sg);
        }
        Query d = em.createNamedQuery("Dozenten.findAll");
        List DList = d.getResultList();
        for (Object DListitem : DList)
        {
        	Dozenten doz =(Dozenten)DListitem;
        	professorList.add(doz);
        }
    }
 
    ArrayList<Dozenten> professorList = new ArrayList<>();
    ArrayList<Modul> moduleList = new ArrayList<>();
    ArrayList<Studiengang> courseList = new ArrayList<>();
    
    private int moduleSemester;
	private String sgmodulNote;
	
    private int courseId;
	private int professorId;
	private int moduleId;
	
	
	List<Sgmodul> sgmodulList;
	
	private Sgmodul sgmodulSelected;
	
	// Getter und Setter
	public String getSgmodulNote() {
		return sgmodulNote;
	}

	public void setSgmodulNote(String sgmodulNote) {
		this.sgmodulNote = sgmodulNote;
	}

	public ArrayList<Dozenten> getProfessorList() {
		return professorList;
	}

	public void setProfessorList(ArrayList<Dozenten> professorList) {
		this.professorList = professorList;
	}
	
	public ArrayList<Studiengang> getCourseList() {
		return courseList;
	}

	public void setCourseList(ArrayList<Studiengang> courseList) {
		this.courseList = courseList;
	}
	public ArrayList<Modul> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<Modul> moduleList) {
		this.moduleList = moduleList;
	}

	public Studiengang getCourse() {
		return course;
	}

	public void setCourse(Studiengang course) {
		this.course = course;
	}
	
	public Modul getModule() {
		return module;
	}

	public void setModule(Modul module) {
		this.module = module;
	}
	
	public Dozenten getProfessor() {
		return professor;
	}

	public void setProfessor(Dozenten professor) {
		this.professor = professor;
	}
	
	public Sgmodul getSgmodul() {
		return sgmodul;
	}

	public void setSgmodul(Sgmodul sgmodul) {
		this.sgmodul = sgmodul;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
			this.courseId = courseId;
	}			

	public int getProfessorId() {
		return professorId;
	}

	public void setProfessorId(int professorId) {
			this.professorId = professorId;
	}
	
	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
			this.moduleId = moduleId;
	}

	public int getModuleSemester() {
		return moduleSemester;
	}

	public void setModuleSemester(int moduleSemester) {
		this.moduleSemester = moduleSemester;
	}

	public List<Sgmodul> getSgmodulList() {
		return sgmodulList;
	}
	
	public void setSgmodulList(List<Sgmodul> sgmodulList) {
		this.sgmodulList = sgmodulList;
		
	}

	
	public Sgmodul getSgmodulSelected() {
		return sgmodulSelected;
	}

	public void setSgmodulSelected(Sgmodul sgmodulSelected) {
		this.sgmodulSelected = sgmodulSelected;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;
	
	/**
	 * Erstellen eines Studiengangmoduls
	 * @throws Exception
	 */
	public void createSgmodul() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Sgmodul sgm = new Sgmodul();  
		sgm.setSGMNotiz(sgmodulNote);
		sgm.setModSemester(moduleSemester);
		sgm.setModul(findMod(moduleId));
		sgm.setDozenten(findDoz(professorId));
		sgm.setStudiengang(findSg(courseId));
		try {
			sgModulFacadeLocal.create(sgm);
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
	 * Erstellt ein Studiengangsmodul und aktualisiert dann die Liste
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoSgmodul() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
			createSgmodul();
			sgmodulList = getSgmodulListAll();
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Laden der Studiengangmodulliste
	 * @return
	 */
	public List<Sgmodul> getSgmodulListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Sgmodul> query = em.createNamedQuery("Sgmodul.findAll", Sgmodul.class);
		return query.getResultList();
	}
	
	 
	/**
	 * Ausgewählte Zeile in sgmodulSelected speichern mit den ganzen Fremdschlüsseln
	 * @param e
	 */
	public void onRowSelect(SelectEvent<Sgmodul> e) {
    	FacesMessage msg = new FacesMessage("Studiengangmodul ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        sgmodulSelected = e.getObject();
        
        professorId = sgmodulSelected.getDozenten().getDid();
        moduleId = sgmodulSelected.getModul().getModID();
        courseId = sgmodulSelected.getStudiengang().getSgid();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löschen eines Studiengangmoduls
     * @throws Exception
     */
    public void deleteSgmodul() throws Exception {
    	String msg;
    	sgmodulList.remove(sgmodulSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Sgmodul> q = em.createNamedQuery("Sgmodul.findBySgmid",Sgmodul.class);
        q.setParameter("sgmid", sgmodulSelected.getSgmid());
        sgmodul = (Sgmodul)q.getSingleResult();
        
        try {
        	sgModulFacadeLocal.remove(sgmodul);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
	    }
	    catch (Exception e) {
	    	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
	    	
	    }
		em.close();
    }
    
   //----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Finden eines Moduls anhand der ID
     * @param mod
     * @return
     */
    private Modul findMod(int mod) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Modul> query
                = em.createNamedQuery("Modul.findByModID",Modul.class);
            query.setParameter("modID", mod);
            module = (Modul)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return module;
    }
    
    /**
     * Finden eines Studiengangs anhand der ID
     * @param sg
     * @return
     */
    private Studiengang findSg(int sg) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Studiengang> query
                = em.createNamedQuery("Studiengang.findBySgid",Studiengang.class);
            query.setParameter("sgid", sg);
            course = (Studiengang)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return course;
    }
    
    /**
     * Finden eines Dozenten anhand der ID
     * @param doz
     * @return
     */
    private Dozenten findDoz(int doz) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Dozenten> query
                = em.createNamedQuery("Dozenten.findByDid",Dozenten.class);
            query.setParameter("did", doz);
            professor = (Dozenten)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return professor;
    }
    
   //----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Bearbeiten eines Studiengangmoduls
     * 
     */
    public void addSgmodul(){
    	EntityManager em = emf.createEntityManager();
    	String msg;
      	 try {
	        em.find(Sgmodul.class, sgmodulSelected.getSgmid());
	        sgmodul.setSgmid(sgmodulSelected.getSgmid());
	        sgmodul.setModSemester(sgmodulSelected.getModSemester());
	        sgmodul.setSGMNotiz(sgmodulSelected.getSGMNotiz());
	        sgmodul.setModul(findMod(moduleId));
	        sgmodul.setDozenten(findDoz(professorId));
	        sgmodul.setStudiengang(findSg(courseId));
	        sgModulFacadeLocal.edit(sgmodul);
	        msg = "Eintrag wurde bearbeitet.";
            addMessage("messages", msg);
   	    }
   	    catch (Exception e) {
   	    	msg = "Eintrag wurde nicht bearbeitet.";
            addMessage("messages", msg);
   	    }
      	sgmodulList = getSgmodulListAll();
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
    
}
