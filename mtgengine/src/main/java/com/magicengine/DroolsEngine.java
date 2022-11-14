package com.magicengine;

import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsEngine {

	private KieSession ksession;
	private Agenda agenda;

	public DroolsEngine() {
		// load up the knowledge base
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		this.ksession = kContainer.newKieSession("ksession-rules");
		/*KieSessionConfiguration kSessionConf = ksession.getSessionConfiguration();
		kSessionConf.setProperty("type", "stateless");
		*/
		agenda = ksession.getAgenda();
	}

	public FactHandle addToDroolsEngine(Object obj) {
		return ksession.insert(obj);
	}

	public void fireAllRules() {
		ksession.fireAllRules();
	}

	public void setFocusAgenda(String group) {
		agenda.getAgendaGroup(group).setFocus();
	}

	public KieSession getKSession() {
		return ksession;

	}
	
	public void updateGame(FactHandle fh, Game g){
		ksession.update(fh, g);
	}
}
