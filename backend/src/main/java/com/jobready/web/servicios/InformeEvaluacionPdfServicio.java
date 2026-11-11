package com.jobready.web.servicios;

import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.entidades.Simulacion;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.FontSelector;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPCellEvent;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Genera el informe PDF de retroalimentación con todo lo que registró el evaluador.
 * Cada apartado va en su propia tarjeta; los textos se copian tal cual (incluidos los saltos de línea).
 */
@Service
public class InformeEvaluacionPdfServicio {
    // Identidad visual de JobReady (mismos colores que el frontend).
    private static final Color AZUL = new Color(0x2b, 0x77, 0xf2);
    private static final Color AZUL_OSCURO = new Color(0x17, 0x20, 0x33);
    private static final Color AZUL_SUAVE = new Color(0xea, 0xf2, 0xfe);
    private static final Color TEXTO = new Color(0x33, 0x44, 0x58);
    private static final Color TEXTO_SUAVE = new Color(0x51, 0x60, 0x71);
    private static final Color BORDE = new Color(0xdc, 0xe4, 0xee);
    private static final Color FONDO_TARJETA = new Color(0xfb, 0xfc, 0xfe);
    private static final Color PISTA_BARRA = new Color(0xe3, 0xe9, 0xf0);
    private static final Color VERDE = new Color(0x10, 0x6b, 0x45);
    private static final Color AMBAR = new Color(0xb5, 0x47, 0x08);
    private static final Color ROJO = new Color(0xb4, 0x23, 0x18);
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm", Locale.forLanguageTag("es-PE"));

    private final BaseFont normal;
    private final BaseFont negrita;
    // Respaldo con más símbolos (flechas, marcas de verificación, letras de otros alfabetos...).
    private final BaseFont normalRespaldo;
    private final BaseFont negritaRespaldo;

    public InformeEvaluacionPdfServicio() {
        this.normal = cargarFuente("liberation/LiberationSans-Regular.ttf");
        this.negrita = cargarFuente("liberation/LiberationSans-Bold.ttf");
        this.normalRespaldo = cargarFuente("fuentes/DejaVuSans.ttf");
        this.negritaRespaldo = cargarFuente("fuentes/DejaVuSans-Bold.ttf");
    }

    /**
     * Caracteres del texto que ninguna fuente del informe puede dibujar (por ejemplo, emojis).
     * Se usa para rechazar la evaluación en vez de generar un PDF al que le falte parte del texto.
     */
    public String caracteresNoSoportados(String texto) {
        if (texto == null) return "";
        Set<String> faltantes = new LinkedHashSet<>();
        texto.codePoints()
                .filter(c -> !Character.isISOControl(c)) // saltos de línea y tabulaciones no se dibujan
                .filter(c -> !normal.charExists(c) && !normalRespaldo.charExists(c))
                .forEach(c -> faltantes.add(new String(Character.toChars(c))));
        return faltantes.stream().collect(Collectors.joining(" "));
    }

    /** Fuente Unicode embebida: el PDF muestra exactamente los caracteres que escribió el evaluador. */
    private static BaseFont cargarFuente(String recurso) {
        try (InputStream entrada = InformeEvaluacionPdfServicio.class.getClassLoader().getResourceAsStream(recurso)) {
            if (entrada == null) throw new IllegalStateException("No se encontró la fuente " + recurso);
            return BaseFont.createFont(recurso, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, entrada.readAllBytes(), null);
        } catch (IOException | DocumentException e) {
            throw new IllegalStateException("No se pudo cargar la fuente " + recurso, e);
        }
    }

