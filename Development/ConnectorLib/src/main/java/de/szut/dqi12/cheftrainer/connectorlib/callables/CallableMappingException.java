package de.szut.dqi12.cheftrainer.connectorlib.callables;

/**
 * Exception, which should be used, when something with the ID <-> Class mapping fails.
 * @author Alexander Brennecke
 *
 */
public class CallableMappingException extends Exception {

	private static final long serialVersionUID = -8528870042800486787L;

	public CallableMappingException() {
		super();
	}

	public CallableMappingException(String message) {
		super(message);
	}

	public CallableMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallableMappingException(Throwable cause) {
		super(cause);
	}
}
