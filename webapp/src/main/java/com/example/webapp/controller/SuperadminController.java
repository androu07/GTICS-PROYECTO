package com.example.webapp.controller;

import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import com.example.webapp.util.Correo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/superadmin")
public class SuperadminController {

    final
    MedicamentosRepository medicamentosRepository;
    UsuarioRepository usuarioRepository;
    PedidosReposicionRepository pedidosReposicionRepository;
    UsuarioHasSedeRepository usuarioHasSedeRepository;
    SedeRepository sedeRepository;
    SedeHasMedicamentosRepository sedeHasMedicamentosRepository;
    DistritoRepository distritoRepository;

    SeguroRepository seguroRepository;

    CodigoColegioRepository codigoColegioRepository;
    private static final Logger logger = LoggerFactory.getLogger(SuperadminController.class);

    public SuperadminController(MedicamentosRepository medicamentosRepository,
                                UsuarioRepository usuarioRepository,
                                PedidosReposicionRepository pedidosReposicionRepository,
                                UsuarioHasSedeRepository usuarioHasSedeRepository,
                                SedeRepository sedeRepository,
                                SedeHasMedicamentosRepository sedeHasMedicamentosRepository,
                                DistritoRepository distritoRepository,
                                SeguroRepository seguroRepository,
                                CodigoColegioRepository codigoColegioRepository) {

        this.medicamentosRepository = medicamentosRepository;

        this.usuarioRepository = usuarioRepository;

        this.pedidosReposicionRepository = pedidosReposicionRepository;

        this.usuarioHasSedeRepository = usuarioHasSedeRepository;

        this.sedeRepository = sedeRepository;

        this.distritoRepository = distritoRepository;

        this.sedeHasMedicamentosRepository = sedeHasMedicamentosRepository;

        this.seguroRepository = seguroRepository;

        this.codigoColegioRepository = codigoColegioRepository;
    }
    @Autowired
    private Correo correo;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping(value = {"", "/"})
    public String Plantilla() {

        return "redirect:/superadmin/Vista_Principal";
    }

    @GetMapping("/Reportes")
    public String Reportes(Model model) {
        List<Sede> listaSedes= sedeRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superadmin/Plantilla_Vista_GenerarReportes";
    }
    @GetMapping("/Registrar_Medicamento")
    public String Registrar_Medicamento(@ModelAttribute ("medicamento") Medicamentos medicamentos) {
        return "superadmin/Plantilla_Vista_Registrar_Medicamento";
    }

    @GetMapping("/Cerrar_Cuenta")
    public String Cerrar_Cuenta() {
        return "IndexDoctor";
    }


    //------------------------------------------------------------------------------------------------------------------
    //Listar Medicamentos
    @GetMapping("/Medicamentos")
    public String Medicamentos(Model model) {
        List<Medicamentos> lista = medicamentosRepository.buscarMedicamentoGeneral(0);
        List<String> listafotos = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            byte[] fotoBytes = lista.get(i).getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
            listafotos.add(fotoBase64);
            System.out.println(listafotos.get(i));
        }

