package controller;

import model.Faculty;
import model.Raum;
import model.Stundenplansemester;
import model.Studiengang;
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

import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

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
	private Stundenplansemester spSemester;
	
	@PostConstruct
    public void init() {
		courseList = getStudiengangList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Faculty.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
            Faculty fac =(Faculty)FListitem;
            facultyList.add(fac.getFacName());
        }
        Query s = em.createNamedQuery("Stundenplansemester.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Stundenplansemester sm =(Stundenplansemester)SListitem;
        	spsList.add(sm.getSpsid());
        }
    }
 
    ArrayList<String> facultyList = new ArrayList<>();
    ArrayList<Integer> spsList = new ArrayList<>();
    
    private int spsId;
    private String facultyName;
    private int semester;
	private String courseShort;
	private String courseName;
	private boolean courseNameOk = false;
	private boolean courseShortOk = false;
	
	List<Studiengang> courseList;
	
	private Studiengang courseSelected;
	
	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public int getSpsId() {
		return spsId;
	}

	public void setSpsId(int spsId) {
		this.spsId = spsId;
	}

	public ArrayList<String> getFacultyList() {
		return facultyList;
	}

	public void setFacultyList(ArrayList<String> facultyList) {
		this.facultyList = facultyList;
	}
	
	public ArrayList<Integer> getSpsList() {
		return spsList;
	}

	public void setSpsList(ArrayList<Integer> spsList) {
		this.spsList = spsList;
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
	
	public Stundenplansemester getSpSemester() {
		return spSemester;
	}

	public void setSpSemester(Stundenplansemester spSemester) {
		this.spSemester = spSemester;
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
	    	FacesMessage message = new FacesMessage("SGName konnte nicht gesetzt werden.");
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
	    	FacesMessage message = new FacesMessage("SGKurz konnte nicht gesetzt werden.");
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
	public void createStudiengang() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Studiengang bg = new Studiengang();  
		bg.setSGName(courseName);
		bg.setSGKurz(courseShort);
		bg.setSemester(semester);
		bg.setFaculty(findFac(facultyName));
		bg.setStundenplansemester(findSP(spsId));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(bg);  
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
	
	public void createDoStudiengang() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(courseNameOk == true && courseShortOk == true) {
			createStudiengang();
			courseList = getStudiengangList();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Studiengang> getStudiengangList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Studiengang> query = em.createNamedQuery("Studiengang.findAll", Studiengang.class);
		courseList = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowSelect(SelectEvent<Studiengang> e) {
    	FacesMessage msg = new FacesMessage("Studiengang ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        courseSelected = e.getObject();
        
        facultyName = courseSelected.getFaculty().getFacName();
        spsId = courseSelected.getStundenplansemester().getSpsid();
         
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStudiengang() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
    	courseList.remove(courseSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Studiengang> q = em.createNamedQuery("Studiengang.findBySgid",Studiengang.class);
        q.setParameter("sgid", courseSelected.getSgid());
        course = (Studiengang)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(course);
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
    
    private Stundenplansemester findSP(int sm) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Stundenplansemester> query
                = em.createNamedQuery("Stundenplansemester.findBySpsid",Stundenplansemester.class);
            query.setParameter("spsid", sm);
            spSemester = (Stundenplansemester)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return spSemester;
    }
    
    public void addRoom(){
      	 try {
      		ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Studiengang.class, courseSelected.getSgid());
	        course.setSgid(courseSelected.getSgid());
	        course.setSGName(courseSelected.getSGName());
	        course.setSGKurz(courseSelected.getSGKurz());
	        course.setSemester(courseSelected.getSemester());
	        course.setFaculty(findFac(facultyName));
	        course.setStundenplansemester(findSP(spsId));
	        em.merge(course);
	        ut.commit(); 
   	    }
   	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
   	        try {
   	            ut.rollback();
   	        } 
   	        catch (IllegalStateException | SecurityException | SystemException ex) {
   	        }
   	    }
      	courseList = getStudiengangList();
      }
}
