package chav1961.calc.interfaces;

import java.net.URI;

public interface PipeContainerControlInterface {
	URI getItemAssociated();
	void clearFires();
	int getCurrentFires();
	void fire(PipeContainerControlInterface another);
	int getRequiredFires();
	void sendFire();
}
