package es.rm.platform.api.utils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * Clase con utilidades relacionadas con Quartz.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public class UtilidadesQuartz {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(UtilidadesQuartz.class);

	/**
	 * Borra un trigger de BD/memoria.
	 * 
	 * @param scheduler
	 *            manejador de schedulers
	 * @param scheduleId
	 *            id del trigger
	 * @param grupo
	 *            grupo del trigger
	 */
	public static void borrarTrigger(Scheduler scheduler, String scheduleId,
			String grupo) {
		try {
			scheduler
					.deleteJob(scheduleId, ArchivoConstantes.GRUPO_TRIGGERS_RM);
		} catch (SchedulerException e) {
			LOG.error(String.format(
					"Fall&oacute; el borrado del trigger con '%s': %s",
					scheduleId, e.getMessage()), e);
		}

	}

	/**
	 * Registra un scheduler.
	 * 
	 * @param schedule
	 *            modelado de scheduler para archivo
	 * @param parameters
	 *            par&aacute;metros
	 * @param scheduler
	 *            manejador de schedulers
	 */
	public static void registrarScheduler(ArchivoScheduleImpl schedule,
			Map<String, Serializable> parameters, Scheduler scheduler) {
		JobDetail job = new JobDetail(schedule.getId(),
				ArchivoConstantes.GRUPO_TRIGGERS_RM, ArchivoJob.class);
		JobDataMap map = job.getJobDataMap();
		map.put("eventId", schedule.getEventId());
		map.put("eventCategory", schedule.getEventCategory());
		map.put("username", schedule.getUsername());
		map.put("docId", schedule.getDocId());

		if (parameters != null) {
			map.putAll(parameters);
		}

		Trigger trigger;
		try {
			trigger = new CronTrigger(schedule.getId(),
					ArchivoConstantes.GRUPO_TRIGGERS_RM,
					schedule.getCronExpression());
			// indicamos el nombre del calendario.
			trigger.setCalendarName(schedule.getEventId() + "-"
					+ schedule.getDocId());
		} catch (ParseException e) {
			LOG.error(
					String.format(
							"Expresi&oacute;n cron inv&aacute;lida '%s' para el 'schedule' '%s'",
							schedule.getCronExpression(), schedule.getId()), e);
			return;
		}
		LOG.debug("Trigger:" + trigger);

		try {
			// Inicia scheduler
			scheduler.scheduleJob(job, trigger);

		} catch (ObjectAlreadyExistsException e) {
			LOG.warn(String.format(
					"El trigger era persistente y ya se ha lanzado. Id : '%s'",
					schedule.getId())); // Cuando los triggers est&aacute;n
										// persistentes en base de datos, pueden
										// estar ya lanzados
		} catch (SchedulerException e) {
			LOG.error(
					String.format(
							"Fall&oacute; la programaci&oacute;n del trigger con id: '%s': %s",
							schedule.getId(), e.getMessage()), e);
		}
	}

	/**
	 * Registra una nueva tarea programada (scheduler).
	 * 
	 * @param schedule
	 *            Modelo de Scheduler
	 * @param parameters
	 *            parametros auxiliares
	 * @param scheduler
	 *            instancia del scheduler
	 */
	public static void registrarSchedule(ArchivoScheduleImpl schedule,
			Map<String, Serializable> parameters, Scheduler scheduler) {
		LOG.info("Registering " + schedule);
		registrarScheduler(schedule, parameters, scheduler);
	}

	/**
	 * Este m&eacute;todo permite lanzar y almacenar una tarea (Scheduler) para
	 * una fecha (o expresi&oacute;n cron) determinada. Es decir, cuando se
	 * cumpla la fecha pasada como par&aacute;metro, autom&aacute;ticamente se
	 * lanzar&aacute; un evento que podr&aacute; ser capturado por Listeners
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param fechaFinRetencion
	 *            fecha fin de retenci&oacute;n
	 * @param documentoAsociado
	 *            documento asociado
	 * @param scheduler
	 *            instancia del scheduler
	 */
	public static void programarScheduler(CoreSession documentManager,
			Calendar fechaFinRetencion, DocumentModel documentoAsociado,
			Scheduler scheduler) {
		try {
			int dayOfMonth = fechaFinRetencion.get(Calendar.DATE);
			int month = fechaFinRetencion.get(Calendar.MONTH);
			int year = fechaFinRetencion.get(Calendar.YEAR);
			// Obtenemos horas y minutos de la fecha de fin de retencion para
			// incrementos
			int deltaHoras = fechaFinRetencion.get(Calendar.HOUR_OF_DAY);
			int deltaMinutos = fechaFinRetencion.get(Calendar.MINUTE);
			// Incrementamos en 1 porque en la clase Calendar los meses van de 0
			// a 11
			month++;
			String horaEjecucionConfig = UtilidadesArchivo
					.getEntryLabelFromDirectory(
							ArchivoConstantes.VOCABULARIO_CONFIGURACION_RM,
							ArchivoConstantes.HORA_EJECUCION_SCHEDULER_VOCABULARIO);
			String minutoEjecucionConfig = UtilidadesArchivo
					.getEntryLabelFromDirectory(
							ArchivoConstantes.VOCABULARIO_CONFIGURACION_RM,
							ArchivoConstantes.MINUTO_EJECUCION_SCHEDULER_VOCABULARIO);

			// Sumamos los deltas de horas y minutos a las que contiene la fecha
			// de fin de retencion
			Calendar fechaConIncrementos = Calendar.getInstance();
			int horaEjecucion = 0;
			try {
				horaEjecucion = Integer.valueOf(horaEjecucionConfig);
			} catch (NumberFormatException e) {
				LOG.warn("Imposible calcular la hora de ejecucion por error de formato");
			}
			fechaConIncrementos.set(Calendar.HOUR_OF_DAY, horaEjecucion);
			int minutoEjecucion = 0;
			try {
				minutoEjecucion = Integer.valueOf(minutoEjecucionConfig);
			} catch (NumberFormatException e) {
				LOG.warn("Imposible calcular el minuto de ejecucion por error de formato");
			}
			fechaConIncrementos.set(Calendar.MINUTE, minutoEjecucion);
			// Incrementamos con deltas
			fechaConIncrementos.add(Calendar.HOUR_OF_DAY, deltaHoras);
			fechaConIncrementos.add(Calendar.MINUTE, deltaMinutos);
			// Construimos el scheduler con fechas incrementadas
			String id = ArchivoConstantes.PREFIJO_TIRGGERS + "-"
					+ documentoAsociado.getId();
			ArchivoScheduleImpl schedule = UtilidadesArchivo
					.construyeModeloScheduler(documentManager,
							documentoAsociado, dayOfMonth, month, year, String
									.valueOf(fechaConIncrementos
											.get(Calendar.HOUR_OF_DAY)), String
									.valueOf(fechaConIncrementos
											.get(Calendar.MINUTE)), id);

			registrarSchedule(schedule, null, scheduler);
			LOG.info("Tarea programada con id: " + id);
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("Error programando Scheduler: ", e);
		}
	}

	/**
	 * Este m&eacute;todo elimina de memoria/bd una tarea programada
	 * (scheduler).
	 * 
	 * @param scheduleId
	 *            es el id del scheduler
	 * @param scheduler
	 *            instancia del scheduler
	 */
	public static void pararScheduler(String scheduleId, Scheduler scheduler) {
		LOG.info("Borrando tarea programada con id:" + scheduleId
				+ " y grupo : " + ArchivoConstantes.GRUPO_TRIGGERS_RM);
		borrarTrigger(scheduler, scheduleId,
				ArchivoConstantes.GRUPO_TRIGGERS_RM);
	}
}
