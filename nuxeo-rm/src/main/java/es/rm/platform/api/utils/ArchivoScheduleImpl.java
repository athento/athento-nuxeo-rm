package es.rm.platform.api.utils;

import org.nuxeo.ecm.platform.scheduler.core.service.ScheduleImpl;

/**
 * Modelo espec&iacute;fico para Archivo que implementa el modelo de Scheduler de Nuxeo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
public class ArchivoScheduleImpl extends ScheduleImpl{
	
	/**
	 * Id del documento.
	 */
	private String docId;
	
	/**
	 * Setter para el parametro id.
	 * 
	 * @param id valor a asignar
	 */
	public final void setId(String id){
		super.id = id;
	}
	
	/**
	 * Setter para la categor&iacute;a del evento.
	 * 
	 * @param eventCategory categor&iacute;a del evento a asignar
	 */
	public final void setEventCategory(String eventCategory){
		super.eventCategory = eventCategory;
	}
	
	/**
	 * Setter para la expresi&oacute;n cron.
	 * 
	 * @param cronExpression expresi&oacute;n cron a asignar
	 */
	public final void setCronExpression(String cronExpression){
		super.cronExpression = cronExpression;
	}
	
	/**
	 * Setter para el nombre de usuario.
	 * 
	 * @param userName nombre de usuario a asignar
	 */
	public final void setUserName(String userName){
		super.username = userName;
	}
	
	/**
	 * Setter para el id del documento.
	 * 
	 * @param docId id del documento a asignar
	 */
	public final void setDocId(String docId){
		this.docId = docId;
	}
	
	/**
	 * Getter para el id del documento.
	 * 
	 * @return valor del docId
	 */
	public final String getDocId(){
		return docId;
	}
}
