package controller;

import model.Faculty;

import model.Studiengang;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

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

import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.primefaces.event.SelectEvent;


import javax.ejb.EJB;
import EJB.StudiengangFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="studiengangController")
@SessionScoped
public class StudiengangController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Studiengang course;
	private Faculty faculty;
	
	
	@EJB
	private StudiengangFacadeLocal studiengangFacadeLocal;
	
	/**
	 * Initialisierung
	 */
	@PostConstruct
    public void init() {
		courseList = getStudiengangList();
        facultyList = getFacultyList();
    }
 
    List<Faculty> facultyList ;
    
    
    
    private int facultyID;
    private int semester;
	private String courseShort;
	private String courseName;
	private boolean courseNameOk = false;
	private boolean courseShortOk = false;
	
	List<Studiengang> courseList;
	
	private Studiengang courseSelected;
	
	// Getter und Setter
	public int getFacultyID() {
		return facultyID;
	}

	public void setFacultyID(int facultyID) {
		this.facultyID = facultyID;
	}
	

	public Studiengang getCourse() {
		return course;
	}

	public void setCourse(Studiengang course) {
		this.course = course;
	}
	
	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		if(courseName!=null){
			this.courseName = courseName;
			courseNameOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Studiengangsname konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StudiengangForm:SGName_reg", message);
	    }
	}

	public String getCourseShort() {
		return courseShort;
	}

	public void setCourseShort(String courseShort) {
		if(courseShort!=null){
			this.courseShort = courseShort;
			courseShortOk=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("Studiengangskürzel konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StudiengangForm:SGKurz_reg", message);
	    }
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public List<Studiengang> getCourseList() {
		return courseList;
	}
	
	public void setCourseList(List<Studiengang> courseList) {
		this.courseList = courseList;
		
	}

	
	public Studiengang getCourseSelected() {
		return courseSelected;
	}

	public void setCourseSelected(Studiengang courseSelected) {
		this.courseSelected = courseSelected;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;
	
	/**
	 * Erstellt einen Studiengangseintrag
	 * @throws Exception
	 */
	public void createStudiengang() throws Exception  {
		String msg;
		EntityManager em = emf.createEntityManager();
		Studiengang bg = new Studiengang();  
		bg.setSGName(courseName);
		bg.setSGKurz(courseShort);
		bg.setSemester(semester);
		bg.setFaculty(findFac(facultyID));
		try {
			studiengangFacadeLocal.create(bg);
			msg = "Eintrag wurde erstellt.";
            addMessage("messages", msg);
		}catch(Exception e) {
			msg = "Eintrag wurde nicht erstellt.";
            addMessage("messages", msg);
		}
		
		em.close();
	}
	
	/**
	 * Schaut, ob Studiengangsname und Studiengangskurzform gesetzt ist, danach wird der eintrag in der Datenbank erstellt und zum Schluß wird die Liste aktualisiert.
	 * @throws SecurityException
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws Exception
	 */
	public void createDoStudiengang() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(courseNameOk == true && courseShortOk == true) {
			createStudiengang();
			courseList = getStudiengangList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	

	/**
	 * Studiengangsliste wird geladen
	 * @return
	 */
	public List<Studiengang> getStudiengangList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Studiengang> query = em.createNamedQuery("Studiengang.findAll", Studiengang.class);
		return query.getResultList();
	}
	
	/**
	 * Fachbereichsliste wird geladen
	 * @return
	 */
	public List<Faculty> getFacultyList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Faculty> query = em.createNamedQuery("Faculty.findAll", Faculty.class);
		return query.getResultList();
	}
	
	
	/**
	 * Die ausgewählte Zeile wird in courseSelected gespeichert
	 * @param e
	 */
	public void onRowSelect(SelectEvent<Studiengang> e) {
    	FacesMessage msg = new FacesMessage("Studiengang ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        courseSelected = e.getObject();
        
        facultyID = courseSelected.getFaculty().getFbid();
        
         
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Löscht einen Studiengang
     * @throws Exception
     */
    public void deleteStudiengang() throws Exception {
    	String msg;
    	courseList.remove(courseSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Studiengang> q = em.createNamedQuery("Studiengang.findBySgid",Studiengang.class);
        q.setParameter("sgid", courseSelected.getSgid());
        course = (Studiengang)q.getSingleResult();
        try {
        	studiengangFacadeLocal.remove(course);
        	msg = "Eintrag wurde gelöscht.";
            addMessage("messages", msg);
        }catch(Exception e) {
        	msg = "Eintrag wurde nicht gelöscht.";
            addMessage("messages", msg);
        }
        
	    em.close();
    }
    
    /**
     * Findet den bestimmten Fachbereichseintrag anhand der Fachbereichsid
     * @param facultyID
     * @return
     */
    private Faculty findFac(int facultyID) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Faculty> query
                = em.createNamedQuery("Faculty.findByFbid",Faculty.class);
            query.setParameter("fbid", facultyID);
            faculty = (Faculty)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return faculty;
    }
   
    /**
     * Bearbeitet einen Studiengang
     */
    public void addStudiengang(){
    		String msg;
	        EntityManager em = emf.createEntityManager();
	        em.find(Studiengang.class, courseSelected.getSgid());
	        course.setSgid(courseSelected.getSgid());
	        course.setSGName(courseSelected.getSGName());
	        course.setSGKurz(courseSelected.getSGKurz());
	        course.setSemester(courseSelected.getSemester());
	        course.setFaculty(findFac(facultyID));
	        try {
	        	studiengangFacadeLocal.edit(course);
	        	msg = "Eintrag wurde bearbeitet.";
	            addMessage("messages", msg);
	        }catch(Exception e) {
	        	msg = "Eintrag wurde nicht bearbeitet.";
	            addMessage("messages", msg);
	        }
	        
	        courseList = getStudiengangList();
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
