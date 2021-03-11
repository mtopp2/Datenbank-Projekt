package controller;

import model.Faculty;
import model.Studiengang;
import model.Lehrveranstaltungsart;
import model.Raum;
import model.Sgmodul;
import model.Stundenplaneintrag;
import model.Stundenplansemester;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;

import EJB.FacultyFacadeLocal;
import EJB.StundenplaneintragFacadeLocal;

/**
*
* @author Manuel
*/
@Named(value="scheduleControllerCopie")
@SessionScoped
public class ScheduleControllerCopie implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject
	private Stundenplansemester spSemester;
	private Stundenplansemester spSemester2;
	
	@EJB
	private StundenplaneintragFacadeLocal speFacadeLocal;
	
	@PostConstruct
	public void init() {
        selection();

    }
	
	// Old
	ArrayList<String> courseList = new ArrayList<>();
    private String courseSelection;
    
    ArrayList<String> facultyList = new ArrayList<>();
    private String facultySelection;
    
    ArrayList<String> sps1List = new ArrayList<>();
    private String spSemesterSelection;
    ArrayList<Integer> sps2List = new ArrayList<>();
    private int spYearSelection;
    //----------------------------------------------------------
    private List<Stundenplaneintrag> speList;
    private Stundenplaneintrag spe;
    private Stundenplaneintrag speNew;
    private int spsId;
    
    //New
    ArrayList<String> courseList2 = new ArrayList<>();
    private String courseSelection2;
    
    ArrayList<String> facultyList2 = new ArrayList<>();
    private String facultySelection2;
    
    ArrayList<String> sps1List2 = new ArrayList<>();
    private String spSemesterSelection2;
    ArrayList<Integer> sps2List2 = new ArrayList<>();
    private int spYearSelection2;
    
    //----------------------------------------------------------
    private List<Stundenplaneintrag> speList2;
    private Stundenplaneintrag spe2;
    private int spsId2;
    
    public void copie() {
    	String msg;
    	spSemester = findSPSelection(spSemesterSelection, spYearSelection);
    	spsId = spSemester.getSpsid();
    	EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Stundenplaneintrag.findAllPlanD", Stundenplaneintrag.class);
        query.setParameter("stgang", courseSelection);
        query.setParameter("facName", facultySelection);
        query.setParameter("spsid", spsId);
        speList = query.getResultList();
        
        spSemester2 = findSPSelection(spSemesterSelection2, spYearSelection2);
    	spsId2 = spSemester2.getSpsid();
        EntityManager em1 = emf.createEntityManager();
        Query query1 = em1.createNamedQuery("Stundenplaneintrag.findAllPlanD", Stundenplaneintrag.class);
        query1.setParameter("stgang", courseSelection2);
        query1.setParameter("facName", facultySelection2);
        query1.setParameter("spsid", spsId2);
        speList2 = query1.getResultList();
        delete();
        create();
        msg = "Einträge wurden erstellt.";
        addMessage("messages", msg);
    }
    
	public void delete(){
	
		for(int i = 0; i < speList2.size(); i++){
			spe2 = speList2.get(i);
			EntityManager em = emf.createEntityManager();
	        em.find(Stundenplaneintrag.class, spe2.getSpid());
	        speFacadeLocal.remove(spe2);
		}
	}
	
	public void create() {
		for(int i = 0; i < speList.size(); i++){
			this.spe = new Stundenplaneintrag();
			spe = speList.get(i);
			EntityManager em = emf.createEntityManager();
	        em.find(Stundenplaneintrag.class, spe.getSpid());
	        spSemester = findSPSelection(spSemesterSelection2, spYearSelection2);
	        spe.setStundenplansemester(spSemester);
	        
	        this.speNew = new Stundenplaneintrag();
	        speNew.setSPEStartZeit(spe.getSPEStartZeit());
	        speNew.setSPEEndZeit(spe.getSPEEndZeit());
	        speNew.setWochentag(spe.getWochentag());
	        speNew.setSPTermin(spe.getSPTermin());
	        speNew.setSgmodul(spe.getSgmodul());
	        speNew.setLehrveranstaltungsart(spe.getLehrveranstaltungsart());
	        speNew.setRaum(spe.getRaum());
	        speNew.setStundenplansemester(spe.getStundenplansemester());
	        speNew.setStudierendenzahl(spe.getStudierendenzahl());
	        speNew.setZeitStempel(spe.getZeitStempel());
	        speFacadeLocal.create(speNew);
		}
		
	}
	
	//----------------------------------------------------------
	
	public void selection() {
		
		EntityManager em1 = emf.createEntityManager();
        Query s11 = em1.createNamedQuery("Studiengang.findAll");
        List S11List = s11.getResultList();
        for (Object S11Listitem : S11List)
        {
        	Studiengang sg11 =(Studiengang)S11Listitem;
            courseList2.add(sg11.getSGName());
        }
        
        EntityManager em2 = emf.createEntityManager();
        Query f1 = em2.createNamedQuery("Faculty.findAll");
        List F1List = f1.getResultList();
        for (Object F1Listitem : F1List)
        {
        	Faculty f11 =(Faculty)F1Listitem;
            facultyList2.add(f11.getFacName());
        }
		
		
        EntityManager em3 = emf.createEntityManager();
        Query sps11 = em3.createNamedQuery("Stundenplansemester.findAllGroupBySpsemester");
        List sps111List = sps11.getResultList();
        for (Object sps11Listitem : sps111List)
        {
        	Stundenplansemester sps111 =(Stundenplansemester)sps11Listitem;
            sps1List2.add(sps111.getSPSemester());
        }
        
        EntityManager em4 = emf.createEntityManager();
        Query sps22 = em4.createNamedQuery("Stundenplansemester.findAllGroupBySpJahr");
        List sps222List = sps22.getResultList();
        for (Object sps22Listitem : sps222List)
        {
        	Stundenplansemester sps222 =(Stundenplansemester)sps22Listitem;
            sps2List2.add(sps222.getSPJahr());
        }
		

		EntityManager em5 = emf.createEntityManager();
        Query s1 = em5.createNamedQuery("Studiengang.findAll");
        List S1List = s1.getResultList();
        for (Object S1Listitem : S1List)
        {
        	Studiengang sg1 =(Studiengang)S1Listitem;
            courseList.add(sg1.getSGName());
        }
        
        EntityManager em6 = emf.createEntityManager();
        Query f = em6.createNamedQuery("Faculty.findAll");
        List FList = f.getResultList();
        for (Object FListitem : FList)
        {
        	Faculty f11 =(Faculty)FListitem;
            facultyList.add(f11.getFacName());
        }
        
        EntityManager em7 = emf.createEntityManager();
        Query sps1 = em7.createNamedQuery("Stundenplansemester.findAllGroupBySpsemester");
        List sps11List = sps1.getResultList();
        for (Object sps1Listitem : sps11List)
        {
        	Stundenplansemester sps1111 =(Stundenplansemester)sps1Listitem;
            sps1List.add(sps1111.getSPSemester());
        }
        
        EntityManager em8 = emf.createEntityManager();
        Query sps = em8.createNamedQuery("Stundenplansemester.findAllGroupBySpJahr");
        List sps22List = sps.getResultList();
        for (Object sps2Listitem : sps22List)
        {
        	Stundenplansemester sps2222 =(Stundenplansemester)sps2Listitem;
            sps2List.add(sps2222.getSPJahr());
        }
	}
	
	//----------------------------------------------------------
	
	private Stundenplansemester findSPSelection(String sps,int sm) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Stundenplansemester> query
                = em.createNamedQuery("Stundenplansemester.findBySPSemesterAndYear",Stundenplansemester.class);
            query.setParameter("SPSemester", sps);
            query.setParameter("SPJahr", sm);
            spSemester = (Stundenplansemester)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return spSemester;
    }

	public StundenplaneintragFacadeLocal getSpeFacadeLocal() {
		return speFacadeLocal;
	}

	public void setSpeFacadeLocal(StundenplaneintragFacadeLocal speFacadeLocal) {
		this.speFacadeLocal = speFacadeLocal;
	}

	public ArrayList<String> getCourseList() {
		return courseList;
	}

	public void setCourseList(ArrayList<String> courseList) {
		this.courseList = courseList;
	}

	public String getCourseSelection() {
		return courseSelection;
	}

	public void setCourseSelection(String courseSelection) {
		this.courseSelection = courseSelection;
	}

	public ArrayList<String> getFacultyList() {
		return facultyList;
	}

	public void setFacultyList(ArrayList<String> facultyList) {
		this.facultyList = facultyList;
	}

	public String getFacultySelection() {
		return facultySelection;
	}

	public void setFacultySelection(String facultySelection) {
		this.facultySelection = facultySelection;
	}

	public ArrayList<String> getSps1List() {
		return sps1List;
	}

	public void setSps1List(ArrayList<String> sps1List) {
		this.sps1List = sps1List;
	}

	public String getSpSemesterSelection() {
		return spSemesterSelection;
	}

	public void setSpSemesterSelection(String spSemesterSelection) {
		this.spSemesterSelection = spSemesterSelection;
	}

	public ArrayList<Integer> getSps2List() {
		return sps2List;
	}

	public void setSps2List(ArrayList<Integer> sps2List) {
		this.sps2List = sps2List;
	}

	public int getSpYearSelection() {
		return spYearSelection;
	}

	public void setSpYearSelection(int spYearSelection) {
		this.spYearSelection = spYearSelection;
	}

	public List<Stundenplaneintrag> getSpeList() {
		return speList;
	}

	public void setSpeList(List<Stundenplaneintrag> speList) {
		this.speList = speList;
	}

	public int getSpsId() {
		return spsId;
	}

	public void setSpsId(int spsId) {
		this.spsId = spsId;
	}

	public ArrayList<String> getCourseList2() {
		return courseList2;
	}

	public void setCourseList2(ArrayList<String> courseList2) {
		this.courseList2 = courseList2;
	}

	public String getCourseSelection2() {
		return courseSelection2;
	}

	public void setCourseSelection2(String courseSelection2) {
		this.courseSelection2 = courseSelection2;
	}

	public ArrayList<String> getFacultyList2() {
		return facultyList2;
	}

	public void setFacultyList2(ArrayList<String> facultyList2) {
		this.facultyList2 = facultyList2;
	}

	public String getFacultySelection2() {
		return facultySelection2;
	}

	public void setFacultySelection2(String facultySelection2) {
		this.facultySelection2 = facultySelection2;
	}

	public ArrayList<String> getSps1List2() {
		return sps1List2;
	}

	public void setSps1List2(ArrayList<String> sps1List2) {
		this.sps1List2 = sps1List2;
	}

	public String getSpSemesterSelection2() {
		return spSemesterSelection2;
	}

	public void setSpSemesterSelection2(String spSemesterSelection2) {
		this.spSemesterSelection2 = spSemesterSelection2;
	}

	public ArrayList<Integer> getSps2List2() {
		return sps2List2;
	}

	public void setSps2List2(ArrayList<Integer> sps2List2) {
		this.sps2List2 = sps2List2;
	}

	public int getSpYearSelection2() {
		return spYearSelection2;
	}

	public void setSpYearSelection2(int spYearSelection2) {
		this.spYearSelection2 = spYearSelection2;
	}

	public List<Stundenplaneintrag> getSpeList2() {
		return speList2;
	}

	public void setSpeList2(List<Stundenplaneintrag> speList2) {
		this.speList2 = speList2;
	}

	public Stundenplaneintrag getSpe2() {
		return spe2;
	}

	public void setSpe2(Stundenplaneintrag spe2) {
		this.spe2 = spe2;
	}

	public int getSpsId2() {
		return spsId2;
	}

	public void setSpsId2(int spsId2) {
		this.spsId2 = spsId2;
	}

	public Stundenplaneintrag getSpe() {
		return spe;
	}

	public void setSpe(Stundenplaneintrag spe) {
		this.spe = spe;
	}

	public Stundenplansemester getSpSemester2() {
		return spSemester2;
	}

	public void setSpSemester2(Stundenplansemester spSemester2) {
		this.spSemester2 = spSemester2;
	}

	public Stundenplansemester getSpSemester() {
		return spSemester;
	}

	public void setSpSemester(Stundenplansemester spSemester) {
		this.spSemester = spSemester;
	}

	public Stundenplaneintrag getSpeNew() {
		return speNew;
	}

	public void setSpeNew(Stundenplaneintrag speNew) {
		this.speNew = speNew;
	}
	
	public void addMessage(String toComponent, String message){
        FacesMessage msg = new FacesMessage(message);
        FacesContext cxt = FacesContext.getCurrentInstance();
        cxt.addMessage(toComponent, msg);
    }
	

}
