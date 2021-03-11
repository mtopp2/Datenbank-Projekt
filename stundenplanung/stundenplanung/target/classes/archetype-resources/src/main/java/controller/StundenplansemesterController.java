package controller;

import model.Raum;
import model.Stundenplansemester;
import model.Stundenplanstatus;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;

import EJB.FacultyFacadeLocal;
import EJB.StundenplansemesterFacadeLocal;

/**
*
* @author Anil
*/

@Named(value="stundenplansemesterController")
@SessionScoped
public class StundenplansemesterController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Stundenplansemester scheduleSemester;
	private Stundenplanstatus scheduleStatus;
	
	@EJB
	private StundenplansemesterFacadeLocal stundenplansemesterFacadeLocal;

	
	@PostConstruct
    public void init() {
		scheduleSemesterList = getStundenplansemesterList();
		scheduleStatusList = getScheduleStatusList();
    }
 
    List<Stundenplanstatus> scheduleStatusList ;

    private int scheduleYear;
	private Integer scheduleCalendarWeek;
	private String scheduleSemesterSection;
	private Date startDate;
	private boolean scheduleCalendarWeekOk = false;
	private boolean scheduleSemesterSectionOk = false;
	private boolean startDateOk = false;
	
	List<Stundenplansemester> scheduleSemesterList;
	private int scheduleSemesterId;
	
	private Stundenplansemester scheduleSemesterSelected;
	
	

	

	public int getScheduleSemesterId() {
		return scheduleSemesterId;
	}

	public void setScheduleSemesterId(int scheduleSemesterId) {
		this.scheduleSemesterId = scheduleSemesterId;
	}

	public Stundenplansemester getScheduleSemester() {
		return scheduleSemester;
	}

	public void setScheduleSemester(Stundenplansemester scheduleSemester) {
		this.scheduleSemester = scheduleSemester;
	}

	public Stundenplanstatus getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(Stundenplanstatus scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public int getScheduleYear() {
		return scheduleYear;
	}

	public void setScheduleYear(int scheduleYear) {
		this.scheduleYear = scheduleYear;
	}

	public Integer getScheduleCalendarWeek() {
		return scheduleCalendarWeek;
	}

	public void setScheduleCalendarWeek(Integer scheduleCalendarWeek) {
		if(scheduleCalendarWeek!=null){
			this.scheduleCalendarWeek = scheduleCalendarWeek;
			scheduleCalendarWeekOk=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Stundenplankalenderwoche konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:SPKw_reg", message);
	    }
	}

	public String getScheduleSemesterSection() {
		return scheduleSemesterSection;
	}

	public void setScheduleSemesterSection(String scheduleSemesterSection) {
		if(scheduleSemesterSection!=null){
			this.scheduleSemesterSection = scheduleSemesterSection;
			scheduleSemesterSectionOk=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Stundenplansemester konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:SPSemester_reg", message);
	    }
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		if(startDate!=null){
			this.startDate = startDate;
			startDateOk=true;
	    }
		else{
	    	FacesMessage message = new FacesMessage("Startdatum konnte nicht gesetzt werden.");
            FacesContext.getCurrentInstance().addMessage("StundenplansemesterForm:startDatum_reg", message);
	    }
	}

	public List<Stundenplansemester> getScheduleSemesterList() {
		return scheduleSemesterList;
	}

	public void setScheduleSemesterList(List<Stundenplansemester> scheduleSemesterList) {
		this.scheduleSemesterList = scheduleSemesterList;
		
	}

	public Stundenplansemester getScheduleSemesterSelected() {
		return scheduleSemesterSelected;
	}

	public void setScheduleSemesterSelected(Stundenplansemester scheduleSemesterSelected) {
		this.scheduleSemesterSelected = scheduleSemesterSelected;
	}
	
	public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }
	  
	private UIComponent reg;  
	public void createStundenplansemester() throws Exception  {
		EntityManager em = emf.createEntityManager();
		Stundenplansemester sps = new Stundenplansemester();
		sps.setSPSemester(scheduleSemesterSection);
		sps.setSPJahr(scheduleYear);
		sps.setSPKw(scheduleCalendarWeek);
		sps.setStartDatum(startDate);
		sps.setStundenplanstatus(findSps(scheduleSemesterId));
		try {
			stundenplansemesterFacadeLocal.create(sps);
	    }
	    catch (Exception e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
	}
	
	public void createDoStundenplansemester() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
		if(scheduleSemesterSectionOk == true && scheduleCalendarWeekOk == true && startDateOk) {
			createStundenplansemester();
			scheduleSemesterList = getStundenplansemesterList();
		}	
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	public List<Stundenplansemester> getStundenplansemesterList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Stundenplansemester> query = em.createNamedQuery("Stundenplansemester.findAll", Stundenplansemester.class);
		scheduleSemesterList = query.getResultList();
		return query.getResultList();
	}
	
	public List<Stundenplanstatus> getScheduleStatusList(){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Stundenplanstatus> query = em.createNamedQuery("Stundenplanstatus.findAll", Stundenplanstatus.class);
		scheduleStatusList = query.getResultList();
		return query.getResultList();
	}
	
	
	public void onRowSelect(SelectEvent<Stundenplansemester> e) {
    	FacesMessage msg = new FacesMessage("Stundenplansemester ausgewählt");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        scheduleSemesterSelected = e.getObject();
        
        scheduleSemesterId = scheduleSemesterSelected.getStundenplanstatus().getSpstid();
        
    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
    
    public void deleteStundenplansemester() throws Exception {
    	scheduleSemesterList.remove(scheduleSemesterSelected);        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplansemester> q = em.createNamedQuery("Stundenplansemester.findBySpsid",Stundenplansemester.class);
        q.setParameter("spsid", scheduleSemesterSelected.getSpsid());
        scheduleSemester = (Stundenplansemester)q.getSingleResult();
        
        try {
        	stundenplansemesterFacadeLocal.remove(scheduleSemester);
	    }
	    catch (Exception e) {
	        try {
	            ut.rollback();
	        } 
	        catch (IllegalStateException | SecurityException | SystemException ex) {
	        }
	    }
		em.close();
    }
    
    private Stundenplanstatus findSps(int spsId) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Stundenplanstatus> query
                = em.createNamedQuery("Stundenplanstatus.findBySpsid",Stundenplanstatus.class);
            query.setParameter("spstid", spsId);
            scheduleStatus = (Stundenplanstatus)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return scheduleStatus;
    }
    
    public void addStundenPlanSemester(){
      	 try {
	        EntityManager em = emf.createEntityManager();
	        em.find(Stundenplansemester.class, scheduleSemesterSelected.getSpsid());
	        scheduleSemester.setSpsid(scheduleSemesterSelected.getSpsid());
	        scheduleSemester.setSPSemester(scheduleSemesterSelected.getSPSemester());
	        scheduleSemester.setSPJahr(scheduleSemesterSelected.getSPJahr());
	        scheduleSemester.setSPKw(scheduleSemesterSelected.getSPKw());
	        scheduleSemester.setStartDatum(scheduleSemesterSelected.getStartDatum());
	        scheduleSemester.setStundenplanstatus(findSps(scheduleSemesterId));
	        stundenplansemesterFacadeLocal.edit(scheduleSemester);
   	    }
   	    catch (Exception e) {
   	        try {
   	            ut.rollback();
   	        } 
   	        catch (IllegalStateException | SecurityException | SystemException ex) {
   	        }
   	    }
      	scheduleSemesterList = getStundenplansemesterList();
      }
  
}