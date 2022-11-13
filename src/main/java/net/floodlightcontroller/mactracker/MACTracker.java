package net.floodlightcontroller.mactracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.restserver.RestApiServer;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;

public class MACTracker implements IOFMessageListener, IFloodlightModule, MACTrackerService {
	
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected ConcurrentCircularBuffer<String> buffer;
	protected IRestApiService restApi;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return MACTracker.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return name.equals("forwarding");
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService >> l =
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(MACTrackerService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService > m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();  
		m.put(MACTrackerService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService >> l =
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		macAddresses = new ConcurrentSkipListSet<Long>();
		restApi = context.getServiceImpl(IRestApiService.class);
		logger = LoggerFactory.getLogger(MACTracker.class);
		buffer = new ConcurrentCircularBuffer<String>(String.class, 1000);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new MACTrackerWebRoutable());
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		Ethernet eth =
				IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		Long sourceMACHash = eth.getSourceMACAddress().getLong();
		if(!macAddresses.contains(sourceMACHash)) {
			macAddresses.add(sourceMACHash);
			logger.info("MAC address: {} seen on switch {}",
					eth.getSourceMACAddress().toString(),
					sw.getId().toString());
			String info = "MAC address: "+ eth.getSourceMACAddress().toString() + " seen on switch " + sw.getId().toString(); 
			buffer.add(info);
		}
		return Command.CONTINUE;
	}

	@Override
	public ConcurrentCircularBuffer<String> getBuffer() {
		return buffer;
	}
	

}