    public byte[] generar(EvaluacionSimulacion evaluacion) {
        Simulacion simulacion = evaluacion.getSimulacion();
        String candidato = simulacion.getUsuario().getNombreCompleto();
        String evaluador = evaluacion.getEspecialista() != null ? evaluacion.getEspecialista().getNombreCompleto() : null;
        String carrera = simulacion.getCarrera() != null ? simulacion.getCarrera().getNombre() : null;

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 42, 42, 42, 56);
        try {
            PdfWriter escritor = PdfWriter.getInstance(documento, salida);
            escritor.setPageEvent(new PiePagina());
            documento.addTitle("Informe de retroalimentación - " + candidato);
            documento.addAuthor(evaluador != null ? evaluador : "JobReady");
            documento.addCreator("JobReady");
            documento.addSubject("Retroalimentación de la entrevista de práctica");
            documento.open();

            documento.add(encabezado());
            documento.add(datosGenerales(candidato, carrera, simulacion.getNumeroSimulacion(), evaluador, evaluacion));
            documento.add(puntajeGeneral(evaluacion.getPuntajeGeneral()));
            documento.add(porcentajes(
                    evaluacion.getSeguridadCalificacion().movePointRight(2).intValue(),
                    evaluacion.getClaridadRespuestas(),
                    evaluacion.getDominioTema()));
            documento.add(tarjetaTexto("Oportunidades de mejora", evaluacion.getOportunidadesMejora()));
            documento.add(tarjetaTexto("Recomendaciones", evaluacion.getRecomendaciones()));
            documento.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF de la evaluación", e);
        }
        return salida.toByteArray();
    }

    private PdfPTable encabezado() {
        PdfPTable tabla = tablaDeAncho(1, 16);
        PdfPCell celda = celdaTarjeta(AZUL, AZUL, 18);
        Paragraph marca = new Paragraph("JobReady", fuente(negrita, 22, Color.WHITE));
        Paragraph titulo = new Paragraph("Informe de retroalimentación de entrevista", fuente(negrita, 14, Color.WHITE));
        titulo.setSpacingBefore(4);
        Paragraph subtitulo = new Paragraph("Evaluación realizada por un especialista a partir del video de práctica.",
                fuente(normal, 10, new Color(0xdd, 0xe9, 0xfd)));
        subtitulo.setSpacingBefore(3);
        celda.addElement(marca);
        celda.addElement(titulo);
        celda.addElement(subtitulo);
        tabla.addCell(celda);
        return tabla;
    }

    private PdfPTable datosGenerales(String candidato, String carrera, Integer numero, String evaluador,
                                     EvaluacionSimulacion evaluacion) {
        PdfPTable tabla = tablaDeAncho(1, 14);
        PdfPCell tarjeta = celdaTarjeta(FONDO_TARJETA, BORDE, 14);
        tarjeta.addElement(tituloTarjeta("Datos de la evaluación"));
        PdfPTable datos = new PdfPTable(new float[]{1, 1});
        datos.setWidthPercentage(100);
        datos.addCell(dato("Candidato", candidato));
        datos.addCell(dato("Fecha de evaluación",
                evaluacion.getFechaEvaluacion() != null ? FECHA.format(evaluacion.getFechaEvaluacion()) : "—"));
        datos.addCell(dato("Carrera", carrera != null ? carrera : "—"));
        datos.addCell(dato("Evaluador", evaluador != null ? evaluador : "—"));
        if (numero != null) {
            datos.addCell(dato("Práctica", "N.º " + numero));
            datos.addCell(celdaVacia());
        }
        tarjeta.addElement(datos);
        tabla.addCell(tarjeta);
        return tabla;
    }

    private PdfPTable puntajeGeneral(int puntaje) {
        PdfPTable tabla = tablaDeAncho(1, 14);
        PdfPCell tarjeta = celdaTarjeta(AZUL_SUAVE, AZUL, 16);
        PdfPTable fila = new PdfPTable(new float[]{1.25f, 2.75f});
        fila.setWidthPercentage(100);

        PdfPCell numero = celdaVacia();
        numero.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Phrase valor = new Phrase();
        valor.add(new Phrase(String.valueOf(puntaje), fuente(negrita, 40, colorSegun(puntaje))));
        valor.add(new Phrase(" /100", fuente(negrita, 14, TEXTO_SUAVE)));
        numero.addElement(new Paragraph(valor));
        fila.addCell(numero);

        PdfPCell explicacion = celdaVacia();
        explicacion.setVerticalAlignment(Element.ALIGN_MIDDLE);
        explicacion.addElement(new Paragraph("Puntaje general", fuente(negrita, 15, AZUL_OSCURO)));
        Paragraph detalle = new Paragraph(
                "Promedio de seguridad al responder, claridad de respuestas y dominio del tema.",
                fuente(normal, 10, TEXTO_SUAVE));
        detalle.setSpacingBefore(3);
        explicacion.addElement(detalle);
        explicacion.addElement(barra(puntaje, 9));
        fila.addCell(explicacion);

        tarjeta.addElement(fila);
        tabla.addCell(tarjeta);
        return tabla;
    }

    private PdfPTable porcentajes(int seguridad, int claridad, int dominio) {
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(14);
        tabla.addCell(tarjetaPorcentaje("Seguridad al responder", seguridad));
        tabla.addCell(tarjetaPorcentaje("Claridad de respuestas", claridad));
        tabla.addCell(tarjetaPorcentaje("Dominio del tema", dominio));
        return tabla;
    }

    private PdfPCell tarjetaPorcentaje(String titulo, int valor) {
        PdfPCell celda = celdaVacia();
        celda.setPaddingRight(5);
        celda.setPaddingLeft(5);
        PdfPCell tarjeta = celdaTarjeta(FONDO_TARJETA, BORDE, 12);
        tarjeta.addElement(new Paragraph(titulo, fuente(negrita, 10, TEXTO_SUAVE)));
        Paragraph numero = new Paragraph(valor + " %", fuente(negrita, 26, colorSegun(valor)));
        numero.setSpacingBefore(4);
        tarjeta.addElement(numero);
        tarjeta.addElement(barra(valor, 6));
        PdfPTable interna = new PdfPTable(1);
        interna.setWidthPercentage(100);
        interna.addCell(tarjeta);
        celda.addElement(interna);
        return celda;
    }

    private PdfPTable tarjetaTexto(String titulo, String texto) {
        PdfPTable tabla = tablaDeAncho(1, 14);
        tabla.setSplitLate(false); // un texto largo continúa en la página siguiente en vez de saltar entero
        PdfPCell tarjeta = celdaTarjeta(FONDO_TARJETA, BORDE, 14);
        tarjeta.addElement(tituloTarjeta(titulo));
        Paragraph contenido = new Paragraph(conRespaldo(texto, false, 11, TEXTO));
        contenido.setLeading(0, 1.45f);
        tarjeta.addElement(contenido);
        tabla.addCell(tarjeta);
        return tabla;
    }

    private Paragraph tituloTarjeta(String titulo) {
        Paragraph parrafo = new Paragraph(titulo, fuente(negrita, 13, AZUL_OSCURO));
        parrafo.setSpacingAfter(8);
        return parrafo;
    }

    private PdfPCell dato(String etiqueta, String valor) {
        PdfPCell celda = celdaVacia();
        celda.setPaddingBottom(8);
        celda.addElement(new Paragraph(etiqueta.toUpperCase(Locale.ROOT), fuente(negrita, 8, TEXTO_SUAVE)));
        Paragraph texto = new Paragraph(conRespaldo(valor, true, 11, AZUL_OSCURO));
        texto.setLeading(0, 1.3f);
        texto.setSpacingBefore(2);
        celda.addElement(texto);
        return celda;
    }

    /** Barra de progreso dibujada con dos rectángulos redondeados (pista y avance). */
    private PdfPTable barra(int porcentaje, float alto) {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(8);
        PdfPCell celda = celdaVacia();
        celda.setFixedHeight(alto);
        int valor = Math.max(0, Math.min(100, porcentaje));
        Color color = colorSegun(valor);
        celda.setCellEvent((cell, posicion, lienzos) -> {
            // LINECANVAS va encima del fondo de la tarjeta (que se pinta en BACKGROUNDCANVAS después de esta celda).
            PdfContentByte lienzo = lienzos[PdfPTable.LINECANVAS];
            float radio = posicion.getHeight() / 2;
            lienzo.saveState();
            lienzo.setColorFill(PISTA_BARRA);
            lienzo.roundRectangle(posicion.getLeft(), posicion.getBottom(), posicion.getWidth(), posicion.getHeight(), radio);
            lienzo.fill();
            if (valor > 0) {
                float ancho = Math.max(posicion.getHeight(), posicion.getWidth() * valor / 100f);
                lienzo.setColorFill(color);
                lienzo.roundRectangle(posicion.getLeft(), posicion.getBottom(), ancho, posicion.getHeight(), radio);
                lienzo.fill();
            }
            lienzo.restoreState();
        });
        tabla.addCell(celda);
        return tabla;
    }

    private static Color colorSegun(int porcentaje) {
        if (porcentaje >= 75) return VERDE;
        if (porcentaje >= 50) return AMBAR;
        return ROJO;
    }

    private static PdfPTable tablaDeAncho(int columnas, float espacioDespues) {
        PdfPTable tabla = new PdfPTable(columnas);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(espacioDespues);
        return tabla;
    }

    /** Celda con fondo y borde redondeados (la "tarjeta"). */
    private static PdfPCell celdaTarjeta(Color fondo, Color borde, float relleno) {
        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(relleno);
        celda.setPaddingTop(relleno - 4);
        celda.setUseAscender(true);
        celda.setCellEvent(new FondoRedondeado(fondo, borde));
        return celda;
    }

    private static PdfPCell celdaVacia() {
        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(0);
        return celda;
    }

    /** Texto escrito por personas: cada carácter usa la primera fuente que lo tenga. */
    private Phrase conRespaldo(String texto, boolean enNegrita, float tamano, Color color) {
        FontSelector selector = new FontSelector();
        selector.addFont(fuente(enNegrita ? negrita : normal, tamano, color));
        selector.addFont(fuente(enNegrita ? negritaRespaldo : normalRespaldo, tamano, color));
        return selector.process(texto);
    }

    private static Font fuente(BaseFont base, float tamano, Color color) {
        return new Font(base, tamano, Font.NORMAL, color);
    }

    private record FondoRedondeado(Color fondo, Color borde) implements PdfPCellEvent {
        @Override
        public void cellLayout(PdfPCell celda, Rectangle posicion, PdfContentByte[] lienzos) {
            PdfContentByte lienzo = lienzos[PdfPTable.BACKGROUNDCANVAS];
            lienzo.saveState();
            lienzo.setColorFill(fondo);
            lienzo.setColorStroke(borde);
            lienzo.setLineWidth(0.8f);
            lienzo.roundRectangle(posicion.getLeft() + 0.5f, posicion.getBottom() + 0.5f,
                    posicion.getWidth() - 1, posicion.getHeight() - 1, 10);
            lienzo.fillStroke();
            lienzo.restoreState();
        }
    }

    /** Pie con la marca y el número de página. */
    private class PiePagina extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter escritor, Document documento) {
            PdfContentByte lienzo = escritor.getDirectContent();
            Font pie = fuente(normal, 8, TEXTO_SUAVE);
            float y = documento.bottom() - 24;
            ColumnText.showTextAligned(lienzo, Element.ALIGN_LEFT,
                    new Phrase("JobReady · Informe de retroalimentación", pie), documento.left(), y, 0);
            ColumnText.showTextAligned(lienzo, Element.ALIGN_RIGHT,
                    new Phrase("Página " + escritor.getPageNumber(), pie), documento.right(), y, 0);
        }
    }
}
