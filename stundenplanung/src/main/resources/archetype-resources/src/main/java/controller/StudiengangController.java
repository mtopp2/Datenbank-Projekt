package controller;

import model.Faculty;
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
import javax.faces.bean.ManagedBean;
import controller.MessageForPrimefaces;

/**
*
* @author Anil
*/

//@ManagedBean(name="StudiengangController")
@Named(value="studiengangController")
@SessionScoped
public class StudiengangController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Studiengang studiengang;
	private Faculty faculty;
	private Stundenplansemester stundenplansemester;
	
	@PostConstruct
    public void init() {
        sglist = getStudiengangList();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Faculty.findAll");
		List FList = q.getResultList();
        for (Object FListitem : FList)
        {
            Faculty fac =(Faculty)FListitem;
            FacultyListe.add(fac.getFacName());
        }
        Query s = em.createNamedQuery("Stundenplansemester.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Stundenplansemester sm =(Stundenplansemester)SListitem;
        	StundenplansemesterListe.add(sm.getSPSemester());
        }
    }
 
    ArrayList<String> FacultyListe = new ArrayList<>();
    ArrayList<String> StundenplansemesterListe = new ArrayList<>();
    
    private String SPSemester;
    private String facName;
    private int semester;
	private String SGKurz;
	private String SGName;
	private boolean SGName_ok = false;
	private boolean SGKurz_ok = false;
	
	List<Studiengang> sglist;
	
	private Studiengang selectedstudiengang;
	
	public String getFacName() {
		return facName;
	}

	public void setFacName(String facName) {
		this.facName = facName;
	}
	public String getSPSemester() {
		return SPSemester;
	}

	public void setSPSemester(String SPSemester) {
		this.SPSemester = SPSemester;
	}

	public ArrayList<String> getFacultyListe() {
		return FacultyListe;
	}

	public void setFacultyListe(ArrayList<String> facultyListe) {
		this.FacultyListe = facultyListe;
	}
	
	public ArrayList<String> getStundenplansemesterListe() {
		return StundenplansemesterListe;
	}

	public void setStundenplansemesterListe(ArrayList<String> stundenplansemesterListe) {
		this.StundenplansemesterListe = stundenplansemesterListe;
	}

	public Studiengang getStudiengang() {
		return studiengang;
	}

	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}
	
	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	
	public Stundenplansemester getStundenplansemester() {
		return stundenplansemester;
	}

	public void setStundenplansemester(Stundenplansemester stundenplansemester) {
		this.stundenplansemester = stundenplansemester;
	}

	public String getSGName() {
		return SGName;
	}

	public void setSGName(String SGName) {
		if(SGName!=null){
			this.SGName = SGName;
			SGName_ok=true;
	    }
	    else{
	    	FacesMessage message = new FacesMessage("SGName konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StudiengangForm:SGName_reg", message);
	    }
	}

	public String getSGKurz() {
		return SGKurz;
	}

	public void setSGKurz(String SGKurz) {
		if(SGKurz!=null){
			this.SGKurz = SGKurz;
			SGKurz_ok=true;
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

	public List<Studiengang> getSglist() {
		return sglist;
	}
	
	public void setSglist(List<Studiengang> sglist) {
		this.sglist = sglist;
		
	}

	
	public Studiengang getSelectedstudiengang() {
		return selectedstudiengang;
	}

	public void setSelectedstudiengang(Studiengang selectedstudiengang) {
		this.selectedstudiengang = selectedstudiengang;
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
		bg.setSGName(SGName);
		bg.setSGKurz(SGKurz);
		bg.setSemester(semester);
		bg.setFaculty(findFac(facName));
		bg.setStundenplansemester(findSP(SPSemester));
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
	
	public String createDoStudiengang() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(SGName_ok == true && SGKurz_ok == true) {
			createStudiengang();
			return "showstudiengang.xhtml";
		}
		else{
			return "createstudiengang.xhtml";
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Studiengang> getStudiengangList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Studiengang> query = em.createNamedQuery("Studiengang.findAll", Studiengang.class);
		sglist = query.getResultList();
		return query.getResultList();
	}
	
	
	
	public void onRowEdit(RowEditEvent<Studiengang> event) {
        FacesMessage msg = new FacesMessage("Studiengang Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        Studiengang newsg = new Studiengang();
        newsg = event.getObject();
        
        try {
	        ut.begin();
	        EntityManager em = emf.createEntityManager();
	        em.find(Studiengang.class, newsg.getSgid());
	        studiengang.setSgid(newsg.getSgid());
	        studiengang.setSGName(newsg.getSGName());
	        studiengang.setSGKurz(newsg.getSGKurz());
	        studiengang.setSemester(newsg.getSemester());
	        studiengang.setFaculty(findFac(newsg.getFaculty().getFacName()));
	        studiengang.setStundenplansemester(findSP(newsg.getStundenplansemester().getSPSemester()));
	        em.merge(studiengang);
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
     
    public void onRowCancel(RowEditEvent<Studiengang> event) {
    	FacesMessage msg = new FacesMessage("Studiengang Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStudiengang() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception {
        sglist.remove(selectedstudiengang);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Studiengang> q = em.createNamedQuery("Studiengang.findBySgid",Studiengang.class);
        q.setParameter("sgid", selectedstudiengang.getSgid());
        studiengang = (Studiengang)q.getSingleResult();
        
        try {
	        ut.begin();   
	        em.joinTransaction();  
	        em.remove(studiengang);
	        ut.commit(); 
	    }
	    catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
        selectedstudiengang = null;
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
    
    private Stundenplansemester findSP(String sm) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Stundenplansemester> query
                = em.createNamedQuery("Stundenplansemester.findBySPSemester",Stundenplansemester.class);
            query.setParameter("SPSemester", sm);
            stundenplansemester = (Stundenplansemester)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return stundenplansemester;
    }
}
