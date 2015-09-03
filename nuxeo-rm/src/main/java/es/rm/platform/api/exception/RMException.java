/**
 * 
 */
package es.rm.platform.api.exception;

/**
 * Excepciones lanzadas por el RM.
 * @author victorsanchez
 *
 */
public class RMException extends Exception {

	/**
	 * UID.
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6997448800731833374L;

	/**
	 * Constructor.
	 */
	public RMException() {
		super();
	}

	/**
	 * Constructor.
	 * @param message mensaje del error
	 * @param cause causa del  error
	 */
	public RMException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * @param message mensaje del error
	 */
	public RMException(final String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param cause causa de error
	 */
	public RMException(final Throwable cause) {
		super(cause);
	}

}
