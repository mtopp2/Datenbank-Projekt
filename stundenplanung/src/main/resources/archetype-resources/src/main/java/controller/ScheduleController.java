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
	
	
	private Date SPEStartZeit;
	private Date SPEEndZeit;
	private int SPTermin;
	private int studentNumber;
	private Timestamp timeStamp;
	
	TimeZone time_zone_default = TimeZone.getDefault(); 
	
	private ScheduleModel eventModel;
    private ScheduleModel lazyEventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Date weekstart;
    
    private List<Stundenplaneintrag> eintragListe;
    private Stundenplaneintrag selectedEvent;
    private Stundenplaneintrag spe;
	
	ArrayList<String> LehrveranstaltungsListe = new ArrayList<>();
    private String lvname;
    
    ArrayList<Integer> RaumListe = new ArrayList<>();
    private int rid;
    
    ArrayList<Integer> SgmodulListe = new ArrayList<>();
    private int sgmid;
    
    ArrayList<Integer> semesterList = new ArrayList<>();
    private int semester;
    private int semesterSelection;
    
    ArrayList<String> studiengangList = new ArrayList<>();
    private String studiengangSelection;
    
    ArrayList<String> facultyList = new ArrayList<>();
    private String facultySelection;
    
    ArrayList<Sgmodul> sgsemModulList = new ArrayList<>();
    ArrayList<String> modulList = new ArrayList<>();
    
    Calendar cal;
    Calendar t;
    Calendar t2;
    LocalDateTime l;
    LocalDateTime l2;
    
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
	        q.setParameter("SGName", studiengangSelection);
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
            query.setParameter("stgang", studiengangSelection);
            eintragListe = query.getResultList();
        }
        catch(Exception e){}
        
        for(int i = 0; i < eintragListe.size(); i++){
        	this.spe = new Stundenplaneintrag();
        	spe = eintragListe.get(i);
            //Eventeinträge
            dayOffset = spe.getSPEStartZeit().getDay()-1;
            
            t = (Calendar) cal.clone();
            //Zeiten
            t.add(Calendar.DATE, dayOffset);
            t.set(Calendar.HOUR_OF_DAY, spe.getSPEStartZeit().getHours());
            t.set(Calendar.MINUTE, spe.getSPEStartZeit().getMinutes());
            t.set(Calendar.SECOND, spe.getSPEStartZeit().getSeconds());
            LocalDateTime ltime = convertToLocalDateTimeViaInstant(t.getTime());
            
            t2 = (Calendar) t.clone();
            t2.set(Calendar.HOUR_OF_DAY, spe.getSPEEndZeit().getHours());
            t2.set(Calendar.MINUTE, spe.getSPEEndZeit().getMinutes());
            t2.set(Calendar.SECOND, spe.getSPEEndZeit().getSeconds());
            LocalDateTime ltime2 = convertToLocalDateTimeViaInstant(t2.getTime());
        	
            
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
            LehrveranstaltungsListe.add(lva.getLvname());
        }
        
        EntityManager em2 = emf.createEntityManager();
        Query r = em2.createNamedQuery("Raum.findAll");
        List RList = r.getResultList();
        for (Object RListitem : RList)
        {
        	Raum ra =(Raum)RListitem;
            RaumListe.add(ra.getRid());
        }
        
        EntityManager em3 = emf.createEntityManager();
        Query s = em3.createNamedQuery("Sgmodul.findAll");
        List SList = s.getResultList();
        for (Object SListitem : SList)
        {
        	Sgmodul sg =(Sgmodul)SListitem;
            SgmodulListe.add(sg.getSgmid());
        }
		
		EntityManager em4 = emf.createEntityManager();
        Query s1 = em4.createNamedQuery("Studiengang.findAll");
        List S1List = s1.getResultList();
        for (Object S1Listitem : S1List)
        {
        	Studiengang sg1 =(Studiengang)S1Listitem;
            studiengangList.add(sg1.getSGName());
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
        studiengangSelection = studiengangList.get(0);

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
                selectedEvent.setSPEStartZeit(asDate(event.getStartDate()));
                selectedEvent.setSPEEndZeit(asDate(event.getEndDate()));
                selectedEvent.setSPTermin(SPTermin);
                selectedEvent.setSgmodul(findSgm(sgmid));
                selectedEvent.setLehrveranstaltungsart(findLva(lvname));
                selectedEvent.setRaum(findRau(rid));
                selectedEvent.setStudierendenzahl(studentNumber);
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                selectedEvent.setZeitStempel(timeStamp);
                
                
                eventLoader();

                
                ut.begin();
            	em.joinTransaction();
                em.persist(selectedEvent);
                ut.commit();
                msg = "Ereignis wurde hinzugefügt!";                
                
            }
            else{/* ID schon vorhanden*/
                lazyEventModel.updateEvent(getEvent());
                ut.begin();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                
                selectedEvent.setSPEStartZeit(selectedEvent.getSPEStartZeit());
                selectedEvent.setSPEEndZeit(selectedEvent.getSPEEndZeit());
                selectedEvent.setSPTermin(selectedEvent.getSPTermin());
                selectedEvent.setSgmodul(findSgm(sgmid));
                selectedEvent.setLehrveranstaltungsart(findLva(lvname));
                selectedEvent.setRaum(findRau(rid));
                selectedEvent.setStudierendenzahl(selectedEvent.getStudierendenzahl());
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                
                selectedEvent.setZeitStempel(timeStamp);
                em.merge(selectedEvent);
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
        q.setParameter("spid", selectedEvent.getSpid());
        selectedEvent = (Stundenplaneintrag)q.getSingleResult();
        
        try{
            ut.begin();
	        em.joinTransaction();  
            em.remove(selectedEvent);
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
        selectedEvent = (Stundenplaneintrag) event.getData();
        
        sgmid = selectedEvent.getSgmodul().getSgmid();
        lvname = selectedEvent.getLehrveranstaltungsart().getLvname();
        rid = selectedEvent.getRaum().getRid();
    }
    
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        this.selectedEvent = new Stundenplaneintrag();
        
        l = selectEvent.getObject();
        l2 = selectEvent.getObject().plusHours(1);
        
        event = DefaultScheduleEvent.builder()
                .title("")
                .startDate(l)
                .endDate(l2)
                .data(selectedEvent)
                .build();
        
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());
        String msg1 = "Event moved."; 
        addMessage("messages", msg1);
        
        selectedEvent = (Stundenplaneintrag) event.getScheduleEvent().getData();
        SPEStartZeit = selectedEvent.getSPEStartZeit();
        SPEEndZeit = selectedEvent.getSPEEndZeit();
        
        
        long dura = event.getDeltaAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = SPEStartZeit.getTime() + dura;
        Date date1 = new Date(sum);
        
        long sum1 = SPEEndZeit.getTime() + dura;
        Date date2 = new Date(sum1);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                ut.begin();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                selectedEvent.setSPEStartZeit(date1);
                selectedEvent.setSPEEndZeit(date2);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                selectedEvent.setZeitStempel(timeStamp);
                em.merge(selectedEvent);
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
        selectedEvent = (Stundenplaneintrag) event.getScheduleEvent().getData();
        SPEStartZeit = selectedEvent.getSPEStartZeit();
        SPEEndZeit = selectedEvent.getSPEEndZeit();
        
        
        long dura = event.getDeltaEndAsDuration().getSeconds();
        dura = dura * 1000;
        long sum = SPEEndZeit.getTime() + dura;
        Date date1 = new Date(sum);
        
        String msg;
        EntityManager em;
        em = emf.createEntityManager();
        
        try{
                ut.begin();
                em.find(Stundenplaneintrag.class, selectedEvent.getSpid());
                selectedEvent.setSPEEndZeit(date1);
                //Zeitstempel
                Date date= new Date();
                long time = date.getTime();
                timeStamp = new Timestamp(time);
                selectedEvent.setZeitStempel(timeStamp);
                em.merge(selectedEvent);
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
    
    public ArrayList<String> getLehrveranstaltungsListe() {
		return LehrveranstaltungsListe;
	}
	public void setLehrveranstaltungsListe(ArrayList<String> lehrveranstaltungsListe) {
		LehrveranstaltungsListe = lehrveranstaltungsListe;
	}
	public String getLvname() {
		return lvname;
	}
	public void setLvname(String lvname) {
		this.lvname = lvname;
	}
	
	//--------------------------------------------------------------
	
	public ArrayList<Integer> getRaumListe() {
		return RaumListe;
	}
	public void setRaumListe(ArrayList<Integer> raumListe) {
		RaumListe = raumListe;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	
	//--------------------------------------------------------------
    
	public ArrayList<Integer> getSgmodulListe() {
		return SgmodulListe;
	}
	public void setSgmodulListe(ArrayList<Integer> sgmodulListe) {
		SgmodulListe = sgmodulListe;
	}
	
	public int getSgmid() {
		return sgmid;
	}
	public void setSgmid(int sgmid) {
		this.sgmid = sgmid;
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
	
	public Date getSPEStartZeit() {
		return SPEStartZeit;
	}
	public void setSPEStartZeit(Date SPEStartZeit) {
		this.SPEStartZeit = SPEStartZeit;
	}
	
	public Date getSPEEndZeit() {
		return SPEEndZeit;
	}
	public void setSPEEndZeit(Date SPEEndZeit) {
		this.SPEEndZeit = SPEEndZeit;
	}
	
	public int getSPTermin() {
		return SPTermin;
	}
	public void setSPTermin(int SPTermin) {
		this.SPTermin = SPTermin;
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
    
    public Stundenplaneintrag getSelectedEvent() {
		return selectedEvent;
	}
	public void setSelectedEvent(Stundenplaneintrag selectedEvent) {
		this.selectedEvent = selectedEvent;
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

	public ArrayList<String> getStudiengangList() {
		return studiengangList;
	}

	public void setStudiengangList(ArrayList<String> studiengangList) {
		this.studiengangList = studiengangList;
	}

	public String getStudiengangSelection() {
		return studiengangSelection;
	}

	public void setStudiengangSelection(String studiengangSelection) {
		this.studiengangSelection = studiengangSelection;
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


	public List<Stundenplaneintrag> getEintragListe() {
		return eintragListe;
	}


	public void setEintragListe(List<Stundenplaneintrag> eintragListe) {
		this.eintragListe = eintragListe;
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

	public LocalDateTime getL() {
		return l;
	}

	public void setL(LocalDateTime l) {
		this.l = l;
	}

	public LocalDateTime getL2() {
		return l2;
	}

	public void setL2(LocalDateTime l2) {
		this.l2 = l2;
	}

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
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

	public Calendar getT() {
		return t;
	}

	public void setT(Calendar t) {
		this.t = t;
	}

	public Calendar getT2() {
		return t2;
	}

	public void setT2(Calendar t2) {
		this.t2 = t2;
	}

	public TimeZone getTime_zone_default() {
		return time_zone_default;
	}

	public void setTime_zone_default(TimeZone time_zone_default) {
		this.time_zone_default = time_zone_default;
	}

}
