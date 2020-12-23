package controller;

import model.Account;
import model.Benutzergruppe;
import model.Modul;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		

		/*
		//Account Account = new Account(6,"us", "1234", "a@a.com", 2, "Nobody", "NOB, 32");
		
		Account Account = new Account();
		Benutzergruppe bg = new Benutzergruppe();
		
		Account.setAccID(1);
		Account.setAccName("Manu");
		Account.setAccName("Manu");
		Account.setAccName("m@m");
		
		bg.setGroupID((byte)1);
		bg.setBGName("Nobody");
		bg.setBGRechte((byte)32);
		bg.setBGShortName("NOB");
		
		
		
		Account.setBenutzergruppe(bg);
		
		System.out.println("Benutzergruppe " + Account.getBenutzergruppe());
		
		List<Modul> modliste;
		
		modliste = modlist;*/
		
		


         //Date object
		 Date date= new Date();
         //getTime() returns current time in milliseconds
		 long time = date.getTime();
         //Passed the milliseconds to constructor of Timestamp class 
		 Timestamp ts = new Timestamp(time);
		 System.out.println("Current Time Stamp: "+ts);

		
	}

}
