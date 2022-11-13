package net.floodlightcontroller.mactracker;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class MACTrackerResource extends ServerResource{
	@Get("json")
	public List<String> retrieve(){
		MACTrackerService macTracker = (MACTrackerService)getContext().getAttributes().get(MACTrackerService.class.getCanonicalName());
		List<String> l = new ArrayList<String>();
		l.addAll(java.util.Arrays.asList(macTracker.getBuffer().snapshot()));
		return l;
	}

}
