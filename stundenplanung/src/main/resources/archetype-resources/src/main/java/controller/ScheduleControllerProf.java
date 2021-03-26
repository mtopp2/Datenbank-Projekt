package controller;


import model.Stundenplaneintrag;
import model.Stundenplansemester;
import model.Dozenten;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;


import java.time.LocalDateTime;

import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.transaction.UserTransaction;

import org.primefaces.model.DefaultScheduleEvent;

import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;




/**
*
* @author Manuel
*/
@Named(value="scheduleControllerProf")
@SessionScoped
public class ScheduleControllerProf implements Serializable{
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
	
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Dozenten professor;
	private Stundenplansemester spSemester;
	
	/**
	 * Initialisierung
	 */
	@PostConstruct
	public void init() {
        selection();
        lazyEventModel = new LazyScheduleModel(){
        	@Override
            public void loadEvents(LocalDateTime start, LocalDateTime end) { 
                eventLoader();
            }   
    	};
    }
	
	private List<Stundenplaneintrag> scheduleEntryList;
	private Stundenplaneintrag spe;
	
	private ScheduleModel eventModel;
	private ScheduleModel lazyEventModel;
	private ScheduleEvent<?> event = new DefaultScheduleEvent<>();
	
    ArrayList<Dozenten> profList = new ArrayList<>();
    private int profSelection;
    
    private boolean showWeekends = false;
    
    ArrayList<String> sps1List = new ArrayList<>();
    private String spSemesterSelection;
    ArrayList<Integer> sps2List = new ArrayList<>();
    private int spYearSelection;
    
    private int spsId;
    
    //-------------------------------------------------------------------------------------------------
	
    /**
     * Toggelt die showWeekend Variable
     */
    public void weekendChange() {
    	if (showWeekends == false) {
    		showWeekends = true;
    	}else {
    		showWeekends = false;
    	}
    }
    
	/**
	 * Laden aller Listen aus der Datenbank
	 */
	public void selection() {
		EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Dozenten.findAll");
        List LList = q.getResultList();
        for (Object LListitem : LList)
        {
        	Dozenten doz =(Dozenten)LListitem;
            profList.add(doz);
        }
        
        EntityManager em7 = emf.createEntityManager();
        Query sps1 = em7.createNamedQuery("Stundenplansemester.findAllGroupBySpsemester");
        List sps11List = sps1.getResultList();
        for (Object sps1Listitem : sps11List)
        {
        	Stundenplansemester sps11 =(Stundenplansemester)sps1Listitem;
            sps1List.add(sps11.getSPSemester());
        }
        
        EntityManager em8 = emf.createEntityManager();
        Query sps2 = em8.createNamedQuery("Stundenplansemester.findAllGroupBySpJahr");
        List sps22List = sps2.getResultList();
        for (Object sps2Listitem : sps22List)
        {
        	Stundenplansemester sps22 =(Stundenplansemester)sps2Listitem;
            sps2List.add(sps22.getSPJahr());
        }
        
        spSemesterSelection = sps1List.get(1);
        spYearSelection = sps2List.get(1);
        spSemester = findSPSelection(spSemesterSelection, spYearSelection);
        spsId = spSemester.getSpsid();
	}
	
	/**
	 * Leeren aller Events und anschließend neu laden
	 */
	public void loadModule() {
        lazyEventModel.clear();
        eventLoader();
	}
	
	// 
	/**
	 * Laden der Events
	 */
	public void eventLoader() {        
        try{/* Laden der Datenbank*/
        	spSemester = findSPSelection(spSemesterSelection, spYearSelection);
            spsId = spSemester.getSpsid();
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Stundenplaneintrag.findAllProf", Stundenplaneintrag.class);
            query.setParameter("did", profSelection);
            query.setParameter("spsid", spsId);
            scheduleEntryList = query.getResultList();
        }
        catch(Exception e){
        }
        
        for(int i = 0; i < scheduleEntryList.size(); i++){
        	this.spe = new Stundenplaneintrag();
        	spe = scheduleEntryList.get(i);
        	LocalDateTime ltime = convertToLocalDateTimeViaInstant(spe.getSPEStartZeit());
        	LocalDateTime ltime2 = convertToLocalDateTimeViaInstant(spe.getSPEEndZeit());
            
            String eintragString = (String) (spe.getSgmodul().getModul().getModKuerzel() +" / "+
                                             spe.getLehrveranstaltungsart().getLvkurz()+ " / " +
                                             spe.getSPTermin() + " \n " +
                                             spe.getRaum().getRName()
                                             );
            
            //Ereignis hinzufügen            
            DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                    .title(eintragString)
                    .startDate(ltime)
                    .endDate(ltime2)
                    .data(spe)
                    .build();
            lazyEventModel.addEvent(event);
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * Umwandlung von Date to LocalDateTime
	 * @param dateToConvert
	 * @return
	 */
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}

	public Dozenten getProfessor() {
		return professor;
	}

	public void setProfessor(Dozenten professor) {
		this.professor = professor;
	}

	public List<Stundenplaneintrag> getScheduleEntryList() {
		return scheduleEntryList;
	}

	public void setScheduleEntryList(List<Stundenplaneintrag> scheduleEntryList) {
		this.scheduleEntryList = scheduleEntryList;
	}

	public Stundenplaneintrag getSpe() {
		return spe;
	}

	public void setSpe(Stundenplaneintrag spe) {
		this.spe = spe;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	public void setLazyEventModel(ScheduleModel lazyEventModel) {
		this.lazyEventModel = lazyEventModel;
	}

	public ScheduleEvent<?> getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent<?> event) {
		this.event = event;
	}

	public ArrayList<Dozenten> getProfList() {
		return profList;
	}

	public void setProfList(ArrayList<Dozenten> profList) {
		this.profList = profList;
	}

	public int getProfSelection() {
		return profSelection;
	}

	public void setProfSelection(int profSelection) {
		this.profSelection = profSelection;
	}
	public boolean isShowWeekends() {
		return showWeekends;
	}

	public void setShowWeekends(boolean showWeekends) {
		this.showWeekends = showWeekends;
	}
	
	/**
	 * Finden des Stundenplansemesters durch Stundenplansemester und Stundenplansemesterjahr
	 * @param sps
	 * @param sm
	 * @return
	 */
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

	public Stundenplansemester getSpSemester() {
		return spSemester;
	}

	public void setSpSemester(Stundenplansemester spSemester) {
		this.spSemester = spSemester;
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

	public int getSpsId() {
		return spsId;
	}

	public void setSpsId(int spsId) {
		this.spsId = spsId;
	}

}
