package com.example.webapp.controller;

import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.lowagie.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class PdfController {
    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);
    final MedicamentosRepository medicamentosRepository;
    final UsuarioRepository usuarioRepository;
    final UsuarioHasSedeRepository usuarioHasSedeRepository;
    final SedeHasMedicamentosRepository sedeHasMedicamentosRepository;


    final PedidosPacienteRecojoRepository pedidosPacienteRecojoRepository;
    final MedicamentosRecojoRepository medicamentosRecojoRepository;
    final SpringTemplateEngine templateEngine;
    public PdfController(MedicamentosRepository medicamentosRepository,
                         UsuarioRepository usuarioRepository,
                         UsuarioHasSedeRepository usuarioHasSedeRepository,
                         SedeHasMedicamentosRepository sedeHasMedicamentosRepository,
                         PedidosPacienteRecojoRepository pedidosPacienteRecojoRepository,
                         MedicamentosRecojoRepository medicamentosRecojoRepository,
                         SpringTemplateEngine templateEngine) {
        this.medicamentosRepository = medicamentosRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioHasSedeRepository = usuarioHasSedeRepository;
        this.sedeHasMedicamentosRepository = sedeHasMedicamentosRepository;
        this.pedidosPacienteRecojoRepository = pedidosPacienteRecojoRepository;
        this.medicamentosRecojoRepository = medicamentosRecojoRepository;
        this.templateEngine = templateEngine;
    }

    @PostMapping("/filtrarSede")
    public ResponseEntity<InputStreamResource> downloadMedicamentosPdf(@RequestParam("filtroSede") String sede,
                                                                       @RequestParam("nuevoRol") String rol,
                                                                       Model model){

        System.out.println("Esta es la Sede Seleccionada");
        System.out.println(sede);
        System.out.println("Esta es el Rol Seleccionado");
        System.out.println(rol);

        int sedeInt = Integer.parseInt(sede);
        int rolInt = Integer.parseInt(rol);
        List<Usuario> usuarios = new ArrayList<>();
        List<Medicamentos> medicamentos = new ArrayList<>();

        if(rolInt == 5){
            List<Usuario> listausuarios = usuarioRepository.buscarDoctor(5,0);
            List<UsuarioHasSede> listausuariosede = usuarioHasSedeRepository.findAll();


            for (Usuario usuario : listausuarios) {
                for(UsuarioHasSede usuairoSede : listausuariosede){
                    if((usuario.getId() == usuairoSede.getUsuario_id_usario().getId()) && (usuairoSede.getSede_id_sede().getId() == sedeInt)){
                        usuarios.add(usuario);
                    }
                }
            }
        }else if(rolInt == 2){
            List<Usuario> listausuarios = usuarioRepository.buscarAdministrador(2,0);
            List<UsuarioHasSede> listausuariosede = usuarioHasSedeRepository.findAll();

            for (Usuario usuario : listausuarios) {
                for(UsuarioHasSede usuairoSede : listausuariosede){
                    if((usuario.getId() == usuairoSede.getUsuario_id_usario().getId()) && (usuairoSede.getSede_id_sede().getId() == sedeInt)){
                        usuarios.add(usuario);
                    }
                }
            }
        }else if(rolInt == 3){
            List<Usuario> listausuarios = usuarioRepository.buscarFarmacistaAceptado(3,0,"Aceptado");
            List<UsuarioHasSede> listausuariosede = usuarioHasSedeRepository.findAll();

            for (Usuario usuario : listausuarios) {
                for(UsuarioHasSede usuairoSede : listausuariosede){
                    if((usuario.getId() == usuairoSede.getUsuario_id_usario().getId()) && (usuairoSede.getSede_id_sede().getId() == sedeInt)){
                        usuarios.add(usuario);
                    }
                }
            }
        }else if(rolInt == 1){
            List<Medicamentos> listamedicamentos = medicamentosRepository.buscarMedicamentoGeneral(0);
            List<SedeHasMedicamentos> listasedemedicamentos = sedeHasMedicamentosRepository.findAll();

            for (Medicamentos medicamento : listamedicamentos) {
                for(SedeHasMedicamentos sedeMedicamento : listasedemedicamentos){
                    if((medicamento.getId() == sedeMedicamento.getId_medicamentos().getId()) && (sedeMedicamento.getId_sede().getId()==sedeInt)){
                        medicamentos.add(medicamento);
                    }
                }
            }
        }


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        if(rolInt == 5) {
            // Añadir título
            Paragraph title = new Paragraph("Clínica PildoPharm S.A." + " Doctores de la Sede " + sede)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
        }else if(rolInt == 2){
            // Añadir título
            Paragraph title = new Paragraph("Clínica PildoPharm S.A." + " Administrador de la Sede " + sede)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
        }else if(rolInt == 3) {
            // Añadir título
            Paragraph title = new Paragraph("Clínica PildoPharm S.A." + " Farmacistas de la Sede " + sede)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
        }else if(rolInt == 1){
            // Añadir título
            Paragraph title = new Paragraph("Clínica PildoPharm S.A." + " Medicamentos de la Sede " + sede)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
        }
        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        if(rolInt == 5) {
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Doctor")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Código de Colegiatura")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Agregar datos a la tabla
            for (Usuario usuario : usuarios) {
                table.addCell(new Cell().add(new Paragraph(usuario.getNombres() + ' ' + usuario.getApellidos())));
                table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getCodigo_colegio().getNombre())))); // Nueva columna
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
            }
        }else if(rolInt == 2){
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Administrador")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Distrito")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Agregar datos a la tabla
            for (Usuario usuario : usuarios) {
                table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
                table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDistrito().getNombre())))); // Nueva columna
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
            }
        }else if(rolInt == 3){
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Farmacista")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Código de Colegiatura")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Agregar datos a la tabla
            for (Usuario usuario : usuarios) {
                table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
                table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getCodigo_colegio().getNombre())))); // Nueva columna
                table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
            }
        }else if(rolInt == 1){
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Descripción")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Número Unidades Disponibles")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Creación")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Agregar datos a la tabla
            for (Medicamentos medicamento : medicamentos) {
                table.addCell(new Cell().add(new Paragraph(medicamento.getNombre())));
                table.addCell(new Cell().add(new Paragraph(medicamento.getDescripcion())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getPrecio_unidad()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getInventario())))); // Nueva columna
                table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getFecha_ingreso())))); // Nueva columna
            }
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        if(rolInt == 5) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Doctores_Sede.pdf");
        }else if(rolInt == 2){
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Administrador_Sede.pdf");
        }else if(rolInt == 3){
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Farmacistas_Sede.pdf");
        }else if(rolInt == 1){
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Medicamentos_Sede.pdf");
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }



    @GetMapping("/pdf_medicamentos")
    public ResponseEntity<InputStreamResource> downloadMedicamentosPdf() {

        List<Medicamentos> medicamentos = medicamentosRepository.findAll();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Añadir título
        Paragraph title = new Paragraph("Clínica PildoPharm S.A.")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Descripción")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Precio")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Número Unidades Disponibles")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Creación")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Agregar datos a la tabla
        for (Medicamentos medicamento : medicamentos) {
            table.addCell(new Cell().add(new Paragraph(medicamento.getNombre())));
            table.addCell(new Cell().add(new Paragraph(medicamento.getDescripcion())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getPrecio_unidad()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getInventario())))); // Nueva columna
            table.addCell(new Cell().add(new Paragraph(String.valueOf(medicamento.getFecha_ingreso())))); // Nueva columna
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Medicamentos.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/pdf_doctores")
    public ResponseEntity<InputStreamResource> downloadDoctores() {

        List<Usuario> usuarios = usuarioRepository.buscarDoctor(5,0);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Añadir título
        Paragraph title = new Paragraph("Clínica PildoPharm S.A.")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Doctor")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Código de Colegiatura")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Agregar datos a la tabla
        for (Usuario usuario : usuarios) {
            table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
            table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getCodigo_colegio().getNombre())))); // Nueva columna
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Doctores.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/pdf_administradores")
    public ResponseEntity<InputStreamResource> downloadAdministradores() {

        List<Usuario> usuarios = usuarioRepository.buscarAdministrador(2,0);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Añadir título
        Paragraph title = new Paragraph("Clínica PildoPharm S.A.")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Administrador")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Distrito")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Agregar datos a la tabla
        for (Usuario usuario : usuarios) {
            table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
            table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDistrito().getNombre())))); // Nueva columna
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Administradores.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/pdf_farmacistas")
    public ResponseEntity<InputStreamResource> downloadFarmacistas() {

        List<Usuario> usuarios = usuarioRepository.buscarFarmacistaAceptado(3,0,"Aceptado");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Añadir título
        Paragraph title = new Paragraph("Clínica PildoPharm S.A.")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Farmacista")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Código de Colegiatura")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Agregar datos a la tabla
        for (Usuario usuario : usuarios) {
            table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
            table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getCodigo_colegio().getNombre())))); // Nueva columna
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Farmacistas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/pdf_pacientes")
    public ResponseEntity<InputStreamResource> downloadPacientes() {

        List<Usuario> usuarios = usuarioRepository.buscarPaciente(4,0);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Añadir título
        Paragraph title = new Paragraph("Clínica PildoPharm S.A.")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Espacio después del título
        document.add(new Paragraph("\n"));

        // Crear tabla con encabezados
        float[] columnWidths = {2, 5, 2, 2, 3}; // Ajuste de anchos de columna para cinco columnas
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Paciente")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Correo")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("DNI")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Seguro Médico")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Ingreso")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Agregar datos a la tabla
        for (Usuario usuario : usuarios) {
            table.addCell(new Cell().add(new Paragraph(usuario.getNombres()+' '+usuario.getApellidos())));
            table.addCell(new Cell().add(new Paragraph(usuario.getCorreo())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getDni()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getSeguro().getNombre())))); // Nueva columna
            table.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getFecha_creacion())))); // Nueva columna
        }

        document.add(table);

        // Espacio antes de la fecha
        document.add(new Paragraph("\n"));

        // Añadir fecha
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Paragraph date = new Paragraph("Fecha: " + formattedDate)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Pacientes.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }




    @GetMapping("/boleta_pdf")
    public ResponseEntity<InputStreamResource> generarBoletaPdf(Model model, @RequestParam("id") int id) {
        logger.debug("Iniciando generación de boleta PDF para el pedido con ID: {}", id);

        Optional<PedidosPacienteRecojo> optionalPedido = pedidosPacienteRecojoRepository.findById(id);

        if (optionalPedido.isPresent()) {
            PedidosPacienteRecojo pedido = optionalPedido.get();
            List<MedicamentoRecojo> medicamentos = medicamentosRecojoRepository.listaMedicamentosReco(id);

            List<Map<String, Object>> medicamentosData = new ArrayList<>();
            double totalCost = 0.0;

            for (MedicamentoRecojo medicamento : medicamentos) {
                Map<String, Object> medData = new HashMap<>();
                medData.put("nombre", medicamento.getNombre_medicamento());
                medData.put("cantidad", medicamento.getCantidad());

                double costo = 0.0;
                try {
                    costo = Double.parseDouble(medicamento.getCosto_medicamento());
                } catch (NumberFormatException e) {
                    logger.warn("Error al parsear costo de medicamento: {}", medicamento.getCosto_medicamento());
                }

                double subtotal = medicamento.getCantidad() * costo;
                medData.put("costo", String.format("%.2f", costo));
                medData.put("subtotal", String.format("%.2f", subtotal));

                medicamentosData.add(medData);
                totalCost += subtotal;
            }

            model.addAttribute("medicamentos", medicamentosData);
            model.addAttribute("totalCost", String.format("%.2f", totalCost));
            model.addAttribute("fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            try {
                String renderedHtml = renderThymeleafTemplate("boleta-template", model);
                ByteArrayInputStream pdfStream = generatePdfFromHtml(renderedHtml);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Boleta_Pedido.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdfStream));
            } catch (Exception e) {
                logger.error("Error al generar boleta PDF para el pedido con ID {}: {}", id, e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } else {
            logger.warn("No se encontró pedido con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    private String renderThymeleafTemplate(String templateName, Model model) {
        Context context = new Context();
        model.asMap().forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

    private ByteArrayInputStream generatePdfFromHtml(String html) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}


