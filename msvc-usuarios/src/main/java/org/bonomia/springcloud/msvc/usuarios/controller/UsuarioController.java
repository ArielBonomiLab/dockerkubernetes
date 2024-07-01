package org.bonomia.springcloud.msvc.usuarios.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.bonomia.springcloud.msvc.usuarios.models.entity.Usuario;
import org.bonomia.springcloud.msvc.usuarios.services.UsuarioService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private Environment env;

	@GetMapping
	public ResponseEntity<?> getUsuarios() {
		Map<String, Object> body = new HashMap<>();
		body.put("usuarios", usuarioService.listar());
		body.put("podinfo", env.getProperty("MY_POD_NAME") +": " +env.getProperty("MY_POD_IP"));		
		//return Collections.singletonMap("usuarios", usuarioService.listar());
		return ResponseEntity.ok(body);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUsuario(@PathVariable Long id) {
		Optional<Usuario> usuario = usuarioService.buscarPorId(id);
		if (usuario.isPresent()) {
			return ResponseEntity.ok(usuario.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> crearUsuario(@Valid @RequestBody Usuario usuario, BindingResult result) {

		if (result.hasErrors()) {
			return validarCampos(result);
		}

		if (!usuario.getEmail().isEmpty() && usuarioService.byEmail(usuario.getEmail()).isPresent()) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese Email."));
		}
 
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editarUsuario(@Valid @RequestBody Usuario usuario, @PathVariable Long id,
			BindingResult result) {
		
		if (result.hasErrors()) {
			return validarCampos(result);
		}
		Optional<Usuario> o = usuarioService.buscarPorId(id);
		if (o.isPresent()) {
			Usuario usuarioDB = o.get();
			if (!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase(usuarioDB.getEmail()) && usuarioService.byEmail(usuario.getEmail()).isPresent()) {
				return ResponseEntity.badRequest()
						.body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese Email."));
			}
			usuarioDB.setNombre(usuario.getNombre());
			usuarioDB.setEmail(usuario.getEmail());
			usuarioDB.setPassword(usuario.getPassword());
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuarioDB));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> borrarUsuario(@PathVariable Long id) {
		Optional<Usuario> o = usuarioService.buscarPorId(id);
		if (o.isPresent()) {
			usuarioService.eliminar(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/usuarios-por-curso")
	public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(usuarioService.listarPorIds(ids));
	}
	
	@GetMapping("/authorized")
	public Map<String, Object> authorized(@RequestParam() String code){
		return Collections.singletonMap("code", code);
	}

	private ResponseEntity<Map<String, String>> validarCampos(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}

}
