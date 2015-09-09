package de.szut.dqi12.cheftrainer.client.controlling;

//import de.szut.dqi12.cheftrainer.client.gamemanagement.League;

public class Controller {
	
	private static Controller INSTANCE = null;
//	private League leauge;
	
    public static Controller getInstance() {
        //Wenn INSTANCE = NULL ist, wird ihm ein neues Controller-Objekt zugewiesen. Dieses wird zurueckgegeben.
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }
    
//    public void fillCommunitiesTable(){
//    	if(leauge != null){
//    		for(int i = 0; i < leauge.getUsers().size(); i++){
//    			new Team(leauge.getUsers().get(i).getName())
//    		}
//    	}
//    }
}
