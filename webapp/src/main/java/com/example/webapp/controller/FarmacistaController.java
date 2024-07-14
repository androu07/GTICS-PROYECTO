package com.example.webapp.controller;
import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class FarmacistaController {

    /*Variables Final de los repository*/
    final
    MedicamentosRepository medicamentosRepository;
    UsuarioRepository usuarioRepository;
    SedeRepository sedeRepository;
    PedidosPacienteRepository pedidosPacienteRepository;
    CarritoRepository carritoRepository;
    PedidosPacienteRecojoRepository pedidosPacienteRecojoRepository;
    MedicamentosRecojoRepository medicamentosRecojoRepository;
    MedicamentosDelPedidoRepository medicamentosDelPedidoRepository;
    SeguroRepository seguroRepository;
    DistritoRepository distritoRepository;
    TarjetaRepository tarjetaRepository;

    public FarmacistaController(MedicamentosRepository medicamentosRepository,
                              UsuarioRepository usuarioRepository,
                              SedeRepository sedeRepository,
                              PedidosPacienteRepository pedidosPacienteRepository,
                              CarritoRepository carritoRepository,
                              PedidosPacienteRecojoRepository pedidosPacienteRecojoRepository,
                              MedicamentosRecojoRepository medicamentosRecojoRepository,
                              MedicamentosDelPedidoRepository medicamentosDelPedidoRepository,
                              SeguroRepository seguroRepository,
                              DistritoRepository distritoRepository, TarjetaRepository tarjetaRepository) {

        this.medicamentosRepository = medicamentosRepository;
        this.sedeRepository = sedeRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidosPacienteRepository = pedidosPacienteRepository;
        this.carritoRepository = carritoRepository;
        this.pedidosPacienteRecojoRepository = pedidosPacienteRecojoRepository;
        this.medicamentosRecojoRepository = medicamentosRecojoRepository;
        this.medicamentosDelPedidoRepository = medicamentosDelPedidoRepository;
        this.seguroRepository = seguroRepository;
        this.distritoRepository = distritoRepository;
        this.tarjetaRepository = tarjetaRepository;
    }
    public static int numeroAleatorioEnRango(int minimo, int maximo) {
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }
    /*---------------------------------------*/

    @GetMapping("/farmacista/medicamentos")
    public String listarMedicamentos(Model model, Authentication authentication){

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();

        Integer idpedido = carritoRepository.idPedidoRegistrando(usuid);
        if(idpedido != null){
            carritoRepository.borrarMedicamentosAlCancelar(usuid, idpedido);
            carritoRepository.cancelarPedidoDely(usuid);
        }

        carritoRepository.cancelarPedidoReco(usuid);
        String banco = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String numpedido = "";

        List<Medicamentos> lista = medicamentosRepository.buscarMedicamentoGeneral(0);
        List<String> listafotos = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            byte[] fotoBytes = lista.get(i).getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
            listafotos.add(fotoBase64);
        }

        model.addAttribute("listaMedicamentos",lista);
        model.addAttribute("listaFotos", listafotos);
        model.addAttribute("cantidadMedicamentos",lista.size());


        //generador de numero de pedidos
        List<String> estadosdecompraporId = carritoRepository.estadosDeCompraPorUsuarioId(usuid);
        boolean soloEstadosRegistrados = true;
        for (String palabra : estadosdecompraporId) {
            if (palabra!=null && palabra.equals("Comprando")) {
                soloEstadosRegistrados = false;
                break;
            }
        }
        if(estadosdecompraporId.isEmpty() || soloEstadosRegistrados){
            boolean i = true;
            List<String> lista1 = pedidosPacienteRepository.numerosDePedidosDely();
            List<String> lista2 = pedidosPacienteRecojoRepository.numerosDePedidosReco();
            List<String> lista3 = carritoRepository.numerosDePedidosCarrito();
            int duplicado = 0;
            while (i){
                for (int x = 0; x < 12; x++) {
                    if (x > 0 && x % 4 == 0) {
                        String guion = "-";
                        numpedido += guion;
                    }
                    int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
                    char caracterAleatorio = banco.charAt(indiceAleatorio);
                    numpedido += caracterAleatorio;
                }
                for (String palabra : lista1) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista2) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista3) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                if(duplicado == 0){
                    break;
                }
            }
        }
        model.addAttribute("numPedido",numpedido);
        int principal = 1;
        model.addAttribute("principal", principal);

        List<String> listaCategorias = medicamentosRepository.listaCategorias();
        Set<String> setCategorias = new HashSet<>(listaCategorias);
        List<String> listaSinDuplicados = new ArrayList<>(setCategorias);
        model.addAttribute("listaCategorias", listaSinDuplicados);

        return "farmacista/medicamentos";
    }


    /*QRUD y vista de MEDICAMENTOS*/

    @PostMapping("/farmacista/medicamentos/buscador")
    public String buscarMedicamentos(@RequestParam("searchField") String searchField,
                                     Authentication authentication, Model model){
        if(searchField.equals("")){
            return "redirect:/farmacista/medicamentos";
        }

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        Integer idpedido = carritoRepository.idPedidoRegistrando(usuid);
        if(idpedido != null){
            carritoRepository.borrarMedicamentosAlCancelar(usuid, idpedido);
            carritoRepository.cancelarPedidoDely(usuid);
        }
        carritoRepository.cancelarPedidoReco(usuid);
        String banco = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String numpedido = "";

        List<Medicamentos> lista = medicamentosRepository.buscarMedicamento(searchField);
        List<String> listafotos = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            byte[] fotoBytes = lista.get(i).getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
            listafotos.add(fotoBase64);
        }

        model.addAttribute("listaMedicamentos", lista);
        model.addAttribute("listaFotos", listafotos);
        model.addAttribute("cantidadMedicamentos", lista.size());

        //generador de numero de pedidos
        List<String> estadosdecompraporId = carritoRepository.estadosDeCompraPorUsuarioId(usuid);
        boolean soloEstadosRegistrados = true;
        for (String palabra : estadosdecompraporId) {
            if (palabra!=null && palabra.equals("Comprando")) {
                soloEstadosRegistrados = false;
                break;
            }
        }
        if(estadosdecompraporId.isEmpty() || soloEstadosRegistrados){
            boolean i = true;
            List<String> lista1 = pedidosPacienteRepository.numerosDePedidosDely();
            List<String> lista2 = pedidosPacienteRecojoRepository.numerosDePedidosReco();
            List<String> lista3 = carritoRepository.numerosDePedidosCarrito();
            int duplicado = 0;
            while (i){
                for (int x = 0; x < 12; x++) {
                    if (x > 0 && x % 4 == 0) {
                        String guion = "-";
                        numpedido += guion;
                    }
                    int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
                    char caracterAleatorio = banco.charAt(indiceAleatorio);
                    numpedido += caracterAleatorio;
                }
                for (String palabra : lista1) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista2) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista3) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                if(duplicado == 0){
                    break;
                }
            }
        }
        model.addAttribute("numPedido",numpedido);
        int principal = 0;
        model.addAttribute("principal", principal);

        List<String> listaCategorias = medicamentosRepository.listaCategorias();
        Set<String> setCategorias = new HashSet<>(listaCategorias);
        List<String> listaSinDuplicados = new ArrayList<>(setCategorias);
        model.addAttribute("listaCategorias", listaSinDuplicados);

        return "farmacista/medicamentos";
    }

    @PostMapping("/farmacista/medicamentos/filtrar")
    public String filtrarMedicamentos(@RequestParam("filtroCategoria") String categoria, @RequestParam("filtroOrden") String orden,
                                      Authentication authentication, Model model){

        if(categoria.equals("") && orden.equals("")){
            return "redirect:/farmacista/medicamentos";
        }
        if(!categoria.equals("")){
            if(!orden.equals("")) {
                if (orden.equals("1")) {
                    List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosCategoriaFiltro1(categoria);
                    List<String> listafotos = new ArrayList<>();

                    for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                        byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        listafotos.add(fotoBase64);
                    }

                    model.addAttribute("listaFotos", listafotos);
                    model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                    model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("orden", "mayor a menor precio");
                }
                if (orden.equals("2")) {
                    List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosCategoriaFiltro2(categoria);
                    List<String> listafotos = new ArrayList<>();

                    for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                        byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        listafotos.add(fotoBase64);
                    }

                    model.addAttribute("listaFotos", listafotos);
                    model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                    model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("orden", "menor a mayor precio");
                }
                if (orden.equals("3")) {
                    List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosCategoriaFiltro3(categoria);
                    List<String> listafotos = new ArrayList<>();

                    for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                        byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        listafotos.add(fotoBase64);
                    }

                    model.addAttribute("listaFotos", listafotos);
                    model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                    model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("orden", "antiguo a nuevo");
                }
                if (orden.equals("4")) {
                    List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosCategoriaFiltro4(categoria);
                    List<String> listafotos = new ArrayList<>();

                    for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                        byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        listafotos.add(fotoBase64);
                    }

                    model.addAttribute("listaFotos", listafotos);
                    model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                    model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("orden", "A -> Z");
                }
                if (orden.equals("5")) {
                    List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosCategoriaFiltro5(categoria);
                    List<String> listafotos = new ArrayList<>();

                    for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                        byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        listafotos.add(fotoBase64);
                    }

                    model.addAttribute("listaFotos", listafotos);
                    model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                    model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                    model.addAttribute("categoria", categoria);
                    model.addAttribute("orden", "Z -> A");
                }
            }
            else{
                List<Medicamentos> medicamentosPorCategoria = medicamentosRepository.listaMedicamentosPorCategoria(categoria);
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosPorCategoria.size(); i++) {
                    byte[] fotoBytes = medicamentosPorCategoria.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosPorCategoria);
                model.addAttribute("cantidadMedicamentos",medicamentosPorCategoria.size());
                model.addAttribute("categoria", categoria);
            }
        }
        else{
            if (orden.equals("1")) {
                List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosFiltro1();
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                    byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                model.addAttribute("orden", "mayor a menor precio");
            }
            if (orden.equals("2")) {
                List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosFiltro2();
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                    byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                model.addAttribute("orden", "menor a mayor precio");
            }
            if (orden.equals("3")) {
                List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosFiltro3();
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                    byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                model.addAttribute("orden", "antiguo a nuevo");
            }
            if (orden.equals("4")) {
                List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosFiltro4();
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                    byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                model.addAttribute("orden", "A -> Z");
            }
            if (orden.equals("5")) {
                List<Medicamentos> medicamentosFiltrados = medicamentosRepository.listaMedicamentosFiltro5();
                List<String> listafotos = new ArrayList<>();

                for (int i = 0; i < medicamentosFiltrados.size(); i++) {
                    byte[] fotoBytes = medicamentosFiltrados.get(i).getFoto();
                    String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                    listafotos.add(fotoBase64);
                }

                model.addAttribute("listaFotos", listafotos);
                model.addAttribute("listaMedicamentos", medicamentosFiltrados);
                model.addAttribute("cantidadMedicamentos",medicamentosFiltrados.size());
                model.addAttribute("orden", "Z -> A");
            }
        }
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        Integer idpedido = carritoRepository.idPedidoRegistrando(usuid);
        if(idpedido != null){
            carritoRepository.borrarMedicamentosAlCancelar(usuid, idpedido);
            carritoRepository.cancelarPedidoDely(usuid);
        }
        carritoRepository.cancelarPedidoReco(usuid);
        String banco = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String numpedido = "";

        //generador de numero de pedidos
        List<String> estadosdecompraporId = carritoRepository.estadosDeCompraPorUsuarioId(usuid);
        boolean soloEstadosRegistrados = true;
        for (String palabra : estadosdecompraporId) {
            if (palabra!=null && palabra.equals("Comprando")) {
                soloEstadosRegistrados = false;
                break;
            }
        }
        if(estadosdecompraporId.isEmpty() || soloEstadosRegistrados){
            boolean i = true;
            List<String> lista1 = pedidosPacienteRepository.numerosDePedidosDely();
            List<String> lista2 = pedidosPacienteRecojoRepository.numerosDePedidosReco();
            List<String> lista3 = carritoRepository.numerosDePedidosCarrito();
            int duplicado = 0;
            while (i){
                for (int x = 0; x < 12; x++) {
                    if (x > 0 && x % 4 == 0) {
                        String guion = "-";
                        numpedido += guion;
                    }
                    int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
                    char caracterAleatorio = banco.charAt(indiceAleatorio);
                    numpedido += caracterAleatorio;
                }
                for (String palabra : lista1) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista2) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista3) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                if(duplicado == 0){
                    break;
                }
            }
        }
        model.addAttribute("numPedido",numpedido);
        int principal = 0;
        model.addAttribute("principal", principal);

        List<String> listaCategorias = medicamentosRepository.listaCategorias();
        Set<String> setCategorias = new HashSet<>(listaCategorias);
        List<String> listaSinDuplicados = new ArrayList<>(setCategorias);
        model.addAttribute("listaCategorias", listaSinDuplicados);

        return "farmacista/medicamentos";
    }
    @GetMapping("/farmacista/medicamentos/info")
    @ResponseBody
    public List<String> informacionDelMedicamento(@RequestParam("id") String strId){
        Integer id = Integer.parseInt(strId);
        Optional<Medicamentos> medicamentos = medicamentosRepository.findById(id);
        List<String> infoMedicamentos = new ArrayList<>();
        if (medicamentos.isPresent()){
            Medicamentos medicamento = medicamentos.get();

            String idStr = Integer.toString(medicamento.getId());
            infoMedicamentos.add(idStr);
            infoMedicamentos.add(medicamento.getNombre());
            infoMedicamentos.add(medicamento.getCategoria());
            infoMedicamentos.add(medicamento.getDescripcion());
            byte[] fotoBytes1 = medicamento.getFoto();
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes1);
            infoMedicamentos.add(fotoBase64);
            return infoMedicamentos;
        }
        else{
            return null;
        }
    }




    @GetMapping("/farmacista/añadirCarrito1")
    public String anadirMedicamentoAlCarrito1(Model model, Authentication authentication,
                                              @RequestParam("id") int id, @RequestParam("numpedido") String numpedido, RedirectAttributes attr){
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();

        List<String> estadosdecompraporId = carritoRepository.estadosDeCompraPorUsuarioId(usuid);
        boolean soloEstadosRegistrados = true;
        for (String palabra : estadosdecompraporId) {
            if (palabra!=null && palabra.equals("Comprando")) {
                soloEstadosRegistrados = false;
                break;
            }
        }
        if(estadosdecompraporId.isEmpty() || soloEstadosRegistrados){
            if(estadosdecompraporId.isEmpty() || soloEstadosRegistrados){
                String registrado = "Registrado";
                carritoRepository.borrarPedidoRegistrado(usuid, registrado);
            }
            String estadocompra = "Comprando";
            int cantidad = 1;
            carritoRepository.AnadirAlCarrito(id, usuid, cantidad, numpedido, estadocompra);
            attr.addFlashAttribute("msg","Se agrego un nuevo producto al carrito!");
        }
        return "redirect:/farmacista/medicamentos";
    }

    @GetMapping("/farmacista/añadirCarrito2")
    public String anadirMedicamentoAlCarrito2(Model model, Authentication authentication,
                                              @RequestParam("id") int id, RedirectAttributes attr){
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();

        String estadocompra = "Comprando";
        List<Carrito> duplicados = carritoRepository.buscarDuplicados(id);
        if (duplicados.isEmpty()){
            int cantidad = 1;
            List<String> numeropedidoporId = carritoRepository.numPedidoPorUsuarioId(usuid);
            String numpedido = numeropedidoporId.get(0);
            carritoRepository.AnadirAlCarrito(id, usuid, cantidad, numpedido, estadocompra);
            attr.addFlashAttribute("msg","Se agrego un nuevo producto al carrito!");
        }
        else{
            int cantidadDelDuplicado = carritoRepository.cantidadDelDuplicado(id);
            int cantidad = cantidadDelDuplicado+1;
            int id1 = id;
            int usuid2 = usuid;
            List<String> numeropedidoporId = carritoRepository.numPedidoPorUsuarioId(usuid);
            String numpedido = numeropedidoporId.get(0);
            carritoRepository.borrarElementoCarrito(id, usuid);
            carritoRepository.AnadirAlCarrito(id1, usuid2, cantidad, numpedido, estadocompra);
            attr.addFlashAttribute("msg","Se agrego un producto existente al carrito!");
        }
        return "redirect:/farmacista/medicamentos";
    }

    /*QRUD y vista del CARRITO*/
    @ResponseBody
    @GetMapping("/tamañocarritof")
    public Integer listarProductosCarritoRT(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        List<Carrito> carritoPorId = carritoRepository.carritoPorId(usuid);
        return carritoPorId.size();
    }
    @GetMapping("/farmacista/carrito")
    public String listarProductosCarritoRT(Model model, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        List<String> numeropedidoporId = carritoRepository.numPedidoPorUsuarioId(usuid);
        if (!numeropedidoporId.isEmpty()){
            String numpedido = numeropedidoporId.get(0);
            model.addAttribute("numpedido", numpedido);
        }
        List<Carrito> listadodelcarritort = carritoRepository.listarCarrito(usuid);
        int car = 0;
        if (listadodelcarritort.isEmpty()){
            String msg1 = "Su carrito esta vacio.";
            String msg2 = "No agrego ningun producto a su carrito.";
            car = 1;
            model.addAttribute("msg1",msg1);
            model.addAttribute("msg2",msg2);
            model.addAttribute("car",car);
        }
        else{
            model.addAttribute("listadoDelCarrito",listadodelcarritort);
            List<Double> listaPrecioxCantidad = carritoRepository.CantidadxPrecioUnitario(usuid);
            double sumaTotal = 0.0;
            for (Double valor : listaPrecioxCantidad) {
                sumaTotal += valor;
            }
            String sumaTotal2D = String.format("%.2f", sumaTotal);
            model.addAttribute("precioTotal",sumaTotal2D);
            int delivery = 0;
            model.addAttribute("delivery",delivery);
            model.addAttribute("car",car);
        }
        return "farmacista/carrito";
    }


    @GetMapping("/farmacista/carrito/pagoTarjeta")
    public String listarProductosCarritoDL(Model model, Authentication authentication){
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        List<String> numeropedidoporId = carritoRepository.numPedidoPorUsuarioId(usuid);
        if (!numeropedidoporId.isEmpty()){
            String numpedido = numeropedidoporId.get(0);
            model.addAttribute("numpedido", numpedido);
        }
        List<Carrito> listadodelcarritort = carritoRepository.listarCarrito(usuid);
        int car = 0;
        if (listadodelcarritort.isEmpty()){
            String msg1 = "Su carrito esta vacio.";
            String msg2 = "No agrego ningun producto a su carrito.";
            car = 1;
            model.addAttribute("msg1",msg1);
            model.addAttribute("msg2",msg2);
            model.addAttribute("car",car);
        }
        else{
            model.addAttribute("listadoDelCarrito",listadodelcarritort);
            List<Double> listaPrecioxCantidad = carritoRepository.CantidadxPrecioUnitario(usuid);
            double sumaTotal = 0.0;
            for (Double valor : listaPrecioxCantidad) {
                sumaTotal += valor;
            }
            String sumaTotal2D = String.format("%.2f", sumaTotal);
            model.addAttribute("precioTotal",sumaTotal2D);
            int delivery = 1;
            model.addAttribute("delivery",delivery);
            model.addAttribute("car",car);
        }
        return "farmacista/carrito";
    }

    @GetMapping("/farmacista/carrito/borrar")
    public String borrarElementoCarrito(Model model, Authentication authentication,
                                        @RequestParam("id") int id) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        carritoRepository.borrarElementoCarrito(id, usuid);
        return "redirect:/farmacista/carrito";
    }

    @GetMapping("/farmacista/carrito/registrarPedido")
    public String registrarPedido(Model model, RedirectAttributes redirectAttributes, Authentication authentication,
                                  @RequestParam("costototal") double costototal, @RequestParam("tipopedido") int tipo, @RequestParam("numtrack") String numtrack){
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();

        Integer idpedido = carritoRepository.idPedidoRegistrando(usuid);
        if(idpedido != null){
            carritoRepository.borrarMedicamentosAlCancelar(usuid, idpedido);
            carritoRepository.cancelarPedidoDely(usuid);
        }
        carritoRepository.cancelarPedidoReco(usuid);

        String tipopedido = "Presencial - Pago en efectivo";
        if(tipo == 1){
            tipopedido = "Presencial - Pago con tarjeta";
            String validacionpedido = "Pendiente";
            String estadopedido = "Registrando";
            carritoRepository.registrarPedidoReco(costototal, tipopedido, validacionpedido, estadopedido, numtrack, usuid);
            return "redirect:/farmacista/carrito/nuevoPedidoPagoTarjeta";
        }
        else{
            String validacionpedido = "Pendiente";
            String estadopedido = "Registrando";
            carritoRepository.registrarPedidoReco(costototal, tipopedido, validacionpedido, estadopedido, numtrack, usuid);
            return "redirect:/farmacista/carrito/nuevoPedidoPagoEfectivo";
        }

    }
    /*---------------------------------------*/



    /*QRUD y vista del FORM*/

    @GetMapping("/farmacista/carrito/nuevoPedidoPagoTarjeta")
    public String formParaFinalizarCompraRecojo1(@ModelAttribute("pedidosPacienteRecojo") PedidosPacienteRecojo pedidosPacienteRecojo,
                                                Model model){
        model.addAttribute("listaseguros", seguroRepository.findAll());
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        model.addAttribute("listasedes", sedeRepository.findAll());
        return "farmacista/formcompra";
    }

    @GetMapping("/farmacista/carrito/nuevoPedidoPagoEfectivo")
    public String formParaFinalizarCompraRecojo2(@ModelAttribute("pedidosPacienteRecojo") PedidosPacienteRecojo pedidosPacienteRecojo,
                                                Model model){
        model.addAttribute("listaseguros", seguroRepository.findAll());
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        model.addAttribute("listasedes", sedeRepository.findAll());
        return "farmacista/formcompradely";
    }

    @PostMapping("/farmacista/guardarPagoEfectivo")
    public String guardarPedidoReco2(@ModelAttribute("pedidosPacienteRecojo") PedidosPacienteRecojo pedidosPacienteRecojo,
                                    Model model, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        boolean nombreError = pedidosPacienteRecojo.getNombre_paciente().isEmpty() || pedidosPacienteRecojo.getNombre_paciente().length() < 3 || pedidosPacienteRecojo.getNombre_paciente().length() > 45 || pedidosPacienteRecojo.getNombre_paciente().contains("1") || pedidosPacienteRecojo.getNombre_paciente().contains("2") || pedidosPacienteRecojo.getNombre_paciente().contains("3") || pedidosPacienteRecojo.getNombre_paciente().contains("4") || pedidosPacienteRecojo.getNombre_paciente().contains("5") || pedidosPacienteRecojo.getNombre_paciente().contains("6") || pedidosPacienteRecojo.getNombre_paciente().contains("7") || pedidosPacienteRecojo.getNombre_paciente().contains("8") || pedidosPacienteRecojo.getNombre_paciente().contains("9") || pedidosPacienteRecojo.getNombre_paciente().contains("0");
        boolean apellidoError = pedidosPacienteRecojo.getApellido_paciente().isEmpty() || pedidosPacienteRecojo.getApellido_paciente().length() < 3 || pedidosPacienteRecojo.getApellido_paciente().length() > 45 || pedidosPacienteRecojo.getApellido_paciente().contains("1") || pedidosPacienteRecojo.getApellido_paciente().contains("2") || pedidosPacienteRecojo.getApellido_paciente().contains("3") || pedidosPacienteRecojo.getApellido_paciente().contains("4") || pedidosPacienteRecojo.getApellido_paciente().contains("5") || pedidosPacienteRecojo.getApellido_paciente().contains("6") || pedidosPacienteRecojo.getApellido_paciente().contains("7") || pedidosPacienteRecojo.getApellido_paciente().contains("8") || pedidosPacienteRecojo.getApellido_paciente().contains("9") || pedidosPacienteRecojo.getApellido_paciente().contains("0");
        boolean dniError = pedidosPacienteRecojo.getDni().isEmpty() || pedidosPacienteRecojo.getDni().length() > 2;

        if (nombreError || apellidoError || dniError || pedidosPacienteRecojo.getMedico_que_atiende().equals("") || pedidosPacienteRecojo.getSeguro().equals("")){
            if(pedidosPacienteRecojo.getNombre_paciente().isEmpty()){
                model.addAttribute("nombreError", "El nombre no puede quedar vacio");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().length() < 3){
                model.addAttribute("nombreError", "El nombre no puede tener menos de 3 caracteres");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().length() > 45){
                model.addAttribute("nombreError", "El nombre no puede tener mas de 45 caracteres");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().contains("1") || pedidosPacienteRecojo.getNombre_paciente().contains("2") || pedidosPacienteRecojo.getNombre_paciente().contains("3") || pedidosPacienteRecojo.getNombre_paciente().contains("4") || pedidosPacienteRecojo.getNombre_paciente().contains("5") || pedidosPacienteRecojo.getNombre_paciente().contains("6") || pedidosPacienteRecojo.getNombre_paciente().contains("7") || pedidosPacienteRecojo.getNombre_paciente().contains("8") || pedidosPacienteRecojo.getNombre_paciente().contains("9") || pedidosPacienteRecojo.getNombre_paciente().contains("0")){
                model.addAttribute("nombreError", "El nombre no puede contener números");
            }

            if(pedidosPacienteRecojo.getApellido_paciente().isEmpty()){
                model.addAttribute("apellidoError", "El apellido no puede quedar vacio");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().length() < 3){
                model.addAttribute("apellidoError", "El apellido no puede tener menos de 3 caracteres");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().length() > 45){
                model.addAttribute("apellidoError", "El apellido no puede tener mas de 45 caracteres");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().contains("1") || pedidosPacienteRecojo.getApellido_paciente().contains("2") || pedidosPacienteRecojo.getApellido_paciente().contains("3") || pedidosPacienteRecojo.getApellido_paciente().contains("4") || pedidosPacienteRecojo.getApellido_paciente().contains("5") || pedidosPacienteRecojo.getApellido_paciente().contains("6") || pedidosPacienteRecojo.getApellido_paciente().contains("7") || pedidosPacienteRecojo.getApellido_paciente().contains("8") || pedidosPacienteRecojo.getApellido_paciente().contains("9") || pedidosPacienteRecojo.getApellido_paciente().contains("0")){
                model.addAttribute("apellidoError", "El apellido no puede contener números");
            }

            if(pedidosPacienteRecojo.getDni().isEmpty()){
                model.addAttribute("edadError", "La edad no puede quedar vacia");
            }
            if(pedidosPacienteRecojo.getDni().length() > 2){
                model.addAttribute("edadError", "La edad no puede tener mas de 2 dígitos");
            }

            if (pedidosPacienteRecojo.getSeguro().equals("")){
                model.addAttribute("seguroError", "Debe seleccionar una opción");
            }
            if (pedidosPacienteRecojo.getMedico_que_atiende().equals("")){
                model.addAttribute("medicoError", "Debe seleccionar una opción");
            }
            model.addAttribute("listaseguros", seguroRepository.findAll());
            model.addAttribute("listausuarios", usuarioRepository.findAll());
            model.addAttribute("listasedes", sedeRepository.findAll());
            return "farmacista/formcompradely";
        }
        else{
            int usuid = usuario.getId();

            List<Integer> ids = carritoRepository.idpedidoPorUsuIdReco(usuid);
            Integer idped = ids.get(0);
            Optional<PedidosPacienteRecojo> optionalPedidosPaciente = pedidosPacienteRecojoRepository.findById(idped);

            if (optionalPedidosPaciente.isPresent()){
                PedidosPacienteRecojo pedidoreco = optionalPedidosPaciente.get();

                pedidosPacienteRecojo.setCosto_total(pedidoreco.getCosto_total());
                pedidosPacienteRecojo.setTipo_de_pedido(pedidoreco.getTipo_de_pedido());
                pedidosPacienteRecojo.setValidacion_del_pedido("Validado");
                pedidosPacienteRecojo.setEstado_del_pedido("Pagado");

                String numTrack = pedidoreco.getNumero_tracking();
                pedidosPacienteRecojo.setNumero_tracking(pedidoreco.getNumero_tracking());

                pedidosPacienteRecojo.setUsuario(usuario);

                LocalDate fechaActual = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String fechasoli = fechaActual.format(formatter);
                pedidosPacienteRecojo.setFecha_solicitud(fechasoli);

                carritoRepository.cancelarPedidoReco(usuid);
                pedidosPacienteRecojoRepository.save(pedidosPacienteRecojo);
                List<Integer> listidpedidoreco = carritoRepository.idpedidoPorUsuIdRecoMedicamentos2(usuid);
                int idpedidomed = listidpedidoreco.get(0);
                carritoRepository.registrarMedicamentosPedidoReco(idpedidomed, usuid);
                carritoRepository.borrarCarritoPorId(usuid);

                List<MedicamentoRecojo> listaMedicamentos = medicamentosRecojoRepository.listaMedicamentosReco(idpedidomed);
                for (int i = 0; i < listaMedicamentos.size(); i++) {
                    Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentos.get(i).getNombre_medicamento());
                    Integer stock = medicamento.getInventario();
                    Integer cantidad = listaMedicamentos.get(i).getCantidad();
                    Integer stocknuevo = stock - cantidad;
                    if(stocknuevo > 0){
                        medicamento.setInventario(stocknuevo);
                        medicamentosRepository.save(medicamento);
                    }
                }

                model.addAttribute("numTracking", numTrack);
                model.addAttribute("dely" , 1);
            }

            return "farmacista/finalmsgCompra";
        }
    }

    @PostMapping("/farmacista/guardarPagoTarjeta")
    public String guardarPedidoReco(@ModelAttribute("pedidosPacienteRecojo") PedidosPacienteRecojo pedidosPacienteRecojo,
                                    Model model, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        boolean nombreError = pedidosPacienteRecojo.getNombre_paciente().isEmpty() || pedidosPacienteRecojo.getNombre_paciente().length() < 3 || pedidosPacienteRecojo.getNombre_paciente().length() > 45 || pedidosPacienteRecojo.getNombre_paciente().contains("1") || pedidosPacienteRecojo.getNombre_paciente().contains("2") || pedidosPacienteRecojo.getNombre_paciente().contains("3") || pedidosPacienteRecojo.getNombre_paciente().contains("4") || pedidosPacienteRecojo.getNombre_paciente().contains("5") || pedidosPacienteRecojo.getNombre_paciente().contains("6") || pedidosPacienteRecojo.getNombre_paciente().contains("7") || pedidosPacienteRecojo.getNombre_paciente().contains("8") || pedidosPacienteRecojo.getNombre_paciente().contains("9") || pedidosPacienteRecojo.getNombre_paciente().contains("0");
        boolean apellidoError = pedidosPacienteRecojo.getApellido_paciente().isEmpty() || pedidosPacienteRecojo.getApellido_paciente().length() < 3 || pedidosPacienteRecojo.getApellido_paciente().length() > 45 || pedidosPacienteRecojo.getApellido_paciente().contains("1") || pedidosPacienteRecojo.getApellido_paciente().contains("2") || pedidosPacienteRecojo.getApellido_paciente().contains("3") || pedidosPacienteRecojo.getApellido_paciente().contains("4") || pedidosPacienteRecojo.getApellido_paciente().contains("5") || pedidosPacienteRecojo.getApellido_paciente().contains("6") || pedidosPacienteRecojo.getApellido_paciente().contains("7") || pedidosPacienteRecojo.getApellido_paciente().contains("8") || pedidosPacienteRecojo.getApellido_paciente().contains("9") || pedidosPacienteRecojo.getApellido_paciente().contains("0");
        boolean dniError = pedidosPacienteRecojo.getDni().isEmpty() || pedidosPacienteRecojo.getDni().length() > 2;

        if (nombreError || apellidoError || dniError || pedidosPacienteRecojo.getMedico_que_atiende().equals("") || pedidosPacienteRecojo.getSeguro().equals("")){
            if(pedidosPacienteRecojo.getNombre_paciente().isEmpty()){
                model.addAttribute("nombreError", "El nombre no puede quedar vacio");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().length() < 3){
                model.addAttribute("nombreError", "El nombre no puede tener menos de 3 caracteres");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().length() > 45){
                model.addAttribute("nombreError", "El nombre no puede tener mas de 45 caracteres");
            }
            if(pedidosPacienteRecojo.getNombre_paciente().contains("1") || pedidosPacienteRecojo.getNombre_paciente().contains("2") || pedidosPacienteRecojo.getNombre_paciente().contains("3") || pedidosPacienteRecojo.getNombre_paciente().contains("4") || pedidosPacienteRecojo.getNombre_paciente().contains("5") || pedidosPacienteRecojo.getNombre_paciente().contains("6") || pedidosPacienteRecojo.getNombre_paciente().contains("7") || pedidosPacienteRecojo.getNombre_paciente().contains("8") || pedidosPacienteRecojo.getNombre_paciente().contains("9") || pedidosPacienteRecojo.getNombre_paciente().contains("0")){
                model.addAttribute("nombreError", "El nombre no puede contener números");
            }

            if(pedidosPacienteRecojo.getApellido_paciente().isEmpty()){
                model.addAttribute("apellidoError", "El apellido no puede quedar vacio");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().length() < 3){
                model.addAttribute("apellidoError", "El apellido no puede tener menos de 3 caracteres");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().length() > 45){
                model.addAttribute("apellidoError", "El apellido no puede tener mas de 45 caracteres");
            }
            if(pedidosPacienteRecojo.getApellido_paciente().contains("1") || pedidosPacienteRecojo.getApellido_paciente().contains("2") || pedidosPacienteRecojo.getApellido_paciente().contains("3") || pedidosPacienteRecojo.getApellido_paciente().contains("4") || pedidosPacienteRecojo.getApellido_paciente().contains("5") || pedidosPacienteRecojo.getApellido_paciente().contains("6") || pedidosPacienteRecojo.getApellido_paciente().contains("7") || pedidosPacienteRecojo.getApellido_paciente().contains("8") || pedidosPacienteRecojo.getApellido_paciente().contains("9") || pedidosPacienteRecojo.getApellido_paciente().contains("0")){
                model.addAttribute("apellidoError", "El apellido no puede contener números");
            }

            if(pedidosPacienteRecojo.getDni().isEmpty()){
                model.addAttribute("edadError", "La edad no puede quedar vacia");
            }
            if(pedidosPacienteRecojo.getDni().length() > 2){
                model.addAttribute("edadError", "La edad no puede tener mas de 2 dígitos");
            }

            if (pedidosPacienteRecojo.getSeguro().equals("")){
                model.addAttribute("seguroError", "Debe seleccionar una opción");
            }
            if (pedidosPacienteRecojo.getMedico_que_atiende().equals("")){
                model.addAttribute("medicoError", "Debe seleccionar una opción");
            }
            model.addAttribute("listaseguros", seguroRepository.findAll());
            model.addAttribute("listausuarios", usuarioRepository.findAll());
            model.addAttribute("listasedes", sedeRepository.findAll());
            return "farmacista/formcompra";
        }
        else{
            int usuid = usuario.getId();

            List<Integer> ids = carritoRepository.idpedidoPorUsuIdReco(usuid);
            Integer idped = ids.get(0);
            Optional<PedidosPacienteRecojo> optionalPedidosPaciente = pedidosPacienteRecojoRepository.findById(idped);

            if (optionalPedidosPaciente.isPresent()){
                PedidosPacienteRecojo pedidoreco = optionalPedidosPaciente.get();

                pedidosPacienteRecojo.setCosto_total(pedidoreco.getCosto_total());
                pedidosPacienteRecojo.setTipo_de_pedido(pedidoreco.getTipo_de_pedido());
                pedidosPacienteRecojo.setValidacion_del_pedido("Validado");
                pedidosPacienteRecojo.setEstado_del_pedido("Por cancelar");

                String numTrack = pedidoreco.getNumero_tracking();
                pedidosPacienteRecojo.setNumero_tracking(pedidoreco.getNumero_tracking());

                pedidosPacienteRecojo.setUsuario(usuario);

                LocalDate fechaActual = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String fechasoli = fechaActual.format(formatter);
                pedidosPacienteRecojo.setFecha_solicitud(fechasoli);

                carritoRepository.cancelarPedidoReco(usuid);
                pedidosPacienteRecojoRepository.save(pedidosPacienteRecojo);
                List<Integer> listidpedidoreco = carritoRepository.idpedidoPorUsuIdRecoMedicamentos(usuid);
                int idpedidomed = listidpedidoreco.get(0);
                carritoRepository.registrarMedicamentosPedidoReco(idpedidomed, usuid);
                carritoRepository.borrarCarritoPorId(usuid);

                List<MedicamentoRecojo> listaMedicamentos = medicamentosRecojoRepository.listaMedicamentosReco(idpedidomed);
                for (int i = 0; i < listaMedicamentos.size(); i++) {
                    Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentos.get(i).getNombre_medicamento());
                    Integer stock = medicamento.getInventario();
                    Integer cantidad = listaMedicamentos.get(i).getCantidad();
                    Integer stocknuevo = stock - cantidad;
                    if(stocknuevo > 0){
                        medicamento.setInventario(stocknuevo);
                        medicamentosRepository.save(medicamento);
                    }
                }

                model.addAttribute("numTracking", numTrack);
                model.addAttribute("recojo" , 1);
            }

            return "farmacista/finalmsgCompra";
        }
    }

    @GetMapping("farmacista/cancelarRegistroPedidoReco")
    public String cancelarRegistroDePedidoReco(Authentication authentication){
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        int usuid = usuario.getId();
        carritoRepository.cancelarPedidoReco(usuid);
        return "redirect:/farmacista/medicamentos";
    }
    /*---------------------------------------*/

    @GetMapping("/farmacista/mensaje")
    public String mostrarMensajeria(Model model) {
        // solo redirige a la vista
        return "farmacista/mensajeriaf";
    }


    /*vista y QRUD de SOLICITUDES DE PEDIDOS*/

    @GetMapping("/farmacista/pedidosPresenciales")
    public String listarPedidosPresenciales(Model model) {
        List<PedidosPacienteRecojo> listaPedidos2 = pedidosPacienteRecojoRepository.findAll();

        List<PedidosPacienteRecojo> listapedidos = new ArrayList<>();

        for (PedidosPacienteRecojo pedido2 : listaPedidos2) {
            if(pedido2.getTipo_de_pedido().equals("Presencial - Pago en efectivo") || pedido2.getTipo_de_pedido().equals("Presencial - Pago con tarjeta")){
                listapedidos.add(pedido2);
            }
        }

        model.addAttribute("listaPedidosDely", listapedidos);
        model.addAttribute("tamanodely", listapedidos.size());

        return "farmacista/lista_pedidos_presenciales";
    }

    @GetMapping("/farmacista/pagoSeguro")
    public String pagoSeguro(@RequestParam("id") String idstr,
                             @RequestParam("tipo") String tipostr, Model model, RedirectAttributes redirectAttributes, Authentication authentication){

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName());
        Integer tipo = Integer.parseInt(tipostr);

        if(tipo < 3 && tipo > 0){
            Integer id = Integer.parseInt(idstr);
            if(id > 0){
                if (tipo == 1){
                    Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
                    if(optionalPedidosPaciente.isPresent()){
                        PedidosPaciente pedidosPaciente = optionalPedidosPaciente.get();
                        Usuario usuarioPedido = pedidosPaciente.getUsuario();

                        if(!pedidosPaciente.getEstado_del_pedido().equals("Por cancelar") || usuarioPedido != usuario){
                            if(pedidosPaciente.getTipo_de_pedido().equals("Web - Delivery")){
                                return "redirect:/farmacista/pedidosPresenciales";
                            }
                            else{
                                return "redirect:/farmacista/pedidosPresenciales";
                            }
                        }

                        model.addAttribute("id", pedidosPaciente.getId());
                        model.addAttribute("preciototal", pedidosPaciente.getCosto_total());
                        model.addAttribute("tipopedido", pedidosPaciente.getTipo_de_pedido());
                        model.addAttribute("numtrack", pedidosPaciente.getNumero_tracking());
                    }
                    else{
                        return "redirect:/farmacista/pedidosPresenciales";
                    }
                }
                if (tipo == 2){
                    Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
                    if(optionalPedidosPacienteRecojo.isPresent()){
                        PedidosPacienteRecojo pedidosPacienteRecojo = optionalPedidosPacienteRecojo.get();
                        Usuario usuarioPedido = pedidosPacienteRecojo.getUsuario();

                        if(!pedidosPacienteRecojo.getEstado_del_pedido().equals("Por cancelar") || usuarioPedido != usuario){
                            return "redirect:/farmacista/pedidosPresenciales";
                        }

                        model.addAttribute("id", pedidosPacienteRecojo.getId());
                        model.addAttribute("preciototal", pedidosPacienteRecojo.getCosto_total());
                        model.addAttribute("tipopedido", pedidosPacienteRecojo.getTipo_de_pedido());
                        model.addAttribute("numtrack", pedidosPacienteRecojo.getNumero_tracking());
                    }
                    else{
                        return "redirect:/farmacista/pedidosPresenciales";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidosPresenciales";
            }
        }
        else{
            return "redirect:/farmacista/pedidosPresenciales";
        }
        return "farmacista/pagoDelPedido";
    }
    @GetMapping("/farmacista/validarPago")
    @ResponseBody
    public Map<String, String> validarElPago(@RequestParam("numero") String numero_tarjeta1,
                                             @RequestParam("cvv") Integer cvv,
                                             @RequestParam("mes") String mes, @RequestParam("anhio") String anhio,
                                             @RequestParam("preciototal") String preciototalStr, @RequestParam("id") String idstr,
                                             @RequestParam("tipopedido") String tipopedidostr){
        Map<String, String> response = new HashMap<>();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numero_tarjeta1.length(); i++) {
            if (numero_tarjeta1.charAt(i) != ' ') {
                if(numero_tarjeta1.charAt(i) != '-'){
                    result.append(numero_tarjeta1.charAt(i));
                }
            }
        }
        String numero_tarjetaStr = result.toString();
        BigInteger numero_tarjeta = new BigInteger(numero_tarjetaStr);

        Tarjeta tarjeta = tarjetaRepository.validarTarjeta(numero_tarjeta,mes,anhio,cvv);

        if (tarjeta != null) {
            double preciototal = Double.valueOf(preciototalStr);
            double ahorros = tarjeta.getAhorros();
            if((ahorros - preciototal) > 0){
                double ahorros2 = ahorros - preciototal;
                tarjeta.setAhorros(ahorros2);
                tarjetaRepository.save(tarjeta);

                Integer id = Integer.parseInt(idstr);

                if (tipopedidostr.equals("Web - Delivery") || tipopedidostr.equals("Pre-orden")){
                    Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
                    if(optionalPedidosPaciente.isPresent()){
                        PedidosPaciente pedidosPaciente = optionalPedidosPaciente.get();
                        pedidosPaciente.setEstado_del_pedido("Pendiente");
                        pedidosPacienteRepository.save(pedidosPaciente);
                        response.put("response", "OK");
                    }
                    else{
                        response.put("response", "NO ENCONTRADO");
                    }
                }
                if (tipopedidostr.equals("Presencial - Pago con tarjeta")){
                    Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
                    if(optionalPedidosPacienteRecojo.isPresent()){
                        PedidosPacienteRecojo pedidosPacienteRecojo = optionalPedidosPacienteRecojo.get();
                        pedidosPacienteRecojo.setEstado_del_pedido("Pagado");
                        pedidosPacienteRecojoRepository.save(pedidosPacienteRecojo);
                        response.put("response", "OK");
                    }
                    else{
                        response.put("response", "NO ENCONTRADO");
                    }
                }
            }
            else {
                response.put("response", "FONDOS INSUFICIENTES");
            }
        } else {
            response.put("response", "NO ENCONTRADO");
        }
        return response;
    }

    /*---------------------------------------*/


    /*vista y QRUD de SOLICITUDES DE PEDIDOS*/

    @GetMapping("/farmacista/pedidos")
    public String listarPedidos(Model model) {
        List<PedidosPaciente> listaPedidos1 = pedidosPacienteRepository.findAll();
        List<PedidosPacienteRecojo> listaPedidos2 = pedidosPacienteRecojoRepository.findAll();


        List<PedidosPaciente> listadelivery = new ArrayList<>();
        List<PedidosPacienteRecojo> listarecojo = new ArrayList<>();
        List<PedidosPaciente> listapreordenes = new ArrayList<>();

        for (PedidosPaciente pedido : listaPedidos1) {
            if(pedido.getEstado_del_pedido().equals("Pendiente")){
                if (pedido.getTipo_de_pedido().equals("Web - Delivery")) {
                    listadelivery.add(pedido);
                } else{
                    listapreordenes.add(pedido);
                }
            }
        }

        for (PedidosPacienteRecojo pedido2 : listaPedidos2) {
            if(pedido2.getEstado_del_pedido().equals("Pendiente")){
                listarecojo.add(pedido2);
            }
        }

        model.addAttribute("listaPedidosDely", listadelivery);
        model.addAttribute("tamanodely", listadelivery.size());

        model.addAttribute("listaPedidosReco", listarecojo);
        model.addAttribute("tamanoreco", listarecojo.size());

        model.addAttribute("listaPedidosPreorden", listapreordenes);
        model.addAttribute("tamanopreorden", listapreordenes.size());

        return "farmacista/lista_solicitudes";
    }
    @GetMapping("/farmacista/validarPedido")
    public String validarPedidos(@RequestParam("id") String idStr,
                                 @RequestParam("tipo") String tipo, RedirectAttributes redirectAttributes) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
            if(optionalPedidosPaciente.isPresent()){
                PedidosPaciente pedido = optionalPedidosPaciente.get();

                if(!pedido.getEstado_del_pedido().equals("Pendiente")){
                    return "redirect:/farmacista/pedidos";
                }

                if(pedido.getTipo_de_pedido().equals("Web - Delivery")) {
                    List<MedicamentosDelPedido> listaMedicamentosDely = medicamentosDelPedidoRepository.listaMedicamentosDely(id);

                    for (int i = 0; i < listaMedicamentosDely.size(); i++) {
                        Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosDely.get(i).getNombre_medicamento());
                        Integer stock = medicamento.getInventario();
                        if (stock <= 25) {
                            redirectAttributes.addFlashAttribute("msg", "No se puede validar el pedido ya que el medicamento " + medicamento.getNombre() + " necesita un pedido de reposición por poco stock. ¡Debe generar una pre-orden a parte!");
                            return "redirect:/farmacista/pedidos";
                        }
                    }
                }

                List<MedicamentosDelPedido> listaMedicamentosDely = medicamentosDelPedidoRepository.listaMedicamentosDely(id);
                for (int i = 0; i < listaMedicamentosDely.size(); i++) {
                    Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosDely.get(i).getNombre_medicamento());
                    Integer stock = medicamento.getInventario();
                    Integer cantidad = listaMedicamentosDely.get(i).getCantidad();
                    Integer stocknuevo = stock - cantidad;
                    if(stocknuevo > 0){
                        medicamento.setInventario(stocknuevo);
                        medicamentosRepository.save(medicamento);
                    }
                    else{
                        redirectAttributes.addFlashAttribute("msg", "No se puede validar el pedido ya que el medicamento " + medicamento.getNombre() + " no cuenta con el stock. ¡Debe esperar a que se reponga o cambiar el medicamento!");
                        return "redirect:/farmacista/pedidos";
                    }
                }

                LocalDate fechaActual = LocalDate.now();
                String fechaValidacion = fechaActual.format(formatter);
                pedido.setFecha_validacion(fechaValidacion);

                if(pedido.getTipo_de_pedido().equals("Web - Delivery")){
                    LocalDate fechaFutura = fechaActual.plusDays(4);
                    String fechaEntrega = fechaFutura.format(formatter);
                    pedido.setFecha_entrega(fechaEntrega);
                }
                else{
                    LocalDate fechaFutura = fechaActual.plusDays(7);
                    String fechaEntrega = fechaFutura.format(formatter);
                    pedido.setFecha_entrega(fechaEntrega);
                }

                pedido.setEstado_del_pedido("Recibido");
                pedido.setValidacion_del_pedido("Validado");

                pedidosPacienteRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha aprobado correctamente el pedido con numero: " + pedido.getNumero_tracking());
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
            if(optionalPedidosPacienteRecojo.isPresent()){
                PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                if(!pedido.getEstado_del_pedido().equals("Pendiente")){
                    return "redirect:/farmacista/pedidos";
                }

                LocalDate fechaActual = LocalDate.now();
                String fechaValidacion = fechaActual.format(formatter);
                pedido.setFecha_validacion(fechaValidacion);

                LocalDate fechaFutura = fechaActual.plusDays(4);
                String fechaEntrega = fechaFutura.format(formatter);
                pedido.setFecha_entrega(fechaEntrega);

                pedido.setEstado_del_pedido("Recibido");
                pedido.setValidacion_del_pedido("Validado");

                pedidosPacienteRecojoRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha aprobado correctamente el pedido con numero: " + pedido.getNumero_tracking());
            }
        }

        return "redirect:/farmacista/pedidos";
    }

    @PostMapping("/farmacista/rechazarPedido")
    public String rechazarPedidos(@RequestParam("pedidoId") String idStr,
                                  @RequestParam("pedidoTipo") String tipo,
                                  @RequestParam("comentario") String comentario ,RedirectAttributes redirectAttributes) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(comentario.length() < 5 || comentario.length() > 90){
            redirectAttributes.addFlashAttribute("msg", "El comentario del rechazo no puede tener menos de 5 caracteres ni mas de 90");
            return "redirect:/farmacista/pedidos";
        }

        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
            if(optionalPedidosPaciente.isPresent()){
                PedidosPaciente pedido = optionalPedidosPaciente.get();

                if(!pedido.getEstado_del_pedido().equals("Pendiente")){
                    return "redirect:/farmacista/pedidos";
                }

                LocalDate fechaActual = LocalDate.now();
                String fechaValidacion = fechaActual.format(formatter);
                pedido.setFecha_validacion(fechaValidacion);

                pedido.setEstado_del_pedido("Rechazado");
                pedido.setValidacion_del_pedido("Rechazado");
                pedido.setComentario(comentario);

                pedidosPacienteRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha rechazado correctamente el pedido con numero: " + pedido.getNumero_tracking());
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
            if(optionalPedidosPacienteRecojo.isPresent()){
                PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                if(!pedido.getEstado_del_pedido().equals("Pendiente")){
                    return "redirect:/farmacista/pedidos";
                }

                LocalDate fechaActual = LocalDate.now();
                String fechaValidacion = fechaActual.format(formatter);
                pedido.setFecha_validacion(fechaValidacion);

                pedido.setEstado_del_pedido("Rechazado");
                pedido.setValidacion_del_pedido("Rechazado");
                pedido.setComentario(comentario);

                pedidosPacienteRecojoRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha rechazado correctamente el pedido con numero: " + pedido.getNumero_tracking());
            }
        }

        return "redirect:/farmacista/pedidos";
    }

    @GetMapping("/farmacista/pedidoInfo")
    public String informacionPedidos(@RequestParam("id") String idStr,
                                     @RequestParam("tipo") String tipo, Model model) {

        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
                if(optionalPedidosPaciente.isPresent()){
                    PedidosPaciente pedido = optionalPedidosPaciente.get();

                    if(pedido.getEstado_del_pedido().equals("Pendiente")){
                        String fotoBase64 = "sin receta";
                        if(pedido.getReceta_foto() != null){
                            byte[] fotoBytes = pedido.getReceta_foto();
                            fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        }
                        List<MedicamentosDelPedido> listaMedicamentosDely = medicamentosDelPedidoRepository.listaMedicamentosDely(id);
                        List<Integer> listaStocks = new ArrayList<>();

                        for (int i = 0; i < listaMedicamentosDely.size(); i++) {
                            Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosDely.get(i).getNombre_medicamento());
                            Integer stock = medicamento.getInventario();
                            listaStocks.add(stock);
                        }

                        model.addAttribute("pedido", pedido);
                        model.addAttribute("fotoBase64", fotoBase64);
                        model.addAttribute("listamedicamentodely", listaMedicamentosDely);
                        model.addAttribute("listaStocks", listaStocks);
                        model.addAttribute("dely", 1);
                        model.addAttribute("reco", 0);
                    }
                    else{
                        return "redirect:/farmacista/pedidos";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidos";
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
                if(optionalPedidosPacienteRecojo.isPresent()){
                    PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                    if(pedido.getEstado_del_pedido().equals("Pendiente")){
                        String fotoBase64 = "sin receta";
                        if(pedido.getReceta_foto() != null){
                            byte[] fotoBytes = pedido.getReceta_foto();
                            fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        }
                        List<MedicamentoRecojo> listaMedicamentosReco = medicamentosRecojoRepository.listaMedicamentosReco(id);
                        List<Integer> listaStocks = new ArrayList<>();

                        for (int i = 0; i < listaMedicamentosReco.size(); i++) {
                            Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosReco.get(i).getNombre_medicamento());
                            Integer stock = medicamento.getInventario();
                            listaStocks.add(stock);
                        }

                        model.addAttribute("pedido", pedido);
                        model.addAttribute("fotoBase64", fotoBase64);
                        model.addAttribute("listamedicamentoreco", listaMedicamentosReco);
                        model.addAttribute("listaStocks", listaStocks);
                        model.addAttribute("reco", 1);
                        model.addAttribute("dely", 0);
                    }
                    else{
                        return "redirect:/farmacista/pedidos";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidos";
            }
        }
        if(!tipo.equals("1") && !tipo.equals("2")){
            return "redirect:/farmacista/pedidos";
        }

        return "farmacista/info_solicitud";
    }

    @GetMapping("/farmacista/pedidoInfo/generarPre-Orden")
    public String generarPreordenDely(@RequestParam("pedidoId") String pedidoIdStr,
                                      @RequestParam("medicamentoNombre") String medicamentoNombre,
                                      @RequestParam("cantidad") String cantidadStr, RedirectAttributes redirectAttributes) {

        Integer pedidoid = Integer.parseInt(pedidoIdStr);
        Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(medicamentoNombre);
        Integer cantidadMedicamento = Integer.parseInt(cantidadStr);

        Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(pedidoid);

        if(optionalPedidosPaciente.isPresent()){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            PedidosPaciente pedidoActual = optionalPedidosPaciente.get();

            Integer stock = medicamento.getInventario();
            Integer stockActual = stock - cantidadMedicamento;

            if(stockActual<0){
                redirectAttributes.addFlashAttribute("msg", "No se puede validar el pedido ya que el medicamento " + medicamento.getNombre() + " no cuenta con el stock. ¡Debe esperar a que se reponga o cambiar el medicamento!");
                return "redirect:/farmacista/pedidos";
            }

            PedidosPaciente pedidoPreorden = new PedidosPaciente();

            pedidoPreorden.setUsuario(pedidoActual.getUsuario());
            pedidoPreorden.setNombre_paciente(pedidoActual.getNombre_paciente());
            pedidoPreorden.setApellido_paciente(pedidoActual.getApellido_paciente());
            pedidoPreorden.setDni(pedidoActual.getDni());
            pedidoPreorden.setMedico_que_atiende(pedidoActual.getMedico_que_atiende());
            pedidoPreorden.setSeguro(pedidoActual.getSeguro());
            pedidoPreorden.setDistrito(pedidoActual.getDistrito());
            pedidoPreorden.setCosto_total(medicamento.getPrecio_unidad() * cantidadMedicamento);
            pedidoPreorden.setTipo_de_pedido("Pre-orden");

            LocalDate fechaActual = LocalDate.now();
            String fechaValidacion = fechaActual.format(formatter);
            pedidoPreorden.setFecha_solicitud(fechaValidacion);
            pedidoPreorden.setFecha_validacion(fechaValidacion);

            LocalDate fechaFutura = fechaActual.plusDays(7);
            String fechaEntrega = fechaFutura.format(formatter);
            pedidoPreorden.setFecha_entrega(fechaEntrega);

            pedidoPreorden.setValidacion_del_pedido("Validado");
            pedidoPreorden.setEstado_del_pedido("Recibido");

            //generador de numero de pedidos
            boolean i = true;
            String numpedido = "";
            String banco = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            List<String> lista1 = pedidosPacienteRepository.numerosDePedidosDely();
            List<String> lista2 = pedidosPacienteRecojoRepository.numerosDePedidosReco();
            List<String> lista3 = carritoRepository.numerosDePedidosCarrito();
            int duplicado = 0;
            while (i){
                for (int x = 0; x < 12; x++) {
                    if (x > 0 && x % 4 == 0) {
                        String guion = "-";
                        numpedido += guion;
                    }
                    int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
                    char caracterAleatorio = banco.charAt(indiceAleatorio);
                    numpedido += caracterAleatorio;
                }
                for (String palabra : lista1) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista2) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                for (String palabra : lista3) {
                    if (palabra.equals(numpedido)) {
                        duplicado++;
                    }
                }
                if(duplicado == 0){
                    break;
                }
            }
            pedidoPreorden.setNumero_tracking(numpedido);

            pedidoPreorden.setAviso_vencimiento("Si");
            pedidoPreorden.setDireccion(pedidoActual.getDireccion());
            pedidoPreorden.setTelefono(pedidoActual.getTelefono());
            pedidoPreorden.setHora_de_entrega(pedidoActual.getHora_de_entrega());

            pedidosPacienteRepository.save(pedidoPreorden);

            MedicamentosDelPedido medicamentosDelPedidoNuevo = new MedicamentosDelPedido();

            medicamentosDelPedidoNuevo.setId(medicamento.getId());
            medicamentosDelPedidoNuevo.setNombre_medicamento(medicamento.getNombre());

            String costomedi = Double.toString(medicamento.getPrecio_unidad());
            medicamentosDelPedidoNuevo.setCosto_medicamento(costomedi);

            medicamentosDelPedidoNuevo.setCantidad(cantidadMedicamento);
            medicamentosDelPedidoNuevo.setPedidosPaciente(pedidoPreorden);
            medicamentosDelPedidoNuevo.setUsuario(pedidoActual.getUsuario());

            medicamentosDelPedidoRepository.save(medicamentosDelPedidoNuevo);

            medicamentosDelPedidoRepository.borrarMedicamentoPocoStock(medicamento.getNombre(), pedidoid, pedidoActual.getUsuario().getId());

            if(cantidadMedicamento == 1){
                redirectAttributes.addFlashAttribute("msg", "Se ha generado una pre-orden de compra para " + cantidadStr + " unidad de " + medicamento.getNombre());
            }
            else{
                redirectAttributes.addFlashAttribute("msg", "Se ha generado una pre-orden de compra para " + cantidadStr + " unidades de " + medicamento.getNombre());
            }
        }

        return "redirect:/farmacista/pedidoInfo?id=" + pedidoid + "&tipo=1";
    }
    /*-----------------------*/

    /*vista y QRUD de PEDIDOS*/

    @GetMapping("/farmacista/pedidosValidados")
    public String listarPedidosValidados(Model model) {
        List<PedidosPaciente> listaPedidos1 = pedidosPacienteRepository.findAll();
        List<PedidosPacienteRecojo> listaPedidos2 = pedidosPacienteRecojoRepository.findAll();


        List<PedidosPaciente> listadelivery = new ArrayList<>();
        List<PedidosPacienteRecojo> listarecojo = new ArrayList<>();
        List<PedidosPaciente> listapreordenes = new ArrayList<>();

        for (PedidosPaciente pedido : listaPedidos1) {
            if(!pedido.getEstado_del_pedido().equals("Pendiente") && pedido.getValidacion_del_pedido().equals("Validado")){
                if (pedido.getTipo_de_pedido().equals("Web - Delivery")) {
                    listadelivery.add(pedido);
                } else{
                    listapreordenes.add(pedido);
                }
            }
        }

        for (PedidosPacienteRecojo pedido2 : listaPedidos2) {
            if(!pedido2.getEstado_del_pedido().equals("Pendiente") && pedido2.getValidacion_del_pedido().equals("Validado")){
                listarecojo.add(pedido2);
            }
        }

        model.addAttribute("listaPedidosDely", listadelivery);
        model.addAttribute("tamanodely", listadelivery.size());

        model.addAttribute("listaPedidosReco", listarecojo);
        model.addAttribute("tamanoreco", listarecojo.size());

        model.addAttribute("listaPedidosPreorden", listapreordenes);
        model.addAttribute("tamanopreorden", listapreordenes.size());

        return "farmacista/lista_pedidos";
    }

    @GetMapping("/farmacista/pedidosValidados/pedidoInfo")
    public String estadoTrack(@RequestParam("id") String idStr,
                              @RequestParam("tipo") String tipo , Model model){

        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
                if(optionalPedidosPaciente.isPresent()){

                    PedidosPaciente pedidosPaciente = optionalPedidosPaciente.get();

                    if(pedidosPaciente.getValidacion_del_pedido().equals("Validado")){

                        List<MedicamentosDelPedido> listaMedicamentosDely = medicamentosDelPedidoRepository.listaMedicamentosDely(id);
                        List<String> listafotos = new ArrayList<>();

                        for (int i = 0; i < listaMedicamentosDely.size(); i++) {
                            Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosDely.get(i).getNombre_medicamento());
                            byte[] fotoBytes = medicamento.getFoto();
                            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                            listafotos.add(fotoBase64);
                        }

                        String fotoBase64 = "sin receta";
                        if(pedidosPaciente.getReceta_foto() != null){
                            byte[] fotoBytes = pedidosPaciente.getReceta_foto();
                            fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        }

                        model.addAttribute("pedido", pedidosPaciente);
                        model.addAttribute("listaFotos", listafotos);
                        model.addAttribute("fotoBase64", fotoBase64);
                        model.addAttribute("listamedicamentos", listaMedicamentosDely);
                        model.addAttribute("dely", 1);
                        model.addAttribute("reco", 0);
                    }
                    else{
                        return "redirect:/farmacista/pedidosValidados";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidosValidados";
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
                if(optionalPedidosPacienteRecojo.isPresent()){
                    PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                    if(pedido.getValidacion_del_pedido().equals("Validado")){

                        List<MedicamentoRecojo> listaMedicamentosReco = medicamentosRecojoRepository.listaMedicamentosReco(id);
                        List<String> listafotos = new ArrayList<>();

                        for (int i = 0; i < listaMedicamentosReco.size(); i++) {
                            Medicamentos medicamento = medicamentosRepository.medicamentoPorNombre(listaMedicamentosReco.get(i).getNombre_medicamento());
                            byte[] fotoBytes = medicamento.getFoto();
                            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                            listafotos.add(fotoBase64);
                        }

                        String fotoBase64 = "sin receta";
                        if(pedido.getReceta_foto() != null){
                            byte[] fotoBytes = pedido.getReceta_foto();
                            fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                        }

                        model.addAttribute("pedido", pedido);
                        model.addAttribute("listafotos", listafotos);
                        model.addAttribute("fotoBase64", fotoBase64);
                        model.addAttribute("listamedicamentos", listaMedicamentosReco);
                        model.addAttribute("reco", 1);
                        model.addAttribute("dely", 0);
                    }
                    else{
                        return "redirect:/farmacista/pedidosValidados";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidosValidados";
            }
        }
        if(!tipo.equals("1") && !tipo.equals("2")){
            return "redirect:/farmacista/pedidosValidados";
        }

        return "farmacista/info_pedido";

    }

    @GetMapping("/farmacista/pedidosValidados/progresarEstado")
    public String progresarTracking(@RequestParam("id") String idStr,
                                    @RequestParam("tipo") String tipo , RedirectAttributes redirectAttrs){
        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
                if(optionalPedidosPaciente.isPresent()){

                    PedidosPaciente pedido = optionalPedidosPaciente.get();

                    if(pedido.getValidacion_del_pedido().equals("Validado")){
                        if(pedido.getEstado_del_pedido().equals("Recibido")){
                            pedido.setEstado_del_pedido("En proceso");
                            pedidosPacienteRepository.save(pedido);
                            redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                        }
                        else{
                            if(pedido.getEstado_del_pedido().equals("En proceso")){
                                pedido.setEstado_del_pedido("Empaquetando");
                                pedidosPacienteRepository.save(pedido);
                                redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                            }
                            if(pedido.getEstado_del_pedido().equals("En ruta")){
                                pedido.setEstado_del_pedido("Entregado");
                                pedidosPacienteRepository.save(pedido);
                                redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                            }
                        }
                    }
                    else{
                        return "redirect:/farmacista/pedidosValidados";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidosValidados";
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            if(id > 0){
                Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
                if(optionalPedidosPacienteRecojo.isPresent()){
                    PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                    if(pedido.getValidacion_del_pedido().equals("Validado")){
                        if(pedido.getEstado_del_pedido().equals("Recibido")){
                            pedido.setEstado_del_pedido("En proceso");
                            pedidosPacienteRecojoRepository.save(pedido);
                            redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                        }
                        else{
                            if(pedido.getEstado_del_pedido().equals("En proceso")){
                                pedido.setEstado_del_pedido("Empaquetando");
                                pedidosPacienteRecojoRepository.save(pedido);
                                redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                            }
                            if(pedido.getEstado_del_pedido().equals("En ruta")){
                                pedido.setEstado_del_pedido("Entregado");
                                pedidosPacienteRecojoRepository.save(pedido);
                                redirectAttrs.addFlashAttribute("msg", "El pedido " + pedido.getNumero_tracking() + " avanzo al estado " + pedido.getEstado_del_pedido());
                            }
                        }
                    }
                    else{
                        return "redirect:/farmacista/pedidosValidados";
                    }
                }
            }
            else{
                return "redirect:/farmacista/pedidosValidados";
            }
        }
        if(!tipo.equals("1") && !tipo.equals("2")){
            return "redirect:/farmacista/pedidosValidados";
        }
        return "redirect:/farmacista/pedidosValidados";
    }

    @PostMapping("/farmacista/pedidosValidados/agregarMapa")
    public String agregarMapaAPedidos(@RequestParam("pedidoId") String idStr,
                                      @RequestParam("pedidoTipo") String tipo,
                                      @RequestParam("mapLink") String mapLink ,RedirectAttributes redirectAttributes) {

        if(mapLink.isEmpty()){
            redirectAttributes.addFlashAttribute("msg", "El link del mapa no puede queda vacio");
            return "redirect:/farmacista/pedidosValidados";
        }

        if(!mapLink.contains("https://www.google.com/maps/embed?pb=")){
            redirectAttributes.addFlashAttribute("msg", "El link del mapa ingresado no es valido");
            return "redirect:/farmacista/pedidosValidados";
        }

        if(tipo.equals("1")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPaciente> optionalPedidosPaciente = pedidosPacienteRepository.findById(id);
            if(optionalPedidosPaciente.isPresent()){
                PedidosPaciente pedido = optionalPedidosPaciente.get();

                pedido.setMapa_tracking(mapLink);
                pedido.setEstado_del_pedido("En ruta");

                pedidosPacienteRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha agregó correctamente el mapa y se avanzó el estado del pedido: " + pedido.getNumero_tracking());
            }
        }
        if(tipo.equals("2")){
            Integer id = Integer.parseInt(idStr);
            Optional<PedidosPacienteRecojo> optionalPedidosPacienteRecojo = pedidosPacienteRecojoRepository.findById(id);
            if(optionalPedidosPacienteRecojo.isPresent()){
                PedidosPacienteRecojo pedido = optionalPedidosPacienteRecojo.get();

                pedido.setMapa_tracking(mapLink);
                pedido.setEstado_del_pedido("En ruta");

                pedidosPacienteRecojoRepository.save(pedido);

                redirectAttributes.addFlashAttribute("msg", "Se ha agregó correctamente el mapa y se avanzó el estado del pedido: " + pedido.getNumero_tracking());
            }
        }

        return "redirect:/farmacista/pedidosValidados";
    }

    /*-----------------------*/

}
