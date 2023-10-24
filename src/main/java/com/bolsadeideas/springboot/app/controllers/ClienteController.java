package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;
import com.bolsadeideas.springboot.app.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private IClienteService clienteService; 
	
	@Autowired
	private IUploadFileService uploadFileService;

	@Secured("ROLE_USER")
	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso=null;
		
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename()+"\"").body(recurso);
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id,Map<String,Object> model,RedirectAttributes flash) {
		
		Cliente cliente =  clienteService.fecthByidWithFacturas(id); //clienteService.findOne(id);
		if(cliente==null) {
			flash.addAttribute("error","el cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle Cliente: " + cliente.getNombre());
		
		return "ver";
	}
	
	@RequestMapping(value={"/listar","/"},method=RequestMethod.GET)
	public String listar(@RequestParam(name="page",defaultValue="0") int page,  Model model,
			Authentication authentication,HttpServletRequest request) {
		
		if(authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null) {
			logger.info("Utilizando Forma estática => Hola usuario autenticado, tu username es: ".concat(auth.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tiene acceso"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" no tiene acceso"));
		}
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,"");
		
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Usando otra forma => Hola ".concat(auth.getName()).concat(" tiene acceso"));
		} else {
			logger.info("Usando otra forma => Hola ".concat(auth.getName()).concat(" no tiene acceso"));
		}
		
		Pageable pageRequest = PageRequest.of(page,5);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender= new PageRender<>("/listar",clientes);

		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes",clientes);
		model.addAttribute("page",pageRender);
		return "listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/form")
	public String crear(Map<String,Object> model) {
		model.put("titulo","formulario de cliente");
		
		Cliente cliente = new Cliente();
		
		model.put("cliente",cliente);
		
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/form",method=RequestMethod.POST)
	public String guardar(@Valid Cliente cliente,BindingResult result,Model model,@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		if(!foto.isEmpty()) {
			
			if(cliente.getId()!=null && cliente.getId()>0 && cliente.getFoto()!=null && cliente.getFoto().length()>0) {
				
				uploadFileService.delete(cliente.getFoto());
			}
			
			String uniqueFilename=null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			flash.addFlashAttribute("info","Ha subido correctamente" + uniqueFilename);
			
			cliente.setFoto(uniqueFilename);
		}
		
		String mensajeFlash = (cliente.getId()!=null)?"Cliente editado con exito":"Cliente Creado con éxito";
		
		clienteService.save(cliente);
		status.setComplete();
		
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";
		
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String,Object> model,RedirectAttributes flash) {
		Cliente cliente = new Cliente();
		
		if(id>0) {
			cliente = clienteService.findOne(id);
			
			if(cliente==null) {
				flash.addFlashAttribute("error", "El Cliente no existe en base de datos");
				return "redirect:/listar";				
			}
			
		}else {
			flash.addFlashAttribute("error", "El ID del Cliente no puede ser cero");
			return "redirect:/listar";
		}
		model.put("titulo","editar cliente");
		model.put("cliente",cliente);
		
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes flash) {
		
		if(id>0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");
			
			if(uploadFileService.delete(cliente.getFoto())) {
					flash.addAttribute("info","Foto " + cliente.getFoto() + " eliminada con éxito");
			}
			
		}
		
		return "redirect:/listar";
	}
	
	
	private boolean hasRole(String role) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();

		if(auth == null) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		return authorities.contains(new SimpleGrantedAuthority(role));
		
	}
}
