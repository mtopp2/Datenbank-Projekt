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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.TimeZone;



import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.transaction.UserTransaction;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.util.GregorianCalendar;

import java.util.Calendar;


import javax.ejb.EJB;
import EJB.StundenplaneintragFacadeLocal;

/*
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;*/

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
	private Studiengang course;
	
	@EJB
	private StundenplaneintragFacadeLocal speFacadeLocal;
	
	/**
	 * Initialisierung des Wochenplans
	 */
	@PostConstruct
	public void init() {
		updateDb();
        selection();
        events = new ScheduleModel[9];
        for(int j = 0; j < 9; j++) { 	
        	events[j] = new DefaultScheduleModel();
        }
        eventLoader();
    }	
	
	// Variablen
	private Date startTime;
	private Date endTime;
	private int spMeeting;
	private int studentNumber;
	private Timestamp timeStamp;
	
	TimeZone timeZoneDefault = TimeZone.getDefault(); 
	
	private ScheduleModel events[] = new ScheduleModel[9];
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
    
    GregorianCalendar calendarStart;
    GregorianCalendar calendarEnd;
    LocalDateTime localTime;
    LocalDateTime localTime2;
    
    private boolean showWeekends = false;
    
    Date weekdayStart;
    Date weekdayEnd;
    
    Date createWeekdayStart;
    Date createWeekdayEnd;
    
    weekDay day;
    
    boolean show1 = false;
    boolean show2 = false;
    boolean show3 = false;
    boolean show4 = false;
    boolean show5 = false;
    boolean show6 = false;
    boolean show7 = false;
    boolean show8 = false;
    
    
    //--------------------------------------------------------------
    
    /**
     * Update der Stundenplaneinträge auf die aktuelle Woche
     */
    public void updateDb() {        
        GregorianCalendar calStart =  new GregorianCalendar();
        GregorianCalendar calEnd =  new GregorianCalendar();
        
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
            	calStart.setTime(spe.getSPEStartZeit());
            	calEnd.setTime(spe.getSPEEndZeit());
            	
                day = weekDay.valueOfLabel(spe.getWochentag());
                weekstart = getWeekdayDate(day);
                
                //Zeiten
                calendarStart.setTime(weekstart);
                calendarStart.set(Calendar.HOUR_OF_DAY, calStart.get(Calendar.HOUR_OF_DAY));
                calendarStart.set(Calendar.MINUTE, calStart.get(Calendar.MINUTE));
                calendarStart.set(Calendar.SECOND, calStart.get(Calendar.SECOND));

                
                calendarEnd = (GregorianCalendar) calendarStart.clone();
                calendarEnd.set(Calendar.HOUR_OF_DAY, calEnd.get(Calendar.HOUR_OF_DAY));
                calendarEnd.set(Calendar.MINUTE, calEnd.get(Calendar.MINUTE));
                calendarEnd.set(Calendar.SECOND, calEnd.get(Calendar.SECOND));

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
    
    /**
     * Schedule Attribut Showweekend ändern
     */
    public void weekendChange() {
    	if (showWeekends == false) {
    		showWeekends = true;
    	}else {
    		showWeekends = false;
    	}
    }
    
    //--------------------------------------------------------------
    
    /**
     * Laden des Stundenplans
     */
    public void loadSchedule() {
        loadModule();
        
        course = findSG(courseSelection, facultySelection);
        
        switch(course.getSemester()) {
	    case 2:
	    	show1 = true;
	    	show2 = true;
	    	show3 = false;
	    	show4 = false;
	    	show5 = false;
	    	show6 = false;
	    	show7 = false;
	    	show8 = false;
	    	break;
	    case 4:
	    	show1 = true;
	    	show2 = true;
	    	show3 = true;
	    	show4 = true;
	    	show5 = false;
	    	show6 = false;
	    	show7 = false;
	    	show8 = false;
	    	break;
	    case 6:
	    	show1 = true;
	    	show2 = true;
	    	show3 = true;
	    	show4 = true;
	    	show5 = true;
	    	show6 = true;
	    	show7 = false;
	    	show8 = false;
	    	break;
	    case 8:
	    	show1 = true;
	    	show2 = true;
	    	show3 = true;
	    	show4 = true;
	    	show5 = true;
	    	show6 = true;
	    	show7 = true;
	    	show8 = true;
	    	break;
	    default:
	    	show1 = false;
	    	show2 = false;
	    	show3 = false;
	    	show4 = false;
	    	show5 = false;
	    	show6 = false;
	    	show7 = false;
	    	show8 = false;
            break;
        }
	}
    
    
    /**
     * Leeren aller Events und anschließend neu laden
     */
    public void loadModule() {
    	for(int j = 0; j < 9; j++) {
        	events[j].clear();
        }
        eventLoader();
    	
	}
    
	/**
	 * Laden aller Events für die entsprechenden Einstellungen
	 */
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
        
        for(int j = 0; j < 9; j++) {
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
	
	/**
	 * Laden aller Listen aus der Datenbank
	 */
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
        
        Query r = em.createNamedQuery("Raum.findAll");
        List RList = r.getResultList();
        for (Object RListitem : RList)
        {
        	Raum ra =(Raum)RListitem;
            roomList.add(ra);
        }
        
        Query s = em.createNamedQuery("Sgmodul.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Sgmodul sg =(Sgmodul)SListitem;
            sgmodulList.add(sg);
        }
		
        Query s1 = em.createNamedQuery("Studiengang.findAll");
        List S1List = s1.getResultList();
        for (Object S1Listitem : S1List)
        {
        	Studiengang sg1 =(Studiengang)S1Listitem;
            courseList.add(sg1.getSGName());
        }
        
        Query f = em.createNamedQuery("Faculty.findAll");
        List FList = f.getResultList();
        for (Object FListitem : FList)
        {
        	Faculty f1 =(Faculty)FListitem;
            facultyList.add(f1.getFacName());
        }
        
        Query sps = em.createNamedQuery("Stundenplansemester.findAll");
        List spssList = sps.getResultList();
        for (Object spsListitem : spssList)
        {
        	Stundenplansemester spss =(Stundenplansemester)spsListitem;
            spsList.add(spss);
        }
        
        Query sps1 = em.createNamedQuery("Stundenplansemester.findAllGroupBySpsemester");
        List sps11List = sps1.getResultList();
        for (Object sps1Listitem : sps11List)
        {
        	Stundenplansemester sps11 =(Stundenplansemester)sps1Listitem;
            sps1List.add(sps11.getSPSemester());
        }
        
        Query sps2 = em.createNamedQuery("Stundenplansemester.findAllGroupBySpJahr");
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
	
    /**
     * Einfügen der Events
     * @throws Exception
     */
    public void addEvent() throws Exception {
        
        String msg;
        GregorianCalendar calStart =  new GregorianCalendar();
        GregorianCalendar calCreateStart =  new GregorianCalendar();
        
        GregorianCalendar calEnd =  new GregorianCalendar();
        GregorianCalendar calCreateEnd =  new GregorianCalendar();
        
        try{
        	
        	weekdayStart = getWeekdayDate(day);
        	calStart.setTime(weekdayStart);
        	calCreateStart.setTime(createWeekdayStart);
        	calStart.set(Calendar.HOUR_OF_DAY, calCreateStart.get(Calendar.HOUR_OF_DAY));
        	calStart.set(Calendar.MINUTE, calCreateStart.get(Calendar.MINUTE));
        	calStart.set(Calendar.SECOND, calCreateStart.get(Calendar.SECOND));
        	weekdayStart = calStart.getTime();

        	weekdayEnd = getWeekdayDate(day);
        	calEnd.setTime(weekdayEnd);
        	calCreateEnd.setTime(createWeekdayEnd);
        	calEnd.set(Calendar.HOUR_OF_DAY, calCreateEnd.get(Calendar.HOUR_OF_DAY));
        	calEnd.set(Calendar.MINUTE, calCreateEnd.get(Calendar.MINUTE));
        	calEnd.set(Calendar.SECOND, calCreateEnd.get(Calendar.SECOND));
        	weekdayEnd = calEnd.getTime();
        	
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
            
            loadModule();
            
            msg = "Ereignis wurde hinzugefügt!";         
            addMessage("messages", msg);
            
            event = new DefaultScheduleEvent();
        }
        catch(Exception e){
            msg = "Eventdaten wurden nicht übergeben!";
            addMessage("messages", msg);
            
        }
        
    }
    
    /**
     * Bearbeiten der Events
     * @throws Exception
     */
    public void editEvent() throws Exception {

    	String msg;
    	EntityManager em;
    	em = emf.createEntityManager();
    	GregorianCalendar calStart =  new GregorianCalendar();
        GregorianCalendar calCreateStart =  new GregorianCalendar();
        
        GregorianCalendar calEnd =  new GregorianCalendar();
        GregorianCalendar calCreateEnd =  new GregorianCalendar();

    	try{
            em.find(Stundenplaneintrag.class, eventSelected.getSpid());            
        	weekdayStart = getWeekdayDate(day);
        	calStart.setTime(weekdayStart);
        	calCreateStart.setTime(createWeekdayStart);
        	calStart.set(Calendar.HOUR_OF_DAY, calCreateStart.get(Calendar.HOUR_OF_DAY));
        	calStart.set(Calendar.MINUTE, calCreateStart.get(Calendar.MINUTE));
        	calStart.set(Calendar.SECOND, calCreateStart.get(Calendar.SECOND));
        	weekdayStart = calStart.getTime();

        	weekdayEnd = getWeekdayDate(day);
        	calEnd.setTime(weekdayEnd);
        	calCreateEnd.setTime(createWeekdayEnd);
        	calEnd.set(Calendar.HOUR_OF_DAY, calCreateEnd.get(Calendar.HOUR_OF_DAY));
        	calEnd.set(Calendar.MINUTE, calCreateEnd.get(Calendar.MINUTE));
        	calEnd.set(Calendar.SECOND, calCreateEnd.get(Calendar.SECOND));
        	weekdayEnd = calEnd.getTime();
        	
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
            
            loadModule();
            
            event = new DefaultScheduleEvent();
       
		}
		catch(Exception e){
		    msg = "Eventdaten wurden nicht übergeben!";
		    addMessage("messages", msg);
		    
		}
		
    }
    
    /**
     * Löschen von Events
     * @throws Exception
     */
    public void deleteEvent() throws Exception{
        String msg;       
        
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
    
    /**
     * Auswahl der Events
     * @param e
     */
    public void onEventSelect(SelectEvent<ScheduleEvent> e) {
        event = e.getObject();
        eventSelected = (Stundenplaneintrag) event.getData();
        semesterSelection = eventSelected.getSgmodul().getModSemester();
        
        sgmodulId = eventSelected.getSgmodul().getSgmid();
        teName = eventSelected.getLehrveranstaltungsart().getLvname();
        roomId = eventSelected.getRaum().getRid();
        spsId = eventSelected.getStundenplansemester().getSpsid();
        
        day = weekDay.valueOfLabel(eventSelected.getWochentag());
        createWeekdayStart = eventSelected.getSPEStartZeit();
        createWeekdayEnd = eventSelected.getSPEEndZeit();
        
    }

    /**
     * Neue Events hinzufügen
     * @param selectEvent
     */
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
    
    /**
     * Event verschieben
     * @param event
     */
    public void onEventMove(ScheduleEntryMoveEvent event) {
        eventSelected = (Stundenplaneintrag) event.getScheduleEvent().getData();
        startTime = eventSelected.getSPEStartZeit();
        endTime = eventSelected.getSPEEndZeit();
        
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
            msg = "Eventdaten wurden nicht übergeben!";
            addMessage("messages", msg);
        }
    }
    
    /**
     * Verändern der Zeit durch hoch und runter ziehen des Events
     * @param event
     */
    public void onEventResize(ScheduleEntryResizeEvent event) {
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
            msg = "Eventdaten wurden nicht übergeben!";
            addMessage("messages", msg);
        }
        
        
    }
    
    
    /**
     * Fehler ausgeben
     * @param toComponent
     * @param message
     */
    public void addMessage(String toComponent, String message){
        FacesMessage msg = new FacesMessage(message);
        FacesContext cxt = FacesContext.getCurrentInstance();
        cxt.addMessage(toComponent, msg);
    }
    
    //--------------------------------------------------------------
    
    // Setter und Getter
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
	
	
	/**
	 * Finden des Raumes durch die Raumid
	 * @param rid
	 * @return
	 */
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
	
	/**
	 * Finden der Lehrveranstaltung durch den Lehrveranstaltungsname
	 * @param lvname
	 * @return
	 */
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
	
	/**
	 * Finden des Studiengangsmoduls durch die Studiengangsid
	 * @param sgmid
	 * @return
	 */
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
	
	/**
	 * Finden des Stundenplansemesters durch die Stundenplansemesterid
	 * @param sm
	 * @return
	 */
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
	
	/**
	 * Finden des Studienganges durch Studiengangsname und Fachbereich
	 * @param courseSelec
	 * @param facultySelec
	 * @return
	 */
	private Studiengang findSG(String courseSelec, String facultySelec) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Studiengang> query
                = em.createNamedQuery("Studiengang.findBySGNameAndFacName",Studiengang.class);
            query.setParameter("SGName", courseSelec);
            query.setParameter("facName", facultySelec);
            course = (Studiengang)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return course;
    }
	
	
	//--------------------------------------------------------------
	
	/**
	 * Finden des Stundenplansemesters durch SPSemester(Wintersemester oder Sommersemester) und des Stundenplansemesterjahres
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
	
	/**
	 * Setzt eine Calendarvariable auf den Anfang der aktuellen Woche
	 * @return
	 */
	public static Date getWeekStartDate() {
	    Calendar calendar = Calendar.getInstance();
	    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
	        calendar.add(Calendar.DATE, -1);
	    }
	    return calendar.getTime();
	}
	
	/**
	 * Date in LocalDateTime umwandeln
	 * @param dateToConvert
	 * @return
	 */
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	/**
	 * LocalDateTime in Date umwandeln
	 * @param dateToConvert
	 * @return
	 */
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
	// Enum Weekday
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

	/**
	 * Enum weekDay wird die jeweilige Calendarvariable umgewandelt
	 * @param weekDay
	 * @return
	 */
	public static Date getWeekdayDate(weekDay weekDay) {
		Date date = getWeekStartDate();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    
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

	public Studiengang getCourse() {
		return course;
	}

	public void setCourse(Studiengang course) {
		this.course = course;
	}

	public boolean isShow1() {
		return show1;
	}

	public void setShow1(boolean show1) {
		this.show1 = show1;
	}

	public boolean isShow2() {
		return show2;
	}

	public void setShow2(boolean show2) {
		this.show2 = show2;
	}

	public boolean isShow3() {
		return show3;
	}

	public void setShow3(boolean show3) {
		this.show3 = show3;
	}

	public boolean isShow4() {
		return show4;
	}

	public void setShow4(boolean show4) {
		this.show4 = show4;
	}

	public boolean isShow5() {
		return show5;
	}

	public void setShow5(boolean show5) {
		this.show5 = show5;
	}

	public boolean isShow6() {
		return show6;
	}

	public void setShow6(boolean show6) {
		this.show6 = show6;
	}

	public boolean isShow7() {
		return show7;
	}

	public void setShow7(boolean show7) {
		this.show7 = show7;
	}

	public boolean isShow8() {
		return show8;
	}

	public void setShow8(boolean show8) {
		this.show8 = show8;
	}

	public GregorianCalendar getCalendarStart() {
		return calendarStart;
	}

	public void setCalendarStart(GregorianCalendar calendarStart) {
		this.calendarStart = calendarStart;
	}

	public GregorianCalendar getCalendarEnd() {
		return calendarEnd;
	}

	public void setCalendarEnd(GregorianCalendar calendarEnd) {
		this.calendarEnd = calendarEnd;
	}
	//--------------------------------------------------------
	
	// Exportieren
	
/*--	public void preProcessPDF(Object doc) {
        Document pdf = (Document) doc;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        pdf.setMargins((float) 2.5, (float) 2.5, (float) 2.5, (float) 2.5);

        createPDF(pdf);
    }

    public void postProcessPDF(Object doc) {
        try {
            Document pdf = (Document) doc;
            pdf.close();
        } catch (Exception  ex) {
            //LOG.log(Level.SEVERE, ex.getMessage());
        	Logger.getLogger(ConsoleHandler.class.getName())
            .log(Level.SEVERE, null, ex);
        }
    }

    public void createPDF(Document pdfdocument) {
        // 1 cm = 0,393700787 inch
        // 72 user unit = 1 inch
        // 1 cm = 28,346456664 user unit

//        OutputStream file = null;
//        if (this.pruefungenEJB != null) {
//            System.out.println("not null");
//        } else {
//            System.out.println("null");
//        }
        try {
            PdfPTable pdfTable;

//            file = new FileOutputStream(new File("SamplePDF.pdf"));
            //float cm = 28.35f;
            //float margin = 0.8f * cm;
            //float marginLeft   = margin;
            //float marginRight  = margin;
            //float marginTop    = margin;
            //float marginBottom = margin;
//            Font font = new Font(Font.HELVETICA, 11);
//            Document pdfdocument = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom );
//            PdfWriter pdfw = PdfWriter.getInstance(document, file);
            pdfdocument.open();
            //pdfdocument.setPageSize(PageSize.A4);
            //pdfdocument.setMargins(marginLeft, marginRight, marginTop, marginBottom);

//            document.add(new Paragraph("First iText PDF"));
//            document.add(new Paragraph(new Date().toString()));
//            document.addAuthor("Krishna Srinivasan");
//            document.addCreationDate();
//            document.addCreator("JavaBeat");
//            document.addTitle("Sample PDF");
//            //Create Paragraph
//            Paragraph paragraph = new Paragraph("Title 1",new Font(Font.TIMES_ROMAN, 18,Font.BOLD));
//            //New line
//            paragraph.add(new Paragraph(" "));
//            paragraph.add("Test Paragraph");
//            paragraph.add(new Paragraph(" "));
//            document.add(paragraph);
//            
//            //Create a table in PDF
//            pdfTable = new PdfPTable(3);
//            PdfPCell cell1 = new PdfPCell(new Phrase("Table Header 1"));
//            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            pdfTable.addCell(cell1);
//            
//            cell1 = new PdfPCell(new Phrase("Table Header 2"));
//            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            pdfTable.addCell(cell1);
//            
//            cell1 = new PdfPCell(new Phrase("Table Header 3"));
//            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            pdfTable.addCell(cell1);
//            
//            pdfTable.setHeaderRows(1);
//            pdfTable.addCell("Row 1 Col 1");
//            pdfTable.addCell("Row 1 Col 2");
//            pdfTable.addCell("Row 1 Col 3");
//            pdfTable.addCell("Row 2 Col 1");
//            pdfTable.addCell("Row 2 Col 2");
//            pdfTable.addCell("Row 2 Col 3");
//            document.add(pdfTable);
//            
//            
//            document.add( new Paragraph(""));
/*--            pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(80.0f); //nur 80% wg. Margins
            pdfTable.setSpacingBefore(15f);
            pdfTable.setWidths(new float[]{30f, 10f, 60f, 30f, 10f, 10f});

            CreatePDFHeader(pdfTable);
            CreatePDFFooter(pdfTable);

            pdfTable.setHeaderRows(3 + 1); // Alle Zeilen Header + Footer
            pdfTable.setFooterRows(1);
            pdfTable.completeRow();

            CreatePDFBody(pdfTable);

            pdfdocument.add(pdfTable);
            //pdfdocument.close();
            //file.close();
            //--Seid jdk 8 wird ConsoleHandler anstatt PrintHandler benutzt
        } catch (Exception ex) {
            Logger.getLogger(ConsoleHandler.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
//        finally {
//            try {
//                file.close();
//            } catch (IOException ex) {
//                Logger.getLogger(PrintHandler.class.getName())
//                      .log(Level.SEVERE, null, ex);
//            }
//        }
    }
/*--
    public void CreatePDFFooter(PdfPTable table) {

        Font font = FontFactory.getFont("Arial", 10, Font.BOLD);
        PdfPCell cell;
        PdfPTable subtable;
        //ArrayList<PdfPCell> rightcells = new ArrayList();
        //ArrayList<PdfPCell> leftcells = new ArrayList();

        // Erstelldatum und Ort
        // Änderungen in Rot
        // Prof. Dr.-Ing. Name
        // Prüfungsausschussvorsitzender
        // DONE (09/2020): Lesen der Prüfformen aus der Stringliste
        String[] lefttexts = new String[5];
        lefttexts[0] = "Bielefeld, " + new SimpleDateFormat("dd.MM.yyyy",
                Locale.GERMAN).format(new Date());
        //lefttexts[1] = "";
        //lefttexts[2] = "Änderungen i.d.R. hervorgehoben.";
        //lefttexts[3] = this.currentPAVString;
        //lefttexts[4] = "Prüfungsausschussvorsitzender";

        subtable = new PdfPTable(1);
        for (int i = 0; i < 1; i++) {
            String text = lefttexts[i];
            cell = new PdfPCell(new Phrase(text, font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (i == 0) {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
            } else if (i < 4) {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            } else {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            }
            subtable.addCell(cell);
        }
        cell = new PdfPCell(subtable);
        cell.setColspan(3);
        table.addCell(cell);

        // Legende
        // jetzt aus der Stringliste
        /*int index = 1;
        String[] righttexts = new String[this.stringListPruefungsformen.size() + 1];
        righttexts[0] = "Legende:";
        for (String pf : stringListPruefungsformen) {
            righttexts[index++] = pf;
        }

        subtable = new PdfPTable(1);
        //Obere Grenze "=" wegen String "Legende" (kommt als String hinzu!)
        for (int i = 0; i <= this.stringListPruefungsformen.size(); i++) {
            String text = righttexts[i];
            cell = new PdfPCell(new Phrase(text, font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (i == 0) {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
            } else if (i < this.stringListPruefungsformen.size()) {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            } else {
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            }
            subtable.addCell(cell);
        }

        cell = new PdfPCell(subtable);
        cell.setColspan(3);
        table.addCell(cell);*/

/*--    }

    public void CreatePDFBody(PdfPTable table) {
        String text;
        
        spSemester = findSPSelection(spSemesterSelection, spYearSelection);
        spsId = spSemester.getSpsid();
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Stundenplaneintrag.findAllPlanD", Stundenplaneintrag.class);
        query.setParameter("stgang", courseSelection);
        query.setParameter("facName", facultySelection);
        query.setParameter("spsid", spsId);
        scheduleEntryList = query.getResultList();

//       List<Pruefplaneintrag> all = pruefungenEJB.findAll();
//        List<Stundenplaneintrag> all
//                = pruefungenEJB.findAllByPruefPeriodeAndStudiengang(
//                        currentPruefperiode, currentStudiengang);
//       PdfPCell cell = table.getDefaultCell();
        /*
        System.out.println("Padding " + "top " + cell.getPaddingTop() + 
                                        "bottom" + cell.getPaddingBottom() +
                "left " + cell.getPaddingLeft() + 
                "right " + cell.getPaddingRight() );
        System.out.println(
                ">width "   + cell.getBorderWidthLeft()
                + "top "   + cell.getBorderWidthTop()
                + "bottom "+ cell.getBorderWidthBottom()
                + "left "  + cell.getBorderWidthLeft()
                + "right " + cell.getBorderWidthRight()
                );
         */
//        table.getDefaultCell().setPadding(2.0f);
//        table.getDefaultCell().setPaddingBottom(50.0f);

        //for (Stundenplaneintrag eintrag : scheduleEntryList) {
/*--    	for(int i = 0; i < scheduleEntryList.size(); i++){
        	this.spe = new Stundenplaneintrag();
        	spe = scheduleEntryList.get(i);
            /*
            System.out.println("Prüfung " + pruefung.getPPEDatZeit() + " " 
                               + pruefung.getSgmid().getModID().getModName() );
             */

/*--            Date startdatum = spe.getSPEStartZeit();
            //boolean renderDate = RenderDate(startdatum);
            //boolean withTopBorder = renderDate;

            // Datum
            text = "";
            /*if (renderDate) {
                text += new SimpleDateFormat("dd.MM.yyyy (EEEE)",
                        Locale.GERMAN).format(startdatum);
            }*/
/*--            text += new SimpleDateFormat("dd.MM.yyyy (EEEE)", Locale.GERMAN).format(startdatum);
            table.addCell(CreateBodyCell(text, Element.ALIGN_LEFT, 1));

            // Prüfung kurz
            text = "";
            text += spe.getSgmodul().getModul().getModKuerzel();


            table.addCell(CreateBodyCell(text, Element.ALIGN_LEFT, 1));

            // Prüfung lang
            text = "";
            text += spe.getSgmodul().getModul().getModName();

            table.addCell(CreateBodyCell(text, Element.ALIGN_LEFT, 1));;

            // Prüfer
            /*text = "";
            text += eintrag.getErstPruefID().getPrName();
            if (eintrag.getZweitPruefID() != null) {
                text += " / ";
                text += eintrag.getZweitPruefID().getPrName();
            }

            table.addCell(CreateBodyCell(text, Element.ALIGN_LEFT, 1));*/

            // Form
            /*text = "";
            text += eintrag.getPfid().getPForm().substring(0, 1);

            table.addCell(CreateBodyCell(text, Element.ALIGN_CENTER, 1));*/

            // Dauer
/*--            text = "";
            text += spe.getLehrveranstaltungsart().getLvdauer();

            table.addCell(CreateBodyCell(text, Element.ALIGN_CENTER, 1));

        }
    }

    public PdfPCell CreateBodyCell(String text, int Alignment, int Colspan) {

        PdfPCell cell;

        Font font = FontFactory.getFont("Arial", 10, Font.NORMAL);
        cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(Colspan);
        cell.setHorizontalAlignment(Alignment);

        return cell;
    }

    public PdfPCell CreateHeaderCell(String text, int Alignment, int Colspan) {

        PdfPCell cell;
        Font font = FontFactory.getFont("Arial", 10, Font.BOLD);
        cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(Colspan);
        cell.setHorizontalAlignment(Alignment);

        return cell;
    }

    public void CreatePDFHeader(PdfPTable table) {

        String text;

        //-----------------------------
        // ERSTE Zeile
        // Stand
        text = "Version: " + new SimpleDateFormat("dd.MM.yyyy",
                Locale.GERMAN).format(new Date());
        table.addCell(CreateHeaderCell(text, Element.ALIGN_RIGHT, 6));

        //-----------------------------
        // ZWEITE Zeile
        // Fachbereich
        text = "" + facultySelection;
        table.addCell(CreateHeaderCell(text, Element.ALIGN_LEFT, 2));
        // Studiengang
        text = "" + courseSelection;
        table.addCell(CreateHeaderCell(text, Element.ALIGN_LEFT, 2));

        // Semester Jahr Termin
        text = "";
        text += spSemesterSelection;
        text += " ";
        text += spYearSelection;

        //text += "        ";
        //if (currentPruefperiode.getPruefTermin().startsWith("T1")) {
        //    text += "1. Termin";
        //} else {
        //    text += "2. Termin";
        //}
        //table.addCell(CreateHeaderCell(text, Element.ALIGN_LEFT, 1));

        // Startdatum - Enddatum
        text = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(
                spSemester.getStartDatum());
        text += " - ";
        text += new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(
                spSemester.getEndDatum());
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 3));

        //-----------------------------
        // DRITTE Zeile
        // Datum
        text = "Datum";
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 1));

        // Fach
        text = "Modul";
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 2));

        // Prüfer
        text = "Prüfer";
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 1));

        // Form
        text = "Form";
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 1));

        // Dauer
        text = "Dauer";
        table.addCell(CreateHeaderCell(text, Element.ALIGN_CENTER, 1));

        // Zeit
        //text = "Zeit";
        //table.addCell( CreateHeaderCell(text, Element.ALIGN_CENTER, 1));
        // Raum
        //text = "Raum";
        //table.addCell( CreateHeaderCell(text, Element.ALIGN_CENTER, 1));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        
    }--*/
	
	
}
