package net.floodlightcontroller.mactracker;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class MACTrackerWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/list/json",MACTrackerResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/vm/mactracker";
	}
	

}
