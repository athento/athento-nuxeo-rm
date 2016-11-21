package org.athento.nuxeo.rm.core.services;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.rm.api.exception.RMException;
import org.athento.nuxeo.rm.api.services.FlujoTrabajoDescriptor;
import org.athento.nuxeo.rm.api.utils.ArchivoConstantes;
import org.athento.nuxeo.rm.api.utils.DispositivoDto;
import org.athento.nuxeo.rm.api.utils.UtilidadesArchivo;
import org.athento.nuxeo.rm.api.utils.UtilidadesQuartz;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import org.athento.nuxeo.rm.api.services.ArchivoServicio;

/**
 * 
 * Implementacion de la interfaz ArchivoServicio.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public class ArchivoServicioImpl extends DefaultComponent implements
		ArchivoServicio {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(ArchivoServicioImpl.class);
	/** Constante TRANSFERENCIA. */
	private static final String TRANSFERENCIA = "Transferencia";
	/** Constante ELIMINACION. */
	private static final String ELIMINACION = "Eliminacion";

	/** Constante Numero 1. */
	private static final int CONT_1 = 1;

	/**
	 * Flujos de trabajo.
	 */
	private Map<String, FlujoTrabajoDescriptor> flujos;

	/**
	 * Programador de tareas.
	 */
	private Scheduler scheduler;

	/**
	 * Activacion del componente.
	 * 
	 * @param context
	 *            (contexto) de activacion
	 * @exception Exception
	 *                excepcion lanzada en caso de error
	 */
	@Override
	public final void activate(final ComponentContext context)  {
		/*
		 * Conflicto con checkstyle. El m&eacute;todo debe lanzar la
		 * excepci&oacute;n Exception ya que hereda de una clase externa que la
		 * declara.
		 */
		LOG.info("Arrancando el servicio para Archivo...");
		this.flujos = new HashMap<String, FlujoTrabajoDescriptor>();
	}

	/**
	 * Una vez arrancada la aplicacion, inicializamos las variables necesarias.
	 * 
	 * @param context
	 *            Contexto del componente
	 * @exception Exception
	 *                excepci&oacute;n controlada en caso de error
	 */
	@Override
	public final void applicationStarted(final ComponentContext context) {
		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			this.scheduler = schedulerFactory.getScheduler();
			if (this.scheduler.isShutdown()) {
				this.scheduler.start();
			}
			LOG.info("Archivo Servicio iniciado");
		} catch (SchedulerException e) {
			LOG.error("Scheduler error in Archivo service", e);
		}
	}

	/**
	 * Registro de la contribucion.
	 * 
	 * @param contribution
	 *            a registrar
	 * @param extensionPoint
	 *            en el que registrar la contribucion
	 * @param contributor
	 *            de la contribucion
	 */
	@Override
	public final void registerContribution(final Object contribution,
			final String extensionPoint, final ComponentInstance contributor) {
		LOG.debug("registrando contribucion...");
		if (ArchivoConstantes.WORKFLOW_EP.equals(extensionPoint)) {
			FlujoTrabajoDescriptor workflow = (FlujoTrabajoDescriptor) contribution;
			this.flujos.put(workflow.getIdentificador(), workflow);
		}
	}

	/**
	 * Metodo que devuelve todos los workflows definidos para el RM.
	 * 
	 * @return ids de los flujos de trabajo
	 */
	@Override
	public final List<String> extraerIdsFlujosTrabajo() {
		List<String> resultado = UtilidadesArchivo.getNuevaListaVacia();
		for (String clave : this.flujos.keySet()) {
			resultado.add(clave);
		}
		return resultado;
	}

	/**
	 * Metodo que devuelve todos los eventos definidos para el RM.
	 * 
	 * @return eventos extra&iacute;dos
	 */
	@Override
	public final List<String> extraerEventos() {
		// FIXME TODO
		return null;
	}

	/**
	 * Metodo que devuelve las posibles fases para asociar a un Calendario de
	 * Conservacion.
	 * 
	 * @return frases extra&iacute;das
	 */
	@Override
	public final List<String> extraerFases() {
		// FIXME: Segun los requisitos son estas 2, pero quiza habr&iacute;a que
		// generalizarlo.
		List<String> fases = UtilidadesArchivo.getNuevaListaVacia();
		fases.add(ArchivoConstantes.FASE_ARCHIVO);
		fases.add(ArchivoConstantes.FASE_VIGENCIA);
		return fases;
	}

	/**
	 * 
	 * Metodo que ejecuta la accion de cerrar una CAdD.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            es el Documento carpeta a cerrar
	 * @return true si la carpeta se ha cerrado correctamente; false en caso
	 *         contrario.
	 */
	@Override
	public final boolean cerrarCarpeta(final CoreSession documentManager,
			final DocumentModel carpeta) {
		boolean result = false;
		try {
			LOG.info("Cerrando carpeta " + carpeta.getName());
			if (carpeta.getType().equals(ArchivoConstantes.TIPO_EXPEDIENTE)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_REA)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

				carpeta.setProperty(ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
						ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS);
				documentManager.saveDocument(carpeta);

				String cicloDeVida = carpeta.getCurrentLifeCycleState();
				result = cicloDeVida.equals(ArchivoConstantes.FASE_CREACION);
				if (result) {
					UtilidadesArchivo.executeTransition(documentManager,
							carpeta,
							ArchivoConstantes.TRANSICION_FASE_TRAMITACION);
				}

				UtilidadesArchivo.executeTransition(documentManager, carpeta,
						ArchivoConstantes.TRANSICION_FASE_VIGENCIA);
				result = true;
				if (!iniciarCalendario(documentManager, carpeta)) {
					LOG.error("No se pudo iniciar el calendario de conservaci&oacute;n... ");
				}

			} else if (carpeta.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {

				carpeta.setProperty(ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
						ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS);
				documentManager.saveDocument(carpeta);

				String cicloDeVida = carpeta.getCurrentLifeCycleState();
				result = cicloDeVida.equals(ArchivoConstantes.FASE_CREACION);

				UtilidadesArchivo.executeTransition(documentManager, carpeta,
						ArchivoConstantes.TRANSICION_FASE_VIGENCIA);
				result = true;
				if (!iniciarCalendario(documentManager, carpeta)) {
					LOG.error("No se pudo iniciar el calendario de conservaci&oacute;n... ");
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo cerrar la carpeta : ", e);
		}
		return result;
	}

	/**
	 * Metodo que ejecuta la accion de reabrir una CAdD cerrada.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            a reabrir
	 */
	@Override
	public final void reabrirCarpeta(final CoreSession documentManager,
			final DocumentModel carpeta) {

		Map<String, Object> propiedades = new HashMap<String, Object>();
		if (carpeta.getType().equals(ArchivoConstantes.TIPO_EXPEDIENTE)
				|| carpeta.getType().equals(
						ArchivoConstantes.TIPO_EXPEDIENTE_REA)
				|| carpeta.getType().equals(
						ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

			propiedades.put(ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
					ArchivoConstantes.ESTADO_ABIERTO_CARPETA_DE_DOCUMENTOS);
			UtilidadesArchivo.updateFields(documentManager, carpeta,
					ArchivoConstantes.ESQUEMA_CADD, propiedades);
			UtilidadesArchivo.executeTransition(documentManager, carpeta,
					ArchivoConstantes.TRANSICION_FASE_TRAMITACION);
			if (!paralizarCalendario(documentManager, carpeta)) {
				LOG.error("No se pudo cancelar el calendario");
			}
		} else if (carpeta.getType().equals(
				ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {

			propiedades.put(ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
					ArchivoConstantes.ESTADO_ABIERTO_CARPETA_DE_DOCUMENTOS);
			UtilidadesArchivo.updateFields(documentManager, carpeta,
					ArchivoConstantes.ESQUEMA_CADD, propiedades);
			UtilidadesArchivo.executeTransition(documentManager, carpeta,
					ArchivoConstantes.TRANSICION_FASE_CREACION);
			if (!paralizarCalendario(documentManager, carpeta)) {
				LOG.error("No se pudo cancelar el calendario");
			}

		}

	}

	/**
	 * Metodo que ejecuta la accion de retener una CAdD.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            es el Documento carpeta a cerrar
	 * @throws RMException
	 *             en caso de error
	 */
	@Override
	public final void retenerCarpeta(final CoreSession documentManager,
			final DocumentModel carpeta) throws RMException {
		try {
			if (carpeta.getType().equals(ArchivoConstantes.TIPO_EXPEDIENTE)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_REA)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

				// comprobamos primero que el expediente tenga calendario
				// asociado.

				DocumentModel calendarioAsociado = buscarCalendarioAsociado(
						documentManager, carpeta);

				if (calendarioAsociado != null) {
					// si el expediente tiene calendario podemos retenerlo.
					carpeta.setProperty(
							ArchivoConstantes.ESQUEMA_CADD,
							ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
							ArchivoConstantes.ESTADO_RETENIDO_CARPETA_DE_DOCUMENTOS);
					documentManager.saveDocument(carpeta);
					UtilidadesQuartz.pararScheduler(
							ArchivoConstantes.PREFIJO_TIRGGERS + "-"
									+ carpeta.getId(), this.scheduler);

					Map<String, Object> objetoAsociado = UtilidadesArchivo
							.encuentraObjetoAsociado(calendarioAsociado,
									carpeta);
					if (objetoAsociado != null) {
						objetoAsociado.put("retenido_manual", true);
						objetoAsociado.put("fecha_ultima_retencion_manual",
								Calendar.getInstance());
						UtilidadesArchivo.actualizaObjetoAsociado(
								documentManager, calendarioAsociado, carpeta,
								objetoAsociado);
					} else {
						// FIXME: No se deberia haber dejado asignar un
						// calendario si no esta bien informado.
						// throw new
						// RMException("El expediente tiene los objetos asociados al calendario vacio.");
					}
				} else {
					throw new RMException(
							"El expediente no tiene calendario asociado.");
				}
			} else if (carpeta.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {
				// comprobamos primero que el expediente tenga calendario
				// asociado.

				DocumentModel calendarioAsociado = buscarCalendarioAsociado(
						documentManager, carpeta);

				if (calendarioAsociado != null) {
					// si el expediente tiene calendario podemos retenerlo.
					carpeta.setProperty(
							ArchivoConstantes.ESQUEMA_CADD,
							ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
							ArchivoConstantes.ESTADO_RETENIDO_CARPETA_DE_DOCUMENTOS);
					documentManager.saveDocument(carpeta);
					UtilidadesQuartz.pararScheduler(
							ArchivoConstantes.PREFIJO_TIRGGERS + "-"
									+ carpeta.getId(), this.scheduler);

					Map<String, Object> objetoAsociado = UtilidadesArchivo
							.encuentraObjetoAsociado(calendarioAsociado,
									carpeta);
					if (objetoAsociado != null) {
						objetoAsociado.put("retenido_manual", true);
						objetoAsociado.put("fecha_ultima_retencion_manual",
								Calendar.getInstance());
						UtilidadesArchivo.actualizaObjetoAsociado(
								documentManager, calendarioAsociado, carpeta,
								objetoAsociado);
					} else {
						// FIXME: No se deberia haber dejado asignar un
						// calendario si no esta bien informado.
						// throw new
						// RMException("El expediente tiene los objetos asociados al calendario vacio.");
					}
				} else {
					throw new RMException(
							"El expediente no tiene calendario asociado.");
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo retener la carpeta : ", e);
		}

	}

	/**
	 * Metodo que cancela la retenci&oacute;n aplicada a la CAdD.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            Documento carpeta a desbloquear
	 */
	public final void desbloquearRetencionCarpeta(
			final CoreSession documentManager, final DocumentModel carpeta) {
		try {
			if (carpeta.getType().equals(ArchivoConstantes.TIPO_EXPEDIENTE)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_REA)
					|| carpeta.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

				carpeta.setProperty(ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
						ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS);
				documentManager.saveDocument(carpeta);
				DocumentModel calendarioAsociado = buscarCalendarioAsociado(
						documentManager, carpeta);

				LOG.debug("******Obtenemos el calendario asociado al expediente : "
						+ calendarioAsociado);
				Calendar fechaRetenidoManual = null;
				Map<String, Object> objetoAsociado = null;
				if (calendarioAsociado != null) {
					objetoAsociado = UtilidadesArchivo.encuentraObjetoAsociado(
							calendarioAsociado, carpeta);
					if (objetoAsociado != null) {
						try {
							LOG.debug("******Obejetos asociados al calendario y al expediente : "
									+ objetoAsociado);
							fechaRetenidoManual = (Calendar) objetoAsociado
									.get("fecha_ultima_retencion_manual");
						} catch (Exception ex) {
							LOG.error("Error obteniendo objetos asodicados:",
									ex);
						}
					}
				}
				/**
				 * Hay que extraer la fecha de la retencion manual, y calcular
				 * cuanto tiempo (en dias) ha estado retenido el expediente. Una
				 * vez calculado, se actualizar&aacute; la fecha prevista de fin
				 * de retenci&oacute;n almacenada.
				 */
				if (fechaRetenidoManual != null) {
					Calendar hoy = Calendar.getInstance();
					Calendar fechaPrevista = (Calendar) objetoAsociado
							.get("fecha_prevista_fin_retencion");
					Calendar nuevaFechaFinRetencion = hoy;
					int dias = UtilidadesArchivo.daysOfDifference(
							fechaRetenidoManual, fechaPrevista);
					nuevaFechaFinRetencion.add(Calendar.DATE, dias);
					objetoAsociado.put("fecha_prevista_fin_retencion",
							nuevaFechaFinRetencion);
					UtilidadesArchivo.actualizaObjetoAsociado(documentManager,
							calendarioAsociado, carpeta, objetoAsociado);
					UtilidadesQuartz.programarScheduler(documentManager,
							nuevaFechaFinRetencion, carpeta, this.scheduler);
				} else {
					LOG.error("No se ha podido reiniciar el calendario de conservacion");
				}

			} else if (carpeta.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {
				carpeta.setProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD,
						ArchivoConstantes.ESTADO_CERRADO_DESBLOQUEADA_CARPETA_DE_DOCUMENTOS);
				documentManager.saveDocument(carpeta);
				DocumentModel calendarioAsociado = buscarCalendarioAsociado(
						documentManager, carpeta);

				LOG.debug("******Obtenemos el calendario asociado al expediente : "
						+ calendarioAsociado);
				Calendar fechaRetenidoManual = null;
				Map<String, Object> objetoAsociado = null;
				if (calendarioAsociado != null) {
					objetoAsociado = UtilidadesArchivo.encuentraObjetoAsociado(
							calendarioAsociado, carpeta);
					if (objetoAsociado != null) {
						try {
							LOG.debug("******Obejetos asociados al calendario y al expediente : "
									+ objetoAsociado);
							fechaRetenidoManual = (Calendar) objetoAsociado
									.get("fecha_ultima_retencion_manual");
						} catch (Exception ex) {
							LOG.error("Error obteniendo objetos asodicados:",
									ex);
						}
					}
				}
				/**
				 * Hay que extraer la fecha de la retencion manual, y calcular
				 * cuanto tiempo (en dias) ha estado retenido el expediente. Una
				 * vez calculado, se actualizar&aacute; la fecha prevista de fin
				 * de retenci&oacute;n almacenada.
				 */
				if (fechaRetenidoManual != null) {
					Calendar hoy = Calendar.getInstance();
					Calendar fechaPrevista = (Calendar) objetoAsociado
							.get("fecha_prevista_fin_retencion");
					Calendar nuevaFechaFinRetencion = hoy;
					int dias = UtilidadesArchivo.daysOfDifference(
							fechaRetenidoManual, fechaPrevista);
					nuevaFechaFinRetencion.add(Calendar.DATE, dias);
					objetoAsociado.put("fecha_prevista_fin_retencion",
							nuevaFechaFinRetencion);
					UtilidadesArchivo.actualizaObjetoAsociado(documentManager,
							calendarioAsociado, carpeta, objetoAsociado);
					UtilidadesQuartz.programarScheduler(documentManager,
							nuevaFechaFinRetencion, carpeta, this.scheduler);
				} else {
					LOG.error("No se ha podido reiniciar el calendario de conservacion");
				}

			}
		} catch (ClientException e) {
			LOG.error("No se pudo desbloquear la carpeta retenida : ", e);
		}
	}

	/**
	 * Mediante este metodo se comprueba si el documento actual es una CAdD y no
	 * esta cerrada, o bien, en caso de no ser una CAdD, comprueba si esta
	 * dentro de la ruta de alguna CAdD que este cerrada.
	 * 
	 * El objetivo de este metodo es comprobar si se puede crear o no contenido
	 * en el documento Actual, ya que, de acuerdo con los requisitos, cuando una
	 * CAdD este cerrada, no se puede crear contenido en su interior.
	 * 
	 * @param documentoActual
	 *            es el documento sobre el que se quiere consultar si es una
	 *            CAdD y y no esta cerrada, o, en caso de no ser una CAdD, si no
	 *            existe en toda la ruta que le contiene ninguna CAdD cerrada
	 * @param sesion
	 *            es la sesion
	 * @return true si la carpeta no esta cerrada, false en otro caso
	 */
	@Override
	public final boolean carpetaNoCerrada(final DocumentModel documentoActual,
			final CoreSession sesion) {
		String tipoDocumento = documentoActual.getType();
		boolean result = true;
		try {
			boolean continuar = true;
			DocumentModel documentoAuxiliar = documentoActual;
			/*
			 * Descripcion del algoritmo seguido:
			 * 
			 * INICIO
			 * 
			 * a) Si estamos en una CAdD:
			 * 
			 * 1. Comprobamos si esta cerrada 2. En caso afirmativo, devolvemos
			 * false 3. En caso negativo, tenemos que comprobar si alguna
			 * carpeta padre esta cerrada. Extraemos el padre de la actual y
			 * volvemos a INICIO
			 * 
			 * b) Si no estamos en una CAdD pero si en uno de sus posibles
			 * documentos extraemos el padre y vamos a INICIO.
			 */
			while (continuar) {
				if (tipoDocumento.equals(ArchivoConstantes.TIPO_ROOT)) {
					continuar = false;
				} else if (tipoDocumento
						.equals(ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {
					String estado = documentoAuxiliar
							.getCurrentLifeCycleState();
					if (estado
							.equals(ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS)
							|| estado
									.equals(ArchivoConstantes.ESTADO_CERRADO_DESBLOQUEADA_CARPETA_DE_DOCUMENTOS)) {
						result = false;
						continuar = false;
					} else {
						documentoAuxiliar = sesion
								.getDocument(documentoAuxiliar.getParentRef());
						tipoDocumento = documentoAuxiliar.getType();
					}
				} else if (tipoDocumento
						.equals(ArchivoConstantes.TIPO_EXPEDIENTE)) {
					String estado = (String) documentoAuxiliar.getProperty(
							ArchivoConstantes.ESQUEMA_CADD,
							ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
					if (estado
							.equals(ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS)) {
						result = false;
						continuar = false;
					} else {
						documentoAuxiliar = sesion
								.getDocument(documentoAuxiliar.getParentRef());
						tipoDocumento = documentoAuxiliar.getType();
					}
				} else {
					continuar = false;
				}
			}
		} catch (ClientException e) {
			LOG.error("Fallo la comprobacion de carpeta no cerrada : ", e);
		}
		return result;
	}

	/**
	 * Metodo que asocia un calendario a un elemento de una CdC y recursivamente
	 * a sus hijos si esta marcada la opcion correspondiente.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documento
	 *            documento actual del CAdD
	 * @param calendario
	 *            calendario a asociar
	 * @throws Exception 
	 */
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	@Override
	public final void asociarCalendario(final CoreSession documentManager,
			final DocumentModel documento, final DocumentModel calendario) throws Exception {
		/*if(true){
			throw new Exception("Entra"+calendario.getId().toString());
		}*/
		try {
			String id = calendario.getId();
			documento.setProperty(ArchivoConstantes.CALENDARIO_ASOCIADO,
					ArchivoConstantes.ID_CALENDARIO, id);
			documentManager.saveDocument(documento);
			List<String> idsAsociados = (List<String>) calendario.getProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS);
			if (!idsAsociados.contains(documento.getId())) {
				idsAsociados.add(documento.getId());
				calendario.setProperty(
						ArchivoConstantes.CALENDARIO_CONSERVACION,
						ArchivoConstantes.ID_DOCUMENTOS, idsAsociados);
				documentManager.saveDocument(calendario);
			}
			documentManager.save();
			LOG.debug("Calendario asignado correctamente");
		} catch (ClientException e) {
			LOG.error("No se pudo asignar el calendario", e);
		}
	}

	/**
	 * Metodo que elimina la asociacion entre un calendario y un documento del
	 * CdC.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documento
	 *            documento actual del CAdD
	 * @param calendario
	 *            calendario a asociar
	 */
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	@Override
	public final void eliminarCalendario(final CoreSession documentManager,
			final DocumentModel documento, final DocumentModel calendario) {
		try {

			List<String> idsAsociados = (List<String>) calendario.getProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS);
			idsAsociados.remove(documento.getId());
			calendario.setProperty(ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS, idsAsociados);
			List<Map<String, Object>> objetosAsociados = (List<Map<String, Object>>) calendario
					.getProperty(ArchivoConstantes.CALENDARIO_CONSERVACION,
							ArchivoConstantes.OBJETOS_ASOCIADOS_CALENDARIO);
			List<Map<String, Object>> nuevosObjetosAsociados = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> objeto : objetosAsociados) {
				String objetoId = (String) objeto.get(ArchivoConstantes.DOC_ID);
				if (!objetoId.equals(documento.getId())) {
					nuevosObjetosAsociados.add(objeto);
				}
			}
			calendario.setProperty(ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.OBJETOS_ASOCIADOS_CALENDARIO,
					nuevosObjetosAsociados);
			documentManager.saveDocument(calendario);
			documentManager.save();
			LOG.debug("Calendario asignado correctamente");
		} catch (ClientException e) {
			LOG.error("Calendario no eliminado", e);
		}
	}

	/**
	 * Metodo que devuelve el calendario asociado a un elemento del CdC.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documento
	 *            documento a analizar
	 * @return calendario
	 */
	@Override
	public final DocumentModel sacarCalendario(
			final CoreSession documentManager, final DocumentModel documento) {
		DocumentModel calendario = null;
		try {
			String id = (String) documento.getProperty(
					ArchivoConstantes.CALENDARIO_ASOCIADO,
					ArchivoConstantes.ID_CALENDARIO);
			if (id != null && !id.isEmpty()) {
				calendario = documentManager.getDocument(UtilidadesArchivo
						.getNuevaReferenciaId(id));
			}
		} catch (ClientException e) {
			LOG.error("Calendario no extra&iacute;do", e);
		}
		return calendario;
	}

	/**
	 * Metodo que recupera un calendario de la papelera.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param calendario
	 *            calendario a asociar
	 */
	@Override
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	public final void eliminarCalendario(final CoreSession documentManager,
			final DocumentModel calendario) {
		try {
			List<String> idsAsociados = (List<String>) calendario.getProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS);
			if (idsAsociados == null || idsAsociados.isEmpty()) {
				calendario.followTransition("delete");
				LOG.debug("Calendario eliminado");
			}
		} catch (ClientException e) {
			LOG.error("Calendario no eliminado", e);
		}
	}

	/**
	 * Recupera un calendario.
	 * 
	 * @param documentManager
	 *            el manejador de documentos
	 * @param calendario
	 *            a recuperar
	 */
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	@Override
	public final void recuperarCalendario(final CoreSession documentManager,
			final DocumentModel calendario) {
		try {
			List<String> idsAsociados = (List<String>) calendario.getProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS);
			if (idsAsociados == null || idsAsociados.isEmpty()) {
				calendario.followTransition("undelete");
				LOG.debug("Calendario recuperado");
			}
		} catch (ClientException e) {
			LOG.error("Calendario no recuperado ", e);
		}
	}

	/**
	 * Metodo que devuelve si existen elementos asociados a un calendario.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param calendario
	 *            calendario a analizar
	 * @return true si los elementos estan asociados al calendario
	 */
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	@Override
	public final boolean elementosAsociadosAlCalendario(
			final CoreSession documentManager, final DocumentModel calendario) {
		boolean result = false;
		try {
			List<String> idsAsociados = (List<String>) calendario.getProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.ID_DOCUMENTOS);
			result = idsAsociados != null && !idsAsociados.isEmpty();
		} catch (ClientException e) {
			LOG.error("Error en la consulta ", e);
		}
		return result;
	}

	/**
	 * Metodo que indica si un documento es elemento de un Cuadro de
	 * Clasificacion. Se comprueba que el modelo contiene la faceta de
	 * 'elemento_cdc'.
	 * 
	 * @param documento
	 *            el documento a analizar
	 * @return true si es documento del CdC, false en caso contrario
	 */
	@Override
	public final boolean esDocumentoDelCdD(final DocumentModel documento) {
		Set<String> facetas = documento.getFacets();
		return facetas
				.contains(ArchivoConstantes.FACETA_ELEMENTO_CUADRO_CLASIFICACION);
	}

	/**
	 * Metodo que devuelve todo el contenido (recursivo) del documento pasado
	 * como parametro, con la condicion de que solo se incluyen aquellos
	 * elementos que pertenecen al CdC.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documentoInicial
	 *            documento a analizar
	 * @return todos los hijos del CdC
	 */
	@Override
	public final DocumentModelList extraerTodosHijosCdC(
			final CoreSession documentManager,
			final DocumentModel documentoInicial) {
		DocumentModelList result = UtilidadesArchivo.getNuevaListaDocumentos();
		try {
			LOG.debug("Extrayendo hijos");
			DocumentModelList hijos = UtilidadesArchivo
					.getNuevaListaDocumentos();
			hijos = documentManager.getChildren(documentoInicial.getRef());
			while (hijos != null && hijos.size() > 0) {
				DocumentModelList aux = UtilidadesArchivo
						.getNuevaListaDocumentos();
				aux.addAll(hijos);
				hijos.clear();
				for (DocumentModel documento : aux) {
					if (documento
							.hasFacet(ArchivoConstantes.FACETA_ELEMENTO_CUADRO_CLASIFICACION)) {
						result.add(documento);
						hijos.addAll(documentManager.getChildren(documento
								.getRef()));
					}
				}
			}
			LOG.debug("EXtra&iacute;dos hijos");
		} catch (ClientException e) {
			LOG.error("No se pudo extraer todos los hijos ", e);
		}
		return result;
	}

	/**
	 * Metodo que permite iniciar una disposicion sobre un elemento del CdC.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documento
	 *            elemento del CdC
	 */
	@SuppressWarnings({ ArchivoConstantes.UNCHECKED_WARNING })
	@Override
	public final void iniciarDisposicion(final CoreSession documentManager,
			final DocumentModel documento) {
		// FIXME: No est&aacute; bien implementado
		LOG.debug("Iniciando disposicion...");
		DocumentModel calendario = sacarCalendario(documentManager, documento);
		try {
			List<Map<String, Object>> fases = (List<Map<String, Object>>) calendario
					.getProperty("calendario_conservacion", "fase");
			for (Map<String, Object> fase : fases) {
				String nombreFase = (String) fase.get("nombre_fase");
				String cicloDeVida = documento.getCurrentLifeCycleState();
				if (cicloDeVida.toLowerCase().equals(nombreFase.toLowerCase())) {
					String transicion = (String) fase.get("id_workflow");
					if (transicion != null && !transicion.isEmpty()) {
						documento.followTransition(transicion);
					}
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo iniciar la disposicion ", e);
		}
	}

	/**
	 * Metodo que permite comprobar si se puede iniciar una disposicion sobre un
	 * docE.
	 * 
	 * @param documentManager
	 *            sesion
	 * @param documento
	 *            para comprobar si puede iniciar la disposicion
	 * @return true si puede iniciar la disposicion, false en otro caso
	 */
	@Override
	public final boolean puedeIniciarDisposicion(
			final CoreSession documentManager, final DocumentModel documento) {
		boolean res = false;
		try {
			String idCalendario = (String) documento.getProperty(
					ArchivoConstantes.CALENDARIO_ASOCIADO,
					ArchivoConstantes.ID_CALENDARIO);
			if (idCalendario != null && !idCalendario.isEmpty()) {
				DocumentModel calendario = sacarCalendario(documentManager,
						documento);
				if (calendario != null) {
					// FIXME: Extraer el periodo dependiendo de la fase. Queda
					// por saber como se saca este valor.
					int periodoVigencia = 0;
					Date inicioVigencia = (Date) documento.getProperty(
							ArchivoConstantes.CALENDARIO_ASOCIADO,
							"inicio_disposicion");
					GregorianCalendar vigCal = UtilidadesArchivo
							.getNewGregorianCalendar();
					vigCal.setTime(inicioVigencia);

					GregorianCalendar fechaActual = UtilidadesArchivo
							.getNewGregorianCalendar();
					Calendar aux = Calendar.getInstance();
					fechaActual.setTime(aux.getTime());

					int diferencia = UtilidadesArchivo.deductDates(vigCal,
							fechaActual) - periodoVigencia;

					if (diferencia >= 0) {
						res = true;
					} else {
						res = false;
					}
				}
			} else {
				res = false;
			}
		} catch (ClientException e) {
			LOG.error("No se pudo realizar la comprobacion ", e);
		}
		return res;
	}

	/**
	 * Metodo que busca si existen calendarios asociados al documento pasado
	 * como par&aacute;metro. En caso de existir lo devuelve.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param documento
	 *            documento a analizar para buscar si tiene calendario asociado
	 * @return Calendario asociado en caso de existir. Nulo en otro caso.
	 */
	@Override
	public final DocumentModel buscarCalendarioAsociado(
			final CoreSession documentManager, final DocumentModel documento) {
		DocumentModel calendarioAsociado = null;
		try {
			String nxQuery = String.format(
					ArchivoConstantes.BUSQUEDA_CALENDARIOS, documento.getId());
			LOG.debug("Executing query: " + nxQuery);
			DocumentModelList results = documentManager.query(nxQuery);
			if (!results.isEmpty()) {
				calendarioAsociado = results.get(0);
			}
		} catch (ClientException e) {
			LOG.error("Error buscando calendario asociado: ", e);
		}
		return calendarioAsociado;
	}

	/**
	 * M&eacute;todo que se encarga de inicializar el Calendario asociado a un
	 * Expediente cuando &eacute;ste es cerrado.
	 * 
	 * @param documentManager
	 *            sesi&oacute;n
	 * @param documento
	 *            es el expediente
	 * @return true si la operaci&oacute;n finaliz&oacute; con &eacute;xito,
	 *         false en otro caso.
	 * @throws ClientException
	 *             en caso de error en el inicio del calendario
	 */
	private boolean iniciarCalendario(final CoreSession documentManager,
			final DocumentModel documento) throws ClientException {
		DocumentModel calendarioAsociado = buscarCalendarioAsociado(
				documentManager, documento);
		boolean result = false;
		if (calendarioAsociado != null) {
			LOG.info("Iniciando calendario para " + documento.getName());
			// Fase actual
			String faseActual = documento.getCurrentLifeCycleState();
			// Programamos el scheduler para el documento en la fase actual
			result = programarScheduler(documentManager, documento,
					calendarioAsociado, faseActual);
		}
		return result;
	}

	/**
	 * Programa el scheduler para un documento y calendario.
	 * 
	 * @param documentManager
	 *            es el document manager
	 * @param documento
	 *            es el documento para el que se asociara el scheduler
	 * @param calendarioAsociado
	 *            es el calendario
	 * @param fase
	 *            es la fase para calcular el scheduler
	 * @return true si se asigna la nueva fecha de fin de retencion
	 */
	private boolean programarScheduler(final CoreSession documentManager,
			final DocumentModel documento,
			final DocumentModel calendarioAsociado, final String fase) {
		boolean result = false;
		String docId = documento.getId();
		// Calculamos la posicion de la fase para realizar deltas de horas y
		// minutos aplicables
		int itemFase = obtenerPosicionFase(calendarioAsociado, fase);
		// Calculamos tiempos de retencion
		Calendar fechaCierre = Calendar.getInstance();
		Calendar fechaFinRetencion = UtilidadesArchivo
				.calcularFechaFinRetencion(calendarioAsociado, fechaCierre,
						fase, itemFase);
		if (fechaFinRetencion != null) {
			result = UtilidadesArchivo.asignaNuevaFechaFinRetencion(
					documentManager, documento, calendarioAsociado, docId,
					fechaCierre, fechaFinRetencion);
		} else {
			LOG.error("Fecha de retencion no asignada!");
		}
		UtilidadesQuartz.programarScheduler(documentManager, fechaFinRetencion,
				documento, this.scheduler);
		return result;
	}

	/**
	 * Obtenemos la posicion de la fase dentro del ciclo de vida del calendario
	 * de conservacion.
	 * 
	 * @param calendario
	 *            calendario de conservacion
	 * @param fase
	 *            es la fase para obtener su posicion
	 * @return posicion desde 1 a n, siendo 1 la primera y n la ultima posicion
	 */
	@SuppressWarnings("unchecked")
	private int obtenerPosicionFase(final DocumentModel calendario,
			final String fase) {
		int pos = CONT_1;
		List<Map<String, Object>> fases;
		try {
			fases = (List<Map<String, Object>>) calendario.getProperty(
					"calendario_conservacion", "fase");
			if (fases == null) {
				pos = CONT_1;
			}
			boolean encontrado = false;
			for (Iterator<Map<String, Object>> faseIt = fases.iterator(); faseIt
					.hasNext() && !encontrado;) {
				String nombreFase = (String) faseIt.next().get("nombre_fase");
				if (nombreFase.equals(fase)) {
					encontrado = true;
				} else {
					pos++;
				}
			}
		} catch (ClientException e) {
			LOG.warn("Imposible obtener la posicion de la fase", e);
		}
		return pos;
	}

	/**
	 * M&eacute;todo que paraliza definitivamente el calendario asociado a un
	 * Expediente.
	 * 
	 * @param documentManager
	 *            sesi&oacute;n
	 * @param documento
	 *            es el expediente
	 * @return true si se ejecut&oacute; la acci&oacute;n con &eacute;xito;
	 *         false en caso contrario
	 */
	@SuppressWarnings(ArchivoConstantes.UNCHECKED_WARNING)
	public final boolean paralizarCalendario(final CoreSession documentManager,
			final DocumentModel documento) {
		boolean result = false;
		DocumentModel calendarioAsociado = buscarCalendarioAsociado(
				documentManager, documento);
		if (calendarioAsociado != null) {
			try {
				List<Map<String, Object>> objetosAsociados = (List<Map<String, Object>>) calendarioAsociado
						.getProperty(ArchivoConstantes.CALENDARIO_CONSERVACION,
								ArchivoConstantes.OBJETOS_ASOCIADOS_CALENDARIO);
				List<Map<String, Object>> nuevosObjetos = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> elemento : objetosAsociados) {
					String docId = (String) elemento
							.get(ArchivoConstantes.DOC_ID);
					if (!docId.equals(documento.getId())) {
						nuevosObjetos.add(elemento);
					}
				}
				calendarioAsociado.setProperty(
						ArchivoConstantes.CALENDARIO_CONSERVACION,
						ArchivoConstantes.OBJETOS_ASOCIADOS_CALENDARIO,
						nuevosObjetos);
				documentManager.saveDocument(calendarioAsociado);
				documentManager.save();
				result = true;
			} catch (ClientException e) {
				LOG.error("Error cancelando calendario: ", e);
			}
			UtilidadesQuartz.pararScheduler(ArchivoConstantes.PREFIJO_TIRGGERS
					+ "-" + documento.getId(), this.scheduler);
		}
		return result;
	}

	/**
	 * Este m&eacute;todo ejecuta la transici&oacute;n entre el estado vigencia
	 * y el estado archivo del calendario de conservaci&oacute;n.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param documento
	 *            documento con el que operar
	 */
	public final void finalizarPeriodoRetencion(
			final CoreSession documentManager, final DocumentModel documento) {
		// Obtenemos el calendario de conservacion asociado
		DocumentModel calendarioAsociado = buscarCalendarioAsociado(
				documentManager, documento);
		if (calendarioAsociado == null) {
			LOG.error("No es posible finalizar la retencion para el "
					+ "documento por no tiene calendario de conservacion");
			return;
		}
		String schedulerId = ArchivoConstantes.PREFIJO_TIRGGERS + "-"
				+ documento.getId();
		LOG.info("Finalizando retencion para scheduler '" + schedulerId + "'");
		// Paramos el scheduler asociado al documento
		UtilidadesQuartz.pararScheduler(schedulerId, this.scheduler);
		// Obtenemos la siguiente transicion a ejecutar tras la finalizacion
		// de retencion
		String faseActual;
		try {
			faseActual = documento.getCurrentLifeCycleState();
			LOG.info("Fase actual del expediente = " + faseActual);
			// Obtenemos la disposicion asociada a la fase actual
			String disposicion = Normalizer.normalize(
					obtenerDisposicion(calendarioAsociado, faseActual),
					Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			LOG.info("Disposicion " + disposicion);
			if (TRANSFERENCIA.equals(disposicion)) {
				String faseSiguiente = obtenerSiguienteTransicion(
						calendarioAsociado, faseActual);
				LOG.info("Fase siguiente para el documento " + faseSiguiente);
				// Comprobamos que la siguiente transicion no es la actual
				if (!faseSiguiente.equals(faseActual)) {
					// Obtenemos la transicion asociada a la fase
					String transicionAEjecutar = obtenerTransicionParaFase(faseSiguiente);
					LOG.info("Transicion a ejecutar '" + transicionAEjecutar
							+ "'");
					// Ejecutamos transicion
					UtilidadesArchivo.executeTransition(documentManager,
							documento, transicionAEjecutar);
					Map<String, Object> objetoAsociado = UtilidadesArchivo
							.encuentraObjetoAsociado(calendarioAsociado,
									documento);
					objetoAsociado.put("fase_calendario", faseSiguiente);
					UtilidadesArchivo.actualizaObjetoAsociado(documentManager,
							calendarioAsociado, documento, objetoAsociado);
					// Iniciamos el proceso de retencion asociado a la nueva
					// fase
					if (!programarScheduler(documentManager, documento,
							calendarioAsociado, faseSiguiente)) {
						LOG.error("No se pudo iniciar la nueva fase del calendario de conservaci&oacute;n... ");
					}
				}
			} else if (ELIMINACION.equals(disposicion)) {
				LOG.info("Eliminacion del expediente en fase '" + faseActual
						+ "'");
				// Se procede a transitar el expediente a "deleted" (papelera)
				documento.followTransition("aEliminado");
				documentManager.saveDocument(documento);
				documentManager.save();
			}
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (RMException e) {
			LOG.error(
					"Imposible transitar despues de finalizacion del scheduler",
					e);
		}

	}

	/**
	 * Obtenemos la disposicion asociada a una fase del calendario.
	 * 
	 * @param calendario
	 *            es el calendario para obtener la disposicion de una fase
	 * @param fase
	 *            es la fase
	 * @return la disposicion asociada (Transferencia/Eliminación)
	 * @throws ClientException
	 *             en caso de error en la obtencion
	 */
	@SuppressWarnings("unchecked")
	private String obtenerDisposicion(final DocumentModel calendario,
			final String fase) throws ClientException {
		List<Map<String, Object>> fases = (List<Map<String, Object>>) calendario
				.getProperty("calendario_conservacion", "fase");
		if (fases == null) {
			throw new ClientException(
					"Imposible obtener las fases del calendario de conservacion para la disposicion.");
		}
		for (Iterator<Map<String, Object>> faseIt = fases.iterator(); faseIt
				.hasNext();) {
			Map<String, Object> faseActual = faseIt.next();
			String nombreFase = (String) faseActual.get("nombre_fase");
			if (nombreFase.equals(fase)) {
				return (String) faseActual.get("id_workflow");
			}
		}
		// Por defecto, transferencia
		return "Transferencia";
	}

	/**
	 * Obtenemos la transicion asociada a la fase actual.
	 * 
	 * @param fase
	 *            es la fase para obtener la transicion
	 * @return transicion asociada a la fase
	 * @throws RMException
	 *             en caso de no enctrar transicion asociada a la fase
	 */
	private String obtenerTransicionParaFase(final String fase)
			throws RMException {
		if ("vigencia".equals(fase)) {
			return "aVigencia";
		} else if ("transferencia".equals(fase)) {
			return "aTransferencia";
		} else if ("archivo".equals(fase)) {
			return "aArchivo";
		} else {
			throw new RMException("No existe transicion para la fase '" + fase
					+ "'");
		}
	}

	/**
	 * Obtenemos la siguiente fase del calendario de conservacion asociado al
	 * documento.
	 * 
	 * @param calendario
	 *            calendario de conservacion
	 * @param faseActual
	 *            fase actual
	 * 
	 * @return siguiente fase
	 * @throws ClientException
	 *             en caso de error en la obtencion
	 */
	@SuppressWarnings("unchecked")
	private String obtenerSiguienteTransicion(final DocumentModel calendario,
			final String faseActual) throws ClientException {
		List<Map<String, Object>> fases = (List<Map<String, Object>>) calendario
				.getProperty("calendario_conservacion", "fase");
		if (fases == null) {
			throw new ClientException(
					"Imposible obtener las fases del calendario de conservacion");
		}
		for (Iterator<Map<String, Object>> faseIt = fases.iterator(); faseIt
				.hasNext();) {
			String nombreFase = (String) faseIt.next().get("nombre_fase");
			if (nombreFase.equals(faseActual)) {
				if (faseIt.hasNext()) {
					return (String) faseIt.next().get("nombre_fase");
				} else {
					return faseActual;
				}
			}
		}
		return null;
	}

	/**
	 * Obtiene una entidad Actividad del Modelo.
	 * 
	 * @param documentManager
	 *            es la sesi&oacute;n
	 * @param docId
	 *            es el id del documento del repositorio
	 * @return Documento de Nuxeo
	 */
	@Override
	public final DocumentModel obtenerActividad(
			final CoreSession documentManager, final String docId) {
		List<String> entidades = UtilidadesArchivo.getNuevaListaVacia();
		entidades.add(ArchivoConstantes.FUNCION_MARCO);
		entidades.add(ArchivoConstantes.FUNCION);
		entidades.add(ArchivoConstantes.ACTIVIDAD);
		entidades.add(ArchivoConstantes.ACCION);
		DocumentModel actividad = UtilidadesArchivo.obtenerEntidad(
				documentManager, entidades, docId);
		if (actividad == null) {
			LOG.error("No se encontr&oacute; ninguna entidad Actividad para el docId: "
					+ docId);
		}
		return actividad;
	}

	/**
	 * Obtiene una entidad Agente del Modelo.
	 * 
	 * @param documentManager
	 *            es la sesi&oacute;n
	 * @param docId
	 *            es el id del documento del repositorio
	 * @return Documento de Nuxeo
	 */
	@Override
	public final DocumentModel obtenerAgente(final CoreSession documentManager,
			final String docId) {
		List<String> entidades = UtilidadesArchivo.getNuevaListaVacia();
		entidades.add(ArchivoConstantes.INSTITUCION);
		entidades.add(ArchivoConstantes.ORGANO);
		entidades.add(ArchivoConstantes.PERSONA);
		entidades.add(ArchivoConstantes.DISPOSITIVO);
		DocumentModel agente = UtilidadesArchivo.obtenerEntidad(
				documentManager, entidades, docId);
		if (agente == null) {
			LOG.error("No se encontr&oacute; ninguna entidad Agente para el docId: "
					+ docId);
		}
		return agente;
	}

	/**
	 * Obtiene una entidad Regulacion del Modelo.
	 * 
	 * @param documentManager
	 *            es la sesi&oacute;n
	 * @param docId
	 *            es el id del documento del repositorio
	 * @return Documento de Nuxeo
	 */
	@Override
	public final DocumentModel obtenerRegulacion(
			final CoreSession documentManager, final String docId) {
		List<String> entidades = UtilidadesArchivo.getNuevaListaVacia();
		entidades.add(ArchivoConstantes.NORMATIVA);
		entidades.add(ArchivoConstantes.PROCEDIMIENTO);
		DocumentModel regulacion = UtilidadesArchivo.obtenerEntidad(
				documentManager, entidades, docId);
		if (regulacion == null) {
			LOG.error("No se encontr&oacute; ninguna entidad Regulacion para el docId: "
					+ docId);
		}
		return regulacion;
	}

	/**
	 * Obtiene una entidad Relacion del Modelo.
	 * 
	 * @param documentManager
	 *            es la sesi&oacute;n
	 * @param docId
	 *            es el id del documento del repositorio
	 * @return Documento de Nuxeo
	 */
	@Override
	public final DocumentModel obtenerRelacion(
			final CoreSession documentManager, final String docId) {
		List<String> entidades = UtilidadesArchivo.getNuevaListaVacia();
		entidades.add(ArchivoConstantes.EVENTO_GESTION_DOCUMENTOS);
		entidades.add(ArchivoConstantes.PROCEDENCIA);
		DocumentModel relacion = UtilidadesArchivo.obtenerEntidad(
				documentManager, entidades, docId);
		if (relacion == null) {
			LOG.error("No se encontr&oacute; ninguna entidad Relacion para el docId: "
					+ docId);
		}
		return relacion;
	}

	/**
	 * Obtiene una entidad Documento del Modelo.
	 * 
	 * @param documentManager
	 *            es la sesi&oacute;n
	 * @param docId
	 *            es el id del documento del repositorio
	 * @return Documento de Nuxeo
	 */
	@Override
	public final DocumentModel obtenerDocumento(
			final CoreSession documentManager, final String docId) {
		List<String> entidades = UtilidadesArchivo.getNuevaListaVacia();
		entidades.add(ArchivoConstantes.GRUPO_DE_FONDO);
		entidades.add(ArchivoConstantes.FONDO);
		entidades.add(ArchivoConstantes.SERIE);
		entidades.add(ArchivoConstantes.AGREGACION);
		entidades.add(ArchivoConstantes.EXPEDIENTE);
		entidades.add(ArchivoConstantes.DOCUMENTO_SIMPLE);
		DocumentModel documento = UtilidadesArchivo.obtenerEntidad(
				documentManager, entidades, docId);
		if (documento == null) {
			LOG.error("No se encontr&oacute; ninguna entidad Regulacion para el docId: "
					+ docId);
		}
		return documento;
	}

	/**
	 * Obtiene todos los dispositovos del directorio de dispositivos.
	 * 
	 * @return lista de dispositivos.
	 */
	@Override
	public List<DispositivoDto> getDispositivos() {
		Session dir = null;
		List<DispositivoDto> listaDispositivos = new ArrayList<DispositivoDto>();
		try {

			dir = getDirectoryService().open("dispositivos");

			DocumentModelList entries = dir.getEntries();
			if (entries != null) {
				DispositivoDto dispositivo;
				for (DocumentModel entry : entries) {
					dispositivo = new DispositivoDto();
					dispositivo.setId((String) entry
							.getPropertyValue("dispAlm:idDispositivo"));
					dispositivo.setNombre((String) entry
							.getPropertyValue("dispAlm:nombreDispositivo"));
					listaDispositivos.add(dispositivo);
				}
			}

		} catch (Exception e) {
			LOG.error("getDispositivos-Error accesing vocabulary: ", e);
		} finally {
			if (dir != null) {
				try {
					dir.close();
				} catch (DirectoryException e1) {
					LOG.error(e1);
				}
			}
		}
		return listaDispositivos;
	}

	/**
	 * Devuelve el servicio directorio.
	 * 
	 * @return servicio directorio
	 * @throws Exception
	 *             en caso de error
	 */
	private DirectoryService getDirectoryService() throws Exception {
		return Framework.getService(DirectoryService.class);
	}
}