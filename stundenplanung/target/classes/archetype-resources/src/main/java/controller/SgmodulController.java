package controller;

import model.Modul;
import model.Raum;
import model.Dozenten;
import model.Studiengang;
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

import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

/**
*
* @author Anil
*/

//@ManagedBean(name="SgmodulController")
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
	
	@PostConstruct
    public void init() {
		sgmodulList = getSgmodulListAll();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Modul.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
        	Modul mod =(Modul)FListitem;
        	moduleList.add(mod.getModID());
        }
        Query s = em.createNamedQuery("Studiengang.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Studiengang sg =(Studiengang)SListitem;
        	courseList.add(sg.getSGName());
        }
        Query d = em.createNamedQuery("Dozenten.findAll");
        List DList = d.getResultList();
        for (Object DListitem : DList)
        {
        	Dozenten doz =(Dozenten)DListitem;
        	professorList.add(doz.getDid());
        }
    }
 
    ArrayList<Integer> professorList = new ArrayList<>();
    ArrayList<Integer> moduleList = new ArrayList<>();
    ArrayList<String> courseList = new ArrayList<>();
    
    private int moduleSemester;
	private String sgmodulNote;
    private String courseName;
	private int professorId;
	private int moduleId;
	
	
	List<Sgmodul> sgmodulList;
	
	private Sgmodul sgmodulSelected;
	
	
	public String getSgmodulNote() {
		return sgmodulNote;
	}

	public void setSgmodulNote(String sgmodulNote) {
		this.sgmodulNote = sgmodulNote;
	}

	public ArrayList<Integer> getProfessorList() {
		return professorList;
	}

	public void setProfessorList(ArrayList<Integer> professorList) {
		this.professorList = professorList;
	}
	
	public ArrayList<String> getCourseList() {
		return courseList;
	}

	public void setCourseList(ArrayList<String> courseList) {
		this.courseList = courseList;
	}
	public ArrayList<Integer> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<Integer> moduleList) {
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

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
			this.courseName = courseName;
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
	public void createSgmodul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
		EntityManager em = emf.createEntityManager();
		Sgmodul sgm = new Sgmodul();  
		sgm.setSGMNotiz(sgmodulNote);
		sgm.setModSemester(moduleSemester);
		sgm.setModul(findMod(moduleId));
		sgm.setDozenten(findDoz(professorId));
		sgm.setStudiengang(findSg(courseName));
		try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.persist(sgm);  
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
	
	public void createDoSgmodul() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
			createSgmodul();
			sgmodulList = getSgmodulListAll();
	
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Sgmodul> getSgmodulListAll(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Sgmodul> query = em.createNamedQuery("Sgmodul.findAll", Sgmodul.class);
		sgmodulList = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowSelect(SelectEvent<Sgmodul> e) {
    	FacesMessage msg = new FacesMessage("Studiengangmodul ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        sgmodulSelected = e.getObject();
        
        professorId = sgmodulSelected.getDozenten().getDid();
        moduleId = sgmodulSelected.getModul().getModID();
        courseName = sgmodulSelected.getStudiengang().getSGName();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteSgmodul() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
    	sgmodulList.remove(sgmodulSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Sgmodul> q = em.createNamedQuery("Sgmodul.findBySgmid",Sgmodul.class);
        q.setParameter("sgmid", sgmodulSelected.getSgmid());
        sgmodul = (Sgmodul)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(sgmodul);
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
    
   //----------------------------------------------------------------------------------------------------------------------------------------------
    
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
    
    private Studiengang findSg(String sg) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Studiengang> query
                = em.createNamedQuery("Studiengang.findBySGName",Studiengang.class);
            query.setParameter("SGName", sg);
            course = (Studiengang)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return course;
    }
    
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
    
    public void addSgmodul(){
      	 try {
      		ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Sgmodul.class, sgmodulSelected.getSgmid());
	        sgmodul.setSgmid(sgmodulSelected.getSgmid());
	        sgmodul.setModSemester(sgmodulSelected.getModSemester());
	        sgmodul.setSGMNotiz(sgmodulSelected.getSGMNotiz());
	        sgmodul.setModul(findMod(moduleId));
	        sgmodul.setDozenten(findDoz(professorId));
	        sgmodul.setStudiengang(findSg(courseName));
	        em.merge(sgmodul);
	        ut.commit();
   	    }
   	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
   	        try {
   	            ut.rollback();
   	        } 
   	        catch (IllegalStateException | SecurityException | SystemException ex) {
   	        }
   	    }
      	sgmodulList = getSgmodulListAll();
      }
    
}
