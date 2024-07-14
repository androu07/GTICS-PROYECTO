package com.example.webapp.controller;

import com.example.webapp.dao.DaoDoctorAdministrador;
import com.example.webapp.dao.DaoFarmacista;
import com.example.webapp.dao.DataDao;
import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import com.example.webapp.util.Correo;
import com.example.webapp.util.Utileria;
import com.example.webapp.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    final
    DataDao dataDao;
    SeguroRepository seguroRepository;
    DistritoRepository distritoRepository;
    CodigoColegioRepository codigoColegioRepository;
    UsuarioHasSedeRepository usuarioHasSedeRepository;
    DaoDoctorAdministrador daoDoctorAdministrador;
    DaoFarmacista daoFarmacista;
    SedeRepository sedeRepository;



    public LoginController(DataDao dataDao, SeguroRepository seguroRepository, DistritoRepository distritoRepository,
                           CodigoColegioRepository codigoColegioRepository, UsuarioHasSedeRepository usuarioHasSedeRepository,
                           DaoDoctorAdministrador daoDoctorAdministrador, DaoFarmacista daoFarmacista, SedeRepository sedeRepository) {
        this.dataDao = dataDao;
        this.seguroRepository = seguroRepository;
        this.distritoRepository = distritoRepository;
        this.codigoColegioRepository = codigoColegioRepository;
        this.usuarioHasSedeRepository = usuarioHasSedeRepository;
        this.daoDoctorAdministrador = daoDoctorAdministrador;
        this.daoFarmacista = daoFarmacista;
        this.sedeRepository = sedeRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Correo correo;

    @Autowired
    private Utileria util;
    @Autowired
    private Utils utils;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private HttpSession session;

    @GetMapping(value = {"/acceso"})
    public String Login(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        if (rol.equalsIgnoreCase("Paciente")) {
            return "redirect:/paciente/inicio";
        }
        if (rol.equalsIgnoreCase("Superadmin")) {
            return "redirect:/superadmin/";
        }
        if (rol.equalsIgnoreCase("Farmacista")) {
            return "redirect:/farmacista/medicamentos";
        }
        if (rol.equalsIgnoreCase("Admin")) {
            return "redirect:/admin/medicamentos";
        }
        return "/login";
    }

    @GetMapping("/reestablecer/generar_token")
    @ResponseBody
    public Map<String, String> recuperarPassword(String correo) {
        List<Usuario> usuarios = usuarioRepository.buscarPorCorreo(correo);
        int result = 0;
        Map<String, String> response = new HashMap<>();

        try {
            if (usuarios.size() > 0) {
                for (Usuario obj : usuarios) {

                    if (obj.getCuenta_activada() == 1) {
                        obj.setToken_recuperacion(util.GenerarToken());

                        String cuerpo = this.correo.construirCuerpoRecuperarPassword(obj);
                        boolean envio = this.correo.EnviarCorreo("Reestablecer contraseña", cuerpo, obj);
                        if (envio) {
                            result = usuarioRepository.actualizarFechaYTokenRecuperacion(obj.getToken_recuperacion(),
                                    obj.getId());
                        }

                        if (result > 0) {
                            response.put("response", "OK");
                        } else {
                            response.put("response", "No se pudo enviar correo para recuperar contraseña.");
                        }
                    } else {
                        response.put("response", "Lo sentimos! Su cuenta no se encuentra activada.");
                    }
                }
            } else {
                response.put("response", "El correo proporcionado no se encuentra registrado!");
            }
        } catch (Exception ex) {
            response.put("error", ex.getMessage());
        }

        return response;
    }

    @GetMapping("/reestablecer/password")
    public String VistaNuevoPassword(@RequestParam(name = "token", required = false) String token,
                                     RedirectAttributes attributes, Model model) {
        if (token == null || token.isEmpty()) {
            attributes.addFlashAttribute("error", "Incorrecto! El token de acceso no es valido!");
            return "redirect:/login";
        }
        Usuario objUsu = usuarioRepository.buscarPorToken(token);

        if (objUsu == null) {
            attributes.addFlashAttribute("error", "Token incorrecto!");
            return "redirect:/login";
        }

        String msg = usuarioRepository.validarToken(objUsu.getId(), token);

        if (!msg.equals("OK")) {
            attributes.addFlashAttribute("error", msg);
            return "redirect:/login";
        }

        model.addAttribute("mensaje", msg);
        model.addAttribute("usuario", objUsu);

        return "sistema/HomeNuevoPassword";
    }

    @GetMapping("/reestablecer/password/save")
    @ResponseBody
    public Map<String, String> GuardarPassword(@RequestParam(name = "password", required = true) String password,
                                               int id,
                                               RedirectAttributes attributes, Model model) {
        Map<String, String> response = new HashMap<>();
        try {
            int result = 0;
            int longitud = 0;
            String mensajeDeValidacion = "Su nueva contraseña debe tener al menos 6 caracteres con 1 mayuscula, 1 minuscula y 1 numero.";
            String passwordcopia = password;

            if (passwordcopia.length() >= 6) {
                longitud = 1;
            }

            boolean hasUpper = false;
            boolean hasLower = false;
            boolean hasDigit = false;

            if(longitud == 1) {
                for (char c : password.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        hasUpper = true;
                    } else if (Character.isLowerCase(c)) {
                        hasLower = true;
                    } else if (Character.isDigit(c)) {
                        hasDigit = true;
                    }
                }
                // Si todas las condiciones se cumplen, no necesitamos seguir verificando
                if (hasUpper && hasLower && hasDigit) {
                    password = encoder.encode(password);
                    result = usuarioRepository.actualizarPasswordyEstado(password, passwordcopia, id);
                }
            }
            if (result > 0) {
                response.put("response", "OK");
            } else {
                response.put("response", mensajeDeValidacion);
            }

        } catch (Exception ex) {
            response.put("response", ex.getMessage());
        }

        return response;
    }

    @GetMapping(value = {"/", ""})
    public String redirigirLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginWindow(Authentication auth, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String errorMessage = (String) session.getAttribute("error");
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            session.removeAttribute("error");
        }
        try {
            Usuario usuario = usuarioRepository.findByCorreo(auth.getName());
            String rol = usuario.getRol().getNombre();
            if (rol.equals("Paciente")) {
                return "redirect:/paciente/inicio";
            }
            if (rol.equals("Superadmin")) {
                return "redirect:/superadmin/";
            }
            if (rol.equals("Farmacista")) {
                return "redirect:/farmacista/medicamentos";
            }
            if (rol.equals("Admin")) {
                return "redirect:/admin/medicamentos";
            }
        }
        catch (Exception ex) {
            return "sistema/Index";
        }
        return "sistema/Index";
    }

    @PostMapping("/registro")
    public String validarPersona(@ModelAttribute("nuevoRol") String nuevoRol,@ModelAttribute("dni") String dni, RedirectAttributes redirectAttributes, Model model) {
        int rol = Integer.parseInt(nuevoRol);
        System.out.println("Este es el rol enviado ###########################################");
        System.out.println(rol);
        try {
            Data data = dataDao.buscarPorDni(dni);
            Integer dniInt = Integer.valueOf(dni);
            List<Integer> listaDNI = usuarioRepository.listaDniExistentes();
            boolean encontrado = false;
            for (Integer dni1 : listaDNI) {
                if (dni1.equals(dniInt)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                try {
                    //NOMBRES -> Nombres
                    String nombreCompleto = data.getNombres();
                    String[] palabras1 = nombreCompleto.split(" ");
                    StringBuilder nombreFormateado = new StringBuilder();
                    for (String palabra : palabras1) {
                        if (!nombreFormateado.toString().isEmpty()) {
                            nombreFormateado.append(" ");
                        }
                        nombreFormateado.append(palabra.substring(0, 1).toUpperCase())
                                .append(palabra.substring(1).toLowerCase());
                    }

                    //APELLIDOS -> Apellidos
                    String apellidoCompleto = data.getApellido_paterno() + " " + data.getApellido_materno();
                    String[] palabras2 = apellidoCompleto.split(" ");
                    StringBuilder apellidoFormateado = new StringBuilder();
                    for (String palabra : palabras2) {
                        if (!apellidoFormateado.toString().isEmpty()) {
                            apellidoFormateado.append(" ");
                        }
                        apellidoFormateado.append(palabra.substring(0, 1).toUpperCase())
                                .append(palabra.substring(1).toLowerCase());
                    }


                    if(rol == 4){
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());
                        model.addAttribute("listaSeguros", seguroRepository.findAll());
                        model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "sistema/FormRegistro";
                    }else if(rol == 5){
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());
                        model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                        //model.addAttribute("listaSeguros", seguroRepository.findAll());
                        //model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "superadmin/FormRegistroDoctor";
                    }else if(rol == 2) {
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());
                        //model.addAttribute("listaSeguros", seguroRepository.findAll());
                        //model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "superadmin/FormRegistroAdministrador";
                    }
                }
                catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "El DNI ingresado no es valido.");
                    return "redirect:/login";
                }
            }
            else{
                redirectAttributes.addFlashAttribute("error", "El DNI ingresado ya esta registrado en el sistema.");
                return "redirect:/login";
            }
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "El DNI ingresado no es valido.");
            return "redirect:/login";
        }
        return "redirect:/login";
    }

    @PostMapping("/registroDoctorAdministrador")
    public String validarDoctorAdministrador(@ModelAttribute("nuevoRol") String nuevoRol,@ModelAttribute("dni") String dni,
                                             RedirectAttributes redirectAttributes, Model model) {
        int rol = Integer.parseInt(nuevoRol);
        System.out.println("Este es el rol enviado ###########################################");
        System.out.println(rol);
        try {
            Data data = daoDoctorAdministrador.buscarPorDni(dni);
            Integer dniInt = Integer.valueOf(dni);
            List<Integer> listaDNI = usuarioRepository.listaDniExistentes();
            boolean encontrado = false;
            for (Integer dni1 : listaDNI) {
                if (dni1.equals(dniInt)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                try {
                    //NOMBRES -> Nombres
                    String nombreCompleto = data.getNombres();
                    String[] palabras1 = nombreCompleto.split(" ");
                    StringBuilder nombreFormateado = new StringBuilder();
                    for (String palabra : palabras1) {
                        if (!nombreFormateado.toString().isEmpty()) {
                            nombreFormateado.append(" ");
                        }
                        nombreFormateado.append(palabra.substring(0, 1).toUpperCase())
                                .append(palabra.substring(1).toLowerCase());
                    }

                    //APELLIDOS -> Apellidos
                    String apellidoCompleto = data.getApellido_paterno() + " " + data.getApellido_materno();
                    String[] palabras2 = apellidoCompleto.split(" ");
                    StringBuilder apellidoFormateado = new StringBuilder();
                    for (String palabra : palabras2) {
                        if (!apellidoFormateado.toString().isEmpty()) {
                            apellidoFormateado.append(" ");
                        }
                        apellidoFormateado.append(palabra.substring(0, 1).toUpperCase())
                                .append(palabra.substring(1).toLowerCase());
                    }


                    if(rol == 5){
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());
                        model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                        //model.addAttribute("listaSeguros", seguroRepository.findAll());
                        //model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "superadmin/FormRegistroDoctor";
                    }else if(rol == 2) {
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());

                        List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                        List<Sede> list1 = sedeRepository.findAll();

                        List<Sede> listaSedes = new ArrayList<>();

                        int i=2;

                        for (Sede sede : list1) {
                            for (UsuarioHasSede usuarioHasSede : list) {
                                if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 2) {
                                    i = 0;
                                    if (usuarioHasSede.getSede_id_sede().getId() == sede.getId()) {
                                        i = 1;
                                        break;
                                    }
                                }
                            }
                            if (i == 0) {
                                listaSedes.add(sede);
                            }
                        }


                        model.addAttribute("listaSedes", listaSedes);
                        //model.addAttribute("listaSeguros", seguroRepository.findAll());
                        //model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "superadmin/FormRegistroAdministrador";
                    }else if(rol == 3) {
                        model.addAttribute("nombresValidados", nombreFormateado.toString());
                        model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                        model.addAttribute("dniValidado", data.getDni());
                        model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                        //model.addAttribute("listaSeguros", seguroRepository.findAll());
                        //model.addAttribute("listaDistritos", distritoRepository.findAll());
                        return "admin/FormRegistroFarmacista";
                    }
                }
                catch (Exception e) {
                    model.addAttribute("dniInvalido", "El DNI ingresado no es valido");
                    System.out.println("Esta es la validacion más interna");
                    if(rol == 5){
                        return "superadmin/IndexDoctor";
                    }else if(rol == 2){
                        return "superadmin/IndexAdministrador";
                    }else if(rol == 3){
                        return "admin/indexFarmacista";
                    }
                }
            }
            else{
                model.addAttribute("dniRepetido", "El DNI ingresado ya esta registrado en el sistema.");
                if(rol == 5){
                    return "superadmin/IndexDoctor";
                }else if(rol == 2){
                    return "superadmin/IndexAdministrador";
                }else if(rol == 3){
                    return "admin/indexFarmacista";
                }
            }
        }
        catch (Exception e) {
            model.addAttribute("dniInvalido", "El DNI ingresado no es valido");
            if(rol == 5){
                return "superadmin/IndexDoctor";
            }else if(rol == 2){
                return "superadmin/IndexAdministrador";
            }else if(rol == 3){
                return "admin/indexFarmacista";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/registro/usuario")
    public String registrarUsuario(@RequestParam(value = "sedeId", required = false) Integer sedeId,@ModelAttribute("nuevoRol") String nuevoRol,@ModelAttribute("usuario") @Valid Usuario usuario,
                                   BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        int rolNuevo = Integer.parseInt(nuevoRol);
        System.out.println("Este es el rol enviado desde el formulario ###########################################");
        System.out.println(rolNuevo);
        System.out.println("Esta es la sede ###########################################");
        System.out.println(sedeId);

        boolean encontrado = false;

        if (usuario.getCorreo() != null){
            String correo = usuario.getCorreo();
            List<String> listaCorreos = usuarioRepository.listaCorreosExistentes();
            for (String correo1 : listaCorreos) {
                if (correo1.equals(correo)) {
                    encontrado = true;
                    break;
                }
            }
        }

        boolean encontrado1 = false;

        if (usuario.getCodigo_colegio() != null){
            int codigo = usuario.getCodigo_colegio().getId();
            List<Usuario> listaCodigos = usuarioRepository.findAll();
            for (int i = 0; i < listaCodigos.size(); i++) {
                if (codigo == listaCodigos.get(i).getCodigo_colegio().getId()) {
                    encontrado1 = true;
                    break;}
            }
        }

        if (rolNuevo == 4){
            if (bindingResult.hasErrors() || usuario.getDistrito() == null || usuario.getSeguro() == null || encontrado) {
                if (usuario.getDistrito() == null){
                    model.addAttribute("distritoError", "Debe seleccionar un distrito");
                }
                if (usuario.getSeguro() == null){
                    model.addAttribute("seguroError", "Debe seleccionar un seguro");
                }
                if (encontrado) {
                    model.addAttribute("correoExistenteError", "El correo ingresado ya ha sido registrado.");
                }

                model.addAttribute("nombresValidados", usuario.getNombres());
                model.addAttribute("apellidosValidados", usuario.getApellidos());
                model.addAttribute("dniValidado", usuario.getDni());
                model.addAttribute("listaSeguros", seguroRepository.findAll());
                model.addAttribute("listaDistritos", distritoRepository.findAll());
                return "sistema/FormRegistro";
            }else{
                Roles rol = new Roles();
                rol.setId(4); // ROL PACIENTE

                usuario.setFecha_creacion(new Date());
                usuario.setCodigo_colegiatura("Sin-Codigo");
                usuario.setRol(rol);
                usuario.setContrasena("");

                try {
                    usuario.setToken_recuperacion(util.GenerarToken()); // Token de ACTIVACION
                    usuario.setCuenta_activada(0);
                    usuarioRepository.save(usuario);

                    String cuerpo = this.correo.construirCuerpoActivarCuenta(usuario);
                    boolean envio = this.correo.EnviarCorreo("Activar cuenta", cuerpo, usuario);

                    redirectAttributes.addFlashAttribute("success", "Registro exitoso. Se te ha enviado un correo de notificación sobre el estado de tu registro para su activación.");
                } catch (Exception e) {
                    logger.error("Error al registrar el usuario", e);
                    redirectAttributes.addFlashAttribute("usuario", usuario);
                    redirectAttributes.addFlashAttribute("error", "Hubo un problema al registrar el usuario. Por favor, Intentelo de nuevo.");
                }

                return "redirect:/login";
            }
        }else if(rolNuevo == 5){
            System.out.println("Codigo de Colegiatura////////////////////////////////////////////////////");
            if (bindingResult.hasErrors() ||  usuario.getCodigo_colegio() == null || encontrado || encontrado1) {
                if (usuario.getCodigo_colegio() == null){
                    model.addAttribute("codigoError1", "Debe seleccionar un codigo de colegiatura");
                }
                if (encontrado1){
                    model.addAttribute("codigoError", "El codigo de colegiatura ya ha sido registrado en el sistema");
                }

                if (encontrado) {
                    model.addAttribute("correoExistenteError", "El correo ingresado ya ha sido registrado.");
                }



                String dni = usuario.getDni();
                String dniString = String.valueOf(dni);
                Data data = daoDoctorAdministrador.buscarPorDni(dniString);

                //NOMBRES -> Nombres
                String nombreCompleto = data.getNombres();
                String[] palabras1 = nombreCompleto.split(" ");
                StringBuilder nombreFormateado = new StringBuilder();
                for (String palabra : palabras1) {
                    if (!nombreFormateado.toString().isEmpty()) {
                        nombreFormateado.append(" ");
                    }
                    nombreFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                //APELLIDOS -> Apellidos
                String apellidoCompleto = data.getApellido_paterno() + " " + data.getApellido_materno();
                String[] palabras2 = apellidoCompleto.split(" ");
                StringBuilder apellidoFormateado = new StringBuilder();
                for (String palabra : palabras2) {
                    if (!apellidoFormateado.toString().isEmpty()) {
                        apellidoFormateado.append(" ");
                    }
                    apellidoFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                model.addAttribute("nombresValidados", nombreFormateado.toString());
                model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                model.addAttribute("dniValidado", data.getDni());
                model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                //model.addAttribute("listaSeguros", seguroRepository.findAll());
                //model.addAttribute("listaDistritos", distritoRepository.findAll());
                return "superadmin/FormRegistroDoctor";
            }else{
                Roles rol = new Roles();
                rol.setId(5); // ROL DOCTOR

                usuario.setFecha_creacion(new Date());
                usuario.setCodigo_colegiatura("Sin-Codigo");
                usuario.setRol(rol);
                usuario.setContrasena("");
                usuario.setCuenta_activada(0);
                usuario.setEstado(1);
                redirectAttributes.addFlashAttribute("msg", "Doctor creado exitosamente");
                usuarioRepository.save(usuario);

                return "redirect:/superadmin/Vista_Principal";
            }

        }else if(rolNuevo == 2) {
            System.out.println("Codigo de Colegiatura////////////////////////////////////////////////////");
            System.out.println(usuario.getCodigo_colegio().getId());
            if (bindingResult.hasErrors() || encontrado || sedeId == null) {
                if (usuario.getCodigo_colegio() == null) {
                    model.addAttribute("codigoError", "Debe seleccionar un codigo de colegiatura");
                }
                if (encontrado) {
                    model.addAttribute("correoExistenteError", "El correo ingresado ya ha sido registrado.");
                }
                if (sedeId == null){
                    model.addAttribute("codigoError1", "Debe seleccionar una de las sedes disponibles");
                }
                String dni = usuario.getDni();
                String dniString = String.valueOf(dni);
                Data data = daoDoctorAdministrador.buscarPorDni(dniString);

                //NOMBRES -> Nombres
                String nombreCompleto = data.getNombres();
                String[] palabras1 = nombreCompleto.split(" ");
                StringBuilder nombreFormateado = new StringBuilder();
                for (String palabra : palabras1) {
                    if (!nombreFormateado.toString().isEmpty()) {
                        nombreFormateado.append(" ");
                    }
                    nombreFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                //APELLIDOS -> Apellidos
                String apellidoCompleto = data.getApellido_paterno() + " " + data.getApellido_materno();
                String[] palabras2 = apellidoCompleto.split(" ");
                StringBuilder apellidoFormateado = new StringBuilder();
                for (String palabra : palabras2) {
                    if (!apellidoFormateado.toString().isEmpty()) {
                        apellidoFormateado.append(" ");
                    }
                    apellidoFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                List<UsuarioHasSede> list = usuarioHasSedeRepository.findAll();
                List<Sede> list1 = sedeRepository.findAll();

                List<Sede> listaSedes = new ArrayList<>();

                int i=2;

                for (Sede sede : list1) {
                    for (UsuarioHasSede usuarioHasSede : list) {
                        if (usuarioHasSede.getUsuario_id_usario().getRol().getId() == 2) {
                            i = 0;
                            if (usuarioHasSede.getSede_id_sede().getId() == sede.getId()) {
                                i = 1;
                                break;
                            }
                        }
                    }
                    if (i == 0) {
                        listaSedes.add(sede);
                    }
                }

                model.addAttribute("listaSedes", listaSedes);
                model.addAttribute("nombresValidados", nombreFormateado.toString());
                model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                model.addAttribute("dniValidado", data.getDni());
                //model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                //model.addAttribute("listaSeguros", seguroRepository.findAll());
                //model.addAttribute("listaDistritos", distritoRepository.findAll());
                return "superadmin/FormRegistroAdministrador";
            } else {
                Roles rol = new Roles();
                rol.setId(2); // ROL DOCTOR
                usuario.setFecha_creacion(new Date());
                usuario.setCodigo_colegiatura("Sin-Codigo");
                usuario.setRol(rol);
                usuario.setContrasena("");
                redirectAttributes.addFlashAttribute("msg", "Administrador creado exitosamente");

                try {
                    usuario.setToken_recuperacion(util.GenerarToken()); // Token de ACTIVACION
                    usuario.setCuenta_activada(0);
                    usuarioRepository.save(usuario);
                    int idUsuario = usuarioRepository.buscarUltimo();
                    usuarioHasSedeRepository.AsignarSede(idUsuario,sedeId);

                    String cuerpo = this.correo.construirCuerpoActivarCuenta(usuario);
                    boolean envio = this.correo.EnviarCorreo("Activar cuenta", cuerpo, usuario);

                    redirectAttributes.addFlashAttribute("success", "Registro exitoso. Se te ha enviado un correo de notificación sobre el estado de tu registro para su activación.");
                } catch (Exception e) {
                    logger.error("Error al registrar el usuario", e);
                    redirectAttributes.addFlashAttribute("usuario", usuario);
                    redirectAttributes.addFlashAttribute("error", "Hubo un problema al registrar el usuario. Por favor, Intentelo de nuevo.");
                }

                return "redirect:/superadmin/Vista_Principal";
            }
        }else if(rolNuevo == 3){
            Usuario admin = (Usuario) session.getAttribute("usuario");
            int idSede = usuarioHasSedeRepository.buscarSedeDeUsuario(admin.getId());

            System.out.println("Codigo de Colegiatura////////////////////////////////////////////////////");
            if (bindingResult.hasErrors() ||  usuario.getCodigo_colegio() == null || encontrado || encontrado1) {
                if (usuario.getCodigo_colegio() == null){
                    model.addAttribute("codigoError1", "Debe seleccionar un codigo de colegiatura");
                }
                if (encontrado1){
                    model.addAttribute("codigoError", "El codigo de colegiatura ya ha sido registrado en el sistema");
                }

                if (encontrado) {
                    model.addAttribute("correoExistenteError", "El correo ingresado ya ha sido registrado.");
                }



                String dni = usuario.getDni();
                String dniString = String.valueOf(dni);
                Data data = daoFarmacista.buscarPorDni(dniString);

                //NOMBRES -> Nombres
                String nombreCompleto = data.getNombres();
                String[] palabras1 = nombreCompleto.split(" ");
                StringBuilder nombreFormateado = new StringBuilder();
                for (String palabra : palabras1) {
                    if (!nombreFormateado.toString().isEmpty()) {
                        nombreFormateado.append(" ");
                    }
                    nombreFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                //APELLIDOS -> Apellidos
                String apellidoCompleto = data.getApellido_paterno() + " " + data.getApellido_materno();
                String[] palabras2 = apellidoCompleto.split(" ");
                StringBuilder apellidoFormateado = new StringBuilder();
                for (String palabra : palabras2) {
                    if (!apellidoFormateado.toString().isEmpty()) {
                        apellidoFormateado.append(" ");
                    }
                    apellidoFormateado.append(palabra.substring(0, 1).toUpperCase())
                            .append(palabra.substring(1).toLowerCase());
                }

                model.addAttribute("nombresValidados", nombreFormateado.toString());
                model.addAttribute("apellidosValidados", apellidoFormateado.toString());
                model.addAttribute("dniValidado", data.getDni());
                model.addAttribute("listaCodigosColegio", codigoColegioRepository.findAll());
                //model.addAttribute("listaSeguros", seguroRepository.findAll());
                //model.addAttribute("listaDistritos", distritoRepository.findAll());
                return "admin/FormRegistroFarmacista";
            }else{
                Roles rol = new Roles();
                rol.setId(3); // ROL Farmacista

                usuario.setFecha_creacion(new Date());
                usuario.setCodigo_colegiatura("Sin-Codigo");
                usuario.setRol(rol);
                usuario.setContrasena("");
                usuario.setCuenta_activada(0);
                usuario.setEstado(1);
                redirectAttributes.addFlashAttribute("msg", "Farmacista creado exitosamente");
                usuarioRepository.save(usuario);
                usuarioHasSedeRepository.AsignarSede(usuario.getId(), idSede);

                return "redirect:/admin/farmacistas";
            }

        }
        return "redirect:/login";
    }

    @PostMapping("/Aceptar_Administrador")
    public String Aceptar_Administrador(@RequestParam("id_usuario") int id) {
        usuarioRepository.aceptarAdministrador("Aceptado", id);
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        Usuario usuario = optUsuario.get();

        try {
            usuario.setToken_recuperacion(util.GenerarToken()); // Token de ACTIVACION
            usuario.setCuenta_activada(0);
            usuarioRepository.save(usuario);

            String cuerpo = this.correo.construirCuerpoActivarCuenta(usuario);
            boolean envio = this.correo.EnviarCorreo("Activar cuenta", cuerpo, usuario);

            //redirectAttributes.addFlashAttribute("success", "Registro exitoso. Se te ha enviado un correo de notificación sobre el estado de tu registro para su activación.");
        } catch (Exception e) {
            logger.error("Error al registrar el usuario", e);
            //redirectAttributes.addFlashAttribute("usuario", usuario);
            //redirectAttributes.addFlashAttribute("error", "Hubo un problema al registrar el usuario. Por favor, Intentelo de nuevo.");
        }

        return "redirect:/superadmin/Estado_Solicitudes_Farmacistas";
    }



    @GetMapping("/reestablecer/activar")
    public String VistaActivarCuentaPassword(@RequestParam(name = "token", required = false) String token,
                                             RedirectAttributes attributes, Model model) {
        if (token == null || token.isEmpty()) {
            attributes.addFlashAttribute("error", "Incorrecto! El token de acceso no es valido!");
            return "redirect:/login";
        }
        Usuario objUsu = usuarioRepository.buscarPorToken(token);

        if (objUsu == null) {
            attributes.addFlashAttribute("error", "Su token es invalido o su cuenta ya ha sido reestablecida!");
            return "redirect:/login";
        }

        if (objUsu.getCuenta_activada() == 1) {
            attributes.addFlashAttribute("error", "Su cuenta ya se encuentra activada!");
            return "redirect:/login";
        }
        model.addAttribute("usuario", objUsu);
        System.out.println("HOLAAAAAAAAAAAAA ID " + objUsu.getId());

        return "sistema/ActivarNuevoPassword";
    }

    @GetMapping("/reestablecer/activar/save")
    @ResponseBody
    public Map<String, String> GuardarActivarPassword(@RequestParam(name = "password", required = true) String password,
                                                      int id,
                                                      RedirectAttributes attributes, Model model) {
        System.out.println("HOLAAAAAAAAAAAAA CONTRASEÑA " + password);
        Map<String, String> response = new HashMap<>();
        try {
            int result = 0;
            int longitud = 0;
            String mensajeDeValidacion = "Su contraseña debe tener al menos 6 caracteres con 1 mayuscula, 1 minuscula y 1 numero.";
            String passwordcopia = password;

            if (passwordcopia.length() >= 6) {
                longitud = 1;
            }

            boolean hasUpper = false;
            boolean hasLower = false;
            boolean hasDigit = false;

            if(longitud == 1) {
                for (char c : password.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        hasUpper = true;
                    } else if (Character.isLowerCase(c)) {
                        hasLower = true;
                    } else if (Character.isDigit(c)) {
                        hasDigit = true;
                    }
                }
                // Si todas las condiciones se cumplen, no necesitamos seguir verificando
                if (hasUpper && hasLower && hasDigit) {

                    System.out.println("HOLAAAAA CONTRASEÑA VALIDA " + password);
                    password = encoder.encode(password);
                    System.out.println("HOLAAAAA CONTRASEÑA HASHEADA " + password);
                    result = usuarioRepository.actualizarPasswordyEstado(password, passwordcopia, id);
                    System.out.println("HOLAAAAA CONFIRMACION " + result);

                    password = encoder.encode(password);
                    result = usuarioRepository.actualizarPasswordyEstado(password, passwordcopia, id);

                }
            }
            if (result > 0) {
                response.put("response", "OK");
            } else {
                response.put("response", mensajeDeValidacion);
            }

        } catch (Exception ex) {
            response.put("response", ex.getMessage());
        }

        return response;
    }

    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }


    @GetMapping("/perfil")
    public String Ver_Perfil(Model model, Authentication auth) {
        String username = utils.obtenerUsername(auth);
        List<Usuario> listUsu = usuarioRepository.buscarPorCorreo(username);
        Usuario obj = listUsu.get(0);
        List<Distrito> listaDistritos = distritoRepository.findAll();
        if (obj == null) {
            return "redirect:/login";
        }
        model.addAttribute("listaDistritos", listaDistritos);
        model.addAttribute("usuario", obj);
        return "sistema/Perfil";
    }

    @PostMapping("/perfil/guardar")
    public String GuardarPerfil(Model model, Usuario obj,
                                RedirectAttributes attributes,
                                @RequestParam("accion") String accion
    ) {
        Usuario data = null;
        try {
            data = usuarioRepository.findById(obj.getId()).orElse(null);

            if (accion.equalsIgnoreCase("perfil")) {
                data.setNombres(obj.getNombres().trim());
                data.setApellidos(obj.getApellidos().trim());
                data.setTelefono(obj.getTelefono().trim());
                data.setDni(obj.getDni());
                data.setCorreo(obj.getCorreo().trim());

                if (obj.getContrasena() != null && obj.getContrasena().trim().length() > 0) {
                    data.setContrasena(obj.getContrasena().trim());
                }
            }

            if (accion.equalsIgnoreCase("avatar")) {
                data.setImagen(obj.getImagen().trim());
            }

            if (accion.equalsIgnoreCase("direccion")) {
                data.setReferencia(obj.getReferencia().trim());
                data.setDistrito(obj.getDistrito());
                data.setDireccion(obj.getDireccion().trim());
            }

            usuarioRepository.save(data);

            if (obj.getId() > 0) {
                attributes.addFlashAttribute("success", "Datos actualizados!!");
                session.setAttribute("avatar", data.getImagen());
                return "redirect:/perfil";
            }
            model.addAttribute("error", "No se pudo actualizar datos!");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
        }
        List<Distrito> listaDistritos = distritoRepository.findAll();
        model.addAttribute("listaDistritos", listaDistritos);
        model.addAttribute("usuario", data);
        return "sistema/Perfil";
    }
}
