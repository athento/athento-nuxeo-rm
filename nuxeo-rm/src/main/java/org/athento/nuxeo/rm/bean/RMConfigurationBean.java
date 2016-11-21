package org.athento.nuxeo.rm.bean;

import static org.athento.nuxeo.rm.api.utils.ArchivoConstantes.HORA_EJECUCION_SCHEDULER_VOCABULARIO;
import static org.athento.nuxeo.rm.api.utils.ArchivoConstantes.MINUTO_EJECUCION_SCHEDULER_VOCABULARIO;
import static org.athento.nuxeo.rm.api.utils.ArchivoConstantes.VOCABULARIO_CONFIGURACION_RM;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.yerbabuena.athento.ecm.utils.vocabularies.AthentoNXVocabularies;
/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
/**
 * --------------------------------------------------------
 */

/**
 * Bean de panel de configuraci&oacute;n.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
@Name("rmConfiguration")
@Scope(ScopeType.PAGE)
public class RMConfigurationBean implements Serializable {

	/**
	 * Numero de serializacion.
	 */
	private static final long serialVersionUID = 3392519305416190920L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(RMConfigurationBean.class);

	/**
	 * Hora de ejecuci&oacute;n de las tareas programadas.
	 */
	private String horaScheduler;

	/**
	 * Minuto de ejecuci&oacute;n de las tareas programadas.
	 */
	private String minutoScheduler;

	/**
	 * Utilizado para mostrar mensajes de informacion por pantalla.
	 */
	@In(create = true, required = false)
	private transient FacesMessages facesMessages;

	/**
	 * Acceso a los recursos de Nuxeo.
	 */
	@In(create = true)
	private ResourcesAccessor resourcesAccessor;

	/**
	 * Getter para horaScheduler.
	 * 
	 * @return horaScheduler
	 */
	public final String getHoraScheduler() {
		return horaScheduler;
	}

	/**
	 * Setter para horaScheduler.
	 * 
	 * @param horaScheduler
	 *            valor para horaScheduler
	 */
	public final void setHoraScheduler(String horaScheduler) {
		this.horaScheduler = horaScheduler;
	}

	/**
	 * Getter para minutoScheduler.
	 * 
	 * @return minutoScheduler
	 */
	public final String getMinutoScheduler() {
		return minutoScheduler;
	}

	/**
	 * Setter para minutoScheduler.
	 * 
	 * @param minutoScheduler
	 *            valor para minutoScheduler
	 */
	public final void setMinutoScheduler(String minutoScheduler) {
		this.minutoScheduler = minutoScheduler;
	}

	/**
	 * M&eacute;todo que guarda la configuraci&oacute;n de la hora y minuto de ejecuci&oacute;n de
	 * los schedulers.
	 *  
	 */
	public void guardarEjecucionScheduler() {
		/* Conflicto con Checkstyle. No se pueden declarar como final los m&eacute;todos de
		 * beans EJB que hagan uso de dependencias inyectadas, ya que dichas
		 * dependencias toman el valor null.
		 */
		try {
			if (horaScheduler != null && !horaScheduler.isEmpty()
					&& minutoScheduler != null && !minutoScheduler.isEmpty()) {
				AthentoNXVocabularies vocabularios = new AthentoNXVocabularies();
				vocabularios.updateEntry(VOCABULARIO_CONFIGURACION_RM,
						HORA_EJECUCION_SCHEDULER_VOCABULARIO, horaScheduler);
				vocabularios
				.updateEntry(VOCABULARIO_CONFIGURACION_RM,
						MINUTO_EJECUCION_SCHEDULER_VOCABULARIO,
						minutoScheduler);
				facesMessages.add(
						StatusMessage.Severity.INFO,
						resourcesAccessor.getMessages().get(
								"label.rm.configuracion.ok"));
			} else {
				LOG.error("Debe introducir valores no vac&iacute;os");
				facesMessages.add(
						StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get(
								"label.rm.configuracion.empty.fields"));
			}
		} 
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha excepci&oacute;n.
		 * En caso contrario, este c&oacute;digo no compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error(
					"No se pudo guardar la configuraci&oacute;n del momento de ejecuci&oacute;n de los Schedulers ",
					e);
			facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor
					.getMessages().get("label.rm.configuracion.ko"));
		}
	}

	/**
	 * Inicializaci&oacute;n del bean.
	 */
	@Create
	public final void iniciarBean() {

		try {
			AthentoNXVocabularies vocabularios = new AthentoNXVocabularies();
			horaScheduler = vocabularios.getEntryLabelFromDirectory(
					VOCABULARIO_CONFIGURACION_RM,
					HORA_EJECUCION_SCHEDULER_VOCABULARIO);
			minutoScheduler = vocabularios.getEntryLabelFromDirectory(
					VOCABULARIO_CONFIGURACION_RM,
					MINUTO_EJECUCION_SCHEDULER_VOCABULARIO);
		} 
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha excepci&oacute;n.
		 * En caso contrario, este c&oacute;digo no compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("Error iniciando bean de configuraci&oacute;n del RM ", e);
		}

	}

}
