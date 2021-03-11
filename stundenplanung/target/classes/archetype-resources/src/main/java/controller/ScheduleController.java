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
@Named(value="scheduleController")
@SessionScoped
public class ScheduleController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
	private EntityManagerFactory emf;
  
	@Resource
	private UserTransaction ut;
	
	@Inject 
	private Raum room;
	private Lehrveranstaltungsart teachingEvent;
	private Sgmodul sgmodul;
	private Faculty faculty;
	private Stundenplansemester spSemester;
	
	@EJB
	private StundenplaneintragFacadeLocal speFacadeLocal;
	
	@PostConstruct
	public void init() {
		updateDb();
        selection();
        events = new ScheduleModel[7];
        for(int j = 0; j < 7; j++) { 	
        	events[j] = new DefaultScheduleModel();
        }
        eventLoader();
    }	
	
	private Date startTime;
	private Date endTime;
	private int spMeeting;
	private int studentNumber;
	private Timestamp timeStamp;
	
	TimeZone timeZoneDefault = TimeZone.getDefault(); 
	
	private ScheduleModel events[] = new ScheduleModel[7];
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Date weekstart;
    
    private List<Stundenplaneintrag> loadDb;
    
    private List<Stundenplaneintrag> scheduleEntryList;
    private Stundenplaneintrag eventSelected;
    private Stundenplaneintrag spe;
	
	ArrayList<String> teachingEventList = new ArrayList<>();
    private String teName;
    
    ArrayList<Raum> roomList = new ArrayList<>();
    private int roomId;
    
    ArrayList<Sgmodul> sgmodulList = new ArrayList<>();
    private int sgmodulId;
    
    ArrayList<Integer> semesterList = new ArrayList<>();
    private int semesterSelection = 6;
    
    ArrayList<String> courseList = new ArrayList<>();
    private String courseSelection;
    
    ArrayList<String> facultyList = new ArrayList<>();
    private String facultySelection;
    
    ArrayList<Stundenplansemester> spsList = new ArrayList<>();
    private int spsId;
    
    ArrayList<String> sps1List = new ArrayList<>();
    private String spSemesterSelection;
    ArrayList<Integer> sps2List = new ArrayList<>();
    private int spYearSelection;
    
    Calendar calendar;
    Calendar calendarStart;
    Calendar calendarEnd;
    LocalDateTime localTime;
    LocalDateTime localTime2;
    
    private boolean showWeekends = false;
    
    Date weekdayStart;
    Date weekdayEnd;
    
    Date createWeekdayStart;
    Date createWeekdayEnd;
    
    weekDay day;
    
    //--------------------------------------------------------------
    
    public void updateDb() {
    	int dayOffset;
        weekstart = getWeekStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekstart);
        EntityManager em = emf.createEntityManager();
    	try{// Laden der Datenbank
            Query query = em.createNamedQuery("Stundenplaneintrag.findAll", Stundenplaneintrag.class);
            loadDb = query.getResultList();
        }
        catch(Exception e){
        	
        }
    	
    	try {
    		for(int i = 0; i < loadDb.size(); i++){            	
            	this.spe = new Stundenplaneintrag();
            	spe = loadDb.get(i);
                dayOffset = spe.getSPEStartZeit().getDay()-1;
                
                calendarStart = (Calendar) cal.clone();
                //Zeiten
                calendarStart.add(Calendar.DATE, dayOffset);
                calendarStart.set(Calendar.HOUR_OF_DAY, spe.getSPEStartZeit().getHours());
                calendarStart.set(Calendar.MINUTE, spe.getSPEStartZeit().getMinutes());
                calendarStart.set(Calendar.SECOND, spe.getSPEStartZeit().getSeconds());

                
                calendarEnd = (Calendar) calendarStart.clone();
                calendarEnd.set(Calendar.HOUR_OF_DAY, spe.getSPEEndZeit().getHours());
                calendarEnd.set(Calendar.MINUTE, spe.getSPEEndZeit().getMinutes());
                calendarEnd.set(Calendar.SECOND, spe.getSPEEndZeit().getSeconds());

                em.find(Stundenplaneintrag.class, spe.getSpid());
                spe.setSPEStartZeit(calendarStart.getTime());
                spe.setSPEEndZeit(calendarEnd.getTime());
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                
                spe.setZeitStempel(timeStamp);
                speFacadeLocal.edit(spe);
                
            }
    	}
    	catch(Exception e){
			
		}

    }
    
    //--------------------------------------------------------------
    
    public void weekendChange() {
    	if (showWeekends == false) {
    		showWeekends = true;
    	}else {
    		showWeekends = false;
    	}
    }
    
    //--------------------------------------------------------------
    public void loadModule() {
    	
    	
    	for(int j = 0; j < 7; j++) {
        	events[j].clear();
        }
        eventLoader();
    	
	}
    
	public void eventLoader() {        
        try{/* Laden der Datenbank*/
        	spSemester = findSPSelection(spSemesterSelection, spYearSelection);
            spsId = spSemester.getSpsid();
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Stundenplaneintrag.findAllPlanD", Stundenplaneintrag.class);
            query.setParameter("stgang", courseSelection);
            query.setParameter("facName", facultySelection);
            query.setParameter("spsid", spsId);
            scheduleEntryList = query.getResultList();
        }
        catch(Exception e){}
        
        for(int j = 0; j < 7; j++) {
	        for(int i = 0; i < scheduleEntryList.size(); i++){
	        	this.spe = new Stundenplaneintrag();
	        	spe = scheduleEntryList.get(i);
	        	if(spe.getSgmodul().getModSemester() == j) {
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
	            events[j].addEvent(event);
	        	}
	        }
        }
        
	}
	
	public void selection() {
		try{
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Sgmodul.findAllOrderBy",Sgmodul.class);
            List liste = q.getResultList();
            
            for(Object obj : liste){
                Sgmodul r = (Sgmodul) obj;
                
                boolean semNichtVorhanden = false;
                if(semesterList.isEmpty()){
                    semesterList.add(r.getModSemester());
                }else{
                    semNichtVorhanden = true;
                    for(Integer sem : semesterList){
                        if(sem.equals(r.getModSemester())){ 
                            semNichtVorhanden = false;
                            break;
                        }
                    }
                    if(semNichtVorhanden)
                        semesterList.add(r.getModSemester());
                }
            }
           
        }
        catch(Exception e){
        	
        }
		
		EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Lehrveranstaltungsart.findAll");
        List LList = q.getResultList();
        for (Object LListitem : LList)
        {
        	Lehrveranstaltungsart lva =(Lehrveranstaltungsart)LListitem;
            teachingEventList.add(lva.getLvname());
        }
        
        EntityManager em2 = emf.createEntityManager();
        Query r = em2.createNamedQuery("Raum.findAll");
        List RList = r.getResultList();
        for (Object RListitem : RList)
        {
        	Raum ra =(Raum)RListitem;
            roomList.add(ra);
        }
        
        EntityManager em3 = emf.createEntityManager();
        Query s = em3.createNamedQuery("Sgmodul.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Sgmodul sg =(Sgmodul)SListitem;
            sgmodulList.add(sg);
        }
		
		EntityManager em4 = emf.createEntityManager();
        Query s1 = em4.createNamedQuery("Studiengang.findAll");
        List S1List = s1.getResultList();
        for (Object S1Listitem : S1List)
        {
        	Studiengang sg1 =(Studiengang)S1Listitem;
            courseList.add(sg1.getSGName());
        }
        
        EntityManager em5 = emf.createEntityManager();
        Query f = em5.createNamedQuery("Faculty.findAll");
        List FList = f.getResultList();
        for (Object FListitem : FList)
        {
        	Faculty f1 =(Faculty)FListitem;
            facultyList.add(f1.getFacName());
        }
        
        EntityManager em6 = emf.createEntityManager();
        Query sps = em6.createNamedQuery("Stundenplansemester.findAll");
        List spssList = sps.getResultList();
        for (Object spsListitem : spssList)
        {
        	Stundenplansemester spss =(Stundenplansemester)spsListitem;
            spsList.add(spss);
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
        
		facultySelection = facultyList.get(8);
        courseSelection = courseList.get(0);

	}
	
	//Einfügen der Events
    public void addEvent() throws Exception {
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
        	
        	weekdayStart = getWeekdayDate(day);
        	weekdayStart.setHours(createWeekdayStart.getHours());
        	weekdayStart.setMinutes(createWeekdayStart.getMinutes());
        	weekdayStart.setSeconds(createWeekdayStart.getSeconds());

        	weekdayEnd = getWeekdayDate(day);
        	weekdayEnd.setHours(createWeekdayEnd.getHours());
        	weekdayEnd.setMinutes(createWeekdayEnd.getMinutes());
        	weekdayEnd.setSeconds(createWeekdayEnd.getSeconds());
        	
            eventSelected.setSPEStartZeit(weekdayStart);
        	eventSelected.setSPEEndZeit(weekdayEnd);
        	eventSelected.setWochentag(day.getLabel());

        	eventSelected.setSPTermin(spMeeting);
        	eventSelected.setSgmodul(findSgm(sgmodulId));
        	eventSelected.setLehrveranstaltungsart(findLva(teName));
        	eventSelected.setRaum(findRau(roomId));
        	eventSelected.setStundenplansemester(findSP(spsId));
        	eventSelected.setStudierendenzahl(studentNumber);
            Date date= new Date();
            long time = date.getTime();
            timeStamp = new Timestamp(time);
            eventSelected.setZeitStempel(timeStamp);
            
            speFacadeLocal.create(eventSelected);
            
            for(int j = 0; j < 7; j++) {
	        	events[j].clear();
	        }
            eventLoader();
            
            msg = "Ereignis wurde hinzugefügt!";         
            addMessage("messages", msg);
            
            event = new DefaultScheduleEvent();
        }
        catch(Exception e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
            
        }
        
    }
    
    //Bearbeiten der Events
    public void editEvent() throws Exception {

    	String msg;
    	EntityManager em;
    	em = emf.createEntityManager();

    	try{
            em.find(Stundenplaneintrag.class, eventSelected.getSpid());            
            weekdayStart = getWeekdayDate(day);
        	weekdayStart.setHours(createWeekdayStart.getHours());
        	weekdayStart.setMinutes(createWeekdayStart.getMinutes());
        	weekdayStart.setSeconds(createWeekdayStart.getSeconds());

        	weekdayEnd = getWeekdayDate(day);
        	weekdayEnd.setHours(createWeekdayEnd.getHours());
        	weekdayEnd.setMinutes(createWeekdayEnd.getMinutes());
        	weekdayEnd.setSeconds(createWeekdayEnd.getSeconds());
        	
            eventSelected.setSPEStartZeit(weekdayStart);
        	eventSelected.setSPEEndZeit(weekdayEnd);
        	eventSelected.setWochentag(day.getLabel());
            
            
            eventSelected.setSPTermin(eventSelected.getSPTermin());
            eventSelected.setSgmodul(findSgm(sgmodulId));
            eventSelected.setLehrveranstaltungsart(findLva(teName));
            eventSelected.setRaum(findRau(roomId));
            eventSelected.setStundenplansemester(findSP(spsId));
            eventSelected.setStudierendenzahl(eventSelected.getStudierendenzahl());
            //Zeitstempel
            Date date= new Date();
            long time = date.getTime();
            timeStamp = new Timestamp(time);
            
            speFacadeLocal.edit(eventSelected);
            
            msg = "Ereignis wurde geändert!";
            addMessage("messages", msg);
            
            for(int j = 0; j < 7; j++) {
	        	events[j].clear();
	        }
	        eventLoader();
            
            event = new DefaultScheduleEvent();
       
		}
		catch(Exception e){
		    msg = "Ereignis-Daten sind noch nicht vorhanden!";
		    addMessage("messages", msg);
		    
		}
		
    }
    
    //Löschen von Events
    public void deleteEvent() throws Exception{
        
        String msg = "";        
        
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplaneintrag> q = em.createNamedQuery("Stundenplaneintrag.findById",Stundenplaneintrag.class);
        q.setParameter("spid", eventSelected.getSpid());
        eventSelected = (Stundenplaneintrag)q.getSingleResult();
        
        try{            
            speFacadeLocal.remove(eventSelected);
            
            msg = "Ereignis wurde gelöscht!";
        }
        catch(Exception e){
            msg = "Ereignis konnte nicht gelöscht werden!";    
        }
        addMessage("messages", msg);
        events[semesterSelection].deleteEvent(event);
    }
    
    //Auswahl der Events
    public void onEventSelect(SelectEvent<ScheduleEvent> e) {
        event = e.getObject();
        eventSelected = (Stundenplaneintrag) event.getData();
        semesterSelection = eventSelected.getSgmodul().getModSemester();
        
        sgmodulId = eventSelected.getSgmodul().getSgmid();
        teName = eventSelected.getLehrveranstaltungsart().getLvname();
        roomId = eventSelected.getRaum().getRid();
        spsId = eventSelected.getStundenplansemester().getSpsid();
        
        //day.label = eventSelected.getWochentag();
        day = weekDay.valueOfLabel(eventSelected.getWochentag());
        createWeekdayStart = eventSelected.getSPEStartZeit();
        createWeekdayEnd = eventSelected.getSPEEndZeit();
        
    }
    
    //Neue Events hinzufügen
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        this.eventSelected = new Stundenplaneintrag();
        
        localTime = selectEvent.getObject();
        localTime2 = selectEvent.getObject().plusHours(1);
        
        createWeekdayStart = convertToDateViaInstant(localTime);
        createWeekdayEnd = convertToDateViaInstant(localTime2);
        
        event = DefaultScheduleEvent.builder()
                .title("")
                .startDate(localTime)
                .endDate(localTime2)
                .data(eventSelected)
                .build();
        
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event wurde verschoben.", "Delta:" + event.getDeltaAsDuration());
        
        eventSelected = (Stundenplaneintrag) event.getScheduleEvent().getData();
        startTime = eventSelected.getSPEStartZeit();
        endTime = eventSelected.getSPEEndZeit();
        //day = weekDay.valueOfLabel(eventSelected.getWochentag());
        
        long dura = event.getDeltaAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = startTime.getTime() + dura;
        Date date1 = new Date(sum);
        
        long sum1 = endTime.getTime() + dura;
        Date date2 = new Date(sum1);
        
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        day = weekDay.valueOfLabel(simpleDateformat.format(date1));
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                em.find(Stundenplaneintrag.class, eventSelected.getSpid());
                eventSelected.setSPEStartZeit(date1);
                eventSelected.setSPEEndZeit(date2);
                
                eventSelected.setWochentag(day.getLabel());
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                eventSelected.setZeitStempel(timeStamp);
                
                speFacadeLocal.edit(eventSelected);
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(Exception e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
    }
    
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Zeit des Events wurde verändert.", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());
        eventSelected = (Stundenplaneintrag) event.getScheduleEvent().getData();
        startTime = eventSelected.getSPEStartZeit();
        endTime = eventSelected.getSPEEndZeit();
        
        
        long dura = event.getDeltaEndAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = endTime.getTime() + dura;
        Date date1 = new Date(sum);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                em.find(Stundenplaneintrag.class, eventSelected.getSpid());
                eventSelected.setSPEEndZeit(date1);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                eventSelected.setZeitStempel(timeStamp);
                speFacadeLocal.edit(eventSelected);
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(Exception e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
        
        
    }
    
    
    // Fehler ausgeben
    public void addMessage(String toComponent, String message){
        FacesMessage msg = new FacesMessage(message);
        FacesContext cxt = FacesContext.getCurrentInstance();
        cxt.addMessage(toComponent, msg);
    }
    
    //--------------------------------------------------------------
    
    public ArrayList<String> getTeachingEventList() {
		return teachingEventList;
	}
	public void setTeachingEventList(ArrayList<String> teachingEventList) {
		this.teachingEventList = teachingEventList;
	}
	public String getTeName() {
		return teName;
	}
	public void setTeName(String teName) {
		this.teName = teName;
	}
	
	//--------------------------------------------------------------
	
	public ArrayList<Raum> getRoomList() {
		return roomList;
	}
	public void setRoomList(ArrayList<Raum> roomList) {
		this.roomList = roomList;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	//--------------------------------------------------------------
    
	public ArrayList<Sgmodul> getSgmodulList() {
		return sgmodulList;
	}
	public void setSgmodulListe(ArrayList<Sgmodul> sgmodulList) {
		this.sgmodulList = sgmodulList;
	}
	
	public int getSgmodulId() {
		return sgmodulId;
	}
	public void setSgmodulId(int sgmodulId) {
		this.sgmodulId = sgmodulId;
	}
	
	//--------------------------------------------------------------
	
	public Raum getRoom() {
		return room;
	}
	public void setRoom(Raum room) {
		this.room = room;
	}
	
	public Lehrveranstaltungsart getTeachingEvent() {
		return teachingEvent;
	}
	public void setTeachingEvent(Lehrveranstaltungsart teachingEvent) {
		this.teachingEvent = teachingEvent;
	}
	
	public Sgmodul getSgmodul() {
		return sgmodul;
	}
	public void setSgmodul(Sgmodul sgmodul) {
		this.sgmodul = sgmodul;
	}
	
	public Faculty getFaculty() {
		return faculty;
	}
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}	
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public int getSpMeeting() {
		return spMeeting;
	}
	public void setSpMeeting(int spMeeting) {
		this.spMeeting = spMeeting;
	}
	
	public int getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(int studentNumber) {
		this.studentNumber = studentNumber;
	}
	
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}	
	
	//--------------------------------------------------------------
    
    public ScheduleEvent getEvent() { 
    	return event; 
    }
    public void setEvent(ScheduleEvent event) { 
    	this.event = event; 
    }
    
    public Stundenplaneintrag getEventSelected() {
		return eventSelected;
	}
	public void setEventSelected(Stundenplaneintrag eventSelected) {
		this.eventSelected = eventSelected;
	}
	
	public Stundenplaneintrag getSpe() {
		return spe;
	}

	public void setSpe(Stundenplaneintrag spe) {
		this.spe = spe;
	}	
	
    //--------------------------------------------------------------
	
	public ArrayList<Integer> getSemesterList() {
		return semesterList;
	}

	public void setSemesterList(ArrayList<Integer> semesterList) {
		this.semesterList = semesterList;
	}

	public int getSemesterSelection() {
		return semesterSelection;
	}

	public void setSemesterSelection(int semesterSelection) {
		this.semesterSelection = semesterSelection;
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
	
	
	// Listen Objekt finden
	private Raum findRau(int rid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Raum> query
                = em.createNamedQuery("Raum.findByRid",Raum.class);
            query.setParameter("rid", rid);
            room = (Raum)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return room;
    }

	private Lehrveranstaltungsart findLva(String lvname) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Lehrveranstaltungsart> query
                = em.createNamedQuery("Lehrveranstaltungsart.findByLvname",Lehrveranstaltungsart.class);
            query.setParameter("lvname", lvname);
            teachingEvent = (Lehrveranstaltungsart)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return teachingEvent;
    }
	
	private Sgmodul findSgm(int sgmid) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Sgmodul> query
                = em.createNamedQuery("Sgmodul.findBySgmid",Sgmodul.class);
            query.setParameter("sgmid", sgmid);
            sgmodul = (Sgmodul)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return sgmodul;
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
	
	//--------------------------------------------------------------
	
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
	
	//--------------------------------------------------------------
	
	public static LocalDateTime asLocalDateTime(Date date) {
	    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static Date asDate(LocalDateTime localDateTime) {
	    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	//--------------------------------------------------------------
	
	public Date getWeekstart() {
		return weekstart;
	}


	public void setWeekstart(Date weekstart) {
		this.weekstart = weekstart;
	}


	public List<Stundenplaneintrag> getScheduleEntryList() {
		return scheduleEntryList;
	}


	public void setScheduleEntryList(List<Stundenplaneintrag> scheduleEntryList) {
		this.scheduleEntryList = scheduleEntryList;
	}

	public ScheduleModel[] getEvents() {
		return events;
	}

	public void setEvents(ScheduleModel[] events) {
		this.events = events;
	}

	public static String formatDuration(Duration duration) {
	    long seconds = duration.getSeconds();
	    long absSeconds = Math.abs(seconds);
	    String positive = String.format(
	        "%d:%02d:%02d",
	        absSeconds / 3600,
	        (absSeconds % 3600) / 60,
	        absSeconds % 60);
	    return seconds < 0 ? "-" + positive : positive;
	}
	
	public static Date addSeconds(Date date, Integer seconds) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.SECOND, seconds);
	    return cal.getTime();
	  }
	
	public static Date getWeekStartDate() {
	    Calendar calendar = Calendar.getInstance();
	    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
	        calendar.add(Calendar.DATE, -1);
	    }
	    return calendar.getTime();
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}

	public LocalDateTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalDateTime localTime) {
		this.localTime = localTime;
	}

	public LocalDateTime getLocalTime2() {
		return localTime2;
	}

	public void setLocalTime2(LocalDateTime localTime2) {
		this.localTime2 = localTime2;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	private Date ConvertToDate(String dateString){
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date convertedDate = new Date();
	    try {
	        convertedDate = dateFormat.parse(dateString);
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	    }
	    return convertedDate;
	}

	public Calendar getCalendarStart() {
		return calendarStart;
	}

	public void setCalendarStart(Calendar calendarStart) {
		this.calendarStart = calendarStart;
	}

	public Calendar getCalendarEnd() {
		return calendarEnd;
	}

	public void setCalendarEnd(Calendar calendarEnd) {
		this.calendarEnd = calendarEnd;
	}

	public TimeZone getTimeZoneDefault() {
		return timeZoneDefault;
	}

	public void setTimeZoneDefault(TimeZone timeZoneDefault) {
		this.timeZoneDefault = timeZoneDefault;
	}

	public boolean isShowWeekends() {
		return showWeekends;
	}

	public void setShowWeekends(boolean showWeekends) {
		this.showWeekends = showWeekends;
	}
	
	public List<Stundenplaneintrag> getLoadDb() {
		return loadDb;
	}
	
	public void setLoadDb(List<Stundenplaneintrag> loadDb) {
		this.loadDb = loadDb;
	}
	
	public int getSpsId() {
		return spsId;
	}

	public void setSpsId(int spsId) {
		this.spsId = spsId;
	}

	public ArrayList<Stundenplansemester> getSpsList() {
		return spsList;
	}

	public void setSpsList(ArrayList<Stundenplansemester> spsList) {
		this.spsList = spsList;
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
	
	//---------------------------------------------------------------------------------
	
	public Date getWeekdayStart() {
		return weekdayStart;
	}

	public void setWeekdayStart(Date weekdayStart) {
		this.weekdayStart = weekdayStart;
	}

	public Date getWeekdayEnd() {
		return weekdayEnd;
	}

	public void setWeekdayEnd(Date weekdayEnd) {
		this.weekdayEnd = weekdayEnd;
	}
	
	public enum weekDay {
		Montag("Montag"), Dienstag("Dienstag"), Mittwoch("Mittwoch"), Donnerstag("Donnerstag"), Freitag("Freitag"), Samstag("Samstag");
		 
		private String label;

	    private weekDay(String label) {
	        this.label = label;
	    }

	    public String getLabel() {
	        return label;
	    }
	    
	    public static weekDay valueOfLabel(String label) {
		    for (weekDay e : values()) {
		        if (e.label.equals(label)) {
		            return e;
		        }
		    }
		    return null;
		}
	}
	
	public static Date getWeekdayDate(weekDay weekDay) {
	    Calendar calendar = Calendar.getInstance();
	    
	    switch(weekDay) {
	    case Montag:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    case Dienstag:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    case Mittwoch:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    case Donnerstag:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    case Freitag:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    case Samstag:
	    	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
		        calendar.add(Calendar.DATE, +1);
		    }
	    	break;
	    default:
            break;
	    
	    }
	    return calendar.getTime();
	}

	public weekDay getDay() {
		return day;
	}

	public void setDay(weekDay day) {
		this.day = day;
	}
	
	public weekDay[] getWeekDays() {
        return weekDay.values();
    }
	
	public Date getCreateWeekdayStart() {
		return createWeekdayStart;
	}

	public void setCreateWeekdayStart(Date createWeekdayStart) {
		this.createWeekdayStart = createWeekdayStart;
	}

	public Date getCreateWeekdayEnd() {
		return createWeekdayEnd;
	}

	public void setCreateWeekdayEnd(Date createWeekdayEnd) {
		this.createWeekdayEnd = createWeekdayEnd;
	}
	
}