        model.addAttribute("listTransportation", lista);
        model.addAttribute("listaFotos", listafotos);
        return "superadmin/Plantilla_Vista_Medicamentos";
    }

    //Ver Medicamento
    @GetMapping("/Ver_Medicamento")
    public String Ver_Medicamento(Model model,
                                  @RequestParam("id") int id) {
        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);


        if (optMedicamento.isPresent()) {

            Medicamentos medicamento = optMedicamento.get();
            List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();


            for (Sede sede : list1) {
                int i = 0;
                for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                    if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                        if (sedeHasMedicamentos.getId_medicamentos().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }

            System.out.println(listaIndicador);

            byte[] fotoBytes = medicamento.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
            model.addAttribute("fotoBase64", fotoBase64);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);


            return "superadmin/Plantilla_Vista_Ver_Medicamento";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Medicamentos";
        }
    }


    //Buscar Medicamento por Nombre
    @PostMapping("/buscarPorNombre")
    public String buscarPorNombre(@RequestParam("searchField") String searchField, Model model) {

        List<Medicamentos> lista = medicamentosRepository.buscarMedicamento(searchField);
        model.addAttribute("listTransportation", lista);
        model.addAttribute("textoBuscado", searchField);
        return "superadmin/Plantilla_Vista_Medicamentos";
    }

    //Editar Medicamento
    @GetMapping("/Editar_Medicamento")
    public String editarMedicamento(Model model,
                                    @RequestParam("id") int id,@ModelAttribute ("medicamento") Medicamentos medicamentos) {

        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);
        if (optMedicamento.isPresent()) {
            Medicamentos medicamento = optMedicamento.get();
            List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                    if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                        if (sedeHasMedicamentos.getId_medicamentos().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }

            System.out.println(listaIndicador);
            byte[] fotoBytes = medicamento.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);

            model.addAttribute("fotoBase64", fotoBase64);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);


            return "superadmin/Plantilla_Vista_Actualizar_Medicamento";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Medicamentos";
        }
    }

    @PostMapping("/Guardar_Medicamento")
    public String guardarNuevoMedicamento(@RequestParam("foto1") Part foto1,
                                          @ModelAttribute ("medicamento") @Valid Medicamentos medicamentos, BindingResult bindingResult,
                                          Model model) {

        if(medicamentos.getId() == 0){
            if (bindingResult.hasErrors()) {
                System.out.println(medicamentos.getNombre());
                return "superadmin/Plantilla_Vista_Registrar_Medicamento";

            } else {

                try {
                    InputStream fotoStream=foto1.getInputStream();
                    byte[] fotoBytes=fotoStream.readAllBytes();
                    medicamentos.setFoto(fotoBytes);
                    medicamentosRepository.save(medicamentos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
                List<Sede> list1 = sedeRepository.findAll();

                List<String> listaIndicador = new ArrayList<>();

                for (Sede sede : list1) {
                    int i = 0;
                    for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                        if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                            if (sedeHasMedicamentos.getId_medicamentos().getId() == medicamentos.getId()) {
                                i=1;
                            }
                        }
                    }
                    if (i==0){
                        listaIndicador.add("NoAsignado");
                    }else{
                        listaIndicador.add("Asignado");
                    }
                }

                System.out.println(listaIndicador);
                byte[] fotoBytes1 = medicamentos.getFoto();
                String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

                model.addAttribute("fotoBase64", fotoBase64);
                model.addAttribute("medicamento", medicamentos);
                model.addAttribute("ListaIndicador", listaIndicador);
                model.addAttribute("ListaSedes",list1);


                return "superadmin/Plantilla_Vista_Ver_Medicamento";

            }
        }else{
            if (bindingResult.hasErrors()) {
                List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
                List<Sede> list1 = sedeRepository.findAll();

                Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(medicamentos.getId());

                Medicamentos medicamento = optMedicamento.get();

                List<String> listaIndicador = new ArrayList<>();

                for (Sede sede : list1) {
                    int i = 0;
                    for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                        if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                            if (sedeHasMedicamentos.getId_medicamentos().getId() == medicamentos.getId()) {
                                i=1;
                            }
                        }
                    }
                    if (i==0){
                        listaIndicador.add("NoAsignado");
                    }else{
                        listaIndicador.add("Asignado");
                    }
                }

                System.out.println(listaIndicador);
                byte[] fotito = medicamento.getFoto();
                String fotoBase = Base64.getEncoder().encodeToString(fotito);

                model.addAttribute("fotoBase64", fotoBase);
                model.addAttribute("medicamento", medicamentos);
                model.addAttribute("ListaIndicador", listaIndicador);
                model.addAttribute("ListaSedes",list1);


                return "superadmin/Plantilla_Vista_Actualizar_Medicamento";

            } else {
                System.out.println("Zise de la Foto Actualizar");
                System.out.println(foto1.getSize());
                System.out.println(medicamentos.getFoto());
                if (foto1.getSize()==0) {
                    System.out.println("No se envio nada, que se quede con la actual");
                    System.out.println(foto1.getSize());

                    Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(medicamentos.getId());

                    Medicamentos medicamento = optMedicamento.get();

                    medicamentos.setFoto(medicamento.getFoto());

                    medicamentosRepository.save(medicamentos);
                    List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                            if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                                if (sedeHasMedicamentos.getId_medicamentos().getId() == medicamentos.getId()) {
                                    i=1;
                                }
                            }
                        }
                        if (i==0){
                            listaIndicador.add("NoAsignado");
                        }else{
                            listaIndicador.add("Asignado");
                        }
                    }

                    System.out.println(listaIndicador);
                    byte[] fotoBytes1 = medicamentos.getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

                    model.addAttribute("fotoBase64", fotoBase64);
                    model.addAttribute("medicamento", medicamentos);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes",list1);


                    return "superadmin/Plantilla_Vista_Ver_Medicamento";
                } else {
                    System.out.println("Se envio una nueva imagen, hay que actaulizar");
                    System.out.println(foto1.getSize());
                    try {
                        InputStream fotoStream=foto1.getInputStream();
                        byte[] fotoBytes=fotoStream.readAllBytes();
                        medicamentos.setFoto(fotoBytes);
                        medicamentosRepository.save(medicamentos);
                        List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
                        List<Sede> list1 = sedeRepository.findAll();

                        List<String> listaIndicador = new ArrayList<>();

                        for (Sede sede : list1) {
                            int i = 0;
                            for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                                if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                                    if (sedeHasMedicamentos.getId_medicamentos().getId() == medicamentos.getId()) {
                                        i=1;
                                    }
                                }
                            }
                            if (i==0){
                                listaIndicador.add("NoAsignado");
                            }else{
                                listaIndicador.add("Asignado");
                            }
                        }

                        System.out.println(listaIndicador);
                        byte[] fotoBytes1 = medicamentos.getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

                        model.addAttribute("fotoBase64", fotoBase64);
                        model.addAttribute("medicamento", medicamentos);
                        model.addAttribute("ListaIndicador", listaIndicador);
                        model.addAttribute("ListaSedes",list1);


                        return "superadmin/Plantilla_Vista_Ver_Medicamento";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }
    }



    //Guardar Medicamento
    /*@PostMapping("/Guardar_Medicamento")
    public String guardarNuevoMedicamento(@RequestParam("foto") Part foto,
                                          @RequestParam("nombre") String nombre,
                                          @RequestParam("descripcion") String descripcion,
                                          @RequestParam("inventario") int inventario,
                                          @RequestParam("precio_unidad") double precio_unidad,
                                          @RequestParam("fecha_ingreso") String fecha_ingreso,
                                          @RequestParam("categoria") String categoria,
                                          @RequestParam("dosis") String dosis,
                                          @RequestParam("borrado_logico") int borrado_logico,
                                          Model model,
                                          @RequestParam("id") int id) {


        Medicamentos medicamento = new Medicamentos();

            try {
                InputStream fotoStream=foto.getInputStream();
                byte[] fotoBytes=fotoStream.readAllBytes();
                        medicamento.setId(id);
                        medicamento.setNombre(nombre);
                        medicamento.setDescripcion(descripcion);
                        medicamento.setFoto(fotoBytes);
                        medicamento.setInventario(inventario);
                        medicamento.setPrecio_unidad(precio_unidad);
                        medicamento.setFecha_ingreso(fecha_ingreso);
                        medicamento.setCategoria(categoria);
                        medicamento.setDosis(dosis);
                        medicamento.setBorrado_logico(borrado_logico);
                        medicamentosRepository.save(medicamento);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);

        if (optMedicamento.isPresent()) {
            Medicamentos medicamento1 = optMedicamento.get();
            List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                    if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                        if (sedeHasMedicamentos.getId_medicamentos().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }

            System.out.println(listaIndicador);
            byte[] fotoBytes1 = medicamento1.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

            model.addAttribute("fotoBase64", fotoBase64);
            model.addAttribute("medicamento", medicamento1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);


            return "superadmin/Plantilla_Vista_Ver_Medicamento";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Medicamentos";
        }
    }
    */
    @GetMapping("/Asignar_Sede_Medicamento")
    public String AsignarMedicamento(Model model,
                                     @RequestParam("id") int id,
                                     @RequestParam("idsede") int idSede) {

        sedeHasMedicamentosRepository.AsignarSedeMedicamento(idSede,id);
        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);
        if (optMedicamento.isPresent()) {
            Medicamentos medicamento = optMedicamento.get();
            List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                    if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                        if (sedeHasMedicamentos.getId_medicamentos().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            byte[] fotoBytes1 = medicamento.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

            model.addAttribute("fotoBase64", fotoBase64);
            System.out.println(listaIndicador);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);


            return "superadmin/Plantilla_Vista_Actualizar_Medicamento";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Medicamentos";
        }

    }

    @GetMapping("/No_Asignar_Sede_Medicamento")
    public String NoAsignarMedicamento(Model model,
                                       @RequestParam("id") int id,
                                       @RequestParam("idsede") int idSede) {

        sedeHasMedicamentosRepository.NoAsignarSedeMedicamento(idSede,id);
        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);
        if (optMedicamento.isPresent()) {
            Medicamentos medicamento = optMedicamento.get();
            List<SedeHasMedicamentos> list = sedeHasMedicamentosRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (SedeHasMedicamentos sedeHasMedicamentos : list) {
                    if (sede.getId() == sedeHasMedicamentos.getId_sede().getId()) {
                        if (sedeHasMedicamentos.getId_medicamentos().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            byte[] fotoBytes1 = medicamento.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);

            model.addAttribute("fotoBase64", fotoBase64);
            System.out.println(listaIndicador);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);


            return "superadmin/Plantilla_Vista_Actualizar_Medicamento";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Medicamentos";
        }

    }

    //Eliminar Medicamento
    @PostMapping("/Eliminar_Medicamento")
    public String borrarTransportista(@RequestParam("id_medicamento") int id) {

        Optional<Medicamentos> optMedicamento = medicamentosRepository.findById(id);

        if (optMedicamento.isPresent()) {
            medicamentosRepository.borradoLogico(1,id);
        }
        return "redirect:/superadmin/Medicamentos";

    }


    //------------------------------------------------------------------------------------------------------------------

    //Nueva Sesion
    @PostMapping("/NuevaSesion")
    public String nuevaSesion(@RequestParam("id_usuario") int id, HttpServletRequest request, HttpServletResponse response, Model model) {
        // Cerrar la sesión actual
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        // Obtener el usuario por ID
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/error";
        }
        System.out.println(usuario.getNombres());
        System.out.println(usuario.getCorreo());
        System.out.println(usuario.getPunto());

        // Agregar las credenciales del usuario al modelo
        model.addAttribute("username", usuario.getCorreo());
        model.addAttribute("password", usuario.getPunto());

        // Devolver la vista HTML directamente
        return "autoLogin";
    }


    //Listar Usuarios
    @GetMapping("/Vista_Principal")
    public String Usuarios(Model model) {
        List<Usuario> lista = usuarioRepository.buscarDoctor(5,0);
        model.addAttribute("listTransportation", lista);
        List<Usuario> lista1 = usuarioRepository.buscarAdministrador(2,0);
        model.addAttribute("listTransportation1", lista1);
        List<Usuario> lista2 = usuarioRepository.buscarFarmacistaAceptado(3,0,"Aceptado");
        model.addAttribute("listTransportation2", lista2);
        List<Usuario> lista3 = usuarioRepository.buscarPaciente(4,0);
        model.addAttribute("listTransportation3", lista3);
        return "superadmin/Plantilla_Vista_Principal";
    }

    @PostMapping("/Guardar_Usuario")
    public String guardar_Doctor(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, Model model) {
        System.out.println(usuario.getNombres());
        System.out.println(usuario.getCorreo());
        System.out.println(usuario.getRol().getId());
        System.out.println(usuario.getRol().getNombre());
        if (usuario.getId() == 0) {
            if (bindingResult.hasErrors()) {
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();

                for (FieldError fieldError : fieldErrors) {
                    String fieldName = fieldError.getField();
                    String errorMessage = fieldError.getDefaultMessage();
                    logger.error("Error de validación en el campo {}: {}", fieldName, errorMessage);
                }
                if (usuario.getRol().getId() == 5) {
                    return "superadmin/Plantilla_Vista_Registro_Doctor";
                } else if (usuario.getRol().getId() == 2) {
                    List<Distrito> listaDistrito = distritoRepository.findAll();
                    model.addAttribute("listaDistritos",listaDistrito);
                    return "superadmin/Plantilla_Vista_Registro_Administrador";
                }
            } else {
                usuario.setCuenta_activada(1);
                usuario.setFecha_creacion(new Date());
                usuario.setContrasena(encoder.encode("" + usuario.getDni()));
                usuario.setPunto("" + usuario.getDni());
                usuarioRepository.save(usuario);

                String html = correo.construirCuerpo(usuario);
                correo.EnviarCorreo("Bienvenido a PildoPharm", html, usuario);

                if (usuario.getRol().getId() == 5) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                    i = 1;
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        } else {
                            listaIndicador.add("Asignado");
                        }
                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Ver_Doctor";
                }
                if (usuario.getRol().getId() == 2) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();
                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 2) {
                                    if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                        i = 1;
                                    } else {
                                        i = 2;
                                    }
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        }
                        if (i == 1) {
                            listaIndicador.add("Asignado");
                        }
                        if (i == 2) {
                            listaIndicador.add("NoDisponible");
                        }
                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Ver_Administrador";
                }
            }

        } else {

            if (bindingResult.hasErrors()) {
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();

                for (FieldError fieldError : fieldErrors) {
                    String fieldName = fieldError.getField();
                    String errorMessage = fieldError.getDefaultMessage();
                    logger.error("Error de validación en el campo {}: {}", fieldName, errorMessage);
                }


                if (usuario.getRol().getId() == 5) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                    i = 1;
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        } else {
                            listaIndicador.add("Asignado");
                        }
                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Actualizar_Doctor";

                } else if (usuario.getRol().getId() == 2) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();
                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 2) {
                                    if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                        i = 1;
                                    } else {
                                        i = 2;
                                    }
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        }
                        if (i == 1) {
                            listaIndicador.add("Asignado");
                        }
                        if (i == 2) {
                            listaIndicador.add("NoDisponible");
                        }
                    }
                    System.out.println(listaIndicador);
                    List<Distrito> listaDistrito = distritoRepository.findAll();
                    model.addAttribute("listaDistritos",listaDistrito);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Actualizar_Administrador";

                } else if (usuario.getRol().getId() == 3) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        int pertenencia = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 3) {
                                    i = i + 1;
                                    if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                        pertenencia = 1;
                                    }
                                }
                            }
                        }
                        if (pertenencia == 1) {
                            listaIndicador.add("Asignado");
                        }
                        if (pertenencia == 0 && i < 3) {
                            listaIndicador.add("Disponible");
                        }
                        if (pertenencia == 0 && i == 3) {
                            listaIndicador.add("NoDisponible");
                        }

                    }
                    System.out.println(listaIndicador);
                    List<Distrito> listaDistrito = distritoRepository.findAll();
                    model.addAttribute("listaDistritos",listaDistrito);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Actualizar_Farmacista";

                } else if (usuario.getRol().equals("Paciente")) {
                    List<Distrito> listaDistrito = distritoRepository.findAll();
                    List<Seguro> listaSeguro = seguroRepository.findAll();
                    model.addAttribute("listaDistritos",listaDistrito);
                    model.addAttribute("listaSeguros",listaSeguro);
                    model.addAttribute("usuario", usuario);
                    return "superadmin/Plantilla_Vista_Actualizar_Paciente";
                } //else if (usuario.getRol().equals("Superadmin")){
                //    model.addAttribute("usuario", usuario);
                //    return "superadmin/Perfil";
                //}
            } else {
                Usuario objUpd = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (objUpd != null) {
                    usuario.setFecha_creacion(objUpd.getFecha_creacion());
                }
                usuarioRepository.save(usuario);



                if (usuario.getRol().getId() == 5) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                    i = 1;
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        } else {
                            listaIndicador.add("Asignado");
                        }
                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Ver_Doctor";
                }
                if (usuario.getRol().getId() == 2) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();
                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 2) {
                                    if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                        i = 1;
                                    } else {
                                        i = 2;
                                    }
                                }
                            }
                        }
                        if (i == 0) {
                            listaIndicador.add("NoAsignado");
                        }
                        if (i == 1) {
                            listaIndicador.add("Asignado");
                        }
                        if (i == 2) {
                            listaIndicador.add("NoDisponible");
                        }
                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Ver_Administrador";
                }
                if (usuario.getRol().getId() == 3) {
                    List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                    List<Sede> list1 = sedeRepository.findAll();

                    List<String> listaIndicador = new ArrayList<>();

                    for (Sede sede : list1) {
                        int i = 0;
                        int pertenencia = 0;
                        for (UsuarioHasSede usuarioHasSede : list) {
                            if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 3) {
                                    i = i + 1;
                                    if (usuarioHasSede.getUsuario_id_usario().getId() == usuario.getId()) {
                                        pertenencia = 1;
                                    }
                                }
                            }
                        }
                        if (pertenencia == 1) {
                            listaIndicador.add("Asignado");
                        }
                        if (pertenencia == 0 && i < 3) {
                            listaIndicador.add("Disponible");
                        }
                        if (pertenencia == 0 && i == 3) {
                            listaIndicador.add("NoDisponible");
                        }

                    }
                    System.out.println(listaIndicador);
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("ListaIndicador", listaIndicador);
                    model.addAttribute("ListaSedes", list1);
                    return "superadmin/Plantilla_Vista_Ver_Farmacista";
                }
                if (usuario.getRol().getId() == 4) {
                    model.addAttribute("usuario", usuario);
                    List<Distrito> listaDistrito = distritoRepository.findAll();
                    List<Seguro> listaSeguro = seguroRepository.findAll();
                    model.addAttribute("listaDistritos",listaDistrito);
                    model.addAttribute("listaSeguros",listaSeguro);
                    return "superadmin/Plantilla_Vista_Ver_Paciente";
                }
                if (usuario.getRol().getId() == 1) {
                    model.addAttribute("usuario", usuario);
                    return "redirect:/superadmin/Ver_Perfil";
                }
            }

        }
        return "redirect:/superadmin/Vista_Principal";
    }
    @GetMapping("/Ver_Perfil")
    public String Actualizar_Superadmin(Model model,@ModelAttribute ("usuario") Usuario usuario) {
        usuario = usuarioRepository.buscarSuperadmin("Superadmin",0);
        model.addAttribute("usuario", usuario);
        return "superadmin/Perfil";
    }
    @GetMapping("/Registro_Administrador")
    public String Registro_Administrador(@ModelAttribute ("usuario") Usuario usuario, Model model) {
        List<Distrito> listaDistrito = distritoRepository.findAll();
        model.addAttribute("listaDistritos",listaDistrito);
        return "superadmin/Plantilla_Vista_Registro_Administrador";
    }

    @GetMapping("/Registro_Doctor")
    public String Registro_Doctor(@ModelAttribute ("usuario") Usuario usuario) {
        return "superadmin/Plantilla_Vista_Registro_Doctor";
    }

    @GetMapping("/Doctor")
    public String RegistroDoctor() {
        return "superadmin/IndexDoctor";
    }
    @GetMapping("/Administrador")
    public String RegistroAdministrador() {
        return "superadmin/IndexAdministrador";
    }


    @PostMapping("/Eliminar_Usuario")
    public String borrarDoctor(@RequestParam("id_usuario") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            usuarioRepository.borradoLogico(1,id);
        }
        return "redirect:/superadmin/Vista_Principal";
    }

    @GetMapping("/Asignar_Sede")
    public String Asignar(Model model,
                          @RequestParam("id") int id,
                          @RequestParam("idsede") int idSede) {

        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario usuario1 = usuario.get();
        if(usuario1.getRol().getNombre().equals("Doctor")) {
            usuarioHasSedeRepository.AsignarSede(id,idSede);
            Optional<Usuario> optusuario = usuarioRepository.findById(id);
            Usuario usuario2 = optusuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario2);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Doctor";
        }
        if(usuario1.getRol().getNombre().equals("Admin")) {
            usuarioHasSedeRepository.AsignarSedeBorrando(id);
            usuarioHasSedeRepository.AsignarSede(id,idSede);
            Optional<Usuario> optusuario = usuarioRepository.findById(id);
            Usuario usuario2 = optusuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<String> listaIndicador = new ArrayList<>();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario2);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Administrador";
        }
        if(usuario1.getRol().getNombre().equals("Farmacista")) {
            usuarioHasSedeRepository.AsignarSedeBorrando(id);
            usuarioHasSedeRepository.AsignarSede(id,idSede);
            Optional<Usuario> optusuario = usuarioRepository.findById(id);
            Usuario usuario2 = optusuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario2);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Farmacista";
        }
        if(usuario1.getRol().getNombre().equals("Paciente")) {
            model.addAttribute("usuario", usuario1);
            return "superadmin/Plantilla_Vista_Actualizar_Paciente";
        }
        return  "superadmin/Vista_Principal";

    }

    @GetMapping("/No_Asignar_Sede")
    public String NoAsignar(Model model,
                            @RequestParam("id") int id,
                            @RequestParam("idsede") int idSede) {

        usuarioHasSedeRepository.NoAsignarSede(id,idSede);
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario usuario1 = usuario.get();

        if(usuario1.getRol().getNombre().equals("Doctor")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Doctor";
        }
        if(usuario1.getRol().getNombre().equals("Admin")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<String> listaIndicador = new ArrayList<>();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Administrador";
        }
        if(usuario1.getRol().getNombre().equals("Farmacista")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<Distrito> listaDistrito = distritoRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistrito);
            return "superadmin/Plantilla_Vista_Actualizar_Farmacista";
        }
        if(usuario1.getRol().getNombre().equals("Paciente")) {
            model.addAttribute("usuario", usuario1);
            return "superadmin/Plantilla_Vista_Actualizar_Paciente";
        }
        return  "superadmin/Vista_Principal";

    }

    @GetMapping("/Pasar_Activo")
    public String Pasar_Activo(Model model,
                               @RequestParam("id") int id) {
        usuarioRepository.pasarActivo(1,id);
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario usuario1 = usuario.get();

        if(usuario1.getRol().getNombre().equals("Doctor")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Doctor";
        }
        if(usuario1.getRol().getNombre().equals("Admin")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Administrador";
        }
        if(usuario1.getRol().getNombre().equals("Farmacista")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Farmacista";
        }
        if(usuario1.getRol().getNombre().equals("Paciente")) {
            model.addAttribute("usuario", usuario1);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            List<Seguro> listaSeguro = seguroRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("listaSeguros",listaSeguro);
            return "superadmin/Plantilla_Vista_Actualizar_Paciente";
        }
        return  "superadmin/Vista_Principal";
    }

    @GetMapping("/Pasar_Inactivo")
    public String Pasar_Inactivo(Model model,
                                 @RequestParam("id") int id) {
        usuarioRepository.pasarInactivo(0,id);
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario usuario1 = usuario.get();

        if(usuario1.getRol().getNombre().equals("Doctor")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Doctor";
        }
        if(usuario1.getRol().getNombre().equals("Admin")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();
            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Administrador";
        }
        if(usuario1.getRol().getNombre().equals("Farmacista")) {
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario1);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Farmacista";
        }
        if(usuario1.getRol().getNombre().equals("Paciente")) {
            model.addAttribute("usuario", usuario1);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            List<Seguro> listaSeguro = seguroRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("listaSeguros",listaSeguro);
            return "superadmin/Plantilla_Vista_Actualizar_Paciente";
        }
        return  "superadmin/Vista_Principal";
    }

    //CRUD DOCTOR
    @GetMapping("/Ver_Doctor")
    public String Ver_Doctor(Model model,
                             @RequestParam("id") int id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Ver_Doctor";

        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }

    @GetMapping("/Editar_Doctor")
    public String editar_Doctor(Model model,
                                @RequestParam("id") int id,@ModelAttribute ("usuario") Usuario usuario) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                            i=1;
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }else{
                    listaIndicador.add("Asignado");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Doctor";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }


    //CRUD ADMINISTRADOR
    @GetMapping("/Ver_Administrador")
    public String Ver_Administrador(Model model,
                                    @RequestParam("id") int id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Ver_Administrador";

        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }


    @GetMapping("/Editar_Administrador")
    public String editar_Administrador(Model model,
                                       @RequestParam("id") int id,@ModelAttribute ("usuario") Usuario usuario) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            usuario = optUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Distrito> listaDistritos = distritoRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Admin")){
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                i=1;
                            }else{
                                i=2;
                            }
                        }
                    }
                }
                if (i==0){
                    listaIndicador.add("NoAsignado");
                }
                if (i==1){
                    listaIndicador.add("Asignado");
                }
                if (i==2){
                    listaIndicador.add("NoDisponible");
                }
            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            model.addAttribute("listaDistritos",listaDistritos);
            return "superadmin/Plantilla_Vista_Actualizar_Administrador";

        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }

    /*@PostMapping("/Aceptar_Administrador")
    public String Aceptar_Administrador(@RequestParam("id_usuario") int id) {
        usuarioRepository.aceptarAdministrador("Aceptado",id);

        return "redirect:/superadmin/Estado_Solicitudes_Farmacistas";
    }*/
    @PostMapping("/Rechazar_Administrador")
    public String Rechazar_Administrador(@RequestParam("id_usuario") int id,@RequestParam("textoRechazoNuevo") String textoRechazo) {
        System.out.println(id);
        System.out.println(textoRechazo);
        usuarioRepository.rechazarAdministrador("Rechazado",textoRechazo,id);

        return "redirect:/superadmin/Estado_Solicitudes_Farmacistas";
    }

    //CRUD FARMACISTA
    @GetMapping("/Ver_Farmacista")
    public String Ver_Farmacista(Model model,
                                 @RequestParam("id") int id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Ver_Farmacista";

        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }

    @GetMapping("/Editar_Farmacista")
    public String editar_Farmacista(Model model,
                                    @RequestParam("id") int id,@ModelAttribute ("usuario") Usuario usuario) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
            List<Sede> list1 = sedeRepository.findAll();

            List<String> listaIndicador = new ArrayList<>();

            for (Sede sede : list1) {
                int i = 0;
                int pertenencia = 0;
                for (UsuarioHasSede usuarioHasSede : list) {
                    if (sede.getId() == usuarioHasSede.getSede_id_sede().getId()) {
                        if(usuarioHasSede.getUsuario_id_usario().getRol().getNombre().equals("Farmacista")){
                            i=i+1;
                            if (usuarioHasSede.getUsuario_id_usario().getId() == id) {
                                pertenencia=1;
                            }
                        }
                    }
                }
                if (pertenencia == 1){
                    listaIndicador.add("Asignado");
                }
                if (pertenencia == 0 && i < 3){
                    listaIndicador.add("Disponible");
                }
                if (pertenencia == 0 && i == 3){
                    listaIndicador.add("NoDisponible");
                }

            }
            System.out.println(listaIndicador);
            List<Distrito> listaDistrito = distritoRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ListaIndicador", listaIndicador);
            model.addAttribute("ListaSedes",list1);
            return "superadmin/Plantilla_Vista_Actualizar_Farmacista";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }

    //CRUD PACIENTE
    @GetMapping("/Ver_Paciente")
    public String Ver_Paciente(Model model,
                               @RequestParam("id") int id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            System.out.println(usuario.getRol());
            System.out.println(usuario.getRol().getNombre());
            System.out.println(usuario.getRol().getId());
            model.addAttribute("usuario", usuario);
            return "superadmin/Plantilla_Vista_Ver_Paciente";
        } else {
            return "redirect:superadmin/Plantilla_Vista_Principal";
        }
    }

    @GetMapping("/Editar_Paciente")
    public String editar_Paciente(Model model,
                                  @RequestParam("id") int id,@ModelAttribute ("usuario") Usuario usuario) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            List<Distrito> listaDistrito = distritoRepository.findAll();
            List<Seguro> listaSeguro = seguroRepository.findAll();
            model.addAttribute("listaDistritos",listaDistrito);
            model.addAttribute("listaSeguros",listaSeguro);
            model.addAttribute("usuario", usuario);
            return "superadmin/Plantilla_Vista_Actualizar_Paciente";
        } else {
            return "redirect:/superadmin/Plantilla_Vista_Principal";
        }
    }

    //CRUD ESTADO SOLICITUDES FARMACISTAS
    @GetMapping("/Estado_Solicitudes_Farmacistas")
    public String Estado_Solicitudes_Farmacistas(Model model) {
        List<Usuario> lista = usuarioRepository.buscarFarmacista(3,0);
        model.addAttribute("listTransportation", lista);
        return "superadmin/Plantilla_Vista_Estado_Solicitudes_Farmacistas";
    }

    //CRUD ESTADO ENVÍOS
    @GetMapping("/Estado_Envios")
    public String Estado_Envios(Model model) {
        List<PedidosReposicion> lista = pedidosReposicionRepository.findAll();
        model.addAttribute("listTransportation",lista);
        return "superadmin/Plantilla_Vista_Estado_Envios";
    }
}

