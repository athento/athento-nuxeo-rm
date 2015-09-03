package es.rm.platform.bean;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.security.Principal;

import javax.faces.context.FacesContext;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.nuxeo.ecm.webapp.dashboard.DashboardNavigationHelper;

/**
 * Clase que se encarga de redirigir la pagina despues del acceso.
 */
@Name("rmStartupHelper")
@Scope(SESSION)
@Install(precedence = Install.FRAMEWORK)
public class RmStartupHelper implements Serializable {

	/**
	 * Variable de serializacion.
	 */
	private static final long serialVersionUID = 3248972387619873245L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(RmStartupHelper.class);

	/**
	 * Vista por defecto.
	 */
	private static final String DOMAINS_VIEW = "view_domains";

	/**
	 * Atributo que representa la interfaz del servicio de repositorios.
	 */
	@In(create = true)
	private transient RepositoryManager repositoryManager;

	/**
	 * Atributo que representa el contexto de navegaci&oacute;n.
	 */
	@In(create = true)
	private transient NavigationContext navigationContext;

	/**
	 * Atributo que representa el Contexto de Nuxeo actual.
	 */
	@In
	private transient Context sessionContext;

	/**
	 * Clase de ayuda para el panel de navegacion.
	 */
	@In(create = true)
	private DashboardNavigationHelper dashboardNavigationHelper;

	/**
	 * Sesion actual del nucleo Nuxeo.
	 */
	@In(create = true, required = false)
	private transient CoreSession documentManager;

	/**
	 * Metodo que se encarga de redirigir a la pagina incial correspondiente.
	 * 
	 * Conflicto con Checkstyle. No se pueden declarar como final los m&eacute;todos de
	 * beans EJB que hagan uso de dependencias inyectadas, ya que dichas
	 * dependencias toman el valor null.
	 * 
	 * @return La cadena de texto que indica la pantalla inical.
	 */
	public String goHome() {
		String result = DOMAINS_VIEW;
		try {
			setupCurrentUser();

			// we try to select the server to go to the next screen
			if (navigationContext.getCurrentServerLocation() == null) {
				// update location
				RepositoryLocation repLoc = new RepositoryLocation(
						repositoryManager.getRepositories().iterator().next()
								.getName());
				navigationContext.setCurrentServerLocation(repLoc);
			}

			if (documentManager == null) {
				documentManager = navigationContext
						.getOrCreateDocumentManager();
			}
			result = dashboardNavigationHelper.navigateToDashboard();
		} catch (ClientException e) {
			LOG.error("Error redirigiendo a administracion: ", e);
		}
		return result;
	}

	/**
	 * Metodo que se encarga de configurar el usuario actual.
	 * 
	 * Conflicto con Checkstyle. No se pueden declarar como final los m&eacute;todos
	 * de beans EJB que hagan uso de dependencias inyectadas, ya que dichas
	 * dependencias toman el valor null.
	 * 
	 * @throws NullPointerException
	 *             excepci&oacute;n manejada en caso de error.
	 */
	public void setupCurrentUser() throws NullPointerException {
		Principal currentUser = FacesContext.getCurrentInstance()
				.getExternalContext().getUserPrincipal();
		sessionContext.set("currentUser", currentUser);
	}
}
