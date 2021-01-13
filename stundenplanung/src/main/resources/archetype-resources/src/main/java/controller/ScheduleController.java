package controller;

import model.Faculty;
import model.Studiengang;
import model.Lehrveranstaltungsart;
import model.Raum;
import model.Sgmodul;
import model.Stundenplaneintrag;
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

/**
*
* @author Anil
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
	
	@PostConstruct
    public void init() {
        selection();
        lazyEventModel = new LazyScheduleModel() {
        	@Override
            public void loadEvents(LocalDateTime start, LocalDateTime end) { 
                eventLoader();
                loadModule();
            }   
        };
    }
	
	
	private Date startTime;
	private Date endTime;
	private int spMeeting;
	private int studentNumber;
	private Timestamp timeStamp;
	
	TimeZone timeZoneDefault = TimeZone.getDefault(); 
	
	private ScheduleModel eventModel;
    private ScheduleModel lazyEventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Date weekstart;
    
    private List<Stundenplaneintrag> scheduleEntryList;
    private Stundenplaneintrag eventSelected;
    private Stundenplaneintrag spe;
	
	ArrayList<String> teachingEventList = new ArrayList<>();
    private String teName;
    
    ArrayList<Integer> roomList = new ArrayList<>();
    private int roomId;
    
    ArrayList<Integer> sgmodulList = new ArrayList<>();
    private int sgmodulId;
    
    ArrayList<Integer> semesterList = new ArrayList<>();
    private int semester;
    private int semesterSelection;
    
    ArrayList<String> courseList = new ArrayList<>();
    private String courseSelection;
    
    ArrayList<String> facultyList = new ArrayList<>();
    private String facultySelection;
    
    ArrayList<Sgmodul> sgsemModulList = new ArrayList<>();
    ArrayList<String> modulList = new ArrayList<>();
    
    Calendar calendar;
    Calendar calendarStart;
    Calendar calendarEnd;
    LocalDateTime localTime;
    LocalDateTime localTime2;
    
  //--------------------------------------------------------------
    public void loadModule() {
		/*try{
	         EntityManager em = emf.createEntityManager();
	         Query q = em.createNamedQuery("Sgmodul.findBySemesterAndStudiengang",Sgmodul.class);
	         q.setParameter("semester", semester_auswahl);
	         q.setParameter("SGName", studiengang_auswahl);
	         
	         List liste = q.getResultList();
	         
	         
	         for(Object obj : liste){
	             Sgmodul r = (Sgmodul) obj;
	             modulList.add(r.getModul().getModName());                        
	             sgsemModulList.add(r);
	                             
	         }
	     }
	     catch(Exception e){
	     }*/
    	
    	try{
	        EntityManager em = emf.createEntityManager();
	        Query q = em.createNamedQuery("Sgmodul.findBySemesterAndStudiengangAndFaculty",Sgmodul.class);
	        q.setParameter("semester", semesterSelection);
	        q.setParameter("SGName", courseSelection);
	        q.setParameter("facName", facultySelection);
	        
	        List liste = q.getResultList();
	        
	        
	        for(Object obj : liste){
	            Sgmodul r = (Sgmodul) obj;
	            modulList.add(r.getModul().getModName());                        
	            sgsemModulList.add(r);
	                            
	        }
	    }
	    catch(Exception e){
	    }
	}
    
	public void eventLoader() {
		int dayOffset;
        weekstart = getWeekStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekstart);
        
        try{/* Laden der Datenbank*/
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Stundenplaneintrag.findAllPlan", Stundenplaneintrag.class);
            query.setParameter("semester", semesterSelection);
            query.setParameter("stgang", courseSelection);
            scheduleEntryList = query.getResultList();
        }
        catch(Exception e){}
        
        for(int i = 0; i < scheduleEntryList.size(); i++){
        	this.spe = new Stundenplaneintrag();
        	spe = scheduleEntryList.get(i);
            //Eventeinträge
            dayOffset = spe.getSPEStartZeit().getDay()-1;
            
            calendarStart = (Calendar) cal.clone();
            //Zeiten
            calendarStart.add(Calendar.DATE, dayOffset);
            calendarStart.set(Calendar.HOUR_OF_DAY, spe.getSPEStartZeit().getHours());
            calendarStart.set(Calendar.MINUTE, spe.getSPEStartZeit().getMinutes());
            calendarStart.set(Calendar.SECOND, spe.getSPEStartZeit().getSeconds());
            LocalDateTime ltime = convertToLocalDateTimeViaInstant(calendarStart.getTime());
            
            calendarEnd = (Calendar) calendarStart.clone();
            calendarEnd.set(Calendar.HOUR_OF_DAY, spe.getSPEEndZeit().getHours());
            calendarEnd.set(Calendar.MINUTE, spe.getSPEEndZeit().getMinutes());
            calendarEnd.set(Calendar.SECOND, spe.getSPEEndZeit().getSeconds());
            LocalDateTime ltime2 = convertToLocalDateTimeViaInstant(calendarEnd.getTime());
        	
            
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
            roomList.add(ra.getRid());
        }
        
        EntityManager em3 = emf.createEntityManager();
        Query s = em3.createNamedQuery("Sgmodul.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Sgmodul sg =(Sgmodul)SListitem;
            sgmodulList.add(sg.getSgmid());
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
        
		facultySelection = facultyList.get(8);
        semester = 6;
        semesterSelection = semester;
        courseSelection = courseList.get(0);

	}
	
	/* Bearbeiten oder Einfügen der Events*/
    public void addEvent() throws NotSupportedException, 
            SystemException, RollbackException, HeuristicMixedException, 
            HeuristicRollbackException, javax.transaction.RollbackException {
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
            if(getEvent().getId() == null){             
            	eventSelected.setSPEStartZeit(asDate(event.getStartDate()));
            	eventSelected.setSPEEndZeit(asDate(event.getEndDate()));
            	eventSelected.setSPTermin(spMeeting);
            	eventSelected.setSgmodul(findSgm(sgmodulId));
            	eventSelected.setLehrveranstaltungsart(findLva(teName));
            	eventSelected.setRaum(findRau(roomId));
            	eventSelected.setStudierendenzahl(studentNumber);
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                eventSelected.setZeitStempel(timeStamp);
                
                
                eventLoader();

                
                ut.begin();
            	em.joinTransaction();
                em.persist(eventSelected);
                ut.commit();
                msg = "Ereignis wurde hinzugefügt!";                
                
            }
            else{/* ID schon vorhanden*/
                lazyEventModel.updateEvent(getEvent());
                ut.begin();
                em.find(Stundenplaneintrag.class, eventSelected.getSpid());
                
                eventSelected.setSPEStartZeit(eventSelected.getSPEStartZeit());
                eventSelected.setSPEEndZeit(eventSelected.getSPEEndZeit());
                eventSelected.setSPTermin(eventSelected.getSPTermin());
                eventSelected.setSgmodul(findSgm(sgmodulId));
                eventSelected.setLehrveranstaltungsart(findLva(teName));
                eventSelected.setRaum(findRau(roomId));
                eventSelected.setStudierendenzahl(eventSelected.getStudierendenzahl());
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                
                eventSelected.setZeitStempel(timeStamp);
                em.merge(eventSelected);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                
            }
            event = new DefaultScheduleEvent();
            addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
    }
    
    //Löschen von Events
    public void deleteEvent() throws javax.transaction.RollbackException{
        
        String msg = "";        
        
        
        EntityManager em = emf.createEntityManager();
        TypedQuery<Stundenplaneintrag> q = em.createNamedQuery("Stundenplaneintrag.findById",Stundenplaneintrag.class);
        q.setParameter("spid", eventSelected.getSpid());
        eventSelected = (Stundenplaneintrag)q.getSingleResult();
        
        try{
            ut.begin();
	        em.joinTransaction();  
            em.remove(eventSelected);
            ut.commit();
            msg = "Ereignis wurde gelöscht!";
        }
        catch(  NotSupportedException | SystemException | 
                RollbackException | HeuristicMixedException | 
                HeuristicRollbackException | SecurityException | 
                IllegalStateException e){
            msg = "Ereignis konnte nicht gelöscht werden!";    
        }
        addMessage("messages", msg);
        lazyEventModel.deleteEvent(event);
    }
    
    //Auswahl der Events
    public void onEventSelect(SelectEvent<ScheduleEvent> e) {
        event = (ScheduleEvent) e.getObject();
        eventSelected = (Stundenplaneintrag) event.getData();
        
        sgmodulId = eventSelected.getSgmodul().getSgmid();
        teName = eventSelected.getLehrveranstaltungsart().getLvname();
        roomId = eventSelected.getRaum().getRid();
    }
    
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        this.eventSelected = new Stundenplaneintrag();
        
        localTime = selectEvent.getObject();
        localTime2 = selectEvent.getObject().plusHours(1);
        
        event = DefaultScheduleEvent.builder()
                .title("")
                .startDate(localTime)
                .endDate(localTime2)
                .data(eventSelected)
                .build();
        
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());
        String msg1 = "Event moved."; 
        addMessage("messages", msg1);
        
        eventSelected = (Stundenplaneintrag) event.getScheduleEvent().getData();
        startTime = eventSelected.getSPEStartZeit();
        endTime = eventSelected.getSPEEndZeit();
        
        
        long dura = event.getDeltaAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = startTime.getTime() + dura;
        Date date1 = new Date(sum);
        
        long sum1 = endTime.getTime() + dura;
        Date date2 = new Date(sum1);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                ut.begin();
                em.find(Stundenplaneintrag.class, eventSelected.getSpid());
                eventSelected.setSPEStartZeit(date1);
                eventSelected.setSPEEndZeit(date2);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                eventSelected.setZeitStempel(timeStamp);
                em.merge(eventSelected);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
            msg = "Ereignis-Daten sind noch nicht vorhanden!";
            addMessage("messages", msg);
        }
    }
    
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());
        String msg1 = "Event resized"; 
        addMessage("message", msg1);
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
                ut.begin();
                em.find(Stundenplaneintrag.class, eventSelected.getSpid());
                eventSelected.setSPEEndZeit(date1);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                eventSelected.setZeitStempel(timeStamp);
                em.merge(eventSelected);
                ut.commit();
                msg = "Ereignis wurde geändert!";
                addMessage("messages", msg);
        }
        catch(NotSupportedException | SystemException | RollbackException | 
                HeuristicMixedException | HeuristicRollbackException | 
                SecurityException | IllegalStateException e){
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
	
	public ArrayList<Integer> getRoomList() {
		return roomList;
	}
	public void setRoomList(ArrayList<Integer> roomList) {
		this.roomList = roomList;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	//--------------------------------------------------------------
    
	public ArrayList<Integer> getSgmodulList() {
		return sgmodulList;
	}
	public void setSgmodulListe(ArrayList<Integer> sgmodulList) {
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
	
	public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }
    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }
    
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

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
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


	public ArrayList<Sgmodul> getSgsemModulList() {
		return sgsemModulList;
	}


	public void setSgsemModulList(ArrayList<Sgmodul> sgsemModulList) {
		this.sgsemModulList = sgsemModulList;
	}


	public ArrayList<String> getModulList() {
		return modulList;
	}


	public void setModulList(ArrayList<String> modulList) {
		this.modulList = modulList;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
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

}
