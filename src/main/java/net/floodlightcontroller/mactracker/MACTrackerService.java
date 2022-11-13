package net.floodlightcontroller.mactracker;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;

public interface MACTrackerService extends IFloodlightService {
	public ConcurrentCircularBuffer<String> getBuffer();

}
