package es.rm.platform.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

import es.rm.platform.api.services.ArchivoServicio;
import es.rm.platform.api.utils.DispositivoDto;

/**
 * Clase dispositivo Bean.
 * 
 * @author victorsanchez
 * 
 */
@Name("dipositivosBean")
@Scope(ScopeType.SESSION)
public class DipositivosBean implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4695724093272287933L;
	/**
	 * Traza error LOG.
	 */
	private static final Log LOG = LogFactory.getLog(DipositivosBean.class);
	/**
	 * Constante numero lineas a mostrar.
	 */
	private static final int NUM_LINEAS_MUESTRO = 1;
	/**
	 * numero lineas.
	 */
	private final int numLineas = NUM_LINEAS_MUESTRO;

	/** Navigation Context. */
	@In
	private NavigationContext navigationContext;
	/** Constante valor vacio. */
	private static final String VACIO_VALOR = null;
	/** Constante seleccione valor. */
	private static final String SELECIONAR_VALOR = "Seleccione un valor";

	/**
	 * Obtendra la lista de fabricante que se representan en las combox.
	 */
	private List<SelectItem> listDispositivos;
	/** Nombre dispositivo. */
	private String nombreDispositivo = VACIO_VALOR;

	/**
	 * Obtiene el nombre del dispositivo.
	 * 
	 * @return the nombreDispositivo
	 */
	public String getNombreDispositivo() {
		return this.nombreDispositivo;
	}

	/**
	 * @param nombreDispositivo
	 *            the nombreDispositivo to set
	 */
	public void setNombreDispositivo(final String nombreDispositivo) {
		this.nombreDispositivo = nombreDispositivo;
	}

	/** Codigo dispositivo. */
	private String codigoDispositivo;

	/**
	 * Devuelve el codigo dispositivo.
	 * 
	 * @return the codigoDispositivo
	 */
	public String getCodigoDispositivo() {
		return this.codigoDispositivo;
	}

	/**
	 * Asigna el codigo dispositivo.
	 * 
	 * @param codigoDispositivo
	 *            the codigoDispositivo to set
	 */
	public void setCodigoDispositivo(final String codigoDispositivo) {
		this.codigoDispositivo = codigoDispositivo;
	}

	/**
	 * Obtiene el nombre del dispositivo.
	 * 
	 * @param id
	 *            identificador de dispositivo.
	 * @return nombre del dispositivo.
	 */
	public String getNombreDispositivosListado(final String id) {
		return obtenerNombre(id, getListDispositivos());
	}

	/**
	 * Obtiene la lista de Dispositivos.
	 * 
	 * @return the listFabricantes
	 * @throws Exception
	 */
	public List<SelectItem> getListDispositivos() {
		LOG.debug("getListFabricantes-BEGIN");
		this.listDispositivos = new ArrayList<SelectItem>();
		this.listDispositivos
				.add(new SelectItem(VACIO_VALOR, SELECIONAR_VALOR));

		ArchivoServicio servicio;
		try {
			servicio = Framework.getService(ArchivoServicio.class);

			List<DispositivoDto> lista = servicio.getDispositivos();
			SelectItem nuevoItem;
			if (lista != null && !lista.isEmpty()) {
				for (DispositivoDto item : lista) {
					nuevoItem = new SelectItem();
					nuevoItem.setValue(item.getId());
					nuevoItem.setLabel(item.getNombre());
					this.listDispositivos.add(nuevoItem);
				}
			}
		} catch (Exception e) {
			LOG.error("Error obteniendo la lista de dispostivos", e);
		}
		return this.listDispositivos;
	}

	/**
	 * Obtiene nombre respecto al id indicado para el dipositivos.
	 * 
	 * @param id
	 *            identificador del dispositivo.
	 * @param lista
	 *            lista de dispositivos.
	 * @return nombre dispositivo.
	 */
	private String obtenerNombre(final String id, final List<SelectItem> lista) {
		Iterator<SelectItem> iterator = lista.iterator();
		SelectItem item;
		String nombre = "";
		boolean encontrado = false;
		while (!encontrado && iterator.hasNext()) {
			item = iterator.next();
			boolean analizarMandatory = (id == null || id.equals(""));
			if (!analizarMandatory && item.getValue() != null
					&& item.getValue().equals(id)) {
				encontrado = true;
				nombre = item.getLabel();
			}
		}
		return nombre;
	}

	/**
	 * Devuelve el numero de lineas.
	 * 
	 * @return the numLineas
	 */
	public int getNumLineas() {
		return this.numLineas;
	}

	/**
	 * Validador JSF si es vacio o no.
	 * 
	 * @param context
	 *            contexto JSF
	 * @param component
	 *            componente JSF
	 * @param value
	 *            valor del componente JSF
	 */
	public void esVacio(final FacesContext context,
			final UIComponent component, final Object value) {
		if (value == null) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar un valor para el dispositivo.", null);
			// also add global message
			context.addMessage(null, message);
			throw new ValidatorException(message);
		} else if (value instanceof String) {
			String objetoValor = (String) value;
			if (objetoValor.equals("")) {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar un valor para el dispositivo.", null);
				// also add global message
				context.addMessage(null, message);
				throw new ValidatorException(message);
			}
		}
	}

}
